import { Component, DoCheck, OnInit, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { DataService } from '../dataservice';
import { ChartComponent, NgApexchartsModule } from "ng-apexcharts";

import {
  ApexNonAxisChartSeries,
  ApexResponsive,
  ApexChart
} from "ng-apexcharts";

export type ChartOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  responsive: ApexResponsive[];
  labels: any;
};

@Component({
  selector: 'app-relationships',
  standalone: true,
  imports: [MatTableModule, MatInputModule, MatFormFieldModule, MatButtonModule, NgApexchartsModule],
  templateUrl: './relationships.component.html',
  styleUrl: './relationships.component.css'
})
export class RelationshipsComponent implements OnInit, DoCheck {
  @ViewChild("chart") chart: ChartComponent;
  public chartOptions: Partial<ChartOptions>;

  nRelationships = 0
  nRelationshipsWType = 0
  relationshipsData: any
  url: string = '../../assets/output.json';

  relationshipsLabels: any[] = []
  dataSource = new MatTableDataSource()
  displayedColumns: string[] = ['type', 'absolute', 'relative', 'actions']
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSource.filter = filterValue.trim().toLowerCase()
  }

  constructor(private http: HttpClient, private router: Router, private dataservice: DataService) { }

  ngOnInit(): void {
    this.http.get(this.url).subscribe(res => {
      this.relationshipsData = (res as any).relationships;
      var series: number[] = []
      var labels: String[] = []
      var other: number = 0
      for (var element of this.relationshipsData.dataLabels) {
        var typeName: string = Object.keys(element)[0]
        this.relationshipsLabels.push({ "type": typeName, "absolute": element[typeName].absolute, "relative": (element[typeName].relative * 100).toFixed(2) + " %", "data": element })
        if (element[typeName].absolute >= this.relationshipsData.nRelationships * 0.05) {
          series.push(element[typeName].absolute)
          labels.push(typeName)
        } else {
          other = other + element[typeName].absolute
        }
      } if (other > 0) {
        series.push(other)
        labels.push("Others")
      }
      this.chartOptions = {
        series: series,
        chart: {
          width: 380,
          type: "pie"
        },
        labels: labels,
        responsive: [
          {
            breakpoint: 480,
            options: {
              chart: {
                width: 200
              },
              legend: {
                position: "bottom"
              }
            }
          }
        ]
      };
    })
  }

  ngDoCheck(): void {
    if (this.relationshipsData != null || this.relationshipsData != undefined) {
      this.nRelationships = this.relationshipsData.nRelationships
      this.nRelationshipsWType = this.relationshipsData.nRelationshipsWLab
      this.dataSource.data = this.relationshipsLabels
    }
  }

  seeLabel(labelData: any): void {
    var typeName: string = Object.keys(labelData)[0]
    this.dataservice.label = labelData
    this.router.navigate(['/types', typeName])
  }
}