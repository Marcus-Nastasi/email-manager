import { Component } from '@angular/core';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { 
  lucideArchive, 
  lucideArchiveX, 
  lucideCircleAlert, 
  lucideFile, 
  lucideInbox, 
  lucideMessagesSquare, 
  lucideSend, 
  lucideShoppingCart, 
  lucideSquareM, 
  lucideTrash2, 
  lucideTriangle, 
  lucideUsers 
} from '@ng-icons/lucide';
import { BrnSelectImports } from '@spartan-ng/brain/select';
import { HlmIconDirective } from '@spartan-ng/ui-icon-helm';
import { 
  HlmMenuComponent, 
  HlmMenuGroupComponent, 
  HlmMenuItemDirective, 
  HlmMenuItemIconDirective, 
  HlmMenuLabelComponent, 
  HlmMenuSeparatorComponent, 
  HlmMenuShortcutComponent 
} from '@spartan-ng/ui-menu-helm';
import { HlmSelectImports } from '@spartan-ng/ui-select-helm';

@Component({
  selector: 'email-menu',
  imports: [
    HlmMenuComponent,
    HlmMenuItemDirective,
    HlmMenuLabelComponent,
    HlmMenuShortcutComponent,
    HlmMenuSeparatorComponent,
    HlmMenuItemIconDirective,
    HlmMenuGroupComponent,
    HlmIconDirective,
    BrnSelectImports,
    HlmSelectImports,
    NgIcon
  ],
  providers: [
    provideIcons({
      lucideInbox,
      lucideSend,
      lucideFile,
      lucideArchiveX,
      lucideTrash2,
      lucideArchive,
      lucideShoppingCart,
      lucideUsers,
      lucideCircleAlert,
      lucideMessagesSquare,
      lucideSquareM,
      lucideTriangle
    })
  ],
  templateUrl: './email-menu.component.html',
  styleUrl: './email-menu.component.scss'
})
export class EmailMenuComponent {

  mailProvider: 'Gmail' | 'Outlook' = "Gmail";
}
