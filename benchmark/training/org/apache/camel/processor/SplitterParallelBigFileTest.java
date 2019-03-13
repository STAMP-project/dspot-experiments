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
package org.apache.camel.processor;


import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.util.StopWatch;
import org.apache.camel.util.TimeUtils;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Manual test")
public class SplitterParallelBigFileTest extends ContextTestSupport {
    private int lines = 20000;

    @Test
    public void testSplitParallelBigFile() throws Exception {
        StopWatch watch = new StopWatch();
        NotifyBuilder builder = whenDone(((lines) + 1)).create();
        boolean done = builder.matches(120, TimeUnit.SECONDS);
        log.info(("Took " + (TimeUtils.printDuration(watch.taken()))));
        if (!done) {
            throw new CamelException("Could not split file in 2 minutes");
        }
        // need a little sleep for capturing memory profiling
        Thread.sleep((60 * 1000));
    }
}

