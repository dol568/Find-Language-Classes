
import { SafeUrl } from '@angular/platform-browser';
import { IUserLanguageClass } from './IUserLanguageClass';

export interface ILanguageClass {
  address: string;
  category: string;
  city: string;
  country: string;
  dayOfWeek: number;
  description: string;
  time: string;
  id: number;
  postalCode: string;
  province: string;
  title: string;
  totalSpots: number;
  userLanguageClasses?: IUserLanguageClass[];
  isGoing?: boolean;
  isHost?: boolean;
  hostName: string;
  hostUserName: string;
  hostImage: string | null;
  comments?: IComment[];
}
export interface IComment {
  id?: number;
  createdAt: Date;
  body: string;
  userName: string;
  fullName: string;
  image: string;
}
export class CommentDto {
  constructor(private body: string, private email: string) {}
}
