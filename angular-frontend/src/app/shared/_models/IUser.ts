export interface IUser {
  userName: string;
  fullName: string;
  email: string;
  token: string;
  photoUrl?: string | null;
}
export interface IUserFormValues {
  email: string;
  password: string;
  fullName?: string;
  userName?: string;
}
