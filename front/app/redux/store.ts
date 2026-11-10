import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../redux/slice/authSlice"
import favoritosReducer from "../redux/slice/favoritosSlice"

export const store = configureStore({
    reducer:{
        auth: authReducer,
        favoritos: favoritosReducer
    }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispath = typeof store.dispatch;