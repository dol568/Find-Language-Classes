import { Component, inject, OnInit } from '@angular/core';
import { AccountService } from '../../core/_services/account.service';
import { Router, RouterModule } from '@angular/router';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { PasswordValidators } from '../../shared/_constVars/password-validators';
import { CommonModule } from '@angular/common';
import { _client_home, _emailOnly } from '../../shared/_constVars/_client_consts';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent implements OnInit {
  #accountService = inject(AccountService);
  #router = inject(Router);
  signUpForm: FormGroup;

  ngOnInit(): void {
    if (this.#accountService.isAuthenticatedUser()) {
      this.#router.navigate(['/']);
    }
    this.signUpForm = new FormGroup({
      userName: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(45),
      ]),
      fullName: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(45),
      ]),
      email: new FormControl('', [
        Validators.required,
        Validators.pattern(_emailOnly),
      ]),
      password: new FormControl(
        '',
        Validators.compose([
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(30),
          PasswordValidators.patternValidator(new RegExp('(?=.*[0-9])'), {
            requiresDigit: true,
          }),
          PasswordValidators.patternValidator(new RegExp('(?=.*[A-Z])'), {
            requiresUppercase: true,
          }),
          PasswordValidators.patternValidator(new RegExp('(?=.*[a-z])'), {
            requiresLowercase: true,
          }),
          PasswordValidators.patternValidator(new RegExp('(?=.*[$@^!%*?&])'), {
            requiresSpecialChars: true,
          }),
        ])
      ),
    });
  }

  public submitFunc(): void {
    this.#accountService.register(this.signUpForm.value).subscribe({
      next: () => this.#router.navigate([_client_home]),
      error: (err) => console.log(err),
    });
  }

  get f() {
    return this.signUpForm.controls;
  }

  get userName() {
    return this.signUpForm.get('userName');
  }

  get fullName() {
    return this.signUpForm.get('fullName');
  }

  get email() {
    return this.signUpForm.get('email');
  }

  get passwordValid() {
    return this.signUpForm.controls['password'].errors === null;
  }

  get requiredValid() {
    return !this.signUpForm.controls['password'].hasError('required');
  }

  get minLengthValid() {
    return !this.signUpForm.controls['password'].hasError('minlength');
  }

  get requiresDigitValid() {
    return !this.signUpForm.controls['password'].hasError('requiresDigit');
  }

  get requiresUppercaseValid() {
    return !this.signUpForm.controls['password'].hasError('requiresUppercase');
  }

  get requiresLowercaseValid() {
    return !this.signUpForm.controls['password'].hasError('requiresLowercase');
  }

  get requiresSpecialCharsValid() {
    return !this.signUpForm.controls['password'].hasError(
      'requiresSpecialChars'
    );
  }
}
