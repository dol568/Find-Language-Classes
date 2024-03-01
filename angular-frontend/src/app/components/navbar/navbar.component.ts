import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterModule} from "@angular/router";
import {AccountService} from "../../core/_services/account.service";
import {_client_add_class} from "../../shared/_constVars/_client_consts";
import {IUser} from "../../shared/_models/IUser";
import {CommonModule} from "@angular/common";
import {AuthenticatePipe} from "../../core/_services/authenticate.pipe";
import {HandleImageErrorDirective} from "../../core/_services/handle-image-error.directive";
import {HttpClient} from "@angular/common/http";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {map, Subscription} from "rxjs";

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [RouterModule, CommonModule, AuthenticatePipe, HandleImageErrorDirective],
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit, OnDestroy {
    #router = inject(Router);
    #accountService = inject(AccountService);
    #httpClient = inject(HttpClient);
    #domSanitizer = inject(DomSanitizer);
    add_class: string = _client_add_class;
    currentUser: IUser = null;
    photoUrl: SafeUrl = null;
    subscription1$: Subscription;
    subscription2$: Subscription;

    ngOnInit(): void {
        const token = sessionStorage.getItem('token');
        if (token) {
            this.subscription1$ = this.#accountService.loadCurrentUser(token)
                .subscribe({
                        next: (user) => {
                            this.currentUser = user;
                            this.#loadUserPhoto(this.currentUser.photoUrl);
                        },
                        error: err => console.error(err)
                    }
                );

            this.subscription2$ = this.#accountService.refresh$.subscribe(() => {
                this.subscription1$.unsubscribe();
                this.subscription1$ = this.#accountService.loadCurrentUser(token).subscribe({
                        next: (user) => {
                            this.currentUser = user;
                            this.#loadUserPhoto(this.currentUser.photoUrl);
                        },
                        error: err => console.error(err)
                    }
                );
            });
        }
    }

    ngOnDestroy(): void {
        // this.subscription1$.unsubscribe();
        // this.subscription2$.unsubscribe();
    }

    #loadUserPhoto(photoUrl: string): void {
        this.#httpClient.get(photoUrl, {responseType: 'blob'}).pipe(
            map(blob => this.#domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob))))
            .subscribe(safeUrl => this.photoUrl = safeUrl);
    }

    logout() {
        this.#accountService.logout();
    }

    goToClasses() {
        this.#router.navigate([`/training-classes`]);
    }
}
