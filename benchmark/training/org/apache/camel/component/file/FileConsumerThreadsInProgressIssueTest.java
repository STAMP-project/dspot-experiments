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
package org.apache.camel.component.file;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class FileConsumerThreadsInProgressIssueTest extends ContextTestSupport {
    private final Map<String, Integer> duplicate = new HashMap<>();

    private final FileConsumerThreadsInProgressIssueTest.SampleProcessor processor = new FileConsumerThreadsInProgressIssueTest.SampleProcessor(duplicate);

    private int number = 2000;

    @Test
    public void testFileConsumerThreadsInProgressIssue() throws Exception {
        // give longer timeout for stopping
        context.getShutdownStrategy().setTimeout(180);
        MockEndpoint mock = getMockEndpoint("mock:done");
        mock.expectedMessageCount(number);
        mock.expectsNoDuplicates(TestSupport.body());
        FileConsumerThreadsInProgressIssueTest.createManyFiles(number);
        context.getRouteController().startRoute("myRoute");
        mock.setResultWaitTime((180 * 1000));
        mock.assertIsSatisfied();
        context.stop();
        int found = 0;
        log.info("=====================");
        log.info("Printing duplicates");
        for (Map.Entry<String, Integer> ent : duplicate.entrySet()) {
            Integer count = ent.getValue();
            if (count > 1) {
                found++;
                log.info((((ent.getKey()) + " :: ") + count));
            }
        }
        Assert.assertEquals("Should not contain duplicates", 0, found);
    }

    private class SampleProcessor implements Processor {
        private Map<String, Integer> duplicate;

        public SampleProcessor(Map<String, Integer> duplicate) {
            this.duplicate = duplicate;
        }

        public void process(Exchange exchange) throws Exception {
            Integer integer = duplicate.get(exchange.toString());
            if (integer == null) {
                duplicate.put(exchange.toString(), 1);
            } else {
                integer++;
                duplicate.put(exchange.toString(), integer);
            }
            log.info(("Process called for-" + exchange));
            Thread.sleep(20);
        }
    }
}

