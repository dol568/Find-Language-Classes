import {Component, inject, OnInit} from '@angular/core';
import {AccountService} from "../../core/_services/account.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {PasswordValidators} from "../../shared/_constVars/password-validators";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  #accountService = inject(AccountService);
  #router = inject(Router);
  signUpForm: FormGroup;
  readonly emailOnly = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;

  ngOnInit(): void {
    this.signUpForm = new FormGroup({
      userName: new FormControl('',[
        Validators.required,
          Validators.minLength(8),
          Validators.maxLength(45)]),
      fullName: new FormControl('',[
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(45)]),
      email: new FormControl('', [Validators.pattern(this.emailOnly), Validators.required]),
      password: new FormControl('',
        Validators.compose([
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(30),
          PasswordValidators.patternValidator(new RegExp("(?=.*[0-9])"), {
            requiresDigit: true
          }),
          PasswordValidators.patternValidator(new RegExp("(?=.*[A-Z])"), {
            requiresUppercase: true
          }),
          PasswordValidators.patternValidator(new RegExp("(?=.*[a-z])"), {
            requiresLowercase: true
          }),
          PasswordValidators.patternValidator(new RegExp("(?=.*[$@^!%*?&])"), {
            requiresSpecialChars: true
          })
        ])
      )
    });
  }

  submitFunc() {
    this.#accountService.register(this.signUpForm.value).subscribe({
      next: () => this.#router.navigate(['/']),
      error: (err) => console.log(err)
    });
  }

  get f() {
    return this.signUpForm.controls;
  }

  get passwordValid() {
    return this.signUpForm.controls["password"].errors === null;
  }

  get requiredValid() {
    return !this.signUpForm.controls["password"].hasError("required");
  }

  get minLengthValid() {
    return !this.signUpForm.controls["password"].hasError("minlength");
  }

  get requiresDigitValid() {
    return !this.signUpForm.controls["password"].hasError("requiresDigit");
  }

  get requiresUppercaseValid() {
    return !this.signUpForm.controls["password"].hasError("requiresUppercase");
  }

  get requiresLowercaseValid() {
    return !this.signUpForm.controls["password"].hasError("requiresLowercase");
  }

  get requiresSpecialCharsValid() {
    return !this.signUpForm.controls["password"].hasError("requiresSpecialChars");
  }
}
