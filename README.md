# Cliente e servidor UDP
Aplicacao Java simples de requisicao-resposta usando DatagramSocket.
## Formato da mensagem
`COMANDO` ou `COMANDO|parametro opcional`.

Comandos aceitos:
- `PING` responde `PONG`.
- `ECHO|texto` devolve o texto.
- `UPPER|texto` devolve maiusculas.
- `REVERSE|texto` devolve o texto invertido.
- `HELP` lista os comandos.

Um comando desconhecido responde `ERRO: comando desconhecido`.
## Compilar
Requer JDK 21 ou superior.
```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src\main\java,src\test\java | ForEach-Object FullName)
```
## Executar
Inicie o servidor:
```powershell
java -cp out br.edu.ads.invocacaoremota.UdpServer 5000
```
Em outro terminal, envie uma requisicao:
```powershell
java -cp out br.edu.ads.invocacaoremota.UdpClient 127.0.0.1 5000 PING
# Saida: PONG
java -cp out br.edu.ads.invocacaoremota.UdpClient 127.0.0.1 5000 ECHO "Ola servidor"
# Saida: Ola servidor
```
O cliente aguarda dois segundos pela resposta.
## Teste
```powershell
java -cp out br.edu.ads.invocacaoremota.UdpServerTest
```
Saida esperada: `Testes dos comandos passaram.`
