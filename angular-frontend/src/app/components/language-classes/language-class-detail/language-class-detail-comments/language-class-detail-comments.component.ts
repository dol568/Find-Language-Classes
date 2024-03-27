import {
  Component,
  EventEmitter,
  InputSignal,
  OnInit,
  Output,
  WritableSignal,
  effect,
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

@Component({
  selector: 'app-language-class-detail-comments',
  standalone: true,
  imports: [
    CommonModule,
    AuthenticatePipe,
    HandleImageErrorDirective,
    ReactiveFormsModule,
    PagerComponent,
  ],
  templateUrl: './language-class-detail-comments.component.html',
  styleUrl: './language-class-detail-comments.component.scss',
})
export class LanguageClassDetailCommentsComponent implements OnInit {
  @Output() sendMessage = new EventEmitter<CommentDto>();
  @Output() sendPage = new EventEmitter<number>();
  comments: InputSignal<IPage<IComment>> = input.required<IPage<IComment>>();
  user: InputSignal<IUser> = input.required<IUser>();
  addComment: FormGroup;

  page = input.required<number>();

  ngOnInit(): void {
    this.addComment = new FormGroup({
      description: new FormControl('', [Validators.required]),
    });
  }

  constructor() {}

  public submitFunc(): void {
    this.sendMessage.emit(new CommentDto(this.addComment.value.description, this.user()?.email));
    this.addComment.reset();
  }

  onPageChanged(page: number) {
    if (this.page() !== page) {
      this.sendPage.emit(page);
    }
  }
}
