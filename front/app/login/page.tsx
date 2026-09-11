'use client'

import axios from "axios";
import { useRouter } from "next/navigation";
import { LoginResponse } from "../(sistema)/types/auth";


export default function Login(){

const router = useRouter();
    const handlelogin = async ( formData:FormData ) => {

        try{
        const emailTela = formData.get("email")?.toString();
        const senhaTela = formData.get("senha")?.toString();

        var loginResposta = await axios.post<LoginResponse>("http://localhost:8080/auth/login", {email:emailTela, senha:senhaTela})

        if (loginResposta.status==200){
            router.push("/home")
        } else{
        alert("Login ou senha inválido")
        }
    }catch (error){
         alert("Login ou senha inválido")
    }
}



    return(
        <div className="min-h-screen flex items-center justify-center bg-[#0a0a0f] px-4 relative overflow-hidden">

                <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[500px] bg-gradient-to-r from-amber-500/15 via-orange-500/10 to-fuchsia-600/15 blur-[140px] rounded-full pointer-events-none"></div>
                <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff08_1px,transparent_1px),linear-gradient(to_bottom,#ffffff08_1px,transparent_1px)] bg-[size:64px_64px] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_50%,#000_50%,transparent_100%)] pointer-events-none"></div>

                <div className="w-full max-w-md bg-white/[0.03] rounded-3xl shadow-2xl shadow-black/60 border border-white/10 backdrop-blur-xl p-8 sm:p-10 relative z-10">

                        <div className="mb-8 text-center">

                                <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-amber-400 via-orange-500 to-fuchsia-600 flex items-center justify-center shadow-lg shadow-orange-500/30 mx-auto mb-5">
                                    <svg className="w-7 h-7 text-black" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                        <path d="M12 5c-3 0-5 2-5 5v1a5 5 0 0 0 10 0v-1c0-3-2-5-5-5z"></path>
                                        <path d="M9 4L7 2"></path>
                                        <path d="M15 4l2-2"></path>
                                        <path d="M9 14v1a3 3 0 0 0 6 0v-1"></path>
                                        <circle cx="10" cy="9" r="1" fill="currentColor"></circle>
                                        <circle cx="14" cy="9" r="1" fill="currentColor"></circle>
                                    </svg>
                                </div>

                                <h1 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
                                    Entrar no sistema
                                </h1>

                                <p className="text-sm text-zinc-400 mt-2"> 
                                    Insira suas credenciais para acessar o painel
                                </p>

                        </div>

                        <div className="w-full">
                            <form action={handlelogin} className="flex flex-col gap-5">



                                    <div className="flex flex-col gap-1.5">
                                        <label className="text-sm font-medium text-zinc-300 ml-1">Digite seu e-mail

                                        </label>
                                        <input 
                                            name="email" 
                                            placeholder="exemplo@email.com"
                                            className="w-full px-4 py-3 bg-white/[0.04] border border-white/10 rounded-xl text-white placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all">
                                        </input>
                                    </div>

                                   <div className="flex flex-col gap-1.5">
                                        <label className="text-sm font-medium text-zinc-300 ml-1">Digite sua senha
                                            
                                        </label>
                                        <input 
                                            name="senha" 
                                            type="password" 
                                            placeholder="••••••••"
                                            className="w-full px-4 py-3 bg-white/[0.04] border border-white/10 rounded-xl text-white placeholder:text-zinc-500 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:border-amber-400/50 transition-all">
                                        </input>
                                    </div>

                                    <button type="submit" className="w-full mt-2 bg-gradient-to-r from-amber-400 to-orange-500 hover:from-amber-300 hover:to-orange-400 text-black font-bold py-3 px-4 rounded-full transition-all duration-200 hover:-translate-y-0.5 focus:outline-none focus:ring-2 focus:ring-amber-400/50 focus:ring-offset-2 focus:ring-offset-[#0a0a0f] shadow-xl shadow-orange-500/25 hover:shadow-orange-400/40">Logar</button>

                            </form>
                        </div>

                </div>


        </div>
    );
}