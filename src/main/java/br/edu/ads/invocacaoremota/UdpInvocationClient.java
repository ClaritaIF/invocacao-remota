package br.edu.ads.invocacaoremota;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicLong;

/** Client primitive: doOperation(remoteRef, operationId, arguments). */
public final class UdpInvocationClient {
    private static final int MAX_DATAGRAM_SIZE = 8_192;
    private static final AtomicLong NEXT_REQUEST_ID = new AtomicLong();
    private final int timeoutMillis;

    public UdpInvocationClient(int timeoutMillis) { this.timeoutMillis = timeoutMillis; }

    public byte[] doOperation(RemoteRef remoteRef, int operationId, byte[] arguments)
            throws IOException, RemoteInvocationException {
        long requestId = NEXT_REQUEST_ID.incrementAndGet();
        byte[] request = Protocol.request(requestId, operationId, arguments);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeoutMillis);
            socket.send(new DatagramPacket(request, request.length, remoteRef.host(), remoteRef.port()));
            DatagramPacket responsePacket = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);
            try { socket.receive(responsePacket); } catch (SocketTimeoutException e) {
                throw new RemoteInvocationException("Tempo de espera excedido");
            }
            try {
                Protocol.Response response = Protocol.parseResponse(responsePacket.getData(), responsePacket.getLength());
                if (response.requestId() != requestId) throw new RemoteInvocationException("Resposta para outra requisicao");
                return response.result();
            } catch (Protocol.ProtocolException e) { throw new RemoteInvocationException(e.getMessage()); }
        }
    }

    public static final class RemoteInvocationException extends Exception {
        private static final long serialVersionUID = 1L;
        RemoteInvocationException(String message) { super(message); }
    }
}
