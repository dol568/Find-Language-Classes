import {
  Component,
  EventEmitter,
  InputSignal,
  OnDestroy,
  OnInit,
  Output,
  Signal,
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
import { CommonModule } from '@angular/common';
import { PagerComponent } from './pager/pager.component';
import { ImageService } from '../../../../core/_services/image.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-language-class-detail-comments',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PagerComponent],
  templateUrl: './language-class-detail-comments.component.html',
  styleUrl: './language-class-detail-comments.component.scss',
})
export class LanguageClassDetailCommentsComponent implements OnInit, OnDestroy {
  #imageService = inject(ImageService);
  #destroySubject$: Subject<void> = new Subject<void>();
  @Output() sendMessage: EventEmitter<CommentDto> = new EventEmitter<CommentDto>();
  @Output() sendPage: EventEmitter<number> = new EventEmitter<number>();
  comments: InputSignal<IComment[]> = input.required<IComment[]>();
  user: InputSignal<IUser> = input.required<IUser>();
  itemsPerPage: WritableSignal<number> = signal<number>(5);
  currentPage: WritableSignal<number> = signal<number>(1);
  commentsList: WritableSignal<IComment[]> = signal<IComment[]>([]);
  paginatedComments: Signal<IComment[]> = computed(() => {
    const startIndex = (this.currentPage() - 1) * this.itemsPerPage();
    const endIndex = startIndex + this.itemsPerPage();
    return this.commentsList()?.slice(startIndex, endIndex);
  });
  addComment: FormGroup;

  constructor() {
    effect(
      () => {
        this.commentsList.set(this.comments());
        this.currentPage.set(Math.ceil(this.comments()?.length / this.itemsPerPage()));
      },
      { allowSignalWrites: true }
    );
  }

  ngOnInit(): void {
    this.addComment = new FormGroup({
      description: new FormControl('', [Validators.required]),
    });
  }

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public submitFunc(): void {
    const newComment: IComment = this.#createComment();
    this.#setUserPhoto(newComment);
    this.sendMessage.emit(new CommentDto(this.addComment.value.description, this.user()?.email));

    if (!this.commentsList()) {
      this.commentsList.set([]);
    }
    this.commentsList.update((vals) => [...vals, newComment]);
    const lastPage = this.#calculateLastPage();
    this.currentPage.set(lastPage);
    this.addComment.reset();
  }

  public onPageChanged(page: number): void {
    if (this.currentPage() !== page) {
      this.currentPage.set(page);
    }
  }

  #calculateLastPage(): number {
    const totalComments = this.commentsList()?.length || 0;
    return Math.max(1, Math.ceil(totalComments / this.itemsPerPage()));
  }

  #createComment(): IComment {
    return {
      createdAt: new Date(),
      body: this.addComment.value.description,
      userName: this.user()?.userName,
      fullName: this.user()?.fullName
    }
  }

  #setUserPhoto(comment: IComment): void {
    const photo = this.user()?.photoUrl;
    if (typeof photo !== 'object') {
      this.#imageService
        .getImage(this.user()?.photoUrl)
        .pipe(takeUntil(this.#destroySubject$))
        .subscribe((image) => {
          comment.image = image as string;
        });
    } else {
      comment.image = photo;
    }
  }
}
