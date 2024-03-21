import { computed, inject, Injectable, Signal, signal, WritableSignal } from '@angular/core';
import { IUser } from '../../shared/_models/IUser';
import { CommentDto, IComment } from '../../shared/_models/ILanguageClass';
import { _authSecretKey } from '../../shared/_constVars/_client_consts';
import { AccountService } from './account.service';
import { _api_chat } from '../../shared/_constVars/_api_consts';

declare var SockJS: any;
declare var Stomp: any;

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  #stompClient: any;
  #accountService = inject(AccountService);
  #connectedUsers: WritableSignal<IUser[]> = signal<IUser[] | undefined>([]);
  #comments: WritableSignal<IComment[]> = signal<IComment[] | undefined>([]);
  connectedUsers: Signal<IUser[]> = computed(this.#connectedUsers);
  comments: Signal<IComment[]> = computed(this.#comments);

  public initializeWebSocketConnection(id: number): void {
    if (this.#accountService.isAuthenticatedUser()) {
      const token = sessionStorage.getItem(_authSecretKey);

      const ws = new SockJS(_api_chat);
      this.#stompClient = Stomp.over(ws);

      const that = this;

      this.#stompClient.connect(
        { 'X-Authorization': 'Bearer ' + token, chatRoomId: id },
        (frame) => {
          that.#stompClient.subscribe('/chatroom/connected.users', function (message: any) {
            that.#connectedUsers.set(JSON.parse(message.body) as IUser[]);
          });
          that.#stompClient.subscribe('/chatroom/old.messages', function (message: any) {
            that.#comments.set(JSON.parse(message.body) as IComment[]);
          });
          that.#stompClient.subscribe('/topic/' + id + '.public.messages', function (message: any) {
            that.#comments.update((values) => {
              return [...values, JSON.parse(message.body) as IComment];
            });
          });
          that.#stompClient.subscribe('/topic/' + id + '.connected.users', function (message: any) {
            that.#connectedUsers.set(JSON.parse(message.body) as IUser[]);
          });
        },
        (error) => {
          console.log('Callback error -> ' + error);
          setTimeout(() => {
            this.initializeWebSocketConnection(id);
          }, 500);
        }
      );
    }
  }

  public disconnect(): void {
    if (this.#stompClient) {
      this.#stompClient.disconnect();
    }
  }

  public postComment(commentDto: CommentDto): void {
    this.#stompClient.send(`/chatroom/send.message`, {}, JSON.stringify(commentDto));
  }
}
