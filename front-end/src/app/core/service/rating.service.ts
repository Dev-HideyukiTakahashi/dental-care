import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { IRating } from '../../model/rating.model';

@Injectable({
  providedIn: 'root',
})
export class RatingService {
  private readonly http = inject(HttpClient);
  private API = environment.api;

  submitRating(body: IRating): Observable<IRating> {
    return this.http.post<IRating>(`${this.API}/ratings`, body);
  }
}
