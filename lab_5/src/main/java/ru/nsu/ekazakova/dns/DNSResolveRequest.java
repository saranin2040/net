package ru.nsu.ekazakova.dns;

import ru.nsu.ekazakova.attachment.ClientHandler;

public class DNSResolveRequest {
    private final String hostToResolve;
    private final ClientHandler clientHandler;

    public DNSResolveRequest( String hostToResolve,  ClientHandler clientHandler) {
        this.hostToResolve = hostToResolve;
        this.clientHandler = clientHandler;
    }

    public String getHostToResolve() {
        return this.hostToResolve;
    }

    public ClientHandler getClientHandler() {
        return this.clientHandler;
    }
}