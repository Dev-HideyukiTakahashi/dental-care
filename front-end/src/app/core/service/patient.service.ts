import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../../model/page.model';
import { IPatientMin } from '../../model/patient-min.model';

@Injectable({
  providedIn: 'root',
})
export class PatientService {
  private http = inject(HttpClient);
  private API = 'http://localhost:8080/api/v1';

  findAll(): Observable<Page<IPatientMin>> {
    return this.http.get<Page<IPatientMin>>(`${this.API}/patients`);
  }
}
