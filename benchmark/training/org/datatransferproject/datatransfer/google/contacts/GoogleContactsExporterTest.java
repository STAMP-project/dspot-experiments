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
package org.datatransferproject.datatransfer.google.contacts;


import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleService.People;
import com.google.api.services.people.v1.PeopleService.People.Connections;
import com.google.api.services.people.v1.PeopleService.People.GetBatchGet;
import com.google.api.services.people.v1.model.FieldMetadata;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.Source;
import ezvcard.VCard;
import ezvcard.io.json.JCardReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.datatransferproject.spi.transfer.provider.ExportResult;
import org.datatransferproject.spi.transfer.types.ContinuationData;
import org.datatransferproject.types.common.ExportInformation;
import org.datatransferproject.types.common.PaginationData;
import org.datatransferproject.types.common.StringPaginationToken;
import org.datatransferproject.types.common.models.contacts.ContactsModelWrapper;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class GoogleContactsExporterTest {
    private static final String RESOURCE_NAME = "resource_name";

    private static final Source SOURCE = new Source().setType("CONTACT");

    private static final FieldMetadata PRIMARY_FIELD_METADATA = new FieldMetadata().setSource(GoogleContactsExporterTest.SOURCE).setPrimary(true);

    private static final Name NAME = new Name().setFamilyName("Turing").setGivenName("Alan").setMetadata(GoogleContactsExporterTest.PRIMARY_FIELD_METADATA);

    private static final Person PERSON = new Person().setNames(Collections.singletonList(GoogleContactsExporterTest.NAME)).setResourceName(GoogleContactsExporterTest.RESOURCE_NAME);

    private static final String NEXT_PAGE_TOKEN = "nextPageToken";

    private PeopleService peopleService;

    private GoogleContactsExporter contactsService;

    private People people;

    private Connections connections;

    private List<Person> connectionsList;

    private GetBatchGet getBatchGet;

    private List listConnectionsRequest;

    private ListConnectionsResponse listConnectionsResponse;

    @Test
    public void exportFirstPage() throws IOException {
        setUpSinglePersonResponse();
        // Looking at first page, with at least one page after it
        listConnectionsResponse.setNextPageToken(GoogleContactsExporterTest.NEXT_PAGE_TOKEN);
        ExportResult<ContactsModelWrapper> result = contactsService.export(UUID.randomUUID(), null, Optional.empty());
        // Check that correct methods were called
        Mockito.verify(connections).list(SELF_RESOURCE);
        InOrder inOrder = Mockito.inOrder(getBatchGet);
        inOrder.verify(getBatchGet).setResourceNames(Collections.singletonList(GoogleContactsExporterTest.RESOURCE_NAME));
        inOrder.verify(getBatchGet).setPersonFields(PERSON_FIELDS);
        inOrder.verify(getBatchGet).execute();
        // Check continuation data
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        assertThat(continuationData.getContainerResources()).isEmpty();
        StringPaginationToken paginationToken = ((StringPaginationToken) (getPaginationData()));
        assertThat(paginationToken.getToken()).isEqualTo(GoogleContactsExporterTest.NEXT_PAGE_TOKEN);
        // Check that the right number of VCards was returned
        JCardReader reader = new JCardReader(result.getExportedData().getVCards());
        List<VCard> vCardList = reader.readAll();
        assertThat(vCardList.size()).isEqualTo(connectionsList.size());
    }

    @Test
    public void exportSubsequentPage() throws IOException {
        setUpSinglePersonResponse();
        // Looking at a subsequent page, with no pages after it
        PaginationData paginationData = new StringPaginationToken(GoogleContactsExporterTest.NEXT_PAGE_TOKEN);
        ExportInformation exportInformation = new ExportInformation(paginationData, null);
        listConnectionsResponse.setNextPageToken(null);
        Mockito.when(listConnectionsRequest.setPageToken(GoogleContactsExporterTest.NEXT_PAGE_TOKEN)).thenReturn(listConnectionsRequest);
        // Run test
        ExportResult<ContactsModelWrapper> result = contactsService.export(UUID.randomUUID(), null, Optional.of(exportInformation));
        // Verify correct calls were made - i.e., token was added before execution
        InOrder inOrder = Mockito.inOrder(listConnectionsRequest);
        inOrder.verify(listConnectionsRequest).setPageToken(GoogleContactsExporterTest.NEXT_PAGE_TOKEN);
        inOrder.verify(listConnectionsRequest).execute();
        // Check continuation data
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        assertThat(continuationData.getContainerResources()).isEmpty();
        assertThat(continuationData.getPaginationData()).isNull();
    }
}

