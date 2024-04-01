import { Component, InputSignal, input } from '@angular/core';
import { ILanguageClass } from '../../../../shared/_models/ILanguageClass';

@Component({
  selector: 'app-language-class-detail-info',
  standalone: true,
  imports: [],
  templateUrl: './language-class-detail-info.component.html',
  styleUrl: './language-class-detail-info.component.scss',
})
export class LanguageClassDetailInfoComponent {
  dayInfo: InputSignal<string> = input.required<string>();
  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();
}
