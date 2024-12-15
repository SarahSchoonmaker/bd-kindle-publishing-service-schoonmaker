package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetPublishingStatusActivity {
    private PublishingStatusDao publishingStatusDao;

    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        // retrieve the list of items
        List<PublishingStatusItem> itemList = publishingStatusDao.getPublishingStatuses(publishingStatusRequest.getPublishingRecordId());

        List<PublishingStatusRecord> statusHistory = new ArrayList<PublishingStatusRecord>();

        for (PublishingStatusItem item: itemList) {
            statusHistory.add(PublishingStatusRecord.builder()
                    .withStatus(item.getStatus().toString())
                    .withStatusMessage(item.getStatusMessage())
                    .withBookId(item.getBookId())
                    .build());
        }

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(statusHistory)
                .build();
    }
}
