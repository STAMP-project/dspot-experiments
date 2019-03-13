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
package org.apache.camel.component.stax;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.camel.util.TimeUtils;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 */
@Ignore("this is a manual test")
public class StAXXPathSplitChoicePerformanceTest extends CamelTestSupport {
    private int size = 20 * 1000;

    private final AtomicInteger tiny = new AtomicInteger();

    private final AtomicInteger small = new AtomicInteger();

    private final AtomicInteger med = new AtomicInteger();

    private final AtomicInteger large = new AtomicInteger();

    private final StopWatch watch = new StopWatch();

    @Test
    public void testXPathSTaXPerformanceRoute() throws Exception {
        NotifyBuilder notify = whenDone(size).create();
        boolean matches = notify.matches(60, TimeUnit.SECONDS);
        log.info(((("Processed file with " + (size)) + " elements in: ") + (TimeUtils.printDuration(watch.taken()))));
        log.info((("Processed " + (tiny.get())) + " tiny messages"));
        log.info((("Processed " + (small.get())) + " small messages"));
        log.info((("Processed " + (med.get())) + " medium messages"));
        log.info((("Processed " + (large.get())) + " large messages"));
        assertEquals((((size) / 10) * 4), tiny.get());
        assertEquals((((size) / 10) * 2), small.get());
        assertEquals((((size) / 10) * 3), med.get());
        assertEquals((((size) / 10) * 1), large.get());
        assertTrue("Should complete route", matches);
    }
}

