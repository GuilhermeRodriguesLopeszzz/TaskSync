'use client'

import axios from "axios"
import Cookies from 'js-cookie'
import { store } from "../redux/store"
import { logout } from "../redux/slice/authSlice"

const api = axios.create({

    baseURL:'http://localhost:8080'
})

// INTERCEPTOR DE REQUISIÇÃO
// Adiciona o token JWT automaticamente
api.interceptors.request.use(
    (config) => {
        const token = Cookies.get('token');

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    }
);

// INTERCEPTOR DE RESPOSTA
// Verifica se o token expirou ou é inválido
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {

        if (error.response?.status === 401) {

            // Verifica se existe usuário autenticado
            const token = store.getState().auth.token;

            if (token) {
                // Limpa o Redux e os cookies
                store.dispatch(logout());
            }
        }

        return Promise.reject(error);
    }
);


export default api;