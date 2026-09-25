export class Tarefa {
  constructor(
    public id: number | null,
    public titulo: string,
    public descricao: string,
    public dataPrazo: string,
    public status: string,
  ) {}
}

export interface TarefaFormProps{
  tarefaExistente?: Tarefa
}