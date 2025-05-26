# Sistema de Agendamento Odontológico

Este sistema de agendamento odontológico facilita a marcação de consultas, o gerenciamento de agendas de dentistas e a interação entre pacientes e profissionais. Pacientes podem agendar, cancelar e avaliar consultas, enquanto dentistas gerenciam suas agendas. Administradores controlam usuários e acessam relatórios.

## Documentação no notion:

- https://grave-hyena-443.notion.site/App-de-Agendamento-Odontol-gico-Java-Angular-1a93a302750380c4a8d3fef126ca0540?pvs=73

---

## Backlog no notion:

- https://grave-hyena-443.notion.site/Backlog-1ac3a30275038027a179fec482aff6f6

---

## Fotos:

<details>
  <summary>Clique para visualizar as imagens do sistema</summary>

![](/recursos/1.png)
![](/recursos/2.png)
![](/recursos/3.png)
![](/recursos/4.png)
![](/recursos/5.png)
![](/recursos/6.png)
![](/recursos/7.png)
![](/recursos/8.png)
![](/recursos/9.png)
![](/recursos/10.png)
![](/recursos/11.png)

</details>

## Funcionalidades

- **Cadastro e Login**: Pacientes podem se registrar, acessar suas contas e recuperar a senha via email. Administradores registram dentistas, que podem alterar sua senha.
- **Agendamento de Consultas**: Pacientes e administradores podem agendar, remarcar ou cancelar consultas com dentistas.
- **Gestão de Agenda**: Dentistas podem definir, atualizar e bloquear horários para consultas.
- **Avaliações**: Pacientes podem avaliar dentistas após o atendimento.
- **Notificações**: Envio de lembretes de consulta para pacientes via email 24h antes da consulta.
- **Relatórios e Estatísticas**: Administradores podem visualizar estatísticas do negócio.
- **Gerenciamento de Usuários**: Administradores podem cadastrar, editar e excluir contas de pacientes e dentistas.

---

## Tecnologias Utilizadas

### Backend:

- **Spring Boot (Java):** Framework para a construção do backend.
- **Spring JPA e PostgreSQL:** Utilizados para persistência de dados.
- **JWT e OAuth2:** Implementação para autenticação segura, garantindo que os usuários possam se autenticar de forma eficaz.
- **Flyway:** Ferramenta para gerenciamento de migrações do banco de dados.
- **Swagger (OpenAPI):**
  Documentação da API em YAML para evitar poluição nos controllers, mantendo o código limpo e modular.
- **Spring Scheduler:**
  Implementação de um scheduler para enviar notificações automáticas aos pacientes 1 dia antes do compromisso.
- **Spring Mail:** Para envio de notificações por e-mail, como lembretes de consultas e confirmações.
- **Spring Validation:** Utilizado para realizar validações de dados em requisições, garantindo a integridade e a precisão das informações enviadas pelos usuários.

---

### Frontend:

- **Angular** para o desenvolvimento do frontend.
- **Bootstrap 5** para estilização.

### Infraestrutura:

- **GitHub** para versionamento e colaboração.
- **GitHub Actions** com cobertura de testes e SonarQube para análise de qualidade de código e monitoramento de cobertura de testes no backend.
- **Trello e Notion** para gestão de projeto.

## Como Rodar o Projeto

### Requisitos

- **Java 17** ou superior.
- **Node.js** (para o frontend).
- **Angular 19** ou superior.
- **PostgreSQL**.
- **Git** (para clonar a aplicação).
- **Maven** (para gerenciar dependências e rodar o projeto backend).

1. Clone o repositório e acesse o diretório

```bash
git clone https://github.com/Dev-HideyukiTakahashi/dental-care.git
```

2. Backend - navegue até o diretório do projeto:

   - pasta back-end

3. Certifique-se de que o PostgreSQL esteja corretamente instalado e em execução na sua máquina. Em seguida, crie o banco de dados com o nome dental_care.

   Opcional Docker:

   - Caso preferir Docker, execute o comando **`docker-compose up`** dentro da pasta back-end para subir um banco postgreSQL já configurado.

4. Configuração de envio de e-mails(**não obrigatório**)
   Para que o sistema possa enviar e-mails (como confirmações de agendamento, recuperação de senha), defina as seguintes variáveis de ambiente no seu sistema, IDE ou no terminal:

   ```bash
   EMAIL_USERNAME=seuemail@gmail.com
   EMAIL_PASSWORD=sua-senha-de-app
   ```

5. Rode o projeto pelo terminal:

   ```bash
   mvn spring-boot:run
   ```

---

7. Frontend - navegue até o diretório do projeto:

   - pasta front-end

8. Instale as dependências pelo terminal:

   ```bash
   npm install
   ```

9. Inicie o servidor de desenvolvimento pelo terminal:

   ```bash
   ng serve
   ```

10. Abra o navegador e acesse `http://localhost:4200`.

## Endpoints da API

- A documentação completa da API está disponível em: `http://localhost:8080/swagger-ui/index.html`

## Collection do Postman

- Disponibilizei a collection do Postman na pasta recursos para auxiliar quem for começar o sistema do zero.

## Licença

Este projeto está licenciado sob a licença MIT.

## Considerações Finais

Este sistema visa melhorar a experiência de pacientes e dentistas ao fornecer uma plataforma eficiente e fácil de usar para agendamento de consultas odontológicas. O desenvolvimento deste projeto foi uma oportunidade para aprimorar minhas habilidades como desenvolvedor fullstack e aplicar boas práticas no desenvolvimento de sistemas.

---
