import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';

/**
 * Google authentication service.
 * 
 * @author Marcus Nastasi
 * @version 1.0.1
 * @since 2025
 */
@Injectable({
  providedIn: 'root'
})
export class GoogleAuthService {

  private readonly url: string = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {}

  /**
   * Get the loged user from Google
   * 
   * @returns an {@link Observable} of {@link String} type with response
   * 
   * @throws an {@link Error} if the request fails 
   */
  public getGoogleUser(): Observable<string> {
    return this.http.get(this.url, {
      responseType: 'text',
      withCredentials: true
    }).pipe(
      tap(user => user),
      catchError(this.handleError)
    );
  }

  /**
   * Get the token from Google loged user
   * 
   * @returns an {@link Observable} of {@link String} type with response
   * 
   * @throws an {@link Error} if the request fails
   */
  public getGoogleToken(): Observable<string> {
    return this.http.get(this.url + '/token', {
      responseType: 'text',
      withCredentials: true
    }).pipe(
      tap(token => token),
      catchError(this.handleError)
    );
  }

  /**
   * Error handler
   * 
   * @param error recieve the error throwed on request
   * 
   * @returns a new personalized {@link Error}
   */
  private handleError(error: HttpErrorResponse) {
    // aqui você pode tratar erros de forma centralizada
    console.error('Erro na requisição:', error);
    return throwError(() => new Error('Erro ao carregar usuário'));
  }
}
