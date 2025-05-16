import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DentistPasswordUpdateComponent } from './dentist-password-update.component';

describe('DentistPasswordUpdateComponent', () => {
  let component: DentistPasswordUpdateComponent;
  let fixture: ComponentFixture<DentistPasswordUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DentistPasswordUpdateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DentistPasswordUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
