import { Component } from '@angular/core';
import { EmailMenuComponent } from '../../shared/components/email-menu/email-menu.component';
import { EmailInboxComponent } from '../../components/email/email-inbox/email-inbox.component';
import { EmailCardResponse } from '../../types/email/EmailCardData';
import { GmailService } from '../../shared/services/google/gmail.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-email',
  imports: [
    EmailMenuComponent, 
    EmailInboxComponent
  ],
  templateUrl: './email.component.html',
  styleUrl: './email.component.scss'
})
export class EmailComponent {

  constructor(private readonly gmailService: GmailService) {}

  protected emailId: string = '';

  protected selectedEmailData = { from: '', subject: '', date: '' };
  
  protected emailCardData: EmailCardResponse[] = [];
  
  protected nextPageToken: string | undefined = '';

  /**
   * On component initialization, run the function to get the first e-mails list.
   */
  ngOnInit(): void {
    this.getEmailsCardData(false);
  }

  /**
   * Set email card data object to an empty array
   */
  private cleanEmailCardData(): void {
    this.emailCardData = [];
  }

  /**
   * This method allows to refresh the e-mails data without reloading the page.
   */
  protected refresh(): void {
    this.cleanEmailCardData();
    this.emailId = '';
    this.getEmailsCardData(true);
  }

  /**
   * This function get the single e-mail general data.
   *
   * @param id the e-mail id to get.
   */
  private async getEmailById(id: string): Promise<void> {
    let email: string = await firstValueFrom(this.gmailService.getEmailById(id));
    const data: EmailCardResponse = JSON.parse(email);
    data.date = data.date.split(',')[1].trim().substring(0, 11);
    if (this.emailId === '') {
      this.emailId = data.id;
    }
    this.emailCardData.push(data);
    if (this.emailId === this.emailCardData[0].id) {
      this.selectedEmailData = {
        from: this.emailCardData[0].from,
        subject: this.emailCardData[0].subject,
        date: this.emailCardData[0].date
      }
    }
  }

  /**
   * This function allows to update the selected e-mail id for the
   * renderization on mail view component.
   *
   * @param newValue the new e-mail id.
   */
  public updateSelectedEmail(newValue: string): void {
    if (newValue && this.emailId !== newValue) {
      this.emailId = newValue;
      this.emailCardData.forEach((cd: EmailCardResponse): void => {
        if (cd.id === this.emailId) {
          this.selectedEmailData = {
            from: cd.from,
            subject: cd.subject,
            date: cd.date
          }
        }
      });
    }
  }

  /**
   * This function allows to construct the emails card data object.
   * It uses the e-mails id's list to loop over and get the e-mails.
   */
  public async getEmailsCardData(refreshFlag: boolean): Promise<void> {
    if (refreshFlag) {
      this.nextPageToken = '';
    }
    let idsList: string[] = await firstValueFrom(this.gmailService.getEmailsList(10, this.nextPageToken));
    if (idsList.length < 11) {
      return
    }
    const nextPageTkn: string | undefined = idsList.pop();
    this.nextPageToken = nextPageTkn;
    idsList.forEach(async (id: string): Promise<void> => {
      await this.getEmailById(id);
    });
  }
}
