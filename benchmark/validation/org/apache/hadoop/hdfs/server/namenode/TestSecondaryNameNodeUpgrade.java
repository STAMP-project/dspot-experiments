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
package org.apache.hadoop.hdfs.server.namenode;


import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Regression test for HDFS-3597, SecondaryNameNode upgrade -- when a 2NN
 * starts up with an existing directory structure with an old VERSION file, it
 * should delete the snapshot and download a new one from the NN.
 */
public class TestSecondaryNameNodeUpgrade {
    @Test
    public void testUpgradeLayoutVersionSucceeds() throws IOException {
        doIt(ImmutableMap.of("layoutVersion", "-39"));
    }

    @Test
    public void testUpgradePreFedSucceeds() throws IOException {
        doIt(ImmutableMap.of("layoutVersion", "-19", "clusterID", "", "blockpoolID", ""));
    }

    @Test
    public void testChangeNsIDFails() throws IOException {
        try {
            doIt(ImmutableMap.of("namespaceID", "2"));
            Assert.fail("Should throw InconsistentFSStateException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Inconsistent checkpoint fields", e);
            System.out.println(("Correctly failed with inconsistent namespaceID: " + e));
        }
    }
}

