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


import java.util.Comparator;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Unit test to verify remotefile sorter option.
 */
// END SNIPPET: e1
public class FromFtpRemoteFileSorterTest extends FtpServerTestSupport {
    @Test
    public void testFtpSorter() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(3);
        mock.expectedBodiesReceived("Hello Copenhagen", "Hello London", "Hello Paris");
        mock.assertIsSatisfied();
    }

    // START SNIPPET: e1
    public class MyRemoteFileSorter implements Comparator<RemoteFile<?>> {
        public int compare(RemoteFile<?> o1, RemoteFile<?> o2) {
            return o1.getFileNameOnly().compareToIgnoreCase(o2.getFileNameOnly());
        }
    }
}

