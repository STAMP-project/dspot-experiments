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
package org.apache.camel.component.ical;


import java.io.File;
import java.io.InputStream;
import java.util.TimeZone;
import net.fortuna.ical4j.model.Calendar;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.IOConverter;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Small unit test which verifies ical data format.
 */
public class ICalDataFormatTest extends CamelTestSupport {
    private TimeZone defaultTimeZone;

    @Test
    public void testUnmarshal() throws Exception {
        InputStream stream = IOConverter.toInputStream(new File("src/test/resources/data.ics"));
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedBodiesReceived(createTestCalendar());
        template.sendBody("direct:unmarshal", stream);
        endpoint.assertIsSatisfied();
    }

    @Test
    public void testMarshal() throws Exception {
        Calendar testCalendar = createTestCalendar();
        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedBodiesReceived(testCalendar.toString());
        template.sendBody("direct:marshal", testCalendar);
        endpoint.assertIsSatisfied();
    }
}

