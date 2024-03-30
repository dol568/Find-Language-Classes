import {
  Component,
  EventEmitter,
  InputSignal,
  OnInit,
  Output,
  WritableSignal,
  computed,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import { CommentDto, IComment } from '../../../../shared/_models/ILanguageClass';
import { IUser } from '../../../../shared/_models/IUser';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthenticatePipe } from '../../../../core/_services/authenticate.pipe';
import { CommonModule } from '@angular/common';
import { HandleImageErrorDirective } from '../../../../core/_services/handle-image-error.directive';
import { PagerComponent } from './pager/pager.component';
import { IPage } from '../../../../shared/_models/IPage';
import { ImageCachePipe } from '../../../../core/_services/image-cache.pipe';
import { ImageService } from '../../../../core/_services/image.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-language-class-detail-comments',
  standalone: true,
  imports: [
    CommonModule,
    AuthenticatePipe,
    HandleImageErrorDirective,
    ReactiveFormsModule,
    PagerComponent,
    ImageCachePipe
  ],
  templateUrl: './language-class-detail-comments.component.html',
  styleUrl: './language-class-detail-comments.component.scss',
})
export class LanguageClassDetailCommentsComponent implements OnInit {
  @Output() sendMessage = new EventEmitter<CommentDto>();
  @Output() sendPage = new EventEmitter<number>();
  comments: InputSignal<IComment[]> = input.required<IComment[]>();
  user: InputSignal<IUser> = input.required<IUser>();
  addComment: FormGroup;

  img = inject(ImageService)

  itemsPerPage = 5;

  commentsList = signal<IComment[]>([]);

  currentPage = signal<number>(1);

  paginatedComments = computed(() => {
    const startIndex = (this.currentPage() - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.commentsList()?.slice(startIndex, endIndex);
  });

  ngOnInit(): void {
    this.addComment = new FormGroup({
      description: new FormControl('', [Validators.required]),
    });
    
  }

  constructor() {
    effect(
      () => {
        // console.log(this.comments())
        // if (!!this.comments()) {

          // this.comments().forEach(comment => {
          //   if (comment.image && (typeof comment.image === 'string')) {
          //     this.img.getImage(comment.image).subscribe(
          //       image => comment.image = image as string
          //     )
          //   }
          // })
          this.commentsList.set(this.comments());
          this.currentPage.set(Math.ceil(this.comments()?.length / 5));
        // }
        // for (let attendee of this.comments()) {
        //   this.serv.getImage(attendee.image).subscribe(reponse => this.photos.push(reponse))
        // }
        
      },
      { allowSignalWrites: true }
    );

    }

    

  public submitFunc(): void {
    this.img.getImage(this.user()?.photoUrl).subscribe(image => {
      // Inside the subscription, create the newComment object with the received image
      const newComment: IComment = {
        createdAt: new Date(),
        body: this.addComment.value.description,
        userName: this.user()?.userName,
        fullName: this.user()?.fullName,
        image: image as string, // Assign the received image to the image property
      };
  
      // Perform any action needed with the newComment object here
      console.log(newComment);
  
      // Don't forget to unsubscribe to avoid memory leaks
      
      this.sendMessage.emit(new CommentDto(this.addComment.value.description, this.user()?.email));
  
      if (!this.commentsList()) {
        this.commentsList.set([]);
      }
      this.commentsList.update((vals) => [...vals, newComment]);
      const lastPage = this.calculateLastPage();
      this.currentPage.set(lastPage);
      this.addComment.reset();
    });
  }

  onPageChanged(page: number) {
    if (this.currentPage() !== page) {
      this.currentPage.set(page);
    }
  }
  private calculateLastPage(): number {
    const totalComments = this.commentsList()?.length || 0;
    return Math.max(1, Math.ceil(totalComments / this.itemsPerPage));
  }
}
