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
package org.apache.camel.component.file.remote;


import java.io.File;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class FtpConsumerAutoCreateTest extends FtpServerTestSupport {
    @Test
    public void testAutoCreate() throws Exception {
        FtpEndpoint<?> endpoint = ((FtpEndpoint<?>) (getMandatoryEndpoint(getFtpUrl())));
        endpoint.start();
        endpoint.getExchanges();
        assertTrue(new File("target/res/home/foo/bar/baz/xxx").exists());
        // producer should create necessary subdirs
        sendFile(getFtpUrl(), "Hello World", "sub1/sub2/hello.txt");
        assertTrue(new File("target/res/home/foo/bar/baz/xxx/sub1/sub2").exists());
        // to see if another connect causes problems with autoCreate=true
        endpoint.stop();
        endpoint.start();
        endpoint.getExchanges();
    }

    @Test
    public void testNoAutoCreate() throws Exception {
        FtpEndpoint<?> endpoint = ((FtpEndpoint<?>) (getMandatoryEndpoint(((getFtpUrl()) + "&autoCreate=false"))));
        endpoint.start();
        try {
            endpoint.getExchanges();
            fail("Should fail with 550 No such directory.");
        } catch (GenericFileOperationFailedException e) {
            assertThat(e.getCode(), CoreMatchers.equalTo(550));
        }
    }
}

