'use client'

import { useRouter } from "next/navigation";


export default function Login(){

const router = useRouter();
    const handlelogin = async ( formData:FormData ) => {

        
        router.push("/home")
}



    return(
        <div className="min-h-screen flex items-center justify-center bg-slate-900 px-4">

                <div className="w-full max-w-md bg-slate-800 rounded-2xl shadow-2xl border border-slate-700 p-8 sm:p-10">

                        <div className="mb-8 text-center">

                                <h1 className="text-2xl sm:text-3xl font-bold text-slate-100">
                                    Entrar no sistema
                                </h1>

                                <p className="text-sm text-slate-400 mt-2"> 
                                    Insira suas credenciais para acessar o painel
                                </p>

                        </div>

                        <div className="w-full">
                            <form action={handlelogin} className="flex flex-col gap-5">



                                    <div className="flex flex-col gap-1.5">
                                        <label className="text-sm font-medium text-slate-300 ml-1">Digite seu e-mail

                                        </label>
                                        <input 
                                            name="email" 
                                            placeholder="exemplo@email.com"
                                            className="w-full px-4 py-3 bg-slate-900 border border-slate-700 rounded-lg text-slate-100 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all">
                                        </input>
                                    </div>

                                   <div className="flex flex-col gap-1.5">
                                        <label className="text-sm font-medium text-slate-300 ml-1">Digite sua senha
                                            
                                        </label>
                                        <input 
                                            name="senha" 
                                            type="password" 
                                            placeholder="••••••••"
                                            className="w-full px-4 py-3 bg-slate-900 border border-slate-700 rounded-lg text-slate-100 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:border-transparent transition-all">
                                        </input>
                                    </div>

                                    <button type="submit" className="w-full mt-2 bg-orange-500 hover:bg-orange-600 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-orange-500 focus:ring-offset-2 focus:ring-offset-slate-800 shadow-lg shadow-orange-500/30">Logar</button>

                            </form>
                        </div>

                </div>


        </div>
    );
}