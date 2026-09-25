export class Desenvolvedor {
  constructor(
    public id: number | null,
    public nome: string,
    public cpf: string,
    public senha: string,
    public email: string,
    public status: string,
  ) {}
}

export interface DesenvolvedorFormProps{
  desenvolvedorExistente?: Desenvolvedor
}