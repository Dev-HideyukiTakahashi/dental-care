import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginData } from '../../model/auth/login.model';
import { IRecoverToken } from '../../model/auth/recover-token.model';
import { IRegisterPatient } from '../../model/auth/register-patient.model';
import { IResetPassword } from '../../model/auth/reset-password.model';
import { UserRole } from '../../model/enum/user-role.enum';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private API = environment.authApi;
  private clientId = environment.clientId;
  private clientSecret = environment.clientSecret;

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

  // ***** RECOVER TOKEN EMAIL *****
  recoverToken(email: IRecoverToken): Observable<IRecoverToken> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(`${this.clientId}:${this.clientSecret}`),
    });

    return this.http.post<IRecoverToken>(`${this.API}/auth/recover-token`, email, { headers });
  }

  // ***** RESET PASSWORD *****
  resetPassword(body: IResetPassword): Observable<IResetPassword> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: 'Basic ' + btoa(`${this.clientId}:${this.clientSecret}`),
    });

    return this.http.put<IResetPassword>(`${this.API}/auth/new-password`, body, { headers });
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

  isLoggedIn(): boolean {
    const token = localStorage.getItem('access_token');
    if (!token) return false;

    try {
      const decoded: any = jwtDecode(token);
      const currentTime = Math.floor(Date.now() / 1000);
      return decoded.exp && decoded.exp > currentTime;
    } catch (e) {
      // invalid token
      return false;
    }
  }

  getUsername(): string | null {
    const decoded = this.getDecodedToken();
    if (!decoded || !decoded.username) return null;

    return String(decoded.username);
  }

  getRole(): UserRole | null {
    if (this.isAdmin()) return UserRole.Admin;
    if (this.isDentist()) return UserRole.Dentist;
    if (this.isPatient()) return UserRole.Patient;
    return null;
  }

  private getUserRole(): string {
    const decoded = this.getDecodedToken();
    return decoded?.authorities?.[0];
  }

  private isAdmin(): boolean {
    if (!this.getDecodedToken()) return false;
    return this.getUserRole() === 'ROLE_ADMIN';
  }

  private isPatient(): boolean {
    if (!this.getDecodedToken()) return false;
    return this.getUserRole() === 'ROLE_PATIENT';
  }

  private isDentist(): boolean {
    if (!this.getDecodedToken()) return false;
    return this.getUserRole() === 'ROLE_DENTIST';
  }
}
