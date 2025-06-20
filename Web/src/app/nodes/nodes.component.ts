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
  selector: 'app-nodes',
  standalone: true,
  imports: [MatTableModule, MatInputModule, MatFormFieldModule, MatButtonModule, NgApexchartsModule],
  templateUrl: './nodes.component.html',
  styleUrl: './nodes.component.css'
})
export class NodesComponent implements OnInit, DoCheck {
  @ViewChild("chart") chart: ChartComponent;
  public chartOptions: Partial<ChartOptions>;

  nNodes = 0
  nNodesWLabel = 0
  nodesData: any
  url: string = '../../assets/output.json';

  nodesLabels: any[] = []
  dataSource = new MatTableDataSource()
  displayedColumns: string[] = ['label', 'absolute', 'relative', 'actions']
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSource.filter = filterValue.trim().toLowerCase()
  }

  constructor(private http: HttpClient, private router: Router, private dataservice: DataService) { }

  ngOnInit(): void {
    this.http.get(this.url).subscribe(res => {
      this.nodesData = (res as any).nodes;
      var series: number[] = []
      var labels: String[] = []
      var other: number = 0
      for (var element of this.nodesData.dataLabels) {
        var labelName: string = Object.keys(element)[0]
        this.nodesLabels.push({ "label": labelName, "absolute": element[labelName].absolute, "relative": (element[labelName].relative * 100).toFixed(2) + " %", "data": element })
        if (element[labelName].absolute >= this.nodesData.nNodes * 0.1) {
          series.push(element[labelName].absolute)
          labels.push(labelName)
        } else {
          other = other + element[labelName].absolute
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
    if (this.nodesData != null || this.nodesData != undefined) {
      this.nNodes = this.nodesData.nNodes
      this.nNodesWLabel = this.nodesData.nNodesWLabel
      this.dataSource.data = this.nodesLabels
    }
  }

  seeLabel(labelData: any): void {
    var labelName: string = Object.keys(labelData)[0]
    this.dataservice.label = labelData
    this.router.navigate(['/labels', labelName])
  }
}