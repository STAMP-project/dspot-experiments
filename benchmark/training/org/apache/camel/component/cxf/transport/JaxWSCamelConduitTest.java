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
package org.apache.camel.component.cxf.transport;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


/**
 * Test CXF-CamelConduit when the destination is not a pipeline
 */
public class JaxWSCamelConduitTest extends JaxWSCamelTestSupport {
    @Test
    public void testStart1() {
        assertThat(getSampleWS("direct:start1").getSomething(), CoreMatchers.is("Something"));
    }

    /**
     * Success
     */
    @Test
    public void testStart2() {
        assertThat(getSampleWSWithCXFAPI("direct:start2").getSomething(), CoreMatchers.is("Something"));
    }

    // test the content type
    @Test
    public void testStart3() {
        assertThat(getSampleWS("direct:start3").getSomething(), CoreMatchers.is("Something"));
    }

    @Test
    public void testAsyncInvocation() throws InterruptedException, ExecutionException {
        Future<?> result = getSampleWSAsyncWithCXFAPI("direct:start2").getSomethingAsync();
        // as the CXF will build the getSomethingResponse by using asm, so we cannot get the response directly.
        assertNotNull(result.get());
    }
}

