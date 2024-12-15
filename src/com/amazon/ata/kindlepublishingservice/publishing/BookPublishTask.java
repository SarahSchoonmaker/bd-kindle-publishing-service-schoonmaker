package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;

import javax.inject.Inject;


public class BookPublishTask implements Runnable{

    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;
    private final BookPublishRequestManager manager;

    @Inject
    public BookPublishTask(PublishingStatusDao publishingStatusDao, CatalogDao catalogDao, BookPublishRequestManager manager) {
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
        this.manager = manager;
    }

    // we all 3 to update and access entries

    /**
     * 1. Adds an entry to the Publishing Status table with state `IN_PROGRESS`
     * 2. Performs formatting and conversion of the book
     * 3. Adds the new book to the `CatalogItemVersion` table
     *     1. If this request is updating an existing book:
     *         1. The entry in `CatalogItemVersion` will use the same `bookId` but with the
     *            version incremented by 1.
     *         1. The previously active version of the book will be marked inactive.
     *     2. Otherwise, a new `bookId` is generated for the book and the book will be stored in
     *         `CatalogItemVersion` as version 1.
     * 4. Adds an item to the Publishing Status table with state `SUCCESSFUL` if all the processing steps
     *     succeed. If an exception is caught while processing, adds an item into the Publishing Status
     *     table with state `FAILED` and includes the exception message.
     */
    @Override
    public void run() {
        // Get the request from the manager
        BookPublishRequest request = manager.getBookPublishRequestToProcess();

        // Verify the request
        if (request == null) {
            return;
        }

        // check the status is correct to proceed with in progress
        // 1. Adds an entry to the Publishing Status table with state `IN_PROGRESS`
        publishingStatusDao.setPublishingStatus(request.getPublishingRecordId()
                , PublishingRecordStatus.IN_PROGRESS
                , request.getBookId());

        // 2. Performs formatting and conversion of the book
        KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(request);

        // 3. Adds the new book to the `CatalogItemVersion` table
        try {
            CatalogItemVersion item = catalogDao.createOrUpdateBook(kindleFormattedBook); // returns the item if needed
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId()
                    , PublishingRecordStatus.SUCCESSFUL
                    , item.getBookId());
        } catch (BookNotFoundException e) {
            publishingStatusDao.setPublishingStatus(request.getPublishingRecordId()
                    , PublishingRecordStatus.FAILED
                    , request.getBookId());
        }
    }
}
