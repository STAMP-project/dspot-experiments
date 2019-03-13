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
package org.apache.camel.dataformat.zipfile;


import java.io.File;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ZipFileSplitAndDeleteTest extends CamelTestSupport {
    @Test
    public void testDeleteZipFileWhenUnmarshalWithDataFormat() throws Exception {
        NotifyBuilder notify = from(("file://target/" + "testDeleteZipFileWhenUnmarshalWithDataFormat")).whenDone(1).create();
        getMockEndpoint("mock:end").expectedMessageCount(2);
        String zipFile = createZipFile("testDeleteZipFileWhenUnmarshalWithDataFormat");
        assertMockEndpointsSatisfied();
        notify.matchesMockWaitTime();
        // the original file should have been deleted
        assertFalse("File should been deleted", new File(zipFile).exists());
    }

    @Test
    public void testDeleteZipFileWhenUnmarshalWithSplitter() throws Exception {
        NotifyBuilder notify = from(("file://target/" + "testDeleteZipFileWhenUnmarshalWithSplitter")).whenDone(1).create();
        getMockEndpoint("mock:end").expectedMessageCount(2);
        String zipFile = createZipFile("testDeleteZipFileWhenUnmarshalWithSplitter");
        assertMockEndpointsSatisfied();
        notify.matchesMockWaitTime();
        // the original file should have been deleted,
        assertFalse("File should been deleted", new File(zipFile).exists());
    }
}

