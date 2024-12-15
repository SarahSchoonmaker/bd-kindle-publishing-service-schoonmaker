package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression<CatalogItemVersion>()
            .withHashKeyValues(book)
            .withScanIndexForward(false) // returns items in descending version order
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    // Marks an existing book as inactive
    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        CatalogItemVersion book = getBookFromCatalog(bookId);

        if (!book.isInactive()) {
            book.setInactive(true);
            dynamoDbMapper.save(book);
        }
        return book;
    }

    // Checks if the given bookId exists in the system
    public CatalogItemVersion validateBookExists(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
        return book;
    }

    // Create, Save and Return a new Book Entry ()
    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook book) {
        CatalogItemVersion item = new CatalogItemVersion();
        item.setBookId(book.getBookId()); // TO BE DETERMINED
        item.setInactive(false);
        item.setTitle(book.getTitle());
        item.setAuthor(book.getAuthor());
        item.setText(book.getText());
        item.setGenre(book.getGenre());

        // if the book exists, then we update the book to the next version
        // if not found, we throw an exception - it's built in
        // otherwise - we generate a new bookId and start w/ version 1
        if (book.getBookId() != null && !book.getBookId().isEmpty()) {
            CatalogItemVersion removedBook = removeBookFromCatalog(book.getBookId());
            item.setVersion(removedBook.getVersion() + 1);
        } else {
            item.setBookId(KindlePublishingUtils.generateBookId());
            item.setVersion(1);
        }

        // save the updated version
        dynamoDbMapper.save(item);
        return item;
    }
}
