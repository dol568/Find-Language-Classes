import { Component, InputSignal, input } from '@angular/core';
import { HandleImageErrorDirective } from '../../../core/_services/handle-image-error.directive';
import { AuthenticatePipe } from '../../../core/_services/authenticate.pipe';
import { IProfile } from '../../../shared/_models/IProfile';
import { SafeUrl } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-profile-header',
  standalone: true,
  imports: [HandleImageErrorDirective, AuthenticatePipe, CommonModule],
  templateUrl: './profile-header.component.html',
  styleUrl: './profile-header.component.scss',
})
export class ProfileHeaderComponent {
  profile: InputSignal<IProfile> = input.required<IProfile>();
  photo: InputSignal<string> = input.required<string>();
}
