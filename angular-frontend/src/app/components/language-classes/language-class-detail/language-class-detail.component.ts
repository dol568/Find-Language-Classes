import { Component, inject, OnDestroy, OnInit, signal, Signal, WritableSignal } from '@angular/core';
import {
  CommentDto,
  IComment,
  ILanguageClass,
} from '../../../shared/_models/ILanguageClass';
import { ReactiveFormsModule } from '@angular/forms';
import { EMPTY, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { LanguageClassesService } from '../../../core/_services/language-classes.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AccountService } from '../../../core/_services/account.service';
import {
  getDaysOfWeekWords,
  getFlagImg,
} from '../../../shared/_constVars/_days';
import { _client_language_classes } from '../../../shared/_constVars/_client_consts';
import { CommonModule } from '@angular/common';
import { ChatService } from '../../../core/_services/chat.service';
import { AuthenticatePipe } from '../../../core/_services/authenticate.pipe';
import { HandleImageErrorDirective } from '../../../core/_services/handle-image-error.directive';
import { IUser } from '../../../shared/_models/IUser';
import { LanguageClassDetailSidebarComponent } from './language-class-detail-sidebar/language-class-detail-sidebar.component';
import { LanguageClassDetailTopComponent } from './language-class-detail-top/language-class-detail-top.component';
import { LanguageClassDetailInfoComponent } from './language-class-detail-info/language-class-detail-info.component';
import { LanguageClassDetailChatComponent } from './language-class-detail-chat/language-class-detail-chat.component';

@Component({
  selector: 'app-Language-class-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    AuthenticatePipe,
    HandleImageErrorDirective,
    LanguageClassDetailSidebarComponent,
    LanguageClassDetailTopComponent,
    LanguageClassDetailInfoComponent,
    LanguageClassDetailChatComponent,
  ],
  templateUrl: './language-class-detail.component.html',
  styleUrl: './language-class-detail.component.scss',
})
export class LanguageClassDetailComponent implements OnInit, OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #languageClassesService = inject(LanguageClassesService);
  #activatedRoute = inject(ActivatedRoute);
  #accountService = inject(AccountService);
  #chatService = inject(ChatService);
  #router = inject(Router);
  protected readonly _client_language_classes = _client_language_classes;
  protected readonly getFlagImg = getFlagImg;
  protected readonly getDaysOfWeekWords = getDaysOfWeekWords;
  id: WritableSignal<number> = signal<number>(undefined);
  languageClass: Signal<ILanguageClass> =
    this.#languageClassesService.languageClass;
  comments: Signal<IComment[]> = this.#chatService.comments;
  connectedUsers: Signal<IUser[]> = this.#chatService.connectedUsers;
  user: Signal<IUser> = this.#accountService.currentUser;

  ngOnInit(): void {
    this.#activatedRoute.paramMap
      .pipe(
        switchMap((params) => {
          const id = Number(params.get('id'));
          if (id) {
            this.id.set(id);
            return this.#languageClassesService.languageClass$(this.id()).pipe(
              tap(() => {
                this.#chatService.initializeWebSocketConnection(this.id());
              })
            );
          } else {
            return EMPTY;
          }
        }),
        takeUntil(this.#destroySubject$)
      )
      .subscribe({
        error: (err) => console.error(err),
      });
  }

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
    this.#chatService.disconnect();
  }

  public goToProfile(username: string): void {
    this.#router.navigate([`/profiles/${username}`]);
  }

  public postComment(data: CommentDto): void {
    this.#chatService.postComment(data);
  }
}
