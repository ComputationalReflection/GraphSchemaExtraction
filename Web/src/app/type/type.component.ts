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
  selector: 'app-type',
  standalone: true,
  imports: [MatTableModule, MatInputModule, MatFormFieldModule, MatButtonModule, MatExpansionModule],
  templateUrl: './type.component.html',
  styleUrl: './type.component.css'
})
export class TypeComponent implements OnInit, DoCheck {

  typeData: any
  url: string = '../../assets/output.json';
  typeName: string = "a"
  absolute: number = 0
  relative: number = 0

  dataSourceOut = new MatTableDataSource()
  dataSourceIn = new MatTableDataSource()
  dataSourceProperties = new MatTableDataSource()
  dataSourceSameNode = new MatTableDataSource()
  dataSourceGivenNodeLabelProperties = new MatTableDataSource()
  displayedColumns: string[] = ['type', 'absolute', 'relative']
  displayedColumnsProperties: string[] = ['type', 'absolute', 'relative', 'actions']
  displayedTypeColumn: string[] = ['type', 'actions']
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
  applyFilterSameLabel(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSourceSameNode.filter = filterValue.trim().toLowerCase()
  }
  applyFilterGivenNodeLabelProperties(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSourceGivenNodeLabelProperties.filter = filterValue.trim().toLowerCase()
  }

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private dataservice: DataService) { }

  ngOnInit(): void {
    if (this.dataservice.label != undefined) {
      this.typeData = this.dataservice.label
      console.log(this.typeData);

    } else {
      this.http.get(this.url).subscribe(res => {
        breakme: for (var element of (res as any).relationships.dataLabels) {
          if (Object.keys(element)[0] == this.route.snapshot.paramMap.get('type')) {
            this.typeData = element
            break breakme;
          }
        }
      })
    }
  }

  ngDoCheck(): void {
    if (this.typeData != undefined) {
      this.typeName = Object.keys(this.typeData)[0]
      this.absolute = this.typeData[this.typeName].absolute
      this.relative = this.typeData[this.typeName].relative * 100

      var relationshipsOutArray: any[] = []
      for (const element of this.typeData[this.typeName].relationshipsWithOutNode) {
        var relationshipsOutName: string = Object.keys(element)[0]
        relationshipsOutArray.push({ "type": relationshipsOutName, "absolute": element[relationshipsOutName][0].absolute, "relative": (element[relationshipsOutName][0].relative * 100).toFixed(4) + " %" })
      }
      this.dataSourceOut.data = relationshipsOutArray

      var relationshipsInArray: any[] = []
      for (const element of this.typeData[this.typeName].relationshipsWithInNode) {
        var relationshipsInName: string = Object.keys(element)[0]
        relationshipsInArray.push({ "type": relationshipsInName, "absolute": element[relationshipsInName][0].absolute, "relative": (element[relationshipsInName][0].relative * 100).toFixed(4) + " %" })
      }
      this.dataSourceIn.data = relationshipsInArray

      var propertiesArray: any[] = []
      for (const element of this.typeData[this.typeName].relationshipProperties) {
        var propertiesName: string = Object.keys(element)[0]
        propertiesArray.push({ "type": propertiesName, "absolute": element[propertiesName][0].absolute, "relative": (element[propertiesName][0].relative * 100).toFixed(4) + " %", "data": element })
      }
      this.dataSourceProperties.data = propertiesArray

      var sameNodeArray: any[] = []
      for (const element of this.typeData[this.typeName].relationshipsWithSameNode) {
        var sameNodeName: string = Object.keys(element)[0]
        sameNodeArray.push({ "type": sameNodeName, "absolute": element[sameNodeName][0].absolute, "relative": (element[sameNodeName][0].relative * 100).toFixed(4) + " %" })
      }
      this.dataSourceSameNode.data = sameNodeArray

      var givenNodeLabelsPropertiesArray: any[] = []
      for (const element of this.typeData[this.typeName].relationshipGivenNodeLabelsProperties) {
        var givenNodeLabelsPropertiesName: string = Object.keys(element)[0]
        givenNodeLabelsPropertiesArray.push({ "type": givenNodeLabelsPropertiesName, "data": element })
      }
      this.dataSourceGivenNodeLabelProperties.data = givenNodeLabelsPropertiesArray
    }
  }

  seeProperty(propertyData: any): void {
    var propertyName: string = Object.keys(propertyData)[0]
    this.dataservice.property = propertyData
    this.router.navigate([this.router.url + '/property', propertyName])
  }

  seeTypeLabel(typeLabelData: any): void {
    this.dataservice.typeLabel = typeLabelData.data
    this.router.navigate([this.router.url + '/' + typeLabelData.type])
  }
}