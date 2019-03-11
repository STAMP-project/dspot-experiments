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
package org.apache.flink.graph.asm.dataset;


import Types.LONG;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.graph.asm.dataset.ChecksumHashCode.Checksum;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ChecksumHashCode}.
 */
public class ChecksumHashCodeTest {
    private ExecutionEnvironment env;

    @Test
    public void testList() throws Exception {
        List<Long> list = Arrays.asList(ArrayUtils.toObject(new long[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
        DataSet<Long> dataset = env.fromCollection(list);
        Checksum checksum = new ChecksumHashCode<Long>().run(dataset).execute();
        Assert.assertEquals(list.size(), checksum.getCount());
        Assert.assertEquals((((list.size()) * ((list.size()) - 1)) / 2), checksum.getChecksum());
    }

    @Test
    public void testEmptyList() throws Exception {
        DataSet<Long> dataset = env.fromCollection(Collections.emptyList(), LONG);
        Checksum checksum = new ChecksumHashCode<Long>().run(dataset).execute();
        Assert.assertEquals(0, checksum.getCount());
        Assert.assertEquals(0, checksum.getChecksum());
    }
}

