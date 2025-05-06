import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Observable } from 'rxjs';
import { LoginData } from '../../model/login.model';
import { IRegisterPatient } from '../../model/register-patient.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private API = 'http://localhost:8080';
  private clientId = 'myclientid';
  private clientSecret = 'myclientsecret';

  private http = inject(HttpClient);
  private router = inject(Router);

  // ***** LOGIN REQUEST *****
  login(userData: LoginData): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: 'Basic ' + btoa(`${this.clientId}:${this.clientSecret}`),
    });

    const body = new HttpParams()
      .set('grant_type', 'password')
      .set('username', userData.username)
      .set('password', userData.password);

    return this.http.post(`${this.API}/oauth2/token`, body.toString(), { headers });
  }

  // ***** SIGNUP REQUEST *****
  signup(patient: IRegisterPatient): Observable<IRegisterPatient> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(`${this.clientId}:${this.clientSecret}`),
    });

    return this.http.post<IRegisterPatient>(`${this.API}/auth/signup`, patient, { headers });
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  getDecodedToken(): any | null {
    const token = localStorage.getItem('access_token');
    if (!token) return null;

    try {
      return jwtDecode(token);
    } catch (e) {
      console.error('Token inválido', e);
      return null;
    }
  }

  getUserRole(): string {
    const decoded = this.getDecodedToken();
    return decoded?.authorities?.[0];
  }

  isAdmin(): boolean {
    if (!this.getDecodedToken()) return false;
    return this.getUserRole() === 'ROLE_ADMIN';
  }

  isPatient(): boolean {
    if (!this.getDecodedToken()) return false;
    return this.getUserRole() === 'ROLE_PATIENT';
  }

  isDentist(): boolean {
    if (!this.getDecodedToken()) return false;
    return this.getUserRole() === 'ROLE_DENTIST';
  }
}
