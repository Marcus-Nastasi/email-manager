import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideSearch } from '@ng-icons/lucide';
import { HlmIconDirective } from '@spartan-ng/ui-icon-helm';
import {
  HlmMenuComponent,
  HlmMenuGroupComponent,
  HlmMenuSeparatorComponent,
} from '@spartan-ng/ui-menu-helm';
import { BrnSelectImports } from '@spartan-ng/brain/select';
import { HlmSelectImports } from '@spartan-ng/ui-select-helm';
import { HlmTabsComponent, HlmTabsListComponent, HlmTabsTriggerDirective } from '@spartan-ng/ui-tabs-helm';
import { HlmInputDirective } from '@spartan-ng/ui-input-helm';
import { HlmFormFieldModule } from '@spartan-ng/ui-formfield-helm';
import { NgScrollbarModule } from 'ngx-scrollbar';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import { EmailCardResponse } from '../../../types/email/EmailCardData';

@Component({
  selector: 'email-inbox',
  imports: [
    HlmFormFieldModule,
    HlmMenuComponent,
    HlmMenuSeparatorComponent,
    HlmMenuGroupComponent,
    HlmIconDirective,
    BrnSelectImports,
    HlmSelectImports,
    HlmTabsComponent,
    HlmTabsListComponent,
    HlmTabsTriggerDirective,
    HlmInputDirective,
    NgScrollbarModule,
    NgIcon,
    HlmButtonDirective
  ],
  templateUrl: './email-inbox.component.html',
  styleUrl: './email-inbox.component.scss'
})
export class EmailInboxComponent {

  @Input() emailId: string = '';
  @Output() emailIdChanged = new EventEmitter<string>();
  @Input() emailsCardData: EmailCardResponse[] = [];
  @Output() loadMoreEmails = new EventEmitter<void>();

  editableEmailId: string = '';

  ngOnChanges() {
    this.editableEmailId = this.emailId;
  }

  notifyParent() {
    this.emailIdChanged.emit(this.editableEmailId);
  }

  requestMoreEmails() {
    this.loadMoreEmails.emit();
  }

  /**
   * 
   * On e-mail selection, emit event to father.
   * 
   * @param id the e-mail id.
   */
  onEmailSelected(id: string) {
    if (this.emailId !== id) {
      this.emailId = id;
      this.emailIdChanged.emit(id);
    }
  }
}
