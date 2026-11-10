export class Usuario {
  constructor(
    public id: number | null,
    public nome: string,
    public email: string,
    public status: string,
    public cpf: string,
    public senha: string,
    public empresaId?: number | null,
    public role?: "ROLE_ADMIN" | "ROLE_GERENTE" | "ROLE_DEV"
  ) {}
}

export interface UsuarioFormProps{
  usuarioExistente?: Usuario
}