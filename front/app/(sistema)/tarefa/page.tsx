import Link from "next/link";

export default function Tarefa() {
    return (
<div>
  <div>
    <h1>Gestão de tarefas</h1>
    <Link href="/tarefas/novo"></Link>
  </div>

  <div>
    <div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Título</th>
            <th>Descrição</th>
            <th>Data Prazo</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>1</td>
            <td>Tela de login</td>
            <td>Tela de login do projeto</td>
            <td>10/10/26</td>
            <td>Ativo</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
    );
  }