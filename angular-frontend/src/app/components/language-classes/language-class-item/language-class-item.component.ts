import {
  Component,
  EventEmitter,
  InputSignal,
  Output,
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
import { ImageService } from '../../../core/_services/image.service';
import { ImageCachePipe } from '../../../core/_services/image-cache.pipe';

@Component({
  selector: 'app-language-class-item',
  standalone: true,
  imports: [RouterLink, AuthenticatePipe, CommonModule, HandleImageErrorDirective, ImageCachePipe],
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

  ph
  photos = []

  serv = inject(ImageService)

  constructor() {
    effect(() => {
      this.serv.getImage(this.languageClass()?.hostImage).subscribe(resp => this.ph = resp);
      for (let user of this.languageClass()?.userLanguageClasses) {
        this.serv.getImage(user.image).subscribe(reponse => this.photos.push(reponse))
      }
    })
  }

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
