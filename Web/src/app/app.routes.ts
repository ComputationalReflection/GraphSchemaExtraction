import { Routes } from '@angular/router';
import { NodesComponent } from './nodes/nodes.component';
import { HomeComponent } from './home/home.component';
import { LabelComponent } from './label/label.component';
import { PropertyComponent } from './property/property.component';
import { RelationshipsComponent } from './relationships/relationships.component';
import { TypeComponent } from './type/type.component';
import { TypeLabelComponent } from './type-label/type-label.component';
import { MultiLabelComponent } from './multi-label/multi-label.component';


export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        title: 'Home page'
    },
    {
        path: 'nodes',
        component: NodesComponent,
        title: 'Nodes home'
    },
    {
        path: 'labels/:label',
        component: LabelComponent,
        title: 'Label detail'
    },
    {
        path: ':part/:name/property/:property',
        component: PropertyComponent,
        title: 'Property detail'
    },
    {
        path: 'relationships',
        component: RelationshipsComponent,
        title: 'Relationships home'
    },
    {
        path: 'types/:type',
        component: TypeComponent,
        title: 'Type detail'
    },
    {
        path: 'types/:type/:label',
        component: TypeLabelComponent,
        title: 'Type-label detail'
    },
    {
        path: ':part/:name/:labelOut/:labelIn/property/:property',
        component: PropertyComponent,
        title: 'Property detail'
    },
    {
        path: 'multiLabel',
        component: MultiLabelComponent,
        title: 'MultiLabel Home'
    }
];
