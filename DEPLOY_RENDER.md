# 🚀 Deploy no Render - Guia Completo

Este guia explica como fazer o deploy da API de Doações no Render usando containers Docker.

## 📋 Pré-requisitos

- ✅ Conta no [Render](https://render.com)
- ✅ Repositório no GitHub com o código da aplicação
- ✅ Dockerfile configurado (já incluído)
- ✅ Arquivos de configuração criados

## 🐳 Arquivos de Containerização

### 1. **Dockerfile**
- ✅ Multi-stage build para otimização
- ✅ Baseado em Eclipse Temurin 17 (OpenJDK oficial)
- ✅ Imagens Alpine para menor tamanho
- ✅ Usuário não-root para segurança
- ✅ Health check configurado
- ✅ Otimizações JVM para containers

### 2. **.dockerignore**
- ✅ Exclui arquivos desnecessários do build
- ✅ Reduz tamanho da imagem
- ✅ Melhora performance do build

### 3. **render.yaml**
- ✅ Configuração declarativa para Render
- ✅ Banco PostgreSQL incluído
- ✅ Variáveis de ambiente configuradas

## 🚀 Passos para Deploy

### Método 1: Deploy Automático via render.yaml

1. **Commit dos arquivos**:
   ```bash
   git add Dockerfile .dockerignore render.yaml
   git commit -m "feat: adicionar configurações para deploy no Render"
   git push origin main
   ```

2. **Acesse o Render Dashboard**:
   - Entre em https://render.com
   - Clique em **"New"** → **"Blueprint"**

3. **Conecte o repositório**:
   - Selecione **"Connect a repository"**
   - Escolha seu repositório GitHub
   - O Render detectará automaticamente o `render.yaml`

4. **Configure o deploy**:
   - Nome do serviço: `donations-api`
   - Branch: `main`
   - Clique em **"Apply"**

### Método 2: Deploy Manual

1. **Criar Web Service**:
   - Dashboard → **"New"** → **"Web Service"**
   - Conecte o repositório GitHub

2. **Configurações do Serviço**:
   - **Name**: `donations-api`
   - **Environment**: `Docker`
   - **Plan**: `Free`
   - **Branch**: `main`
   - **Dockerfile Path**: `./Dockerfile`

3. **Configurar Variáveis de Ambiente**:
   ```
   SPRING_PROFILES_ACTIVE=render
   PORT=8080
   JWT_SECRET=[gerar chave forte]
   DATABASE_URL=[será preenchido após criar DB]
   ```

4. **Criar Banco de Dados**:
   - Dashboard → **"New"** → **"PostgreSQL"**
   - **Name**: `donations-db`
   - **Plan**: `Free`
   - **Database Name**: `donation_platform`
   - **User**: `donations_user`

5. **Conectar Banco ao Serviço**:
   - Copie a **CONNECTION STRING** do banco
   - Cole na variável `DATABASE_URL` do web service
   - **Importante**: Use a CONNECTION STRING completa, não separe user/password

## 🔧 Configurações Importantes

### Variáveis de Ambiente Obrigatórias

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `SPRING_PROFILES_ACTIVE` | Perfil ativo | `render` |
| `PORT` | Porta do serviço | `8080` |
| `JWT_SECRET` | Chave secreta JWT | `sua-chave-super-secreta-aqui` |
| `DATABASE_URL` | URL completa do PostgreSQL | `postgresql://user:pass@host:5432/db` |

### Health Check
- **Path**: `/actuator/health`
- **Timeout**: 30 segundos
- **Intervalo**: 30 segundos

## 🌐 URLs de Acesso

Após o deploy bem-sucedido, sua API estará disponível em:

- **API Base**: `https://donations-api-[seu-id].onrender.com/api`
- **Swagger UI**: `https://donations-api-[seu-id].onrender.com/swagger-ui.html`
- **Health Check**: `https://donations-api-[seu-id].onrender.com/actuator/health`

## 📝 Testando o Deploy

### 1. Health Check
```bash
curl https://donations-api-[seu-id].onrender.com/actuator/health
```

### 2. Registro de Usuário
```bash
curl -X POST https://donations-api-[seu-id].onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Teste",
    "email": "teste@example.com",
    "password": "123456"
  }'
```

### 3. Login
```bash
curl -X POST https://donations-api-[seu-id].onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@example.com",
    "password": "123456"
  }'
```

## 🔄 Atualizações

Para atualizar a aplicação:

1. **Faça as alterações no código**
2. **Commit e push para o GitHub**:
   ```bash
   git add .
   git commit -m "feat: nova funcionalidade"
   git push origin main
   ```
3. **Deploy automático**: Render detectará as mudanças e fará deploy automaticamente

## 📊 Monitoramento

### Logs do Serviço
- Dashboard do Render → Seu serviço → **"Logs"**

### Métricas
- Dashboard do Render → Seu serviço → **"Metrics"**

### Banco de Dados
- Dashboard do Render → Banco → **"Info"** para conexão
- Use ferramentas como pgAdmin ou DBeaver para administração

## 🚨 Troubleshooting

### Build Falha
1. **Verifique os logs** no dashboard do Render
2. **Certifique-se** que todas as dependências estão no `pom.xml`
3. **Teste localmente** (se tiver Docker):
   ```bash
   docker build -t donations-api .
   docker run -p 8080:8080 donations-api
   ```

**Problemas Comuns de Build:**
- ❌ **Imagem não encontrada**: Usar `eclipse-temurin` em vez de `openjdk` (descontinuada)
- ❌ **Maven Wrapper não encontrado**: Usar Maven diretamente em vez de mvnw
- ❌ **Dependência faltando**: Verificar `pom.xml` 
- ❌ **Timeout**: Plano gratuito tem limite de tempo de build (15 min)
- ❌ **Memória insuficiente**: Otimizar configurações JVM

### Aplicação não Inicia
1. **Verifique variáveis de ambiente**
2. **Confirme URL do banco de dados**
3. **Verifique logs de aplicação**

### Erro de Conexão com Banco
1. **Verifique a CONNECTION STRING do PostgreSQL**
2. **Use a URL completa**: `postgresql://user:pass@host:port/database`
3. **Não separe credenciais**: Render fornece URL completa
4. **Confirme se o banco está ativo**
5. **Verifique perfil**: Use `SPRING_PROFILES_ACTIVE=render`

### Erro Maven Wrapper
Se aparecer erro sobre `mvnw` ou `.mvn/`:
1. **Normal**: Projeto não usa Maven Wrapper
2. **Solução**: Dockerfile usa Maven diretamente
3. **Verificação**: Confirme que `pom.xml` existe

### Performance Lenta
1. **Considere upgrade do plano** (plano grátis tem limitações)
2. **Otimize queries SQL**
3. **Configure cache** se necessário

## 💰 Custos

### Plano Gratuito
- **Web Service**: $0/mês (com limitações)
- **PostgreSQL**: $0/mês (512MB)
- **Limitações**: 
  - Aplicação hiberna após 15min de inatividade
  - 750 horas/mês de uso
  - Bandwidth limitado

### Planos Pagos
- **Starter**: $7/mês por serviço
- **Standard**: $25/mês por serviço
- **Pro**: $85/mês por serviço

## 🔐 Segurança

### Boas Práticas Implementadas
- ✅ Container roda com usuário não-root
- ✅ JWT secret via variável de ambiente
- ✅ Credenciais de banco via variáveis
- ✅ HTTPS automático no Render
- ✅ Health checks configurados

### Recomendações Adicionais
- 🔄 Rotacionar JWT secret regularmente
- 🔒 Usar senhas fortes para banco
- 📊 Monitorar logs de segurança
- 🚫 Não commitar secrets no código

## 📞 Suporte

### Render
- 📖 [Documentação](https://render.com/docs)
- 💬 [Community](https://community.render.com)
- 📧 [Support](https://render.com/support)

### Projeto
- 🐛 Issues no GitHub
- 📝 Documentação do projeto
- 📊 Collection Postman para testes

---

🎉 **Deploy concluído!** Sua API estará disponível para integração com o frontend em poucos minutos!
