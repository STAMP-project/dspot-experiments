/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs;


import java.io.File;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to make sure df can run and work.
 */
public class TestDFCachingGetSpaceUsed {
    private static final File DF_DIR = GenericTestUtils.getTestDir("testdfspace");

    public static final int FILE_SIZE = 1024;

    @Test
    public void testCanBuildRun() throws Exception {
        File file = writeFile("testCanBuild");
        GetSpaceUsed instance = new CachingGetSpaceUsed.Builder().setPath(file).setInterval(50060).setKlass(DFCachingGetSpaceUsed.class).build();
        Assert.assertTrue((instance instanceof DFCachingGetSpaceUsed));
        Assert.assertTrue(((instance.getUsed()) >= ((TestDFCachingGetSpaceUsed.FILE_SIZE) - 20)));
        close();
    }
}

