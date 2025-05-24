import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  IAppointment,
  IAppointmentCreate,
} from '../../model/appointment.model';
import { Page } from '../../model/page.model';
import { IUpdateAppointment } from '../../model/update-appointment-model';

@Injectable({
  providedIn: 'root',
})
export class AppointmentService {
  private http = inject(HttpClient);

  private API = 'http://localhost:8080/api/v1';

  findAll(page: number, size: number): Observable<Page<IAppointment>> {
    return this.http.get<Page<IAppointment>>(
      `${this.API}/appointments?page=${page}&size=${size}&sort=date,desc`
    );
  }

  findByDate(
    page: number,
    size: number,
    date: string
  ): Observable<Page<IAppointment>> {
    return this.http.get<Page<IAppointment>>(
      `${this.API}/appointments/date?date=${date}&page=${page}&size=${size}&sort=date,desc`
    );
  }

  findById(id: number): Observable<IAppointment> {
    return this.http.get<IAppointment>(`${this.API}/appointments/${id}`);
  }

  updateAppointment(
    id: number,
    body: IUpdateAppointment
  ): Observable<IAppointment> {
    return this.http.put<IAppointment>(`${this.API}/appointments/${id}`, body);
  }

  create(body: IAppointmentCreate): Observable<IAppointmentCreate> {
    return this.http.post<IAppointmentCreate>(`${this.API}/appointments`, body);
  }

  cancelAppointment(id: number): Observable<IAppointment> {
    return this.http.put<IAppointment>(
      `${this.API}/appointments/${id}/cancel`,
      null
    );
  }

  completeAppointment(id: number): Observable<IAppointment> {
    return this.http.put<IAppointment>(
      `${this.API}/appointments/${id}/complete`,
      null
    );
  }
}
