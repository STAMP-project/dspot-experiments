/**
 * Copyright 2018 The Data Transfer Project Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datatransferproject.datatransfer.google.mail;


import GoogleMailExporter.PAGE_SIZE;
import GoogleMailExporter.USER;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.Gmail.Users;
import com.google.api.services.gmail.Gmail.Users.Messages;
import com.google.api.services.gmail.Gmail.Users.Messages.Get;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.datatransferproject.datatransfer.google.common.GoogleCredentialFactory;
import org.datatransferproject.spi.transfer.provider.ExportResult;
import org.datatransferproject.spi.transfer.types.ContinuationData;
import org.datatransferproject.types.common.ExportInformation;
import org.datatransferproject.types.common.PaginationData;
import org.datatransferproject.types.common.StringPaginationToken;
import org.datatransferproject.types.common.models.mail.MailContainerResource;
import org.datatransferproject.types.common.models.mail.MailMessageModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GoogleMailExporterTest {
    private static final UUID JOB_ID = UUID.randomUUID();

    private static final String NEXT_TOKEN = "next_token";

    private static final String MESSAGE_ID = "messageId";

    private static final String MESSAGE_RAW = "message contents";

    private static final List<String> MESSAGE_LABELS = ImmutableList.of("label1", "label2");

    private static final Message INITIAL_MESSAGE = new Message().setId(GoogleMailExporterTest.MESSAGE_ID);

    private static final Message FULL_MESSAGE = new Message().setId(GoogleMailExporterTest.MESSAGE_ID).setRaw(GoogleMailExporterTest.MESSAGE_RAW).setLabelIds(GoogleMailExporterTest.MESSAGE_LABELS);

    @Mock
    private Users users;

    @Mock
    private Messages messages;

    @Mock
    private List messageListRequest;

    @Mock
    private Get get;

    @Mock
    private Gmail gmail;

    @Mock
    private GoogleCredentialFactory googleCredentialFactory;

    private ListMessagesResponse messageListResponse;

    private GoogleMailExporter googleMailExporter;

    @Test
    public void exportMessagesFirstSet() throws IOException {
        setUpSingleMessageResponse();
        // Looking at first page, with at least one page after it
        messageListResponse.setNextPageToken(GoogleMailExporterTest.NEXT_TOKEN);
        // Run test
        ExportResult<MailContainerResource> result = googleMailExporter.export(GoogleMailExporterTest.JOB_ID, null, Optional.empty());
        // Check results
        // Verify correct methods were called
        InOrder inOrder = Mockito.inOrder(messages, messageListRequest, get);
        // First request
        inOrder.verify(messages).list(USER);
        inOrder.verify(messageListRequest).setMaxResults(PAGE_SIZE);
        Mockito.verify(messageListRequest, Mockito.never()).setPageToken(Matchers.anyString());
        // Second request
        inOrder.verify(messages).get(USER, GoogleMailExporterTest.MESSAGE_ID);
        inOrder.verify(get).setFormat("raw");
        inOrder.verify(get).execute();
        // Check pagination token
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken.getToken()).isEqualTo(GoogleMailExporterTest.NEXT_TOKEN);
        // Check messages
        Collection<MailMessageModel> actualMail = result.getExportedData().getMessages();
        assertThat(actualMail.stream().map(MailMessageModel::getRawString).collect(Collectors.toList())).containsExactly(GoogleMailExporterTest.MESSAGE_RAW);
        assertThat(actualMail.stream().map(MailMessageModel::getContainerIds).collect(Collectors.toList())).containsExactly(GoogleMailExporterTest.MESSAGE_LABELS);
    }

    @Test
    public void exportMessagesSubsequentSet() throws IOException {
        setUpSingleMessageResponse();
        // Looking at subsequent page, with no page after it
        PaginationData paginationData = new StringPaginationToken(GoogleMailExporterTest.NEXT_TOKEN);
        ExportInformation exportInformation = new ExportInformation(paginationData, null);
        messageListResponse.setNextPageToken(null);
        // Run test
        ExportResult<MailContainerResource> result = googleMailExporter.export(GoogleMailExporterTest.JOB_ID, null, Optional.of(exportInformation));
        // Check results
        // Verify correct calls were made (i.e., token was set before execution)
        InOrder inOrder = Mockito.inOrder(messageListRequest);
        inOrder.verify(messageListRequest).setPageToken(GoogleMailExporterTest.NEXT_TOKEN);
        inOrder.verify(messageListRequest).execute();
        // Check pagination token (should be null)
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken).isNull();
    }
}

