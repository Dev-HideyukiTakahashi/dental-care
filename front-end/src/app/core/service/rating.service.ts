import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IRating } from '../../model/rating.model';

@Injectable({
  providedIn: 'root',
})
export class RatingService {
  private readonly http = inject(HttpClient);
  private readonly API = 'http://localhost:8080/api/v1';

  submitRating(body: IRating): Observable<IRating> {
    return this.http.post<IRating>(`${this.API}/ratings`, body);
  }
}
