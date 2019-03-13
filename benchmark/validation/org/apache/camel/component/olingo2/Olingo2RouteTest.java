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
package org.apache.camel.component.olingo2;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.olingo2.api.Olingo2App;
import org.apache.camel.component.olingo2.internal.Olingo2Constants;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Test;


public class Olingo2RouteTest extends CamelTestSupport {
    private static final int PORT = AvailablePortFinder.getNextAvailable();

    private static final String TEST_SERVICE_URL = ("http://localhost:" + (Olingo2RouteTest.PORT)) + "/MyFormula.svc";

    private static final String ID_PROPERTY = "Id";

    private static Olingo2App olingoApp;

    private static Olingo2SampleServer server;

    @Test
    public void testRead() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        headers.put(((Olingo2Constants.PROPERTY_PREFIX) + "keyPredicate"), "'1'");
        final ODataEntry manufacturer = requestBodyAndHeaders("direct:READENTRY", null, headers);
        assertNotNull(manufacturer);
        final Map<String, Object> properties = manufacturer.getProperties();
        assertEquals("Manufacturer Id", "1", properties.get(Olingo2RouteTest.ID_PROPERTY));
    }
}

