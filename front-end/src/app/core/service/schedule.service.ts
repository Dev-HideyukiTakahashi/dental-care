import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IAbsence } from '../../model/absence.model';

@Injectable({
  providedIn: 'root',
})
export class ScheduleService {
  private http = inject(HttpClient);
  private API = 'http://localhost:8080/api/v1';

  findSelfAbsence(): Observable<IAbsence> {
    return this.http.get<IAbsence>(`${this.API}/schedules/self`);
  }

  createAbsence(body: IAbsence): Observable<IAbsence> {
    return this.http.post<IAbsence>(`${this.API}/schedules`, body);
  }

  deleteAbsence(): Observable<void> {
    return this.http.delete<void>(`${this.API}/schedules`);
  }
}
