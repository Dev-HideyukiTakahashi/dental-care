import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IAppointment } from '../../model/appointment.model';
import { Page } from '../../model/page.model';

@Injectable({
  providedIn: 'root',
})
export class AppointmentService {
  private http = inject(HttpClient);

  private API = 'http://localhost:8080/api/v1';

  findAll(page: number, size: number): Observable<Page<IAppointment>> {
    return this.http.get<Page<IAppointment>>(
      `${this.API}/appointments?page=${page}&size=${size}&sort=date,desc`,
    );
  }

  findByDate(page: number, size: number, date: string): Observable<Page<IAppointment>> {
    return this.http.get<Page<IAppointment>>(
      `${this.API}/appointments/date?date=${date}&page=${page}&size=${size}&sort=date,desc`,
    );
  }
}
