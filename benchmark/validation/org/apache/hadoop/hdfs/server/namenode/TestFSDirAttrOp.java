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


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link FSDirAttrOp}.
 */
public class TestFSDirAttrOp {
    public static final Logger LOG = LoggerFactory.getLogger(TestFSDirAttrOp.class);

    @Test
    public void testUnprotectedSetTimes() throws Exception {
        // atime < access time + precision
        Assert.assertFalse(("SetTimes should not update access time " + "because it's within the last precision interval"), unprotectedSetTimes(100, 0, 1000, (-1), false));
        // atime = access time + precision
        Assert.assertFalse(("SetTimes should not update access time " + "because it's within the last precision interval"), unprotectedSetTimes(1000, 0, 1000, (-1), false));
        // atime > access time + precision
        Assert.assertTrue("SetTimes should update access time", unprotectedSetTimes(1011, 10, 1000, (-1), false));
        // atime < access time + precision, but force is set
        Assert.assertTrue("SetTimes should update access time", unprotectedSetTimes(100, 0, 1000, (-1), true));
        // atime < access time + precision, but mtime is set
        Assert.assertTrue("SetTimes should update access time", unprotectedSetTimes(100, 0, 1000, 1, false));
    }
}

