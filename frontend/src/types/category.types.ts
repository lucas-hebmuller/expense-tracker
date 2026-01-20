import type { User } from "./auth.types";

export interface Category {
  id: number;
  name: string;
  user: User;
  version: number;
}

export interface CategoryCreateRequest {
  name: string;
}
