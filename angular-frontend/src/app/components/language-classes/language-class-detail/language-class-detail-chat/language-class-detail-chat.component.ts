import { CommonModule } from '@angular/common';
import {
  AfterViewChecked,
  Component,
  ElementRef,
  EventEmitter,
  InputSignal,
  OnInit,
  Output,
  ViewChild,
  WritableSignal,
  input,
  signal,
} from '@angular/core';
import { AuthenticatePipe } from '../../../../core/_services/authenticate.pipe';
import { HandleImageErrorDirective } from '../../../../core/_services/handle-image-error.directive';
import { CommentDto, IComment } from '../../../../shared/_models/ILanguageClass';
import { IUser } from '../../../../shared/_models/IUser';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-language-class-detail-chat',
  standalone: true,
  imports: [CommonModule, AuthenticatePipe, HandleImageErrorDirective, ReactiveFormsModule],
  templateUrl: './language-class-detail-chat.component.html',
  styleUrl: './language-class-detail-chat.component.scss',
})
export class LanguageClassDetailChatComponent implements OnInit, AfterViewChecked {
  @Output() sendMessage = new EventEmitter<CommentDto>();
  // comments: InputSignal<IComment[]> = input.required<IComment[]>();
  connectedUsers: InputSignal<IUser[]> = input.required<IUser[]>();
  user: InputSignal<IUser> = input.required<IUser>();
  scrolltop: WritableSignal<number> = signal<number>(undefined);
  @ViewChild('scrollMe') comment: ElementRef;
  addComment: FormGroup;

  ngOnInit(): void {
    this.addComment = new FormGroup({
      description: new FormControl('', [Validators.required]),
    });
  }

  ngAfterViewChecked(): void {
    if (this.comment) {
      this.scrolltop.set(this.comment.nativeElement.scrollHeight);
    }
  }

  public submitFunc(): void {
    this.sendMessage.emit(new CommentDto(this.addComment.value.description, this.user()?.email));
    this.addComment.reset();
  }
}
