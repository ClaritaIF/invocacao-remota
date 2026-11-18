package br.edu.ads.invocacaoremota;

import java.net.InetAddress;

/** Address and port that locate a remote server. */
public record RemoteRef(InetAddress host, int port) {
    public RemoteRef {
        if (host == null || port < 1 || port > 65_535) throw new IllegalArgumentException("Referencia remota invalida");
    }
}
