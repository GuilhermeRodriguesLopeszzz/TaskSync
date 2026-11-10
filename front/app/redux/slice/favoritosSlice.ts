
import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import Cookies from "js-cookie";

// Guarda as tarefas favoritas de cada usuário
interface FavoritosState {
    porUsuario: Record<string, number[]>;
}

// Recupera os favoritos salvos nos cookies
const favoritosRecover = Cookies.get("favoritos");

const initialState: FavoritosState = {
    porUsuario: favoritosRecover
        ? JSON.parse(favoritosRecover)
        : {}
};

const favoritosSlice = createSlice({

    name: "favoritos",
    initialState,

    reducers: {

        // Adiciona ou remove uma tarefa dos favoritos
        alternarFavorito: (
            state,
            action: PayloadAction<{
                usuarioId: number;
                tarefaId: number;
            }>
        ) => {

            const usuarioId = String(action.payload.usuarioId);
            const tarefaId = action.payload.tarefaId;

            // Cria uma lista vazia se o usuário ainda não tiver favoritos
            if (!state.porUsuario[usuarioId]) {
                state.porUsuario[usuarioId] = [];
            }

            const favoritos = state.porUsuario[usuarioId];

            // Se a tarefa já estiver favoritada, remove
            if (favoritos.includes(tarefaId)) {

                state.porUsuario[usuarioId] = favoritos.filter(
                    id => id !== tarefaId
                );

            } else {

                // Caso contrário, adiciona aos favoritos
                favoritos.push(tarefaId);
            }

            // Salva os favoritos nos cookies
            Cookies.set(
                "favoritos",
                JSON.stringify(state.porUsuario),
                { expires: 7 }
            );
        }
    }
});

export const { alternarFavorito } = favoritosSlice.actions;

export default favoritosSlice.reducer;
