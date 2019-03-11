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
package org.apache.hadoop.hdfs.util;


import java.util.Random;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.junit.Test;


/**
 * Test {@link Diff} with {@link INode}.
 */
public class TestDiff {
    private static final Random RANDOM = new Random();

    private static final int UNDO_TEST_P = 10;

    private static final PermissionStatus PERM = PermissionStatus.createImmutable("user", "group", FsPermission.createImmutable(((short) (0))));

    /**
     * Test directory diff.
     */
    @Test(timeout = 60000)
    public void testDiff() throws Exception {
        for (int startSize = 0; startSize <= 10000; startSize = TestDiff.nextStep(startSize)) {
            for (int m = 0; m <= 10000; m = TestDiff.nextStep(m)) {
                runDiffTest(startSize, m);
            }
        }
    }
}

