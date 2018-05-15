import {AuthResponse} from "./AuthResponse";

export class WaviUser implements AuthResponse {
  id: string;
  name: string;
  family_name: string;
  given_name: string;
  picture_URL: string;
  email: string;
  email_verified: boolean;
  locale: string;

  songNames: string[];
  songURLS: string[];
}
