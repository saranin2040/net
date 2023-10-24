package ru.nsu.ekazakova.dns;


import java.util.concurrent.BlockingQueue;

public interface DNSQueueStrategy {
    void appendResolveRequest( BlockingQueue<DNSResolveRequest> queue,  DNSResolveRequest dnsResolveRequest);

    DNSResolveRequest takeResolveRequest( BlockingQueue<DNSResolveRequest> queue);
}
