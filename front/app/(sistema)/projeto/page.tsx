import Link from "next/link";

export default function Projeto() {
    return (
<div>
  <div>
    <h1>Gestão de Projeto</h1>
    <Link href="Projeto/novo"></Link>
  </div>

  <div>
    <div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Data inicio</th>
            <th>Data fim</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>1</td>
            <td>TaskSync</td>
            <td>Projeto de gerenciamento de projeto</td>
            <td>10/06/26</td>
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