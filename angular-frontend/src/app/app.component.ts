import { Component, effect, inject, Input, OnInit, signal, Signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';
import { filter } from 'rxjs';
import { NgxSpinnerModule } from 'ngx-spinner';
import { AccountService } from './core/_services/account.service';
import { _authSecretKey, _client_home, _client_signup } from './shared/_constVars/_client_consts';
import { IUser } from './shared/_models/IUser';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, NgxSpinnerModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  #router = inject(Router);
  #accountService = inject(AccountService);
  currentUser: Signal<IUser> = this.#accountService.currentUser;
  showNavbar: WritableSignal<boolean> = signal<boolean>(false);
  @Input() userName = '';

  constructor() {
    
    this.#router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        event.url == '/' + _client_home || event.url == '/' + _client_signup
          ? this.showNavbar.set(true)
          : this.showNavbar.set(false);
      });
  }

  ngOnInit(): void {
    if (!this.currentUser() && sessionStorage.getItem(_authSecretKey)) {
      this.#accountService.loadCurrentUser().subscribe();
    }
  }
}
