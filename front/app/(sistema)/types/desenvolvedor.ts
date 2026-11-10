export class Desenvolvedor {
  constructor(
    public id: number | null,
    public nome: string,
    public cpf: string,
    public senha: string,
    public email: string,
    public status: string,
    public empresaId: number | null,
    public role: "ROLE_DEV"
  ) {}
}

export class DesenvolvedorRequest {
  constructor(
    public nome: string,
    public cpf: string,
    public email: string,
    public senha: string,
    public empresaId: number | null,
    public role: "ROLE_DEV"
  ) {}
}

export interface DesenvolvedorFormProps {
  desenvolvedorExistente?: Desenvolvedor;
}