package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;

import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {

    private CatalogDao catalogDao;
    @Inject
    RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
    this.catalogDao = catalogDao;
    }
    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {

        String bookId = removeBookFromCatalogRequest.getBookId();
        catalogDao.removeBookFromCatalog(bookId);
        RemoveBookFromCatalogResponse response = new RemoveBookFromCatalogResponse();
        return response;
    }

}
