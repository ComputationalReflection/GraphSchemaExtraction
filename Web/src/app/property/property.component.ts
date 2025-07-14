import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { DataService } from '../dataservice';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { JsonPipe } from '@angular/common';


@Component({
  selector: 'app-property',
  standalone: true,
  imports: [MatTableModule, MatInputModule, MatFormFieldModule, MatButtonModule, MatExpansionModule, MatIconModule, JsonPipe],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  templateUrl: './property.component.html',
  styleUrl: './property.component.css'
})
export class PropertyComponent implements OnInit {

  propertyData: any
  url: string = '../../assets/output.json';
  propertyName: string
  absolute: number = 0
  relative: number = 0
  expandedElement: Property | null

  dataSource: MatTableDataSource<Property>
  columnsToDisplay = ['type', 'absolute', 'relative'];
  columnsToDisplayWithExpand = [...this.columnsToDisplay, 'expand'];

  constructor(private http: HttpClient, private route: ActivatedRoute, private dataservice: DataService) { }

  ngOnInit(): void {
    if (this.dataservice.property != undefined) {
      this.propertyData = this.dataservice.property
      this.createDataSource()
    } else {
      this.http.get(this.url).subscribe(res => {
        var elements = (res as any)
        if (this.route.snapshot.paramMap.get('part') == 'labels') {
          elements = (res as any).nodes.dataLabels
        }
        else elements = (res as any).relationships.dataLabels
        breakme: for (var element of elements) {
          if (Object.keys(element)[0] == this.route.snapshot.paramMap.get('name')) {
            var properties = element[Object.keys(element)[0]]
            if (this.route.snapshot.paramMap.get('part') == 'labels')
              properties = element[Object.keys(element)[0]].nodeProperties
            else if (this.route.snapshot.paramMap.get('labelOut') == null)
              properties = element[Object.keys(element)[0]].relationshipProperties
            else {
              const labelOut = this.route.snapshot.paramMap.get('labelOut');
              const labelIn = this.route.snapshot.paramMap.get('labelIn');
              if (labelOut !== null && labelIn !== null) {
                breakme2: for (var element of element[Object.keys(element)[0]].relationshipGivenNodeLabelsProperties) {
                  if (Object.keys(element)[0] == labelOut) {
                    for (var element of element[Object.keys(element)[0]]) {
                      if (Object.keys(element)[0] == labelIn) {
                        properties = element[Object.keys(element)[0]];
                        break breakme2;
                      }
                    }
                  }
                }
              }
            }
            for (var property of properties) {
              if (Object.keys(property)[0] == this.route.snapshot.paramMap.get('property')) {
                this.propertyData = property;
                this.createDataSource();
                break breakme;
              }
            }
          }
        }
      })
    }
  }

  createDataSource(): void {
    this.propertyName = Object.keys(this.propertyData)[0]
    this.absolute = this.propertyData[this.propertyName][0].absolute
    this.relative = Number((this.propertyData[this.propertyName][0].relative * 100).toFixed(2))

    var propertyDataArray: Property[] = []
    for (const element of this.propertyData[this.propertyName][0].byType) {
      var propertyType: string = Object.keys(element)[0]
      propertyDataArray.push({ "type": propertyType, "absolute": element[propertyType][0].absolute, "relative": (element[propertyType][0].relative * 100).toFixed(2) + " %", "analysis": element[propertyType][0].analysis })
    }
    this.dataSource = new MatTableDataSource(propertyDataArray)
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value
    this.dataSource.filter = filterValue.trim().toLowerCase()
  }
}

export interface Property {
  type: string;
  absolute: number;
  relative: string;
  analysis: any;
}