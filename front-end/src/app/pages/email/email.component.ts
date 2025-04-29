import { Component } from '@angular/core';
import { EmailMenuComponent } from '../../shared/components/email-menu/email-menu.component';
import { EmailInboxComponent } from '../../components/email/email-inbox/email-inbox.component';

@Component({
  selector: 'app-email',
  imports: [EmailMenuComponent, EmailInboxComponent],
  templateUrl: './email.component.html',
  styleUrl: './email.component.scss'
})
export class EmailComponent {

}
