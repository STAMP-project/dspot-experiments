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
package org.apache.camel.component.aws.sqs;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class SqsConcurrentConsumerTest extends CamelTestSupport {
    private static final int NUM_CONCURRENT = 10;

    private static final int NUM_MESSAGES = 100;

    final Set<Long> threadNumbers = new HashSet<>();

    @Test
    public void consumeMessagesFromQueue() throws Exception {
        // simple test to make sure that concurrent consumers were used in the test
        NotifyBuilder notifier = whenCompleted(SqsConcurrentConsumerTest.NUM_MESSAGES).create();
        assertTrue((("We didn't process " + (SqsConcurrentConsumerTest.NUM_MESSAGES)) + " messages as we expected!"), notifier.matches(5, TimeUnit.SECONDS));
        if (isPlatform("windows")) {
            // threading is different on windows
        } else {
            // usually we use all threads evenly but sometimes threads are reused so just test that 50%+ was used
            if ((threadNumbers.size()) < ((SqsConcurrentConsumerTest.NUM_CONCURRENT) / 2)) {
                fail(String.format("We were expecting to have about half of %d numbers of concurrent consumers, but only found %d", SqsConcurrentConsumerTest.NUM_CONCURRENT, threadNumbers.size()));
            }
        }
    }
}

