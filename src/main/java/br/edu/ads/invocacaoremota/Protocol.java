package br.edu.ads.invocacaoremota;

import java.nio.ByteBuffer;

/** Binary request-reply protocol carried by one UDP datagram per message. */
final class Protocol {
    static final byte REQUEST = 1;
    static final byte RESPONSE = 2;
    private static final int REQUEST_HEADER_SIZE = 17;
    private static final int RESPONSE_HEADER_SIZE = 13;

    private Protocol() { }

    static byte[] request(long requestId, int operationId, byte[] arguments) {
        ByteBuffer buffer = ByteBuffer.allocate(REQUEST_HEADER_SIZE + arguments.length);
        buffer.put(REQUEST).putLong(requestId).putInt(operationId).putInt(arguments.length).put(arguments);
        return buffer.array();
    }

    static byte[] response(long requestId, byte[] result) {
        ByteBuffer buffer = ByteBuffer.allocate(RESPONSE_HEADER_SIZE + result.length);
        buffer.put(RESPONSE).putLong(requestId).putInt(result.length).put(result);
        return buffer.array();
    }

    static Request parseRequest(byte[] data, int length) throws ProtocolException {
        if (length < REQUEST_HEADER_SIZE) throw new ProtocolException("Requisicao menor que o cabecalho");
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
        if (buffer.get() != REQUEST) throw new ProtocolException("Tipo de mensagem invalido");
        long requestId = buffer.getLong();
        int operationId = buffer.getInt();
        int argumentLength = buffer.getInt();
        if (argumentLength < 0 || argumentLength != buffer.remaining()) throw new ProtocolException("Tamanho de argumentos invalido");
        byte[] arguments = new byte[argumentLength];
        buffer.get(arguments);
        return new Request(requestId, operationId, arguments);
    }

    static Response parseResponse(byte[] data, int length) throws ProtocolException {
        if (length < RESPONSE_HEADER_SIZE) throw new ProtocolException("Resposta menor que o cabecalho");
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
        if (buffer.get() != RESPONSE) throw new ProtocolException("Tipo de mensagem invalido");
        long requestId = buffer.getLong();
        int resultLength = buffer.getInt();
        if (resultLength < 0 || resultLength != buffer.remaining()) throw new ProtocolException("Tamanho de resposta invalido");
        byte[] result = new byte[resultLength];
        buffer.get(result);
        return new Response(requestId, result);
    }

    record Request(long requestId, int operationId, byte[] arguments) { }
    record Response(long requestId, byte[] result) { }

    static final class ProtocolException extends Exception {
        private static final long serialVersionUID = 1L;
        ProtocolException(String message) { super(message); }
    }
}
