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
package org.apache.hadoop.hbase;


import Size.Unit;
import Size.Unit.BYTE;
import Size.Unit.GIGABYTE;
import Size.Unit.KILOBYTE;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestSize {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSize.class);

    @Test
    public void testConversion() {
        Size kbSize = new Size(1024.0, Unit.MEGABYTE);
        Assert.assertEquals(1.0, kbSize.get(GIGABYTE), 0);
        Assert.assertEquals(1024.0, kbSize.get(), 0);
        Assert.assertEquals((1024.0 * 1024.0), kbSize.get(KILOBYTE), 0);
        Assert.assertEquals(((1024.0 * 1024.0) * 1024.0), kbSize.get(BYTE), 0);
    }

    @Test
    public void testCompare() {
        Size size00 = new Size(100.0, Unit.GIGABYTE);
        Size size01 = new Size(100.0, Unit.MEGABYTE);
        Size size02 = new Size(100.0, Unit.BYTE);
        Set<Size> sizes = new TreeSet<>();
        sizes.add(size00);
        sizes.add(size01);
        sizes.add(size02);
        int count = 0;
        for (Size s : sizes) {
            switch (count++) {
                case 0 :
                    Assert.assertEquals(size02, s);
                    break;
                case 1 :
                    Assert.assertEquals(size01, s);
                    break;
                default :
                    Assert.assertEquals(size00, s);
                    break;
            }
        }
        Assert.assertEquals(3, count);
    }

    @Test
    public void testEqual() {
        Assert.assertEquals(new Size(1024.0, Unit.TERABYTE), new Size(1.0, Unit.PETABYTE));
        Assert.assertEquals(new Size(1024.0, Unit.GIGABYTE), new Size(1.0, Unit.TERABYTE));
        Assert.assertEquals(new Size(1024.0, Unit.MEGABYTE), new Size(1.0, Unit.GIGABYTE));
        Assert.assertEquals(new Size(1024.0, Unit.KILOBYTE), new Size(1.0, Unit.MEGABYTE));
        Assert.assertEquals(new Size(1024.0, Unit.BYTE), new Size(1.0, Unit.KILOBYTE));
    }
}

