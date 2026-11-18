package br.edu.ads.invocacaoremota;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/** Server primitives: getRequest() receives and sendReply() completes the request-reply cycle. */
public final class UdpInvocationServer implements AutoCloseable {
    private static final int MAX_DATAGRAM_SIZE = 8_192;
    private final DatagramSocket socket;
    private volatile boolean running = true;

    public UdpInvocationServer(int port) throws SocketException { socket = new DatagramSocket(port); }
    public int port() { return socket.getLocalPort(); }

    public ReceivedRequest getRequest() throws IOException, Protocol.ProtocolException {
        DatagramPacket packet = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);
        socket.receive(packet);
        Protocol.Request request = Protocol.parseRequest(packet.getData(), packet.getLength());
        return new ReceivedRequest(request, packet.getAddress(), packet.getPort());
    }

    public void sendReply(ReceivedRequest request, byte[] result) throws IOException {
        byte[] response = Protocol.response(request.request().requestId(), result);
        socket.send(new DatagramPacket(response, response.length, request.clientHost(), request.clientPort()));
    }

    public void serve() throws IOException {
        RemoteService service = new RemoteService();
        while (running) try {
            ReceivedRequest request = getRequest();
            byte[] result;
            try { result = service.invoke(request.request().operationId(), request.request().arguments()); }
            catch (RemoteService.InvocationException e) { result = ("ERRO: " + e.getMessage()).getBytes(java.nio.charset.StandardCharsets.UTF_8); }
            sendReply(request, result);
        } catch (SocketException e) { if (running) throw e; }
          catch (Protocol.ProtocolException e) { /* malformed datagram is discarded */ }
    }

    @Override public void close() { running = false; socket.close(); }
    public record ReceivedRequest(Protocol.Request request, java.net.InetAddress clientHost, int clientPort) { }
}
