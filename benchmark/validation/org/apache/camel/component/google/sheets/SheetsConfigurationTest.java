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
package org.apache.camel.component.google.sheets;


import org.apache.camel.component.google.sheets.internal.GoogleSheetsApiCollection;
import org.apache.camel.component.google.sheets.internal.SheetsSpreadsheetsApiMethod;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class SheetsConfigurationTest extends CamelTestSupport {
    private static final String PATH_PREFIX = GoogleSheetsApiCollection.getCollection().getApiName(SheetsSpreadsheetsApiMethod.class).getName();

    private static final String TEST_URI = ("google-sheets://" + (SheetsConfigurationTest.PATH_PREFIX)) + "/create?clientId=a&clientSecret=b&applicationName=c&accessToken=d&refreshToken=e";

    @Test
    public void testConfiguration() throws Exception {
        GoogleSheetsEndpoint endpoint = getMandatoryEndpoint(SheetsConfigurationTest.TEST_URI, GoogleSheetsEndpoint.class);
        GoogleSheetsConfiguration configuration = endpoint.getConfiguration();
        assertNotNull(configuration);
        assertEquals("a", configuration.getClientId());
        assertEquals("b", configuration.getClientSecret());
        assertEquals("c", configuration.getApplicationName());
        assertEquals("d", configuration.getAccessToken());
        assertEquals("e", configuration.getRefreshToken());
    }
}

