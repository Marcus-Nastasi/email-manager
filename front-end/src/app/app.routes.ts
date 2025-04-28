import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { EmailComponent } from './pages/email/email.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', component: EmailComponent }
];
