import {
  Component,
  EventEmitter,
  InputSignal,
  OnInit,
  Output,
  computed,
  effect,
  inject,
  input,
} from '@angular/core';
import { _client_language_classes } from '../../../shared/_constVars/_client_consts';
import { RouterLink } from '@angular/router';
import { getDaysOfWeekWords } from '../../../shared/_constVars/_days';
import { AuthenticatePipe } from '../../../core/_services/authenticate.pipe';
import { CommonModule } from '@angular/common';
import { HandleImageErrorDirective } from '../../../core/_services/handle-image-error.directive';
import { ILanguageClass } from '../../../shared/_models/ILanguageClass';
import { DomSanitizer } from '@angular/platform-browser';
import { AccountService } from '../../../core/_services/account.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-language-class-item',
  standalone: true,
  imports: [RouterLink, AuthenticatePipe, CommonModule, HandleImageErrorDirective],
  templateUrl: './language-class-item.component.html',
  styleUrl: './language-class-item.component.scss',
})
export class LanguageClassItemComponent {
  protected readonly getDaysOfWeekWords = getDaysOfWeekWords;

  @Output() goToProfile: EventEmitter<string> = new EventEmitter<string>();
  @Output() deleteClass: EventEmitter<number> = new EventEmitter<number>();
  @Output() joinClass: EventEmitter<number> = new EventEmitter<number>();
  @Output() quitClass: EventEmitter<number> = new EventEmitter<number>();

  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();

  client_language_classes: string = _client_language_classes;

  public navigateToProfile(username: string): void {
    this.goToProfile.emit(username);
  }

  public joinLanguageClass(id: number): void {
    this.joinClass.emit(id);
  }

  public deleteLanguageClass(id: number): void {
    this.deleteClass.emit(id);
  }

  public quitLanguageClass(id: number): void {
    this.quitClass.emit(id);
  }
}
