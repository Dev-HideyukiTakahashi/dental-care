import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';
import { DentistService } from '../../core/service/dentist.service';
import { IDentistMin } from '../../model/dentist-min.model';
import { IDentist } from '../../model/dentist.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { DentistFormModalComponent } from '../../shared/components/dentist-form-modal/dentist-form-modal.component';

@Component({
  selector: 'app-dentist-list',
  imports: [CommonModule, DentistFormModalComponent],
  templateUrl: './dentist-list.component.html',
  styleUrl: './dentist-list.component.scss',
})
export class DentistListComponent implements OnInit {
  private dentistService = inject(DentistService);

  dentists$!: Observable<IDentistMin[]>;
  showDentistModal = false;
  selectedDentist: IDentist | null = null;
  formErrorMessage: string | null = null;
  deleteErrorMessage: string | null = null;

  constructor(private dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadDentists();
  }

  loadDentists(): void {
    this.dentistService.findAll(0, 10).subscribe((response) => {
      this.dentists$ = of(response.content);
    });
  }

  openAddDentistModal(): void {
    this.formErrorMessage = '';
    this.showDentistModal = true;
  }

  openEditModal(number: number): void {
    this.formErrorMessage = '';
    this.dentistService.findById(number).subscribe({
      next: (response) => {
        (this.selectedDentist = response), (this.showDentistModal = true);
      },
    });
  }

  closeModal(): void {
    this.showDentistModal = false;
    this.selectedDentist = null;
  }

  handleSave(dentist: IDentist): void {
    if (dentist.id) {
      // UPDATE
      this.dentistService.updateDentist(dentist).subscribe({
        next: () => {
          this.loadDentists();
          setTimeout(() => this.closeModal(), 1000);
        },
        error: (err: any) => this.handleFormError(err),
      });
    } else {
      // CREATE
      this.dentistService.createDentist(dentist).subscribe({
        next: () => {
          this.loadDentists();
          setTimeout(() => this.closeModal(), 1000);
        },
        error: (err: any) => this.handleFormError(err),
      });
    }
  }

  handleFormError(err: any): void {
    if (err.status === 409) {
      this.formErrorMessage = 'O e-mail informado já está em uso.';
    } else {
      this.formErrorMessage = 'Erro ao salvar os dados. Tente novamente.';
    }
  }

  confirmDelete(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: 'Tem certeza que deseja remover este dentista?',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.dentistService.deleteDentist(id).subscribe({
          next: () => this.loadDentists(),
          error: () => {
            this.deleteErrorMessage = 'Dentista associado a registros não pode ser excluído.';
            setTimeout(() => {
              this.deleteErrorMessage = '';
            }, 3000);
          },
        });
      }
    });
  }
}
