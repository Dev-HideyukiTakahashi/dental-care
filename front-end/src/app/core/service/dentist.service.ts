import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IDentistMin } from '../../model/dentist-min.model';
import { IDentist } from '../../model/dentist.model';
import { Page } from '../../model/page.model';

@Injectable({
  providedIn: 'root',
})
export class DentistService {
  private http = inject(HttpClient);
  private API = 'http://localhost:8080/api/v1';

  getDentistsWithRatings(): Observable<Page<IDentistMin>> {
    return this.http.get<Page<IDentistMin>>(`${this.API}/dentists?sort=score,desc`);
  }

  findAll(page: number, size: number): Observable<Page<IDentistMin>> {
    return this.http.get<Page<IDentistMin>>(`${this.API}/dentists?page=${page}&size=${size}`);
  }

  findById(id: number): Observable<IDentist> {
    return this.http.get<IDentist>(`${this.API}/dentists/${id}`);
  }

  updateDentist(dentist: IDentist): Observable<IDentist> {
    return this.http.put<IDentist>(`${this.API}/dentists/${dentist.id}`, dentist);
  }

  createDentist(): any {
    return null;
  }

  deleteDentist(): any {
    return null;
  }
}
