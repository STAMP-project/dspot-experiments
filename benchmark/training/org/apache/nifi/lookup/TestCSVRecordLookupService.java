/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.lookup;


import CSVRecordLookupService.CHARSET;
import CSVRecordLookupService.CSV_FILE;
import CSVRecordLookupService.CSV_FORMAT;
import CSVRecordLookupService.LOOKUP_KEY_COLUMN;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestCSVRecordLookupService {
    private static final Optional<Record> EMPTY_RECORD = Optional.empty();

    @Test
    public void testSimpleCsvFileLookupService() throws IOException, LookupFailureException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final CSVRecordLookupService service = new CSVRecordLookupService();
        runner.addControllerService("csv-file-lookup-service", service);
        runner.setProperty(service, CSV_FILE, "src/test/resources/test.csv");
        runner.setProperty(service, CSV_FORMAT, "RFC4180");
        runner.setProperty(service, LOOKUP_KEY_COLUMN, "key");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final CSVRecordLookupService lookupService = ((CSVRecordLookupService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("csv-file-lookup-service")));
        Assert.assertThat(lookupService, CoreMatchers.instanceOf(LookupService.class));
        final Optional<Record> property1 = lookupService.lookup(Collections.singletonMap("key", "property.1"));
        Assert.assertEquals("this is property 1", property1.get().getAsString("value"));
        Assert.assertEquals("2017-04-01", property1.get().getAsString("created_at"));
        final Optional<Record> property2 = lookupService.lookup(Collections.singletonMap("key", "property.2"));
        Assert.assertEquals("this is property 2", property2.get().getAsString("value"));
        Assert.assertEquals("2017-04-02", property2.get().getAsString("created_at"));
        final Optional<Record> property3 = lookupService.lookup(Collections.singletonMap("key", "property.3"));
        Assert.assertEquals(TestCSVRecordLookupService.EMPTY_RECORD, property3);
    }

    @Test
    public void testSimpleCsvFileLookupServiceWithCharset() throws IOException, LookupFailureException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final CSVRecordLookupService service = new CSVRecordLookupService();
        runner.addControllerService("csv-file-lookup-service", service);
        runner.setProperty(service, CSV_FILE, "src/test/resources/test_Windows-31J.csv");
        runner.setProperty(service, CSV_FORMAT, "RFC4180");
        runner.setProperty(service, CHARSET, "Windows-31J");
        runner.setProperty(service, LOOKUP_KEY_COLUMN, "key");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final Optional<Record> property1 = service.lookup(Collections.singletonMap("key", "property.1"));
        Assert.assertThat(property1.isPresent(), CoreMatchers.is(true));
        Assert.assertThat(property1.get().getAsString("value"), CoreMatchers.is("this is property \uff11"));
        Assert.assertThat(property1.get().getAsString("created_at"), CoreMatchers.is("2017-04-01"));
    }
}

