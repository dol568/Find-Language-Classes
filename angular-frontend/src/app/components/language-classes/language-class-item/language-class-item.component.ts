import {
  Component,
  EventEmitter,
  InputSignal,
  Output,
  input,
} from '@angular/core';
import { _client_language_classes } from '../../../shared/_constVars/_client_consts';
import { RouterLink } from '@angular/router';
import { getDaysOfWeekWords } from '../../../shared/_constVars/_days';
import { CommonModule } from '@angular/common';
import { ILanguageClass } from '../../../shared/_models/ILanguageClass';

@Component({
  selector: 'app-language-class-item',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './language-class-item.component.html',
  styleUrl: './language-class-item.component.scss',
})
export class LanguageClassItemComponent {
  readonly getDaysOfWeekWords = getDaysOfWeekWords;

  @Output() goToProfile: EventEmitter<string> = new EventEmitter<string>();
  @Output() deleteClass: EventEmitter<number> = new EventEmitter<number>();
  @Output() joinClass: EventEmitter<number> = new EventEmitter<number>();
  @Output() quitClass: EventEmitter<number> = new EventEmitter<number>();

  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();

  language_classes: string = _client_language_classes;

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
