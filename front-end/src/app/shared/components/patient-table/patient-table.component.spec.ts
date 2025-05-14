import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatientTableComponent } from './patient-table.component';

describe('PatientTableComponent', () => {
  let component: PatientTableComponent;
  let fixture: ComponentFixture<PatientTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatientTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PatientTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
