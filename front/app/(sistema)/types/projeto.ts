export class Projeto {
  constructor(
    public id: number | null,
    public nome: string,
    public descricao: string,
    public dataInicio: string,
    public dataPrevistaFim: string,
    public status: string,
    public desenvolvedoresIds: number[] = [],
    public empresaId: number | null = null
  ) {}
}

export interface ProjetoFormProps{
  projetoExistente?: Projeto
}







