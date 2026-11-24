export class Empresa {
  constructor(
    public id: number | null,
    public cnpj: string,
    public nomeFantasia: string,
    public razaoSocial: string,
    public status: string,
    public cep: string | null = "",
    public logradouro: string | null = "",
    public bairro: string | null = "",
    public cidade: string | null = "",
    public uf: string | null = "",
  ) {}
}

export interface EmpresaFormProps {
  empresaExistente?: Empresa;
}

export interface Endereco {
  cep: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  uf: string;
}
