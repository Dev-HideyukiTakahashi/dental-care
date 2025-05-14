import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';
import { PatientService } from '../../core/service/patient.service';
import { Page } from '../../model/page.model';
import { IPatientMin } from '../../model/patient-min.model';
import { IPatient } from '../../model/patient-model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { PatientDetailsComponent } from '../../shared/components/patient-details/patient-details.component';
import { PatientTableComponent } from '../../shared/components/patient-table/patient-table.component';

@Component({
  selector: 'app-patient-list',
  imports: [PaginationComponent, PatientTableComponent, CommonModule, PatientDetailsComponent],
  templateUrl: './patient-list.component.html',
  styleUrl: './patient-list.component.scss',
})
export class PatientListComponent implements OnInit {
  private patientService = inject(PatientService);

  pageInfo: Page<IPatientMin> = {
    content: [],
    page: { size: 10, number: 0, totalElements: 0, totalPages: 1 },
  };

  patients$!: Observable<IPatientMin[]>;
  selectedPatient: IPatient | null = null;
  showPatientDetailsModal = false;
  showEditModal = false;
  editedPatient!: IPatient;
  editMessage: string | null = null;
  editSuccess = false;
  deleteErrorMessage: string | null = null;

  constructor(private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadPatients(0);
  }

  loadPatients(page: number): void {
    this.patientService.findAll(page, 10).subscribe({
      next: (response) => {
        this.pageInfo = response;
        this.patients$ = of(response.content);
      },
      error: (err: any) => console.error(err),
    });
  }

  openPatientDetails(patientId: number): void {
    this.patientService.findById(patientId).subscribe({
      next: (patient) => {
        this.selectedPatient = patient;
        this.showPatientDetailsModal = true;
      },
      error: (err: any) => console.error(err),
    });
  }

  closeDetailsModal(): void {
    this.showPatientDetailsModal = false;
  }

  openEditModal(id: number): void {
    // this.editedPatient =
    // TODO
    this.showEditModal = true;
    this.editMessage = null;
    this.editSuccess = false;
  }

  closeEditModal(): void {
    this.showEditModal = false;
  }

  openAddPatientModal() {}

  updatePatient(updateData: IPatient): void {
    this.patientService.updatePatient(updateData).subscribe({
      next: () => {
        this.editMessage = 'Paciente atualizado com sucesso!';
        this.editSuccess = true;
        this.loadPatients(this.pageInfo.page.number);
        setTimeout(() => this.closeEditModal(), 2000);
      },
      error: (err: any) => {
        this.editMessage = 'Erro ao atualizar paciente';
        console.error(err);
      },
    });
  }

  onDeletePatient(patientId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: 'Tem certeza que deseja excluir este paciente?',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.patientService.delete(patientId).subscribe({
          next: () => this.loadPatients(this.pageInfo.page.number),
          error: () => {
            this.deleteErrorMessage = 'Paciente associado a registros não pode ser excluído.';
            setTimeout(() => {
              this.deleteErrorMessage = '';
            }, 3000);
          },
        });
      }
    });
  }
}
