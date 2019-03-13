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
package org.apache.camel.issues;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 *
 */
public class SplitterParallelIssueTest extends ContextTestSupport {
    private int size = 20;

    private int delay = 100;

    @Test
    public void testSplitParallel() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:end");
        mock.expectedMessageCount(size);
        int time = Math.max(10000, (((size) * 2) * (delay)));
        mock.setResultWaitTime(time);
        for (int i = 0; i < (size); i++) {
            final int num = i;
            new Thread(new Runnable() {
                public void run() {
                    template.sendBody("direct:start", ("" + num));
                }
            }).start();
        }
        assertMockEndpointsSatisfied();
    }
}

