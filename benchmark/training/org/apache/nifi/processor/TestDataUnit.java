/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processor;


import DataUnit.B;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TestDataUnit {
    @Test
    public void testParseWithIntegerValue() {
        Assert.assertEquals(300L, DataUnit.parseDataSize("300 B", B).longValue());
        Assert.assertEquals((300L * 1024L), DataUnit.parseDataSize("300 KB", B).longValue());
        Assert.assertEquals(((300L * 1024L) * 1024L), DataUnit.parseDataSize("300 MB", B).longValue());
        Assert.assertEquals((((300L * 1024L) * 1024L) * 1024L), DataUnit.parseDataSize("300 GB", B).longValue());
    }

    @Test
    public void testParseWithDecimalValue() {
        Assert.assertEquals(300L, DataUnit.parseDataSize("300 B", B).longValue());
        Assert.assertEquals(((long) (3.22 * 1024.0)), DataUnit.parseDataSize("3.22 KB", B).longValue());
        Assert.assertEquals(((long) ((3.22 * 1024.0) * 1024.0)), DataUnit.parseDataSize("3.22 MB", B).longValue());
        Assert.assertEquals(((long) (((3.22 * 1024.0) * 1024.0) * 1024.0)), DataUnit.parseDataSize("3.22 GB", B).longValue());
    }
}

