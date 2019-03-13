/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.integration.accesscontrol;


import java.util.Collections;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


/**
 * Access control test for funnels.
 */
public class ITCountersAccessControl {
    private static AccessControlHelper helper;

    private static String uri;

    /**
     * Test get counters.
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testGetCounters() throws Exception {
        ITCountersAccessControl.helper.testGenericGetUri(ITCountersAccessControl.uri);
    }

    /**
     * Ensures the READ user can get counters.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testUpdateCounters() throws Exception {
        final String counterUri = (ITCountersAccessControl.uri) + "/my-counter";
        Response response;
        // read
        response = ITCountersAccessControl.helper.getReadUser().testPut(counterUri, Collections.emptyMap());
        Assert.assertEquals(403, response.getStatus());
        // read/write
        response = ITCountersAccessControl.helper.getReadWriteUser().testPut(counterUri, Collections.emptyMap());
        Assert.assertEquals(404, response.getStatus());
        // write
        response = ITCountersAccessControl.helper.getWriteUser().testPut(counterUri, Collections.emptyMap());
        Assert.assertEquals(404, response.getStatus());
        // none
        response = ITCountersAccessControl.helper.getNoneUser().testPut(counterUri, Collections.emptyMap());
        Assert.assertEquals(403, response.getStatus());
    }
}

