import { Component } from '@angular/core';
import { EmailMenuComponent } from '../../shared/components/email-menu/email-menu.component';
import { EmailInboxComponent } from '../../components/email/email-inbox/email-inbox.component';
import { EmailCardResponse } from '../../types/email/EmailCardData';

@Component({
  selector: 'app-email',
  imports: [EmailMenuComponent, EmailInboxComponent],
  templateUrl: './email.component.html',
  styleUrl: './email.component.scss'
})
export class EmailComponent {

  /**
   * @param gmailService Uses the Gmail Service to make API calls.
   */
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
   * This function updates the email id.
   *
   * @param emailId the new e-mail id.
   */
  private updateEmailId(emailId: string): void {
    this.emailId = emailId;
  }

  /**
   * This function updates the data to email card.
   *
   * @param data the new card data to push.
   */
  private updateEmailCardData(data: EmailCardResponse): void {
    this.emailCardData.push(data);
  }

  /**
   * Set email card data object to an empty array
   */
  private cleanEmailCardData(): void {
    this.emailCardData = [];
  }

  /**
   * This function updates the token reffering the next page.
   *
   * @param token the new token.
   */
  protected updateNextPageToken(token: string | undefined): void {
    this.nextPageToken = token;
  }

  /**
   * This method allows to refresh the e-mails data without reloading the page.
   */
  protected async refresh(): Promise<void> {
    this.cleanEmailCardData();
    this.updateEmailId('');
    await this.getEmailsCardData(true);
  }

  /**
   * This function get the single e-mail general data.
   *
   * @param id the e-mail id to get.
   */
  private async getEmailById(id: string): Promise<void> {
    const response: string = await this.gmailService.getEmailById(id);
    const data: EmailCardResponse = JSON.parse(response);
    data.date = data.date.split(',')[1].trim().substring(0, 11);
    if (this.emailId === '') {
      this.updateEmailId(data.id);
    }
    this.updateEmailCardData(data);
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
      this.updateEmailId(newValue);
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
      this.updateNextPageToken('');
    }
    const idsList: string[] = await this.gmailService.getEmailsList(10, this.nextPageToken);
    if (idsList.length < 11 || idsList[idsList.length - 1] === null) {
      return
    }
    const nextPageTkn: string | undefined = idsList.pop();
    this.updateNextPageToken(nextPageTkn);
    idsList.forEach(async (id: string): Promise<void> => {
      this.getEmailById(id);
    });
  }
}
