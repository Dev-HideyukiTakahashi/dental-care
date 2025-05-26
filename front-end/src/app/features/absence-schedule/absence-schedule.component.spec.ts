import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AbsenceScheduleComponent } from './absence-schedule.component';

describe('AbsenceScheduleComponent', () => {
  let component: AbsenceScheduleComponent;
  let fixture: ComponentFixture<AbsenceScheduleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AbsenceScheduleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AbsenceScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
