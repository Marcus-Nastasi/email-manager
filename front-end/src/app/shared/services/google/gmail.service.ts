import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { EmailCardResponse } from '../../../types/email/EmailCardData';

/**
 * The gmail service.
 * 
 * @author Marcus Nastasi
 * @version 1.0.1
 * @since 2025
 */
@Injectable({
  providedIn: 'root'
})
export class GmailService {

  private url: string = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {}

  /**
   * This function allows to get a list of strings that are the e-mail ids and page token.
   * 
   * @param maxResults the max number of e-mails returned.
   * @param pageToken the token that reffers the next page.
   * 
   * @returns a list of strings. 
   */
  public getEmailsList(maxResults: number = 7, pageToken: string = ''): Observable<string[]> {
    const params: HttpParams = new HttpParams()
      .append('maxResults', maxResults.toString())
      .append('pageToken', pageToken);
    return this.http.get<string[]>(`${this.url}/gmail/find/email`, {
      responseType: 'json',
      withCredentials: true,
      headers: {
        "Content-Type": "application/json"
      },
      params: params
    }).pipe(
      tap(response => response),
      catchError(this.handleError)
    );
  }

  /**
   * This function allows to get single e-mails data by id.
   * 
   * @param id the e-mail id.
   * 
   * @returns the data from e-mail string.
   */
  public getEmailById(id: string): Observable<EmailCardResponse> {
    return this.http.get<EmailCardResponse>(`${this.url}/gmail/find/email/${id}`, {
      responseType: 'json',
      withCredentials: true,
      headers: {
        "Content-Type": "application/json"
      }
    }).pipe(
      tap((response: EmailCardResponse) => response),
      catchError(this.handleError)
    );
  }

  /**
   * This function allows to get single e-mails htmls by e-mail id.
   * 
   * @param id the e-mail id.
   * 
   * @returns the html string.
   */
  public getEmailHtml(id: string): Observable<string> {
    return this.http.get<string>(`${this.url}/gmail/find/email/html/${id}`, {
      responseType: 'json',
      withCredentials: true,
      headers: {
        "Content-Type": "application/json"
      }
    }).pipe(
      tap((response: string) => response),
      catchError(this.handleError)
    );
  }

  /**
   * This function allows to move and e-mail to trash based on it's id.
   * 
   * @param id the e-mail id.
   * @returns a string to identify the e-mail moved to trash.
   */
  public moveToTrash(id: string): Observable<string> {
    return this.http.patch<string>(`${this.url}/gmail/trash/email/${id}`, {}, {
      responseType: 'json',
      withCredentials: true,
      headers: {
        "Content-Type": "application/json"
      }
    }).pipe(
      tap((response: string) => response),
      catchError(this.handleError)
    );
  }

  /**
   * Centralized error handler
   * 
   * @param error recieve the error throwed on request
   * 
   * @returns a new personalized {@link Error}
   */
  private handleError(error: HttpErrorResponse) {
    console.error('Request error:', error);
    window.open('/login', '_self');
    return throwError(() => new Error('Error on gmail service'));
  }
}
