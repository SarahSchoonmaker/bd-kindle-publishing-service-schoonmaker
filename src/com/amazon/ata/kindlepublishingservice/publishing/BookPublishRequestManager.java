package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class BookPublishRequestManager {
    Queue<BookPublishRequest> queue;

    @Inject
    public BookPublishRequestManager() {
        queue = new ConcurrentLinkedQueue<>();
    }

    public void addBookPublishRequest(BookPublishRequest request) {
        queue.offer(request);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        return queue.poll();
    }
}
