package ru.nsu.ekazakova.dns;


import java.util.concurrent.BlockingQueue;

public class DNSQueueOperationsWithBlockStrategy implements DNSQueueStrategy {
    @Override
    public void appendResolveRequest( BlockingQueue<DNSResolveRequest> queue,  DNSResolveRequest dnsResolveRequest) {
        try {
            queue.put(dnsResolveRequest);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread was interrupted while waiting put request to queue", e);
        }
    }

    @Override
    public DNSResolveRequest takeResolveRequest( BlockingQueue<DNSResolveRequest> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread was interrupted while waiting take request from queue", e);
        }
    }
}
