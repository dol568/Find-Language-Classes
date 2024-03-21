import { Component, InputSignal, WritableSignal, effect, input, signal } from '@angular/core';
import { ILanguageClass } from '../../../../shared/_models/ILanguageClass';
import { getDaysOfWeekWords } from '../../../../shared/_constVars/_days';

@Component({
  selector: 'app-language-class-detail-info',
  standalone: true,
  imports: [],
  templateUrl: './language-class-detail-info.component.html',
  styleUrl: './language-class-detail-info.component.scss',
})
export class LanguageClassDetailInfoComponent {
  dateInfo: WritableSignal<string> = signal<string>('');
  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();

  constructor() {
    effect(
      () => {
        if (!!this.languageClass()) {
          this.dateInfo.set(getDaysOfWeekWords(this.languageClass()?.dayOfWeek));
        }
      },
      { allowSignalWrites: true }
    );
  }
}
