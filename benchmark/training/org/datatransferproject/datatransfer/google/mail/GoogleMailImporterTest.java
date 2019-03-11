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


import Labels.Create;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.Gmail.Users;
import com.google.api.services.gmail.Gmail.Users.Labels;
import com.google.api.services.gmail.Gmail.Users.Messages;
import com.google.api.services.gmail.Gmail.Users.Messages.Insert;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.datatransferproject.datatransfer.google.common.GoogleCredentialFactory;
import org.datatransferproject.spi.cloud.storage.JobStore;
import org.datatransferproject.spi.transfer.provider.ImportResult;
import org.datatransferproject.types.common.models.mail.MailContainerResource;
import org.datatransferproject.types.common.models.mail.MailMessageModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;


@RunWith(MockitoJUnitRunner.class)
public class GoogleMailImporterTest {
    private static final UUID JOB_ID = UUID.randomUUID();

    private static final String MESSAGE_RAW = "message content";

    private static final String LABEL1 = "label1";

    private static final String LABEL2 = "label2";

    private static final List<String> MESSAGE_LABELS = ImmutableList.of(GoogleMailImporterTest.LABEL1, GoogleMailImporterTest.LABEL2);

    private static final MailMessageModel MESSAGE_MODEL = new MailMessageModel(GoogleMailImporterTest.MESSAGE_RAW, GoogleMailImporterTest.MESSAGE_LABELS);

    @Mock
    private Gmail gmail;

    @Mock
    private Users users;

    @Mock
    private Messages messages;

    @Mock
    private Insert insert;

    @Mock
    private Labels labels;

    @Mock
    private List labelsList;

    @Mock
    private Create labelsCreate;

    @Mock
    private GoogleCredentialFactory googleCredentialFactory;

    private JobStore jobStore;

    private ListLabelsResponse labelsListResponse;

    private GoogleMailImporter googleMailImporter;

    @Test
    public void importMessage() throws IOException {
        MailContainerResource resource = new MailContainerResource(null, Collections.singletonList(GoogleMailImporterTest.MESSAGE_MODEL));
        ImportResult result = googleMailImporter.importItem(GoogleMailImporterTest.JOB_ID, null, resource);
        // Getting list of labels from Google
        Mockito.verify(labelsList).execute();
        // Importing message
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(messages).insert(eq(GoogleMailImporter.USER), messageArgumentCaptor.capture());
        assertThat(messageArgumentCaptor.getValue().getRaw()).isEqualTo(GoogleMailImporterTest.MESSAGE_RAW);
        // TODO(olsona): test labels
    }
}

