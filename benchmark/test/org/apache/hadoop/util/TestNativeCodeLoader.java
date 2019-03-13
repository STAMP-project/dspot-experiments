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
package org.apache.hadoop.util;


import org.apache.hadoop.crypto.OpensslCipher;
import org.apache.hadoop.io.compress.Lz4Codec;
import org.apache.hadoop.io.compress.SnappyCodec;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestNativeCodeLoader {
    static final Logger LOG = LoggerFactory.getLogger(TestNativeCodeLoader.class);

    @Test
    public void testNativeCodeLoaded() {
        if ((TestNativeCodeLoader.requireTestJni()) == false) {
            TestNativeCodeLoader.LOG.info("TestNativeCodeLoader: libhadoop.so testing is not required.");
            return;
        }
        if (!(NativeCodeLoader.isNativeCodeLoaded())) {
            Assert.fail(("TestNativeCodeLoader: libhadoop.so testing was required, but " + "libhadoop.so was not loaded."));
        }
        Assert.assertFalse(NativeCodeLoader.getLibraryName().isEmpty());
        // library names are depended on platform and build envs
        // so just check names are available
        Assert.assertFalse(ZlibFactory.getLibraryName().isEmpty());
        if (NativeCodeLoader.buildSupportsSnappy()) {
            Assert.assertFalse(SnappyCodec.getLibraryName().isEmpty());
        }
        if (NativeCodeLoader.buildSupportsOpenssl()) {
            Assert.assertFalse(OpensslCipher.getLibraryName().isEmpty());
        }
        Assert.assertFalse(Lz4Codec.getLibraryName().isEmpty());
        TestNativeCodeLoader.LOG.info("TestNativeCodeLoader: libhadoop.so is loaded.");
    }
}

