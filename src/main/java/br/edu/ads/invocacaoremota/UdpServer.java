package br.edu.ads.invocacaoremota;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/** UDP server: receives COMMAND|optional-parameters and sends the command result. */
public final class UdpServer {
    private static final int BUFFER_SIZE = 1_024;

    private UdpServer() { }

    public static void main(String[] args) throws IOException {
        int port = args.length == 0 ? 5_000 : Integer.parseInt(args[0]);
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("Servidor UDP escutando na porta " + port);
            while (true) {
                DatagramPacket request = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                socket.receive(request);
                String command = new String(request.getData(), request.getOffset(), request.getLength(), StandardCharsets.UTF_8);
                String result = execute(command);
                byte[] responseBytes = result.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(responseBytes, responseBytes.length, request.getAddress(), request.getPort()));
                System.out.printf("Recebido: %s | Respondido: %s%n", command, result);
            }
        }
    }

    static String execute(String request) {
        String[] parts = request.split("\\|", 2);
        String command = parts[0].trim().toUpperCase(Locale.ROOT);
        String parameter = parts.length == 2 ? parts[1] : "";
        return switch (command) {
            case "PING" -> "PONG";
            case "ECHO" -> parameter;
            case "UPPER" -> parameter.toUpperCase(Locale.ROOT);
            case "REVERSE" -> new StringBuilder(parameter).reverse().toString();
            case "HELP" -> "Comandos: PING, ECHO|texto, UPPER|texto, REVERSE|texto";
            default -> "ERRO: comando desconhecido";
        };
    }
}
