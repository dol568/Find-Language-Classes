import { Component, InputSignal, input } from '@angular/core';
import { IProfile } from '../../../shared/_models/IProfile';
import { SafeUrl } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-profile-header',
    standalone: true,
    templateUrl: './profile-header.component.html',
    styleUrl: './profile-header.component.scss',
    imports: [CommonModule]
})
export class ProfileHeaderComponent {
  profile: InputSignal<IProfile> = input.required<IProfile>();
  photo: InputSignal<SafeUrl> = input.required<SafeUrl>();
}
