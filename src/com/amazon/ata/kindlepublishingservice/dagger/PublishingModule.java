package com.amazon.ata.kindlepublishingservice.dagger;

import com.amazon.ata.kindlepublishingservice.publishing.*;

import dagger.Module;
import dagger.Provides;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Singleton;

@Module
public class PublishingModule {

    // TODO: remove the NoOpTask() with BookPublishTask - the Task will already be self injected so I don't need any more methods?
    @Provides
    @Singleton
    public BookPublisher provideBookPublisher(ScheduledExecutorService scheduledExecutorService, BookPublishTask bookPublishTask) {
        return new BookPublisher(scheduledExecutorService, bookPublishTask);
    }

    @Provides
    @Singleton
    public ScheduledExecutorService provideBookPublisherScheduler() {
        return Executors.newScheduledThreadPool(1);
    }

    @Provides
    @Singleton
    public BookPublishRequestManager provideBookPublishRequestManager() {
        return new BookPublishRequestManager(new LinkedList<BookPublishRequest>());
        // return new BookPublishRequestManager(new ConcurrentLinkedQueue<BookPublishRequest>());
    }
}
