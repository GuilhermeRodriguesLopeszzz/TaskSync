export class Empresa {
  constructor(
    public id: number | null,
    public cnpj: string,
    public nomeFantasia: string,
    public razaoSocial: string,
    public status: string,
  ) {}
}

export interface EmpresaFormProps {
  empresaExistente?: Empresa;
}