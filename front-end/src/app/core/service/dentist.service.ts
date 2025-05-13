import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IDentistMin } from '../../model/dentist-min.model';
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
}
