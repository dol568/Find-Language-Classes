import { Component, EventEmitter, InputSignal, Output, input } from '@angular/core';
import { ILanguageClass } from '../../../../shared/_models/ILanguageClass';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-language-class-detail-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './language-class-detail-sidebar.component.html',
  styleUrl: './language-class-detail-sidebar.component.scss'
})
export class LanguageClassDetailSidebarComponent {
  @Output() goToProfile: EventEmitter<string> = new EventEmitter<string>();
  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();

  public navigateToProfile(username: string): void {
    this.goToProfile.emit(username);
  }
}
