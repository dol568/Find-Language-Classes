import { Component } from '@angular/core';
import {_client_home} from "../../shared/_constVars/_client_consts";
import {RouterModule} from "@angular/router";

@Component({
  selector: 'app-notfound',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './notfound.component.html',
  styleUrl: './notfound.component.scss'
})
export class NotfoundComponent {
  home: string = _client_home;
}
