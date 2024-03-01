export interface IProfile {
  id: string;
  fullName: string;
  username: string;
  bio: string;
  photoUrl: string | null;
  isFollowed: boolean;
  profileTrainingClasses: IProfileTrainingClass[];
  followings: IProfileFollowing[];
  followers: IProfileFollowing[];
}
export interface IProfileFollowing {
  fullName: string;
  username: string;
  bio: string;
  photoUrl: string | null;
}
export interface IProfileEdit {
  fullName: string;
  bio: string;
}
export interface IProfileTrainingClass {
  id: string;
  title: string;
  category: string;
  time: string;
  dayOfWeek: number;
  hostUserName: string;
}
export enum ProfileInfo {
  ABOUT, PHOTO, CLASSES, FOLLOWERS, FOLLOWINGS
}
