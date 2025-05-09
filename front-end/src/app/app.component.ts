import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BrnSelectImports } from '@spartan-ng/brain/select';
import { HlmSelectImports } from '@spartan-ng/ui-select-helm';
import { BrnMenuTriggerDirective } from '@spartan-ng/brain/menu';
import { HlmButtonDirective } from '@spartan-ng/ui-button-helm';
import {
  HlmMenuComponent,
  HlmMenuGroupComponent,
  HlmMenuItemDirective,
  HlmMenuLabelComponent,
  HlmMenuSeparatorComponent
} from '@spartan-ng/ui-menu-helm';
import { GoogleAuthService } from './shared/services/google-auth/google-auth.service';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { lucideMoon, lucideSun } from '@ng-icons/lucide';
import {
  lucideArchive,
  lucideArchiveX,
  lucideTrash2,
  lucideClock,
  lucideCornerUpLeft,
  lucideReplyAll,
  lucideForward,
  lucideEllipsisVertical,
  lucideSunMoon
} from '@ng-icons/lucide';

/**
 * App component.
 * 
 * @author Marcus Nastasi
 * @version 1.0.1
 * @since 2025
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    HlmButtonDirective,
    BrnSelectImports,
    HlmSelectImports,
    BrnMenuTriggerDirective,
    HlmMenuComponent,
    HlmMenuGroupComponent,
    HlmMenuItemDirective,
    HlmMenuLabelComponent,
    HlmMenuSeparatorComponent,
    NgIcon
  ],
  providers: [
    provideIcons({
      lucideArchive,
      lucideArchiveX,
      lucideTrash2,
      lucideClock,
      lucideCornerUpLeft,
      lucideReplyAll,
      lucideForward,
      lucideEllipsisVertical,
      lucideMoon,
      lucideSun,
      lucideSunMoon
    }),
  ],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

  constructor(private readonly googleAuthService: GoogleAuthService) {}

  // State variables.
  theme: string = 'light';

  ngOnInit(): void {
    this.checkThemeOnInit();
    // call backend to get user
    this.googleAuthService.getGoogleUser().subscribe({
      next: user => localStorage.setItem('email_manager_user', user),
      error: error => console.error(error)
    });
    // call backend to get token
    this.googleAuthService.getGoogleToken().subscribe({
      next: token => localStorage.setItem('access_token', token),
      error: error => console.error(error)
    });
  }

  /**
   * Checking the theme configured and localStorage data
   */
  private checkThemeOnInit(): void {
    const htmlEl: HTMLElement = document.getElementsByTagName('html')[0];
    const theme: string | null = localStorage.getItem('theme');
    const htmlClassList: DOMTokenList = htmlEl.classList;
    if (theme) {
      if (theme === 'dark' && htmlClassList.contains('light')) {
        htmlClassList.replace('light', 'dark');
      } else if (theme === 'light' && htmlClassList.contains('dark')) {
        htmlClassList.replace('dark', 'light');
      }
    }
  }

  /**
   * Handling global theme on app.
   */
  protected handleThemeChange($event: Event): void {
    const htmlEl: HTMLElement = document.getElementsByTagName('html')[0];
    const themeEvent: string = ($event.target as HTMLButtonElement).value;
    const htmlClassList: DOMTokenList = htmlEl.classList;
    if (themeEvent === 'Light' && htmlClassList.contains('dark')) {
      htmlClassList.replace('dark', 'light');
      localStorage.setItem('theme', 'light');
    } else if (themeEvent === 'Dark' && htmlClassList.contains('light')) {
      htmlClassList.replace('light', 'dark');
      localStorage.setItem('theme', 'dark');
    }
  }
}
