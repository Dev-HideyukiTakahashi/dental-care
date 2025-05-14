import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../../model/page.model';
import { IPatientMin } from '../../model/patient-min.model';
import { IPatient } from '../../model/patient-model';

@Injectable({
  providedIn: 'root',
})
export class PatientService {
  private http = inject(HttpClient);
  private API = 'http://localhost:8080/api/v1';

  findAll(page: number, size: number): Observable<Page<IPatientMin>> {
    return this.http.get<Page<IPatientMin>>(`${this.API}/patients?page=${page}&size=${size}`);
  }

  findById(id: number): Observable<IPatient> {
    return this.http.get<IPatient>(`${this.API}/patients/${id}`);
  }

  updatePatient(patient: IPatient): Observable<IPatient> {
    return this.http.put<IPatient>(`${this.API}/patients/${patient.id}`, patient);
  }

  createPatient(patient: IPatient): Observable<IPatient> {
    return this.http.post<IPatient>(`${this.API}/patients`, patient);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/patients/${id}`);
  }
}
