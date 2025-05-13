import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DentistFormModalComponent } from './dentist-form-modal.component';

describe('DentistFormModalComponent', () => {
  let component: DentistFormModalComponent;
  let fixture: ComponentFixture<DentistFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DentistFormModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DentistFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
