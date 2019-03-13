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
package org.apache.hadoop.io;


import org.junit.Assert;
import org.junit.Test;


public class TestBooleanWritable {
    @Test
    public void testCompareUnequalWritables() throws Exception {
        DataOutputBuffer bTrue = writeWritable(new BooleanWritable(true));
        DataOutputBuffer bFalse = writeWritable(new BooleanWritable(false));
        WritableComparator writableComparator = WritableComparator.get(BooleanWritable.class);
        Assert.assertEquals(0, compare(writableComparator, bTrue, bTrue));
        Assert.assertEquals(0, compare(writableComparator, bFalse, bFalse));
        Assert.assertEquals(1, compare(writableComparator, bTrue, bFalse));
        Assert.assertEquals((-1), compare(writableComparator, bFalse, bTrue));
    }

    /**
     * test {@link BooleanWritable} methods hashCode(), equals(), compareTo()
     */
    @Test
    public void testCommonMethods() {
        Assert.assertTrue("testCommonMethods1 error !!!", TestBooleanWritable.newInstance(true).equals(TestBooleanWritable.newInstance(true)));
        Assert.assertTrue("testCommonMethods2 error  !!!", TestBooleanWritable.newInstance(false).equals(TestBooleanWritable.newInstance(false)));
        Assert.assertFalse("testCommonMethods3 error !!!", TestBooleanWritable.newInstance(false).equals(TestBooleanWritable.newInstance(true)));
        Assert.assertTrue("testCommonMethods4 error !!!", checkHashCode(TestBooleanWritable.newInstance(true), TestBooleanWritable.newInstance(true)));
        Assert.assertFalse("testCommonMethods5 error !!! ", checkHashCode(TestBooleanWritable.newInstance(true), TestBooleanWritable.newInstance(false)));
        Assert.assertTrue("testCommonMethods6 error !!!", ((TestBooleanWritable.newInstance(true).compareTo(TestBooleanWritable.newInstance(false))) > 0));
        Assert.assertTrue("testCommonMethods7 error !!!", ((TestBooleanWritable.newInstance(false).compareTo(TestBooleanWritable.newInstance(true))) < 0));
        Assert.assertTrue("testCommonMethods8 error !!!", ((TestBooleanWritable.newInstance(false).compareTo(TestBooleanWritable.newInstance(false))) == 0));
        Assert.assertEquals("testCommonMethods9 error !!!", "true", TestBooleanWritable.newInstance(true).toString());
    }
}

