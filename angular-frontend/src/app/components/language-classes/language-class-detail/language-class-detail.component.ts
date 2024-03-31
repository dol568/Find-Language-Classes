import {
  Component,
  computed,
  effect,
  inject,
  OnDestroy,
  OnInit,
  signal,
  Signal,
  WritableSignal,
} from '@angular/core';
import { CommentDto, IComment, ILanguageClass } from '../../../shared/_models/ILanguageClass';
import { ReactiveFormsModule } from '@angular/forms';
import { map, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { LanguageClassesService } from '../../../core/_services/language-classes.service';
import { ActivatedRoute, ParamMap, Router, RouterModule } from '@angular/router';
import { AccountService } from '../../../core/_services/account.service';
import { _client_language_classes } from '../../../shared/_constVars/_client_consts';
import { CommonModule } from '@angular/common';
import { AuthenticatePipe } from '../../../core/_services/authenticate.pipe';
import { HandleImageErrorDirective } from '../../../core/_services/handle-image-error.directive';
import { IUser } from '../../../shared/_models/IUser';
import { LanguageClassDetailSidebarComponent } from './language-class-detail-sidebar/language-class-detail-sidebar.component';
import { LanguageClassDetailTopComponent } from './language-class-detail-top/language-class-detail-top.component';
import { LanguageClassDetailInfoComponent } from './language-class-detail-info/language-class-detail-info.component';
import { LanguageClassDetailCommentsComponent } from './language-class-detail-comments/language-class-detail-comments.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { IPage } from '../../../shared/_models/IPage';
import { ShopParams } from '../../../shared/_models/ShopParams';
import { SnackbarService } from '../../../core/_services/snackbar.service';

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
  #router = inject(Router);
  client_language_classes: string = _client_language_classes;
  user: Signal<IUser> = this.#accountService.currentUser;

  params: Signal<ParamMap> = toSignal(this.#activatedRoute.paramMap);
  id = Number(this.params().get('id'));

  languageClass = this.#languageClassesService.langClassSignal(this.id);

  comments = computed(() => this.languageClass()?.comments)

  ngOnDestroy(): void {
    this.#destroySubject$.next();
    this.#destroySubject$.complete();
  }

  public goToProfile(username: string): void {
    this.#router.navigate([`/profiles/${username}`]);
  }

  public postComment(data: CommentDto): void {
      this.#languageClassesService
        .postComment(data, this.id)
        .subscribe();
  }
}
