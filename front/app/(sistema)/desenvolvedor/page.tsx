import Link from "next/link";

export default function Desenvolvedor() {
    return (
<div>
  <div>
    <h1>Gestão de desenvolvedor</h1>
    <Link href="/desenvolvedor/novo"></Link>
  </div>

  <div>
    <div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>CPF</th>
            <th>E-mail</th>
            <th>Senha</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>1</td>
            <td>Guilherme</td>
            <td>12312312312</td>
            <td>nomes@alunos</td>
            <td>123456</td>
            <td>Ativo</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
    );
  }