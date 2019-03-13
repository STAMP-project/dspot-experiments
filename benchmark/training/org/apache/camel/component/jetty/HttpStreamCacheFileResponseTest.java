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
package org.apache.camel.component.jetty;


import java.io.File;
import org.junit.Test;


public class HttpStreamCacheFileResponseTest extends BaseJettyTest {
    private String body = "12345678901234567890123456789012345678901234567890";

    private String body2 = "Bye " + (body);

    @Test
    public void testStreamCacheToFileShouldBeDeletedInCaseOfResponse() throws Exception {
        String out = template.requestBody("http://localhost:{{port}}/myserver", body, String.class);
        assertEquals(body2, out);
        // give time for file to be deleted
        Thread.sleep(500);
        // the temporary files should have been deleted
        File file = new File("target/cachedir");
        String[] files = file.list();
        assertEquals("There should be no files", 0, files.length);
    }
}

