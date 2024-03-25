import { Directive, HostListener, Input } from '@angular/core';
import { _img_default } from '../../shared/_constVars/_client_consts';

@Directive({
  selector: 'img[handleImageError]',
  standalone: true,
})
export class HandleImageErrorDirective {
  @Input() handleImgError?: string;

  constructor() {}

  @HostListener('error', ['$event'])
  handleImageError(event: Event): void {
    const image = event.target as HTMLInputElement;
    // image.src = this.handleImgError ?? '/user.jpg';
    image.src = this.handleImgError ?? `${_img_default}/user.jpg`;
  }
}
