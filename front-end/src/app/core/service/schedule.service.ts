import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IAbsence } from '../../model/absence.model';

@Injectable({
  providedIn: 'root',
})
export class ScheduleService {
  private http = inject(HttpClient);
  private API = environment.api;

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
