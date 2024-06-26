import { Component, DoCheck, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../dataservice';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { CommonModule, JsonPipe } from '@angular/common';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-type-label',
  standalone: true,
  imports: [MatTableModule, MatInputModule, MatFormFieldModule, MatButtonModule, MatExpansionModule, CommonModule, MatIconModule, JsonPipe],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  templateUrl: './type-label.component.html',
  styleUrl: './type-label.component.css'
})
export class TypeLabelComponent implements OnInit {

  typeData: any
  url: string = '../../assets/output.json';
  typeLabelName: string
  inLabels: any = []

  displayedColumnsProperties: string[] = ['type', 'absolute', 'relative', 'actions']

  applyFilterProperties(event: Event, i: number) {
    const filterValue = (event.target as HTMLInputElement).value
    this.inLabels[i].data.filter = filterValue.trim().toLowerCase()
    console.log(this.inLabels[i]);

  }

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute, private dataservice: DataService) { }

  ngOnInit(): void {
    let dataPromise: Promise<any>;
    if (this.dataservice.typeLabel != undefined) {
      this.typeData = this.dataservice.typeLabel;
      dataPromise = Promise.resolve(this.typeData);
      this.typeLabelName = this.route.snapshot.paramMap.get('label') ?? '';
    } else {
      dataPromise = this.http.get(this.url).toPromise().then(res => {
        const typeName = this.route.snapshot.paramMap.get('type');
        for (let element of (res as any).relationships.dataLabels) {
          if (Object.keys(element)[0] == typeName) {
            const type = element[typeName];
            const typeLabelName = this.route.snapshot.paramMap.get('label');
            for (let element of (type as any).relationshipGivenNodeLabelsProperties) {
              if (Object.keys(element)[0] == typeLabelName) {
                this.typeData = element;
                this.typeLabelName = Object.keys(this.typeData)[0]
                return this.typeData;
              }
            }
          }
        }
      });
    }
    dataPromise.then(() => {
      this.inLabels = this.typeData[this.typeLabelName].map((element: any) => {
        return { name: Object.keys(element)[0], data: createData(element[Object.keys(element)[0]]) };
      });
    });
  }

  seeProperty(propertyData: any, labelName: string): void {
    var propertyName: string = Object.keys(propertyData)[0]
    this.dataservice.property = propertyData
    this.router.navigate([this.router.url + '/' + labelName + '/property', propertyName])
  }
}

function createData(properties: any): MatTableDataSource<any> {
  var propertyDataArray: any[] = []
  for (const element of properties) {
    var propertyType: string = Object.keys(element)[0]
    propertyDataArray.push({ "type": propertyType, "absolute": element[propertyType][0].absolute, "relative": (element[propertyType][0].relative * 100).toFixed(4) + " %", data: element })
  }
  return new MatTableDataSource(propertyDataArray)
}
