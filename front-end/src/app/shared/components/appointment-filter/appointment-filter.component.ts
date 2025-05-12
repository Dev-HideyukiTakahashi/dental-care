import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-appointment-filter',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  templateUrl: './appointment-filter.component.html',
  styleUrls: ['./appointment-filter.component.scss'],
})
export class AppointmentFilterComponent {
  @Output() dateSelected = new EventEmitter<Date | null>();
  @Output() clearFilter = new EventEmitter<void>();

  selectedDate: Date | null = null;

  onDateChange(): void {
    this.dateSelected.emit(this.selectedDate);
  }

  onClearFilter(): void {
    this.selectedDate = null;
    this.clearFilter.emit();
  }
}
