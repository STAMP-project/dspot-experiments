/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.google.calendar;


import com.google.api.services.calendar.model.Acl;
import org.apache.camel.component.google.calendar.internal.CalendarAclApiMethod;
import org.apache.camel.component.google.calendar.internal.GoogleCalendarApiCollection;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link com.google.api.services.calendar.Calendar$Acl} APIs.
 */
public class CalendarAclIntegrationTest extends AbstractGoogleCalendarTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarAclIntegrationTest.class);

    private static final String PATH_PREFIX = GoogleCalendarApiCollection.getCollection().getApiName(CalendarAclApiMethod.class).getName();

    @Test
    public void testList() throws Exception {
        // using String message body for single parameter "calendarId"
        final Acl result = requestBody("direct://LIST", getCalendar().getId());
        // should have at least one rule (reader, owner, etc.) for the calendar
        // or we wouldn't be able to view it!
        assertTrue(((result.getItems().size()) > 0));
        CalendarAclIntegrationTest.LOG.debug(("list: " + result));
    }
}

