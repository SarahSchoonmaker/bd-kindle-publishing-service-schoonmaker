package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatus;

import javax.inject.Inject;
import javax.xml.catalog.Catalog;

public class BookPublishTask implements Runnable {
    BookPublishRequestManager manager;
    PublishingStatusDao publishingStatusDao;
    CatalogDao catalogDao;

    @Inject
    public BookPublishTask(BookPublishRequestManager manager, PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.manager = manager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

    @Override
    public void run() {
        BookPublishRequest request = manager.getBookPublishRequestToProcess();
        if (request == null) return;

        publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(), PublishingRecordStatus.IN_PROGRESS, request.getBookId());

        try {
            CatalogItemVersion item = catalogDao.createOrUpdateBook(KindleFormatConverter.format(request));
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(), PublishingRecordStatus.SUCCESSFUL, item.getBookId());
        } catch (BookNotFoundException e) {
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId(), PublishingRecordStatus.FAILED, request.getBookId());
        }
    }
}

