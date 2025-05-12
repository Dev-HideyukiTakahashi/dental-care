import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppointmentDetailsModalComponent } from './appointment-details-modal.component';

describe('AppointmentDetailsModalComponent', () => {
  let component: AppointmentDetailsModalComponent;
  let fixture: ComponentFixture<AppointmentDetailsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppointmentDetailsModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppointmentDetailsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
