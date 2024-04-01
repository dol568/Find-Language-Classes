import {
  Component,
  EventEmitter,
  InputSignal,
  Output,
  input,
} from '@angular/core';
import { ILanguageClass } from '../../../../shared/_models/ILanguageClass';
import { RouterLink } from '@angular/router';
import { _client_language_classes } from '../../../../shared/_constVars/_client_consts';

@Component({
  selector: 'app-language-class-detail-top',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './language-class-detail-top.component.html',
  styleUrl: './language-class-detail-top.component.scss',
})
export class LanguageClassDetailTopComponent {
  @Output() goToProfile = new EventEmitter<string>();
  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();
  client_language_classes: string = _client_language_classes;
  dayInfo = input.required<string>();
  backgroundImage = input.required<string>();

  public navigateToProfile(username: string): void {
    this.goToProfile.emit(username);
  }

  public totalSpotsLeft(): string {
    const totalSpots =
      this.languageClass()?.totalSpots - this.languageClass()?.userLanguageClasses.length;
    return totalSpots > 0 ? '(' + totalSpots + ' spots left' + ')' : '(No more spots left)';
  }
}
