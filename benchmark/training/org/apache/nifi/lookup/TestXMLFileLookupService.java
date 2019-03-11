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


import XMLFileLookupService.CONFIGURATION_FILE;
import java.util.Collections;
import java.util.Optional;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestXMLFileLookupService {
    static final Optional<String> EMPTY_STRING = Optional.empty();

    @Test
    public void testXMLFileLookupService() throws LookupFailureException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(TestProcessor.class);
        final XMLFileLookupService service = new XMLFileLookupService();
        runner.addControllerService("xml-file-lookup-service", service);
        runner.setProperty(service, CONFIGURATION_FILE, "src/test/resources/test.xml");
        runner.enableControllerService(service);
        runner.assertValid(service);
        final XMLFileLookupService lookupService = ((XMLFileLookupService) (runner.getProcessContext().getControllerServiceLookup().getControllerService("xml-file-lookup-service")));
        Assert.assertThat(lookupService, CoreMatchers.instanceOf(LookupService.class));
        final Optional<String> property1 = lookupService.lookup(Collections.singletonMap("key", "properties.property(0)"));
        Assert.assertEquals(Optional.of("this is property 1"), property1);
        final Optional<String> property2 = lookupService.lookup(Collections.singletonMap("key", "properties.property(1)"));
        Assert.assertEquals(Optional.of("this is property 2"), property2);
        final Optional<String> property3 = lookupService.lookup(Collections.singletonMap("key", "properties.property(2)[@value]"));
        Assert.assertEquals(Optional.of("this is property 3"), property3);
        final Optional<String> property4 = lookupService.lookup(Collections.singletonMap("key", "properties.property(3)"));
        Assert.assertEquals(TestXMLFileLookupService.EMPTY_STRING, property4);
    }
}

