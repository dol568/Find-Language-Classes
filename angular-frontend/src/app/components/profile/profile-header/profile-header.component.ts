import { Component, InputSignal, effect, inject, input } from '@angular/core';
import { HandleImageErrorDirective } from '../../../core/_services/handle-image-error.directive';
import { AuthenticatePipe } from '../../../core/_services/authenticate.pipe';
import { IProfile } from '../../../shared/_models/IProfile';
import { SafeUrl } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { ImageService } from '../../../core/_services/image.service';
import { ImageCachePipe } from "../../../core/_services/image-cache.pipe";

@Component({
    selector: 'app-profile-header',
    standalone: true,
    templateUrl: './profile-header.component.html',
    styleUrl: './profile-header.component.scss',
    imports: [HandleImageErrorDirective, AuthenticatePipe, CommonModule, ImageCachePipe]
})
export class ProfileHeaderComponent {
  profile: InputSignal<IProfile> = input.required<IProfile>();
  // photo: InputSignal<string> = input.required<string>();
  photo: InputSignal<SafeUrl> = input.required<SafeUrl>();
  // ph

  // serv = inject(ImageService)

  // constructor() {
  //   effect(() => {
  //     this.serv.getImage(this.profile()?.photoUrl).subscribe(resp => this.ph = resp)
  //   })
  // }
}
