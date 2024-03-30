import { Component, EventEmitter, InputSignal, Output, effect, inject, input } from '@angular/core';
import { AuthenticatePipe } from '../../../../core/_services/authenticate.pipe';
import { ILanguageClass } from '../../../../shared/_models/ILanguageClass';
import { HandleImageErrorDirective } from '../../../../core/_services/handle-image-error.directive';
import { CommonModule } from '@angular/common';
import { ImageCachePipe } from '../../../../core/_services/image-cache.pipe';
import { ImageService } from '../../../../core/_services/image.service';

@Component({
  selector: 'app-language-class-detail-sidebar',
  standalone: true,
  imports: [AuthenticatePipe, HandleImageErrorDirective, CommonModule, ImageCachePipe],
  templateUrl: './language-class-detail-sidebar.component.html',
  styleUrl: './language-class-detail-sidebar.component.scss'
})
export class LanguageClassDetailSidebarComponent {
  @Output() goToProfile = new EventEmitter<string>();
  languageClass: InputSignal<ILanguageClass> = input.required<ILanguageClass>();

  public navigateToProfile(username: string): void {
    this.goToProfile.emit(username);
  }

      // ph
      // photos = []
      
      // img = inject(ImageService)
      
      // constructor() {
      //     effect(() => {
             
          // this.languageClass().userLanguageClasses.forEach(comment => {
          //   if (comment.image && (typeof comment.image === 'string')) {
          //     this.img.getImage(comment.image).subscribe(
          //       image => comment.image = image as string
          //     )
          //   }
          // })
         
  //   })
  // }

  
}
