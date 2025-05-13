import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';
import { DentistService } from '../../core/service/dentist.service';
import { IDentistMin } from '../../model/dentist-min.model';
import { IDentist } from '../../model/dentist.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-dentist-list',
  imports: [CommonModule],
  templateUrl: './dentist-list.component.html',
  styleUrl: './dentist-list.component.scss',
})
export class DentistListComponent implements OnInit {
  private dentistService = inject(DentistService);

  dentists$!: Observable<IDentistMin[]>;
  showDentistModal = false;
  selectedDentist: IDentist | null = null;

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
    this.selectedDentist = null;
    this.showDentistModal = true;
  }

  openEditModal(number: number): void {
    this.showDentistModal = true;
  }

  closeModal(): void {
    this.showDentistModal = false;
    this.selectedDentist = null;
  }

  handleSave(dentist: IDentist): void {
    if (dentist.id) {
      // Atualizar existente
      this.dentistService.updateDentist().subscribe({
        next: () => this.loadDentists(),
        error: (err: any) => console.error(err),
      });
    } else {
      // Criar novo
      this.dentistService.createDentist().subscribe({
        next: () => this.loadDentists(),
        error: (err: any) => console.error(err),
      });
    }
    this.closeModal();
  }

  confirmDelete(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: 'Tem certeza que deseja remover este dentista?',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.dentistService.deleteDentist().subscribe({
          next: () => this.loadDentists(),
          error: (err: any) => console.error(err),
        });
      }
    });
  }
}
