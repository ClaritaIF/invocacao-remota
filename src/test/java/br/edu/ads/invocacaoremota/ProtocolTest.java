package br.edu.ads.invocacaoremota;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** Dependency-free checks for message framing and operation dispatch. */
public final class ProtocolTest {
    public static void main(String[] args) throws Exception {
        byte[] bytes = "ola|mundo".getBytes(StandardCharsets.UTF_8);
        byte[] message = Protocol.request(7L, RemoteService.MAIUSCULO, bytes);
        Protocol.Request request = Protocol.parseRequest(message, message.length);
        if (request.requestId() != 7L || request.operationId() != RemoteService.MAIUSCULO
                || !Arrays.equals(bytes, request.arguments())) throw new AssertionError("Requisicao invalida");
        RemoteService service = new RemoteService();
        String result = new String(service.invoke(request.operationId(), request.arguments()), StandardCharsets.UTF_8);
        if (!"OLA|MUNDO".equals(result)) throw new AssertionError("Resultado invalido: " + result);
        byte[] reply = Protocol.response(request.requestId(), result.getBytes(StandardCharsets.UTF_8));
        Protocol.Response parsedReply = Protocol.parseResponse(reply, reply.length);
        if (parsedReply.requestId() != 7L) throw new AssertionError("Correlacao invalida");
        System.out.println("Testes de protocolo passaram.");
    }
}
