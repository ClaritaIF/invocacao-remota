package br.edu.ads.invocacaoremota;

/** Tests command interpretation independently from the network. */
public final class UdpServerTest {
    public static void main(String[] args) {
        assertEquals("PONG", UdpServer.execute("PING"));
        assertEquals("ola", UdpServer.execute("ECHO|ola"));
        assertEquals("OLA", UdpServer.execute("UPPER|ola"));
        assertEquals("alo", UdpServer.execute("REVERSE|ola"));
        assertEquals("ERRO: comando desconhecido", UdpServer.execute("INEXISTENTE"));
        System.out.println("Testes dos comandos passaram.");
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) throw new AssertionError("Esperado " + expected + ", recebido " + actual);
    }
}
