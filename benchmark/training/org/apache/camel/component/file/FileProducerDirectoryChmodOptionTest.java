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
package org.apache.camel.component.file;


import org.apache.camel.ContextTestSupport;
import org.junit.Test;


public class FileProducerDirectoryChmodOptionTest extends ContextTestSupport {
    public static final String TEST_DIRECTORY = "target/data/chmoddir/foo/";

    @Test
    public void testWriteValidNoDir() throws Exception {
        if (!(canTest())) {
            return;
        }
        runChmodCheck("NoDir", null, "rwxr-xr-x");
    }

    @Test
    public void testWriteValidChmod0755() throws Exception {
        if (!(canTest())) {
            return;
        }
        runChmodCheck("0755", "rwxrwxrwx", "rwxr-xr-x");
    }

    @Test
    public void testWriteValidChmod666() throws Exception {
        if (!(canTest())) {
            return;
        }
        runChmodCheck("666", "rwxrwxrwx", "rw-rw-rw-");
    }
}

