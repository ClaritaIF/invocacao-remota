package br.edu.ads.invocacaoremota;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** UDP client: sends one command datagram and prints the server response. */
public final class UdpClient {
    private static final int BUFFER_SIZE = 1_024;
    private static final int TIMEOUT_MILLIS = 2_000;

    private UdpClient() { }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Uso: UdpClient <host> <porta> <comando> [parametro]");
            System.exit(2);
        }
        String request = args[2] + (args.length > 3 ? "|" + String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : "");
        byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);
        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MILLIS);
            socket.send(new DatagramPacket(requestBytes, requestBytes.length, host, port));
            DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            try {
                socket.receive(response);
                System.out.println(new String(response.getData(), response.getOffset(), response.getLength(), StandardCharsets.UTF_8));
            } catch (SocketTimeoutException exception) {
                System.err.println("ERRO: servidor nao respondeu em " + TIMEOUT_MILLIS + " ms");
                System.exit(1);
            }
        }
    }
}
