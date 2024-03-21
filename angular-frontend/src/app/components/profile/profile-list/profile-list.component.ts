import {Component, EventEmitter, Output} from '@angular/core';
import {ProfileInfo} from "../../../shared/_models/IProfile";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-profile-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-list.component.html',
  styleUrl: './profile-list.component.scss'
})
export class ProfileListComponent {
  protected readonly ProfileInfo = ProfileInfo;
  info: ProfileInfo = ProfileInfo.ABOUT;
  @Output() listInfo = new EventEmitter<ProfileInfo>();

  setInfo(value: ProfileInfo) {
    this.listInfo.emit(value);
    this.info = value;
  }
}
