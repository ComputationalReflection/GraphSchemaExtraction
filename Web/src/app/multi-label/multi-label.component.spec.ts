import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiLabelComponent } from './multi-label.component';

describe('MultiLabelComponent', () => {
  let component: MultiLabelComponent;
  let fixture: ComponentFixture<MultiLabelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MultiLabelComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MultiLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
