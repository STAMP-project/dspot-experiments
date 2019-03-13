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
import Calendar.Calendars.Insert;
import Calendar.Events;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.datatransferproject.datatransfer.google.common.GoogleCredentialFactory;
import org.datatransferproject.spi.cloud.storage.JobStore;
import org.datatransferproject.spi.transfer.types.TempCalendarData;
import org.datatransferproject.types.common.models.calendar.CalendarContainerResource;
import org.datatransferproject.types.common.models.calendar.CalendarEventModel;
import org.datatransferproject.types.common.models.calendar.CalendarModel;
import org.junit.Test;
import org.mockito.Mockito;


public class GoogleCalendarImporterTest {
    private static final String CALENDAR_ID = "calendar_id";

    private static final CalendarListEntry CALENDAR_LIST_ENTRY = new CalendarListEntry().setId(GoogleCalendarImporterTest.CALENDAR_ID);

    private static final String EVENT_DESCRIPTION = "event_description";

    private static final Event EVENT = new Event().setDescription(GoogleCalendarImporterTest.EVENT_DESCRIPTION);

    private GoogleCalendarImporter calendarService;

    private JobStore jobStore;

    private GoogleCredentialFactory credentialFactory;

    private Calendar calendarClient;

    private Calendars calendarCalendars;

    private Insert calendarInsertRequest;

    private Events calendarEvents;

    private Calendar.Events.Insert eventInsertRequest;

    @Test
    public void importCalendarAndEvent() throws IOException {
        String modelCalendarId = "modelCalendarId";
        String googleCalendarId = "googleCalendarId";
        UUID jobId = UUID.randomUUID();
        // Set up calendar, events, and mocks
        CalendarModel calendarModel = new CalendarModel(modelCalendarId, null, null);
        com.google.api.services.calendar.model.Calendar calendarToInsert = GoogleCalendarImporter.convertToGoogleCalendar(calendarModel);
        com.google.api.services.calendar.model.Calendar responseCalendar = new com.google.api.services.calendar.model.Calendar().setId(googleCalendarId);
        CalendarEventModel eventModel = new CalendarEventModel(modelCalendarId, null, null, null, null, null, null, null);
        Event eventToInsert = GoogleCalendarImporter.convertToGoogleCalendarEvent(eventModel);
        Event responseEvent = new Event();
        Mockito.when(eventInsertRequest.execute()).thenReturn(responseEvent);
        Mockito.when(calendarEvents.insert(googleCalendarId, eventToInsert)).thenReturn(eventInsertRequest);
        Mockito.when(calendarInsertRequest.execute()).thenReturn(responseCalendar);
        Mockito.when(calendarCalendars.insert(calendarToInsert)).thenReturn(calendarInsertRequest);
        CalendarContainerResource calendarContainerResource = new CalendarContainerResource(Collections.singleton(calendarModel), Collections.singleton(eventModel));
        // Run test
        calendarService.importItem(jobId, null, calendarContainerResource);
        // Check the right methods were called
        Mockito.verify(calendarCalendars).insert(calendarToInsert);
        Mockito.verify(calendarInsertRequest).execute();
        Mockito.verify(calendarEvents).insert(googleCalendarId, eventToInsert);
        Mockito.verify(eventInsertRequest).execute();
        // Check jobStore contents
        assertThat(jobStore.findData(jobId, "tempCalendarData", TempCalendarData.class).getImportedId(modelCalendarId)).isEqualTo(googleCalendarId);
    }
}

