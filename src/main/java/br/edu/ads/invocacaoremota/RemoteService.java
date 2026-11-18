package br.edu.ads.invocacaoremota;

import java.nio.charset.StandardCharsets;

/** Example service. Arguments and results cross the network as byte arrays. */
final class RemoteService {
    static final int ECO = 1;
    static final int MAIUSCULO = 2;
    static final int INVERSO = 3;

    byte[] invoke(int operationId, byte[] arguments) throws InvocationException {
        String value = new String(arguments, StandardCharsets.UTF_8);
        String result = switch (operationId) {
            case ECO -> value;
            case MAIUSCULO -> value.toUpperCase();
            case INVERSO -> new StringBuilder(value).reverse().toString();
            default -> throw new InvocationException("Operacao inexistente: " + operationId);
        };
        return result.getBytes(StandardCharsets.UTF_8);
    }

    static final class InvocationException extends Exception {
        private static final long serialVersionUID = 1L;
        InvocationException(String message) { super(message); }
    }
}
