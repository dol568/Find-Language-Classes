import {
  Component,
  EventEmitter,
  InputSignal,
  Output,
  WritableSignal,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import { ILanguageClass } from '../../../../shared/_models/ILanguageClass';
import { getDaysOfWeekWords, getFlagImg } from '../../../../shared/_constVars/_days';
import { RouterLink } from '@angular/router';
import { _client_language_classes } from '../../../../shared/_constVars/_client_consts';
import { ImageService } from '../../../../core/_services/image.service';
import { SafeUrl } from '@angular/platform-browser';

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
  dateInfo: WritableSignal<string> = signal<string>('');
  bgImg: WritableSignal<SafeUrl> = signal<SafeUrl>('');
  image = inject(ImageService)

  constructor() {
    effect(
      () => {
        if (!!this.languageClass()) {
          this.dateInfo.set(getDaysOfWeekWords(this.languageClass()?.dayOfWeek));
          // this.bgImg.set(getFlagImg(this.languageClass()?.category));
          this.image.getImage(this.languageClass()?.category).subscribe(
            response => this.bgImg.set(response)
          )
          
        }
      },
      { allowSignalWrites: true }
    );
  }

  public navigateToProfile(username: string): void {
    this.goToProfile.emit(username);
  }

  public totalSpotsLeft(): string {
    const totalSpots =
      this.languageClass()?.totalSpots - this.languageClass()?.userLanguageClasses.length;
    return totalSpots > 0 ? '(' + totalSpots + ' spots left' + ')' : '(No more spots left)';
  }
}
