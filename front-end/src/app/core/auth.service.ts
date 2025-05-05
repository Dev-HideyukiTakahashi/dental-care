import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { Observable } from 'rxjs';
import { LoginData } from '../shared/model/login.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private API = 'http://localhost:8080/oauth2/token';

  constructor(private http: HttpClient) {}

  login(userData: LoginData): Observable<any> {
    const clientId = 'myclientid';
    const clientSecret = 'myclientsecret';

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: 'Basic ' + btoa(`${clientId}:${clientSecret}`),
    });

    const body = new HttpParams()
      .set('grant_type', 'password')
      .set('username', userData.username)
      .set('password', userData.password);

    return this.http.post(this.API, body.toString(), { headers });
  }

  logout(): void {
    localStorage.clear();
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
}
