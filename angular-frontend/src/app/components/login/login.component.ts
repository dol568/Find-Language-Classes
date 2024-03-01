import {Component, inject, OnInit} from '@angular/core';
import {Router, RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Observable} from "rxjs";
import {AccountService} from "../../core/_services/account.service";
import {_client_home, _client_signup, _client_training_classes} from "../../shared/_constVars/_client_consts";
import {IUser} from "../../shared/_models/IUser";

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [RouterModule, CommonModule, FormsModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
    #router = inject(Router);
    #accountService = inject(AccountService);
    currentUser$: Observable<IUser>;
    training_classes: string = _client_training_classes;
    client_signup: string = _client_signup;

    ngOnInit(): void {
        this.currentUser$ = this.#accountService.currentUser$;
    }

    isValidateTextFalse(data: any) {
        return !!(data.touched && data.invalid);
    }

    submitFunc(data: any, event: Event) {
        event.preventDefault();
        this.#accountService.login(data.value)
            .subscribe({
                next: () => this.#router.navigate([_client_home]),
                error: err => console.error(err)
            });
    }
}
