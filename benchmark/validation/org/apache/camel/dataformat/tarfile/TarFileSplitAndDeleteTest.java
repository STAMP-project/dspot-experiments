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
package org.apache.camel.dataformat.tarfile;


import java.io.File;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class TarFileSplitAndDeleteTest extends CamelTestSupport {
    @Test
    public void testDeleteTarFileWhenUnmarshalWithDataFormat() throws Exception {
        NotifyBuilder notify = from(("file://target/" + "testDeleteTarFileWhenUnmarshalWithDataFormat")).whenDone(1).create();
        getMockEndpoint("mock:end").expectedMessageCount(3);
        String tarFile = createTarFile("testDeleteTarFileWhenUnmarshalWithDataFormat");
        assertMockEndpointsSatisfied();
        notify.matchesMockWaitTime();
        // the original file should have been deleted
        assertFalse("File should been deleted", new File(tarFile).exists());
    }

    @Test
    public void testDeleteTarFileWhenUnmarshalWithSplitter() throws Exception {
        NotifyBuilder notify = from(("file://target/" + "testDeleteTarFileWhenUnmarshalWithSplitter")).whenDone(1).create();
        getMockEndpoint("mock:end").expectedMessageCount(3);
        String tarFile = createTarFile("testDeleteTarFileWhenUnmarshalWithSplitter");
        assertMockEndpointsSatisfied();
        notify.matchesMockWaitTime();
        // the original file should have been deleted,
        assertFalse("File should been deleted", new File(tarFile).exists());
    }
}

