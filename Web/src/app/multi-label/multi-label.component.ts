import { Component, OnInit } from '@angular/core';
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
  selector: 'app-multi-label',
  standalone: true,
  imports: [MatTableModule, MatInputModule, MatFormFieldModule, MatButtonModule, MatExpansionModule, CommonModule, MatIconModule, JsonPipe],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  templateUrl: './multi-label.component.html',
  styleUrl: './multi-label.component.css'
})
export class MultiLabelComponent implements OnInit {

  typeData: any
  url: string = '../../assets/output.json';
  typeLabelName: string
  inLabels: any = []

  expandedElement: MultiLabelNode | null

  columnsToDisplay = ['absolute', 'relative'];
  columnsToDisplayWithExpand = [...this.columnsToDisplay, 'expand'];

  applyFilterProperties(event: Event, i: number) {
    const filterValue = (event.target as HTMLInputElement).value
    this.inLabels[i].data.filter = filterValue.trim().toLowerCase()
  }

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.http.get(this.url).subscribe(res => {
      for (let [key, value] of Object.entries((res as any).multiLabelNodes[0])) {
        var values = []
        for (let [key2, value2] of Object.entries((value as any[])[0])) {
          const { absolute, relative, ...analysis } = (value2 as any[])[0]
          values.push({ absolute: (value2 as any[])[0].absolute, relative: (value2 as any[])[0].relative.toFixed(2) + "%", analysis: analysis })
        }
        const dataSource = new MatTableDataSource(values);
        dataSource.filterPredicate = (data, filter: string) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
        this.inLabels.push({ data: dataSource, name: key })
      }
    });
  }
}

export interface MultiLabelNode {
  absolute: number;
  relative: string;
  analysis: any;
}