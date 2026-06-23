# Política de Segurança

## Versões suportadas

Apenas a versão mais recente da branch `main` recebe atualizações de segurança.

| Versão       | Suportada          |
| ------------ | ------------------ |
| main (latest)| :white_check_mark: |
| outras       | :x:                |

## Como reportar uma vulnerabilidade

Se você encontrar uma vulnerabilidade de segurança, reporte via:

- **GitHub Issues** — abra uma issue marcando como confidencial, ou
- **E-mail** — nickalves88@gmail.com

Inclua na mensagem:
- Descrição do problema e possível impacto
- Passos para reproduzir (endpoint, payload, etc.)
- Versão do projeto afetada (hash do commit, se possível)

Você pode esperar uma resposta em até **7 dias úteis**. Vulnerabilidades confirmadas serão corrigidas e comunicadas com nota no histórico de commits.

## Medidas de segurança implementadas

| Área | Implementação |
|------|--------------|
| Autenticação | JWT com HMAC-SHA; segredo configurável via `JWT_SECRET` (mínimo 32 bytes) |
| Senhas | BCrypt (Spring Security `BCryptPasswordEncoder`) |
| Sessão | Stateless — sem cookies, sem CSRF |
| CORS | Origens permitidas configuradas via `CORS_ALLOWED_ORIGINS` |
| Isolamento de dados | Cada usuário acessa apenas seus próprios registros |
| SQL injection | Prevenido via JPA/Hibernate com parâmetros tipados |
| Migrations | Flyway com DDL versionado; Hibernate sem auto-DDL (`validate`) |
| Segredos | Todos via variáveis de ambiente; nunca em código ou no repositório |

## Escopo do sistema

Este projeto **não** processa pagamentos, não integra gateways financeiros e não realiza cobranças online. A API registra entradas informadas manualmente e calcula alocações financeiras automáticas.

Dados sensíveis armazenados:
- Nome e e-mail do usuário
- Hash BCrypt da senha (o valor em texto nunca é persistido)

## Recomendações para produção

- Definir `REGISTRATION_ENABLED=false` após criar a conta inicial.
- Definir `SPRINGDOC_ENABLED=false` para ocultar o Swagger UI em produção.
- Usar `JWT_SECRET` com pelo menos 32 bytes gerados aleatoriamente.
- Garantir HTTPS — o Railway gerencia TLS automaticamente; em outros ambientes use um proxy reverso (nginx, Caddy, etc.).
