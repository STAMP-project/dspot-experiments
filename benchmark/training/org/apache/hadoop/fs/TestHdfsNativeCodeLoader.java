/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import org.apache.hadoop.util.NativeCodeLoader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHdfsNativeCodeLoader {
    static final Logger LOG = LoggerFactory.getLogger(TestHdfsNativeCodeLoader.class);

    @Test
    public void testNativeCodeLoaded() {
        if ((TestHdfsNativeCodeLoader.requireTestJni()) == false) {
            TestHdfsNativeCodeLoader.LOG.info("TestNativeCodeLoader: libhadoop.so testing is not required.");
            return;
        }
        if (!(NativeCodeLoader.isNativeCodeLoaded())) {
            String LD_LIBRARY_PATH = System.getenv().get("LD_LIBRARY_PATH");
            if (LD_LIBRARY_PATH == null)
                LD_LIBRARY_PATH = "";

            Assert.fail((("TestNativeCodeLoader: libhadoop.so testing was required, but " + "libhadoop.so was not loaded.  LD_LIBRARY_PATH = ") + LD_LIBRARY_PATH));
        }
        TestHdfsNativeCodeLoader.LOG.info("TestHdfsNativeCodeLoader: libhadoop.so is loaded.");
    }
}

