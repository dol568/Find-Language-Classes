import {
  Component,
  computed,
  effect,
  inject,
  OnDestroy,
  signal,
  Signal,
  WritableSignal,
} from '@angular/core';
import { CommentDto, IComment, ILanguageClass } from '../../../shared/_models/ILanguageClass';
import { ReactiveFormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { LanguageClassesService } from '../../../core/_services/language-classes.service';
import { ActivatedRoute, ParamMap, Router, RouterModule } from '@angular/router';
import { AccountService } from '../../../core/_services/account.service';
import { _client_language_classes, _client_profiles } from '../../../shared/_constVars/_client_consts';
import { CommonModule } from '@angular/common';
import { IUser } from '../../../shared/_models/IUser';
import { LanguageClassDetailSidebarComponent } from './language-class-detail-sidebar/language-class-detail-sidebar.component';
import { LanguageClassDetailTopComponent } from './language-class-detail-top/language-class-detail-top.component';
import { LanguageClassDetailInfoComponent } from './language-class-detail-info/language-class-detail-info.component';
import { LanguageClassDetailCommentsComponent } from './language-class-detail-comments/language-class-detail-comments.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { getDaysOfWeekWords } from '../../../shared/_constVars/_days';
import { ImageService } from '../../../core/_services/image.service';

@Component({
  selector: 'app-Language-class-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    LanguageClassDetailSidebarComponent,
    LanguageClassDetailTopComponent,
    LanguageClassDetailInfoComponent,
    LanguageClassDetailCommentsComponent,
  ],
  templateUrl: './language-class-detail.component.html',
  styleUrl: './language-class-detail.component.scss',
})
export class LanguageClassDetailComponent implements OnDestroy {
  #destroySubject$: Subject<void> = new Subject<void>();
  #languageClassesService = inject(LanguageClassesService);
  #activatedRoute = inject(ActivatedRoute);
  #accountService = inject(AccountService);
  #imageService = inject(ImageService);
  #router = inject(Router);
  
  params: Signal<ParamMap> = toSignal(this.#activatedRoute.paramMap);
  id: number = Number(this.params().get('id'));
  
  languageClass: Signal<ILanguageClass> = this.#languageClassesService.languageClass(this.id);
  comments: Signal<IComment[]> = computed(() => this.languageClass()?.comments);
  dayInfo: Signal<string> = computed(() => getDaysOfWeekWords(this.languageClass()?.dayOfWeek));
  user: Signal<IUser> = this.#accountService.currentUser;

  backgroundImage: WritableSignal<string> = signal<string>('');

  constructor() {
    effect(
      () => {
        this.#imageService
          .getImage(this.languageClass()?.category)
          .subscribe((response) => this.backgroundImage.set(response as string));
      },
      { allowSignalWrites: true }
    );
  }

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public goToProfile(username: string): void {
    this.#router.navigate(['/' + _client_profiles + `/${username}`]);
  }

  public postComment(data: CommentDto): void {
    this.#languageClassesService
      .postComment(data, this.id)
      .pipe(takeUntil(this.#destroySubject$))
      .subscribe();
  }
}
