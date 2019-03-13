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
package org.apache.camel.component.file.stress;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Manual test")
public class FileAsyncStressTest extends ContextTestSupport {
    private int files = 150;

    @Test
    public void testAsyncStress() throws Exception {
        // do not test on windows
        if (TestSupport.isPlatform("windows")) {
            return;
        }
        // start route when all the files have been written
        context.getRouteController().startRoute("foo");
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(100);
        mock.setResultWaitTime(30000);
        assertMockEndpointsSatisfied();
    }
}

