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
package org.datatransferproject.datatransfer.google.calendar;


import Calendar.Calendars;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
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
import org.datatransferproject.types.common.models.ContainerResource;
import org.datatransferproject.types.common.models.IdOnlyContainerResource;
import org.datatransferproject.types.common.models.calendar.CalendarContainerResource;
import org.datatransferproject.types.common.models.calendar.CalendarEventModel;
import org.datatransferproject.types.common.models.calendar.CalendarModel;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class GoogleCalendarExporterTest {
    private static final UUID JOB_ID = UUID.fromString("9b969983-a09b-4cb0-8017-7daae758126b");

    private static final String CALENDAR_ID = "calendar_id";

    private static final CalendarListEntry CALENDAR_LIST_ENTRY = new CalendarListEntry().setId(GoogleCalendarExporterTest.CALENDAR_ID);

    private static final String EVENT_DESCRIPTION = "event_description";

    private static final Event EVENT = new Event().setDescription(GoogleCalendarExporterTest.EVENT_DESCRIPTION);

    private static final String NEXT_TOKEN = "next_token";

    private GoogleCredentialFactory credentialFactory;

    private GoogleCalendarExporter googleCalendarExporter;

    private Calendar calendarClient;

    private Calendars calendarCalendars;

    private CalendarList calendarCalendarList;

    private List calendarListRequest;

    private CalendarList calendarListResponse;

    private Events calendarEvents;

    private List eventListRequest;

    private Events eventListResponse;

    @Test
    public void exportCalendarFirstSet() throws IOException {
        setUpSingleCalendarResponse();
        // Looking at first page, with at least one page after it
        calendarListResponse.setNextPageToken(GoogleCalendarExporterTest.NEXT_TOKEN);
        // Run test
        ExportResult<CalendarContainerResource> result = googleCalendarExporter.export(GoogleCalendarExporterTest.JOB_ID, null, Optional.empty());
        // Check results
        // Verify correct methods were called
        Mockito.verify(calendarClient).calendarList();
        Mockito.verify(calendarCalendarList).list();
        Mockito.verify(calendarListRequest).execute();
        // Check pagination token
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken.getToken()).isEqualTo(((CALENDAR_TOKEN_PREFIX) + (GoogleCalendarExporterTest.NEXT_TOKEN)));
        // Check calendars
        Collection<CalendarModel> actualCalendars = result.getExportedData().getCalendars();
        assertThat(actualCalendars.stream().map(CalendarModel::getId).collect(Collectors.toList())).containsExactly(GoogleCalendarExporterTest.CALENDAR_ID);
        // Check events (should be empty, even though there is an event in the calendar)
        Collection<CalendarEventModel> actualEvents = result.getExportedData().getEvents();
        assertThat(actualEvents).isEmpty();
        // Should be one container in the resource list
        List<ContainerResource> actualResources = continuationData.getContainerResources();
        assertThat(actualResources.stream().map(( a) -> ((IdOnlyContainerResource) (a)).getId()).collect(Collectors.toList())).containsExactly(GoogleCalendarExporterTest.CALENDAR_ID);
    }

    @Test
    public void exportCalendarSubsequentSet() throws IOException {
        setUpSingleCalendarResponse();
        // Looking at subsequent page, with no page after it
        PaginationData paginationData = new StringPaginationToken(((CALENDAR_TOKEN_PREFIX) + (GoogleCalendarExporterTest.NEXT_TOKEN)));
        ExportInformation exportInformation = new ExportInformation(paginationData, null);
        calendarListResponse.setNextPageToken(null);
        // Run test
        ExportResult<CalendarContainerResource> result = googleCalendarExporter.export(UUID.randomUUID(), null, Optional.of(exportInformation));
        // Check results
        // Verify correct calls were made
        InOrder inOrder = Mockito.inOrder(calendarListRequest);
        inOrder.verify(calendarListRequest).setPageToken(GoogleCalendarExporterTest.NEXT_TOKEN);
        inOrder.verify(calendarListRequest).execute();
        // Check pagination token
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken).isNull();
    }

    @Test
    public void exportEventFirstSet() throws IOException {
        setUpSingleEventResponse();
        // Looking at first page, with at least one page after it
        ContainerResource containerResource = new IdOnlyContainerResource(GoogleCalendarExporterTest.CALENDAR_ID);
        ExportInformation exportInformation = new ExportInformation(null, containerResource);
        eventListResponse.setNextPageToken(GoogleCalendarExporterTest.NEXT_TOKEN);
        // Run test
        ExportResult<CalendarContainerResource> result = googleCalendarExporter.export(UUID.randomUUID(), null, Optional.of(exportInformation));
        // Check results
        // Verify correct methods were called
        Mockito.verify(calendarEvents).list(GoogleCalendarExporterTest.CALENDAR_ID);
        Mockito.verify(eventListRequest).setMaxAttendees(MAX_ATTENDEES);
        Mockito.verify(eventListRequest).execute();
        // Check events
        Collection<CalendarEventModel> actualEvents = result.getExportedData().getEvents();
        assertThat(actualEvents.stream().map(CalendarEventModel::getCalendarId).collect(Collectors.toList())).containsExactly(GoogleCalendarExporterTest.CALENDAR_ID);
        assertThat(actualEvents.stream().map(CalendarEventModel::getTitle).collect(Collectors.toList())).containsExactly(GoogleCalendarExporterTest.EVENT_DESCRIPTION);
        // Check pagination token
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken.getToken()).isEqualTo(((EVENT_TOKEN_PREFIX) + (GoogleCalendarExporterTest.NEXT_TOKEN)));
    }

    @Test
    public void exportEventSubsequentSet() throws IOException {
        setUpSingleEventResponse();
        // Looking at subsequent page, with no pages after it
        ContainerResource containerResource = new IdOnlyContainerResource(GoogleCalendarExporterTest.CALENDAR_ID);
        PaginationData paginationData = new StringPaginationToken(((EVENT_TOKEN_PREFIX) + (GoogleCalendarExporterTest.NEXT_TOKEN)));
        ExportInformation exportInformation = new ExportInformation(paginationData, containerResource);
        eventListResponse.setNextPageToken(null);
        // Run test
        ExportResult<CalendarContainerResource> result = googleCalendarExporter.export(UUID.randomUUID(), null, Optional.of(exportInformation));
        // Check results
        // Verify correct methods were called in order
        InOrder inOrder = Mockito.inOrder(eventListRequest);
        inOrder.verify(eventListRequest).setPageToken(GoogleCalendarExporterTest.NEXT_TOKEN);
        inOrder.verify(eventListRequest).execute();
        // Check pagination token
        ContinuationData continuationData = ((ContinuationData) (result.getContinuationData()));
        StringPaginationToken paginationToken = ((StringPaginationToken) (continuationData.getPaginationData()));
        assertThat(paginationToken).isNull();
    }
}

