import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TypeLabelComponent } from './type-label.component';

describe('TypeLabelComponent', () => {
  let component: TypeLabelComponent;
  let fixture: ComponentFixture<TypeLabelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TypeLabelComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TypeLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
