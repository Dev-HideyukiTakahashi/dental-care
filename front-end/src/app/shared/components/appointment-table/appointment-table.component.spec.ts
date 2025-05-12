import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentTableComponent } from './appointment-table.component';

describe('AppointmentTableComponent', () => {
  let component: AppointmentTableComponent;
  let fixture: ComponentFixture<AppointmentTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppointmentTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
