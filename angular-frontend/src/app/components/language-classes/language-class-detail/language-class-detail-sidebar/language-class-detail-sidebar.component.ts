import { Component, EventEmitter, InputSignal, Output, input } from '@angular/core';
import { AuthenticatePipe } from '../../../../core/_services/authenticate.pipe';
import { ILanguageClass } from '../../../../shared/_models/ILanguageClass';
import { HandleImageErrorDirective } from '../../../../core/_services/handle-image-error.directive';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-language-class-detail-sidebar',
  standalone: true,
  imports: [AuthenticatePipe, HandleImageErrorDirective, CommonModule],
  templateUrl: './language-class-detail-sidebar.component.html',
  styleUrl: './language-class-detail-sidebar.component.scss'
})
export class LanguageClassDetailSidebarComponent {
  @Output() goToProfile = new EventEmitter<string>();
  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();

  public navigateToProfile(username: string): void {
    this.goToProfile.emit(username);
  }
}
