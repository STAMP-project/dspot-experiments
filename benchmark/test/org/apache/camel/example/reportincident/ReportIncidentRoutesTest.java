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
package org.apache.camel.example.reportincident;


import org.apache.camel.spring.Main;
import org.junit.Test;


/**
 * Unit test of our routes
 */
public class ReportIncidentRoutesTest {
    // should be the same address as we have in our route
    private static String url;

    protected Main main;

    @Test
    public void testReportIncident() throws Exception {
        // start camel
        startCamel();
        runTest();
        // stop camel
        stopCamel();
    }
}

