# Invocacao remota de metodos com UDP

Implementacao do protocolo de middleware **requisicao-resposta** apresentado no material. A aplicacao usa `DatagramSocket` e as primitivas `doOperation`, `getRequest` e `sendReply`.

## Modelo implementado

- `RemoteRef`: endereco IP e porta do servidor remoto.
- `doOperation(RemoteRef, operationId, byte[])`: primitiva do cliente; empacota operacao e argumentos, envia a requisicao e bloqueia ate receber a resposta.
- `getRequest()`: primitiva do servidor; bloqueia aguardando uma requisicao UDP e a desempacota.
- `sendReply(request, byte[])`: envia o resultado ao endereco/porta do cliente, encerrando o ciclo requisicao-resposta.

Cada mensagem usa um vetor de bytes binario:

| Mensagem | Campos |
| --- | --- |
| Requisicao | tipo (1 byte), ID sequencial (8), ID da operacao (4), tamanho (4), argumentos (`byte[]`) |
| Resposta | tipo (1 byte), ID sequencial (8), tamanho (4), resultado (`byte[]`) |

O ID sequencial correlaciona resposta e requisicao; tamanho e tipo permitem detectar datagramas malformados. Como UDP nao garante entrega, o cliente possui timeout de espera. Uma extensao natural seria retransmitir a requisicao apos o timeout.

## Operacoes do exemplo

| ID | Operacao | Argumento/resultado |
| --- | --- | --- |
| 1 | `ECO` | devolve o texto recebido |
| 2 | `MAIUSCULO` | devolve o texto em maiusculas |
| 3 | `INVERSO` | devolve o texto invertido |

Os argumentos e resultados sao transmitidos como `byte[]`; o servico de exemplo os interpreta em UTF-8.

## Compilar e testar

Requer JDK 21+ e nenhuma dependencia externa.

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src\main\java,src\test\java | ForEach-Object FullName)
java -cp out br.edu.ads.invocacaoremota.ProtocolTest
```

Saida esperada: `Testes de protocolo passaram.`

## Uso na aplicacao

O servidor deve ser criado com `new UdpInvocationServer(5000)` e executar `serve()`. O cliente cria uma referencia e invoca:

```java
RemoteRef ref = new RemoteRef(InetAddress.getByName("127.0.0.1"), 5000);
byte[] resultado = new UdpInvocationClient(2000)
    .doOperation(ref, 2, "ola".getBytes(StandardCharsets.UTF_8));
```

O resultado sera `OLA`.
