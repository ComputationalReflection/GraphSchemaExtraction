import { Component, DoCheck, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../dataservice';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';

@Component({
  selector: 'app-label',
  standalone: true,
  imports: [MatTableModule, MatInputModule, MatFormFieldModule, MatButtonModule, MatExpansionModule],
  templateUrl: './label.component.html',
  styleUrl: './label.component.css'
})
export class LabelComponent implements OnInit, DoCheck {

  labelData: any
  url: string = '../../assets/output.json';
  labelName: string = "a"
  absolute: number = 0
  relative: number = 0

  dataSourceOut = new MatTableDataSource()
  dataSourceIn = new MatTableDataSource()
  dataSourceProperties = new MatTableDataSource()
  displayedColumns: string[] = ['type', 'absolute', 'relative']
  displayedColumnsProperties: string[] = ['type', 'absolute', 'relative', 'actions']
  applyFilterOut(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSourceOut.filter = filterValue.trim().toLowerCase()
  }
  applyFilterIn(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSourceIn.filter = filterValue.trim().toLowerCase()
  }
  applyFilterProperties(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSourceProperties.filter = filterValue.trim().toLowerCase()
  }

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private dataservice: DataService) { }

  ngOnInit(): void {
    if (this.dataservice.label != undefined) {
      this.labelData = this.dataservice.label
    } else {
      this.http.get(this.url).subscribe(res => {
        breakme: for (var element of (res as any).nodes.dataLabels) {
          if (Object.keys(element)[0] == this.route.snapshot.paramMap.get('label')) {
            this.labelData = element
            break breakme;
          }
        }
      })
    }
  }

  ngDoCheck(): void {
    if (this.labelData != undefined) {
      this.labelName = Object.keys(this.labelData)[0]
      this.absolute = this.labelData[this.labelName].absolute
      this.relative = this.labelData[this.labelName].relative * 100

      var relationshipsOutArray: any[] = []
      for (const element of this.labelData[this.labelName].relationshipsOut) {
        var relationshipsOutName: string = Object.keys(element)[0]
        relationshipsOutArray.push({ "type": relationshipsOutName, "absolute": element[relationshipsOutName][0].absolute, "relative": (element[relationshipsOutName][0].relative * 100).toFixed(4) + " %" })
      }
      this.dataSourceOut.data = relationshipsOutArray

      var relationshipsInArray: any[] = []
      for (const element of this.labelData[this.labelName].relationshipsIn) {
        var relationshipsInName: string = Object.keys(element)[0]
        relationshipsInArray.push({ "type": relationshipsInName, "absolute": element[relationshipsInName][0].absolute, "relative": (element[relationshipsInName][0].relative * 100).toFixed(4) + " %" })
      }
      this.dataSourceIn.data = relationshipsInArray

      var propertiesArray: any[] = []
      for (const element of this.labelData[this.labelName].nodeProperties) {
        var propertiesName: string = Object.keys(element)[0]
        propertiesArray.push({ "type": propertiesName, "absolute": element[propertiesName][0].absolute, "relative": (element[propertiesName][0].relative * 100).toFixed(4) + " %", "data": element })
      }
      this.dataSourceProperties.data = propertiesArray
    }
  }

  seeProperty(propertyData: any): void {
    var propertyName: string = Object.keys(propertyData)[0]
    this.dataservice.property = propertyData
    this.router.navigate([this.router.url + '/property', propertyName])
  }
}