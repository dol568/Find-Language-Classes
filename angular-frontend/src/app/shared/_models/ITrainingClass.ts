import {IUserTrainingClass} from './IUserTrainingClass';

export interface ITrainingClass {
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
  userTrainingClasses?: IUserTrainingClass[];
  isGoing?: boolean;
  isHost?: boolean;
  hostName: string;
  hostUserName: string;
  hostImage: string | null;
  comments?: IComment[];
}
export interface IComment {
  id: number;
  createdAt: Date;
  body: string;
  userName: string;
  fullName: string;
  image: string;
}
export class CommentDto {
  body: string;
  email: string;
}
