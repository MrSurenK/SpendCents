import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

type SignInCredentials = {
  username: string;
  password: string;
};

type LoginResponse = {
  userId: number;
  username: string;
  lastLogin: string;
  message: string;
  accessTokenExpiresIn: number;
  refreshTokenExpiresIn: number;
};

type LogOutResponse = {
  success: boolean;
  message: string;
  data: null;
};

export const authApi = createApi({
  reducerPath: "authApi",
  baseQuery: fetchBaseQuery({
    baseUrl: import.meta.env.VITE_API_BASE_URL,
  }),
  endpoints: (build) => ({
    //the query accepts sign in credentials of type SignInCredentials and returns a message of type Message
    login: build.mutation<LoginResponse, SignInCredentials>({
      query: (credentials) => ({
        url: "/auth/login",
        method: "POST",
        body: credentials,
        headers: { "Content-Type": "application/json" },
        credentials: "include",
      }),
    }),
    logout: build.mutation<LogOutResponse, void>({
      query: () => ({
        url: "/logout",
        method: "POST",
        credentials: "include",
      }),
    }),
  }),
});

//export format according to redux framework : use[endpoint_name]Mutation
export const { useLoginMutation, useLogoutMutation } = authApi;

export default authApi;
