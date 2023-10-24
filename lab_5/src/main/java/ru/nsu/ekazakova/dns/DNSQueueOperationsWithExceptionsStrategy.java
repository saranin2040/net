package ru.nsu.ekazakova.dns;


import java.util.concurrent.BlockingQueue;

public class DNSQueueOperationsWithExceptionsStrategy implements DNSQueueStrategy {
    @Override
    public void appendResolveRequest( BlockingQueue<DNSResolveRequest> queue,  DNSResolveRequest dnsResolveRequest) {
        queue.add(dnsResolveRequest);
    }

    @Override
    public DNSResolveRequest takeResolveRequest( BlockingQueue<DNSResolveRequest> queue) {
        if (queue.isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread was interrupted while waiting take request from queue", e);
        }
    }
}