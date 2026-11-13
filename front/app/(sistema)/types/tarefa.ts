export class Tarefa {
  constructor(
    public id: number | null,
    public titulo: string,
    public descricao: string,
    public dataPrazo: string,
    public status: string,
    public projetoId: number | null,
    public desenvolvedorId: number | null
  ) {}
}

export interface TarefaFormProps{
  tarefaExistente?: Tarefa
}