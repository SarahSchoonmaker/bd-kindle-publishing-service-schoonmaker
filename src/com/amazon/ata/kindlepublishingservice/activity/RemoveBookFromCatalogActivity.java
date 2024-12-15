package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {
    private final CatalogDao catalogDao;

    @Inject
    RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }

    /**
     * MT1: Milestone 2
     * @param removeBookFromCatalogRequest contains the book to mark as inactive
     * @return RemoveBookFromCatalogResponse
     */
    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        CatalogItemVersion book = catalogDao.removeBookFromCatalog(removeBookFromCatalogRequest.getBookId());

        return new RemoveBookFromCatalogResponse();
    }
}
