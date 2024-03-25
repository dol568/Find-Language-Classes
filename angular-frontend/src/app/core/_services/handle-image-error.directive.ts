import { Directive, HostListener, Input } from '@angular/core';

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
    image.src = this.handleImgError ?? './assets/user.jpg';
  }
}
