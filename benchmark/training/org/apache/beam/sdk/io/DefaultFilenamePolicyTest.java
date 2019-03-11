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
package org.apache.beam.sdk.io;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of {@link DefaultFilenamePolicy}.
 */
@RunWith(JUnit4.class)
public class DefaultFilenamePolicyTest {
    @Test
    public void testConstructName() {
        Assert.assertEquals("/path/to/output-001-of-123.txt", DefaultFilenamePolicyTest.constructName("/path/to/output", "-SSS-of-NNN", ".txt", 1, 123, null, null));
        Assert.assertEquals("/path/to/out.txt/part-00042", DefaultFilenamePolicyTest.constructName("/path/to/out.txt", "/part-SSSSS", "", 42, 100, null, null));
        Assert.assertEquals("/path/to/out.txt", DefaultFilenamePolicyTest.constructName("/path/to/ou", "t.t", "xt", 1, 1, null, null));
        Assert.assertEquals("/path/to/out0102shard.txt", DefaultFilenamePolicyTest.constructName("/path/to/out", "SSNNshard", ".txt", 1, 2, null, null));
        Assert.assertEquals("/path/to/out-2/1.part-1-of-2.txt", DefaultFilenamePolicyTest.constructName("/path/to/out", "-N/S.part-S-of-N", ".txt", 1, 2, null, null));
    }

    @Test
    public void testConstructNameWithLargeShardCount() {
        Assert.assertEquals("/out-100-of-5000.txt", DefaultFilenamePolicyTest.constructName("/out", "-SS-of-NN", ".txt", 100, 5000, null, null));
    }

    @Test
    public void testConstructWindowedName() {
        Assert.assertEquals("/path/to/output-001-of-123.txt", DefaultFilenamePolicyTest.constructName("/path/to/output", "-SSS-of-NNN", ".txt", 1, 123, null, null));
        Assert.assertEquals("/path/to/output-001-of-123-PPP-W.txt", DefaultFilenamePolicyTest.constructName("/path/to/output", "-SSS-of-NNN-PPP-W", ".txt", 1, 123, null, null));
        Assert.assertEquals(("/path/to/out" + ".txt/part-00042-myPaneStr-myWindowStr"), DefaultFilenamePolicyTest.constructName("/path/to/out.txt", "/part-SSSSS-P-W", "", 42, 100, "myPaneStr", "myWindowStr"));
        Assert.assertEquals("/path/to/out.txt", DefaultFilenamePolicyTest.constructName("/path/to/ou", "t.t", "xt", 1, 1, "myPaneStr2", "anotherWindowStr"));
        Assert.assertEquals("/path/to/out0102shard-oneMoreWindowStr-anotherPaneStr.txt", DefaultFilenamePolicyTest.constructName("/path/to/out", "SSNNshard-W-P", ".txt", 1, 2, "anotherPaneStr", "oneMoreWindowStr"));
        Assert.assertEquals(("/out-2/1.part-1-of-2-slidingWindow1-myPaneStr3-windowslidingWindow1-" + "panemyPaneStr3.txt"), DefaultFilenamePolicyTest.constructName("/out", "-N/S.part-S-of-N-W-P-windowW-paneP", ".txt", 1, 2, "myPaneStr3", "slidingWindow1"));
        // test first/last pane
        Assert.assertEquals("/out.txt/part-00042-myWindowStr-pane-11-true-false", DefaultFilenamePolicyTest.constructName("/out.txt", "/part-SSSSS-W-P", "", 42, 100, "pane-11-true-false", "myWindowStr"));
        Assert.assertEquals("/path/to/out.txt", DefaultFilenamePolicyTest.constructName("/path/to/ou", "t.t", "xt", 1, 1, "pane", "anotherWindowStr"));
        Assert.assertEquals("/out0102shard-oneMoreWindowStr-pane--1-false-false-pane--1-false-false.txt", DefaultFilenamePolicyTest.constructName("/out", "SSNNshard-W-P-P", ".txt", 1, 2, "pane--1-false-false", "oneMoreWindowStr"));
        Assert.assertEquals("/path/to/out-2/1.part-1-of-2-sWindow1-winsWindow1-ppaneL.txt", DefaultFilenamePolicyTest.constructName("/path/to/out", "-N/S.part-S-of-N-W-winW-pP", ".txt", 1, 2, "paneL", "sWindow1"));
    }
}

