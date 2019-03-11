/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.dfs;


import FileSystem.DEFAULT_FS;
import FileSystem.FS_DEFAULT_NAME_KEY;
import java.io.InputStream;
import org.apache.drill.exec.ops.OpProfileDef;
import org.apache.drill.exec.ops.OperatorStats;
import org.apache.drill.exec.proto.UserBitShared.OperatorProfile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


public class TestDrillFileSystem {
    private static String tempFilePath;

    @Test
    public void testIOStats() throws Exception {
        DrillFileSystem dfs = null;
        InputStream is = null;
        Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, DEFAULT_FS);
        OpProfileDef profileDef = /* operatorId */
        /* operatorType */
        /* inputCount */
        new OpProfileDef(0, 0, 0);
        OperatorStats stats = /* allocator */
        new OperatorStats(profileDef, null);
        // start wait time method in OperatorStats expects the OperatorStats state to be in "processing"
        stats.startProcessing();
        try {
            dfs = new DrillFileSystem(conf, stats);
            is = dfs.open(new Path(TestDrillFileSystem.tempFilePath));
            byte[] buf = new byte[8000];
            while ((is.read(buf, 0, buf.length)) != (-1)) {
            } 
        } finally {
            stats.stopProcessing();
            if (is != null) {
                is.close();
            }
            if (dfs != null) {
                dfs.close();
            }
        }
        OperatorProfile operatorProfile = stats.getProfile();
        Assert.assertTrue("Expected wait time is non-zero, but got zero wait time", ((operatorProfile.getWaitNanos()) > 0));
    }
}

