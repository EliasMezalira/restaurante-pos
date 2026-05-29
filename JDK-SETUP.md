# 🔧 Instalação do JDK - Guia de Configuração

## ⚠️ Problema Atual

Seu sistema tem apenas uma **JRE (Java Runtime Environment)** instalada. Para executar os testes e compilar o projeto, você precisa de um **JDK (Java Development Kit)**.

### Diferença
- **JRE**: Apenas para executar aplicações Java (tem `java`)
- **JDK**: Para desenvolvimento (tem `java` + `javac` + ferramentas)

---

## 🚀 Opção 1: Instalar Eclipse Temurin OpenJDK (Recomendado)

### Windows

1. Baixe o instalador: https://adoptium.net/
2. Escolha **Java 8 LTS** ou **Java 21 LTS**
3. Baixe a versão **JDK** (não JRE)
4. Siga o instalador
5. Reinicie o PowerShell/Terminal

### Verificar instalação
```powershell
javac -version
java -version
```

Ambos devem retornar versões (não erro).

---

## 🚀 Opção 2: Instalar via Chocolatey

Se você tem **Chocolatey** instalado:

```powershell
choco install openjdk -y
```

---

## 🚀 Opção 3: Instalar via Scoop

Se você tem **Scoop** instalado:

```powershell
scoop install openjdk
```

---

## 🔍 Configurar JAVA_HOME (Opcional)

Se o JDK foi instalado mas o Maven ainda não encontra:

### Windows

1. Abra **Variáveis de Ambiente** (Environment Variables)
2. Clique em **Variáveis de Ambiente**
3. Clique **Novo** na seção "Variáveis de Usuário"
4. Nome: `JAVA_HOME`
5. Valor: `C:\Program Files\Eclipse Adoptium\jdk-8.0.xxx` (ajuste o caminho)
6. Clique OK e reinicie PowerShell

### Verificar
```powershell
$env:JAVA_HOME
javac -version
```

---

## ✅ Após Instalar o JDK

### Executar testes
```bash
./mvnw test
```

### Executar apenas a aplicação (sem testes)
```bash
./mvnw clean quarkus:dev -DskipTests
```

### Executar testes específicos
```bash
./mvnw test -Dtest=UserServiceTest
./mvnw test -Dtest=UserControllerTest
./mvnw test -Dtest=UsuarioRepositoryTest
```

---

## 🐛 Troubleshooting

### Erro: "No compiler is provided"
```bash
# Solução: Instale JDK (não apenas JRE)
# Ou execute com profile skip-tests
./mvnw package -Pskip-tests
```

### Erro: "javac not found"
```powershell
# Reinicie PowerShell/Terminal
# Ou defina JAVA_HOME manualmente
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-8.0.xxx"
```

### Maven não encontra java
```bash
# Verifique se JAVA_HOME está configurado
echo $env:JAVA_HOME

# Se vazio, defina manualmente
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-8.0.xxx"
```

---

## 📝 Próximos Passos

Após instalar o JDK:

1. **Limpar build anterior**
   ```bash
   ./mvnw clean
   ```

2. **Rodar testes**
   ```bash
   ./mvnw test
   ```

3. **Rodar aplicação**
   ```bash
   ./mvnw quarkus:dev
   ```

---

## 📚 Links Úteis

- **Eclipse Adoptium**: https://adoptium.net/
- **OpenJDK**: https://openjdk.java.net/
- **Oracle JDK**: https://www.oracle.com/java/technologies/downloads/
- **Chocolatey OpenJDK**: https://community.chocolatey.org/packages/openjdk
- **Scoop**: https://scoop.sh/

---

## 💡 Dica: Verificar Versões

```powershell
# Listar tudo relacionado a Java
java -version
javac -version
$env:JAVA_HOME
$env:PATH | Select-String -Pattern "Java"
```

---

**Após instalar o JDK, rode `./mvnw test` para executar a suíte de testes completa (66 testes).**
