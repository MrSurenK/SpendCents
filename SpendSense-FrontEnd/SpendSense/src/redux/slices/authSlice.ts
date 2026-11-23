import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { RootState } from "../store";
//Can use mutating logic as Immer library will detect changes and make immutable copies to satisfy reacts state rules

export type AuthState = {
  userId: number | null;
  username: string | null;
  lastLogIn: string | null;
  isLoggedIn: boolean;
};

const initialState: AuthState = {
  userId: null,
  username: null,
  lastLogIn: null,
  isLoggedIn: false,
};

export type LoginPayload = {
  userId: number;
  username: string;
  lastLogin: string;
};

//define actions
export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    loginState: (state, action: PayloadAction<LoginPayload>) => {
      //user logged in
      const { userId, username, lastLogin } = action.payload;
      state.userId = userId;
      state.username = username;
      state.lastLogIn = lastLogin;
      state.isLoggedIn = true;
    },
    logoutState: (state) => {
      state.userId = null;
      state.username = null;
      state.lastLogIn = null;
      state.isLoggedIn = false;
    },
  },
});

export const { loginState, logoutState } = authSlice.actions;

export const selectAuthStatus = (state: RootState) => state.auth;

export default authSlice.reducer;
