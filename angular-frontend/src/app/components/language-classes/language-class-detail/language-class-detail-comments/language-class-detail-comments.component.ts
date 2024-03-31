import {
  Component,
  EventEmitter,
  InputSignal,
  OnInit,
  Output,
  computed,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import { CommentDto, IComment } from '../../../../shared/_models/ILanguageClass';
import { IUser } from '../../../../shared/_models/IUser';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PagerComponent } from './pager/pager.component';
import { ImageService } from '../../../../core/_services/image.service';

@Component({
  selector: 'app-language-class-detail-comments',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PagerComponent],
  templateUrl: './language-class-detail-comments.component.html',
  styleUrl: './language-class-detail-comments.component.scss',
})
export class LanguageClassDetailCommentsComponent implements OnInit {
  @Output() sendMessage = new EventEmitter<CommentDto>();
  @Output() sendPage = new EventEmitter<number>();
  comments: InputSignal<IComment[]> = input.required<IComment[]>();
  user: InputSignal<IUser> = input.required<IUser>();
  addComment: FormGroup;

  img = inject(ImageService);

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
        this.commentsList.set(this.comments());
        this.currentPage.set(Math.ceil(this.comments()?.length / 5));
      },
      { allowSignalWrites: true }
    );
  }

  public submitFunc(): void {
    const newComment: IComment = {
      createdAt: new Date(),
      body: this.addComment.value.description,
      userName: this.user()?.userName,
      fullName: this.user()?.fullName
    };
    if (typeof this.user()?.photoUrl && typeof this.user()?.photoUrl === 'object') {
      newComment.image = this.user()?.photoUrl;
    } else {
      this.img.getImage(this.user()?.photoUrl).subscribe((image) => {
        newComment.image = image as string;
      });
    }
    this.sendMessage.emit(new CommentDto(this.addComment.value.description, this.user()?.email));

    if (!this.commentsList()) {
      this.commentsList.set([]);
    }
    this.commentsList.update((vals) => [...vals, newComment]);
    const lastPage = this.calculateLastPage();
    this.currentPage.set(lastPage);
    this.addComment.reset();
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
