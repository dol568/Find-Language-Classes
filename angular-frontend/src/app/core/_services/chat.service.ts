import { Injectable } from '@angular/core';
import {IUser} from "../../shared/_models/IUser";
import {CommentDto, IComment} from "../../shared/_models/ITrainingClass";
import {BehaviorSubject} from "rxjs";
declare var SockJS: any;
declare var Stomp: any;

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  stompClient: any;
  private connectedUsersSource = new BehaviorSubject<IUser[]>(null);
  connectedUsers$ = this.connectedUsersSource.asObservable();
  private msgSource = new BehaviorSubject<IComment[]>(null);
  msg$ = this.msgSource.asObservable();

  constructor() { }

  initializeWebSocketConnection(id) {
    let token = sessionStorage.getItem('token');
    if (token) {
      const serverUrl = 'http://localhost:9000/ws';

      const ws = new SockJS(serverUrl);
      this.stompClient = Stomp.over(ws);

      const that = this;

      this.stompClient.connect({'X-Authorization': token, 'chatRoomId': id}, stompSuccess, stompFailure);

      function stompSuccess(frame) {
        that.stompClient.subscribe('/chatroom/connected.users', function (message: any) {
          const data = JSON.parse(message.body) as IUser[]
          that.connectedUsersSource.next(data)
        });
        that.stompClient.subscribe('/chatroom/old.messages', function (message: any) {
          const data = JSON.parse(message.body) as IComment[];
          that.msgSource.next(data)

        })
        that.stompClient.subscribe('/topic/' + id + '.public.messages', function (message: any) {
          const data = JSON.parse(message.body) as IComment;
          const currentMessages = that.msgSource.getValue();
          const updatedMessages = currentMessages.concat(data);
          that.msgSource.next(updatedMessages);
        })
        that.stompClient.subscribe('/topic/' + id + '.connected.users', function (message: any) {
          const data = JSON.parse(message.body) as IUser[]
          that.connectedUsersSource.next(data)
        })
      }

      function stompFailure(frame) {
      }
    }
  }

  disconnect() {
    if (this.stompClient) {

    this.stompClient.disconnect()
    }
  }

  sendName(commentDto: CommentDto) {
    this.stompClient.send(`/chatroom/send.message`, {}, JSON.stringify(commentDto));
  }
}
