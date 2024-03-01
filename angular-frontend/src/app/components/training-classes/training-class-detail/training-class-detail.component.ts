import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {CommentDto, IComment, ITrainingClass} from "../../../shared/_models/ITrainingClass";
import {IUser} from "../../../shared/_models/IUser";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {BehaviorSubject, Observable, Subscription, switchMap} from "rxjs";
import {TrainingClassesService} from "../../../core/_services/training-classes.service";
import {ActivatedRoute, Router, RouterModule} from "@angular/router";
import {AccountService} from "../../../core/_services/account.service";
import {getDaysOfWeekWords, getFlagImg} from "../../../shared/_constVars/_days";
import {_client_training_classes} from "../../../shared/_constVars/_client_consts";
import {CommonModule} from "@angular/common";
import {ChatService} from "../../../core/_services/chat.service";
import {AuthenticatePipe} from "../../../core/_services/authenticate.pipe";
import {HandleImageErrorDirective} from "../../../core/_services/handle-image-error.directive";

@Component({
    selector: 'app-training-class-detail',
    standalone: true,
    imports: [CommonModule, RouterModule, ReactiveFormsModule, AuthenticatePipe, HandleImageErrorDirective],
    templateUrl: './training-class-detail.component.html',
    styleUrl: './training-class-detail.component.scss'
})
export class TrainingClassDetailComponent implements OnInit, OnDestroy {
    #trainingClassesService = inject(TrainingClassesService);
    #activatedRoute = inject(ActivatedRoute);
    #accountService = inject(AccountService);
    #chatService = inject(ChatService);
    #router = inject(Router);
    trainingClass: ITrainingClass;
    user: IUser;
    addComment: FormGroup;
    body: string;
    trainingClassId: number;
    protected readonly _client_training_classes = _client_training_classes;
    protected readonly getFlagImg = getFlagImg;
    protected readonly getDaysOfWeekWords = getDaysOfWeekWords;
    connectedUsers$: Observable<IUser[]>;
    msg$: Observable<IComment[]>;
    subscriptions: Subscription[] = [];
    subscription1$: Subscription;
    subscription2$: Subscription;
    subscription3$: Subscription;

    ngOnInit(): void {
        this.trainingClassId = Number(this.#activatedRoute.snapshot.params['id']);
        this.loadTrainingClass();

        this.msg$ = this.#chatService.msg$;
        this.connectedUsers$ = this.#chatService.connectedUsers$;
        const token = sessionStorage.getItem('token');
        if (token) {
            this.subscription1$ = this.#accountService.loadCurrentUser(token).subscribe({
                next: user => this.user = user,
                error: (err) => console.log(err)
            }
        )}
        this.addComment = new FormGroup({
            description: new FormControl('', [Validators.required])
        });
        this.subscriptions.push(this.subscription1$)
    }

    ngOnDestroy(): void {
        this.#chatService.disconnect();
        this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    }

    goToProfile(username: string) {
        this.#router.navigate([`/profiles/${username}`]).then();
    }

    loadTrainingClass() {
        this.subscription2$ = this.#trainingClassesService.trainingClass$(this.trainingClassId)
            .subscribe({
                next: (trainingClass) => {
                    this.trainingClass = trainingClass;
                    this.subscription3$ = this.#activatedRoute.params.subscribe(
                        params => {
                            this.#chatService.initializeWebSocketConnection(params['id']);
                        })
                },
                error: (err) => console.log(err)
            });
        this.subscriptions.push(this.subscription2$)
        this.subscriptions.push(this.subscription3$)
    }

    totalSpotsLeft() {
        let totalSpots = this.trainingClass.totalSpots - this.trainingClass.userTrainingClasses.length;
        return totalSpots > 0 ? '(' + totalSpots + ' spots left' + ')' : '(No more spots left)';
    }

    submitFunc() {
        const commentDto = new CommentDto();
        commentDto.body = this.addComment.value.description;
        commentDto.email = this.user.email
        this.#chatService.sendName(commentDto);
        this.addComment.get('description').setValue('');
    }
}
