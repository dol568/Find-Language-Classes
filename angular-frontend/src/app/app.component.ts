import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {AccountService} from "./core/_services/account.service";
import {NavbarComponent} from "./components/navbar/navbar.component";
import {filter} from "rxjs";
import {NgxSpinnerModule} from "ngx-spinner";

@Component({
  selector: 'app-root',
  standalone: true,
    imports: [CommonModule, RouterOutlet, NavbarComponent, NgxSpinnerModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  #accountService = inject(AccountService);
  #router = inject(Router);
  showNavbar: boolean = true;

  constructor() {
    this.#router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        event.url == '/' || event.url == '/signup'
          ? this.showNavbar = false
          : this.showNavbar = true;
      });
  }

  ngOnInit(): void {
    this.loadCurrentUser();
    this.#router.routeReuseStrategy.shouldReuseRoute = function () {
      return false;
    };

    this.#router.events.subscribe((evt) => {
      if (evt instanceof NavigationEnd) {
        // trick the Router into believing its last link wasn't previously loaded
        this.#router.navigated = false;
        // if you need to scroll back to top, here is the right place
        window.scrollTo(0, 0);
      }
    });
  }

  private loadCurrentUser() {

    const token = sessionStorage.getItem('token');
    if (token) {
      this.#accountService.loadCurrentUser(token).subscribe();
    }
  }
}
