# Sistema de Agendamento Odontológico

Este sistema de agendamento odontológico facilita a marcação de consultas, o gerenciamento de agendas de dentistas e a interação entre pacientes e profissionais. Pacientes podem agendar, cancelar e avaliar consultas, enquanto dentistas gerenciam suas agendas. Administradores controlam usuários e acessam relatórios.

## EM CONSTRUÇÃO

## Documentação no notion:

- https://grave-hyena-443.notion.site/App-de-Agendamento-Odontol-gico-Java-Angular-1a93a302750380c4a8d3fef126ca0540?pvs=73

---

## Backlog no notion:

- https://grave-hyena-443.notion.site/Backlog-1ac3a30275038027a179fec482aff6f6

---

## Funcionalidades

- **Cadastro e Login**: Pacientes, dentistas e administradores podem se registrar e acessar suas contas.
- **Agendamento de Consultas**: Pacientes podem agendar, remarcar ou cancelar consultas com dentistas.
- **Gestão de Agenda**: Dentistas podem definir, atualizar e bloquear horários para consultas.
- **Avaliações**: Pacientes podem avaliar dentistas após o atendimento.
- **Notificações**: Envio de lembretes de consulta para pacientes e dentistas.
- **Relatórios e Estatísticas**: Administradores podem visualizar dados sobre consultas realizadas e feedbacks.
- **Gerenciamento de Usuários**: Administradores podem cadastrar, editar e excluir contas de pacientes e dentistas.

---

## Tecnologias Utilizadas

### Backend:

- **Spring Boot (Java):** Framework para a construção do backend.
- **Spring JPA e PostgreSQL:** Utilizados para persistência de dados.
- **JWT e OAuth2:** Implementação para autenticação segura, garantindo que os usuários possam se autenticar de forma eficaz.
- **Flyway:** Ferramenta para gerenciamento de migrações do banco de dados.
- **Swagger:** Para documentação da API, facilitando o consumo da API por desenvolvedores.
- **Spring Mail:** Para envio de notificações por e-mail, como lembretes de consultas e confirmações.
- **Spring Validation:** Utilizado para realizar validações de dados em requisições, garantindo a integridade e a precisão das informações enviadas pelos usuários.

---

### Frontend:

- **Angular** para o desenvolvimento do frontend.
- **Bootstrap 5** para estilização.
- **Jasmine** para testes unitários.

### Infraestrutura:

- **GitHub** para versionamento e colaboração.
- **GitHub Actions** com cobertura de testes e SonarQube para análise de qualidade de código e monitoramento de cobertura de testes no backend.
- **Trello** para gestão de projeto.

## Como Rodar o Projeto

### Requisitos

- **Java 17** ou superior.
- **Node.js** (para o frontend).
- **PostgreSQL**.
- **Git** (para clonar a aplicação).

1. Clone o repositório e acesse o diretório

```bash
git clone https://github.com/Dev-HideyukiTakahashi/dental-care.git
```

2. Backend - navegue até o diretório do projeto:

- pasta back-end
- pelo terminal `cd back-end`

3. Certifique-se de que o PostgreSQL esteja corretamente instalado e em execução na sua máquina ou em um container (como Docker). Em seguida, crie o banco de dados com o nome dental_care.

4. Configuração de envio de e-mails
   Para que o sistema possa enviar e-mails (como confirmações de agendamento), defina as seguintes variáveis de ambiente no seu sistema, IDE ou no terminal:

```bash
EMAIL_USERNAME=seuemail@gmail.com
EMAIL_PASSWORD=sua-senha-de-app
```

5. Rode o projeto pelo terminal:

```bash
mvn spring-boot:run
```

5. Frontend - navegue até o diretório do projeto:

- pasta front-end
- pelo terminal `cd front-end`

6. Instale as dependências pelo terminal:

```bash
npm install
```

7. Inicie o servidor de desenvolvimento pelo terminal:

```bash
ng serve
```

8. Abra o navegador e acesse `http://localhost:4200`.

## Endpoints da API

- A documentação completa da API está disponível em: `http://localhost:8080/swagger-ui/`

## Fluxo de Trabalho

### Para Pacientes

1. **Cadastro e Login**: O paciente se cadastra e realiza o login.
2. **Agendamento de Consultas**: O paciente agenda uma consulta com um dentista disponível.
3. **Avaliação do Dentista**: Após a consulta, o paciente pode avaliar o dentista.

### Para Dentistas

1. **Cadastro e Login**: O dentista se cadastra e realiza o login.
2. **Gerenciamento de Agenda**: O dentista define seus horários de atendimento.
3. **Gerenciamento de Consultas**: O dentista gerencia consultas agendadas.

### Para Administradores

1. **Gerenciamento de Usuários**: O administrador pode gerenciar contas de pacientes e dentistas.
2. **Relatórios**: O administrador acessa relatórios e estatísticas.

## Casos de Uso

- **Paciente**: Agendar consultas, cancelar, avaliar dentistas, receber notificações.
- **Dentista**: Gerenciar horários, confirmar consultas, interagir com pacientes.
- **Administrador**: Gerenciar usuários, visualizar relatórios de consultas e estatísticas.

## Licença

Este projeto está licenciado sob a licença MIT.

## Considerações Finais

Este sistema visa melhorar a experiência de pacientes e dentistas ao fornecer uma plataforma eficiente e fácil de usar para agendamento de consultas odontológicas. O desenvolvimento deste projeto foi uma oportunidade para aprimorar minhas habilidades como desenvolvedor fullstack e aplicar boas práticas no desenvolvimento de sistemas.

---
