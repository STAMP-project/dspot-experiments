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
package org.apache.flink.api.common.operators.util;


import FieldList.EMPTY_LIST;
import org.junit.Assert;
import org.junit.Test;


public class FieldListTest {
    @Test
    public void testFieldListConstructors() {
        FieldListTest.check(new FieldList());
        FieldListTest.check(EMPTY_LIST);
        FieldListTest.check(new FieldList(14), 14);
        FieldListTest.check(new FieldList(Integer.valueOf(3)), 3);
        FieldListTest.check(new FieldList(7, 4, 1), 7, 4, 1);
        FieldListTest.check(new FieldList(7, 4, 1, 4, 7, 1, 4, 2), 7, 4, 1, 4, 7, 1, 4, 2);
    }

    @Test
    public void testFieldListAdds() {
        FieldListTest.check(new FieldList().addField(1).addField(2), 1, 2);
        FieldListTest.check(EMPTY_LIST.addField(3).addField(2), 3, 2);
        FieldListTest.check(new FieldList(13).addFields(new FieldList(17, 31, 42)), 13, 17, 31, 42);
        FieldListTest.check(new FieldList(14).addFields(new FieldList(17)), 14, 17);
        FieldListTest.check(new FieldList(3).addFields(2, 8, 5, 7), 3, 2, 8, 5, 7);
    }

    @Test
    public void testImmutability() {
        FieldList s1 = new FieldList();
        FieldList s2 = new FieldList(5);
        FieldList s3 = new FieldList(Integer.valueOf(7));
        FieldList s4 = new FieldList(5, 4, 7, 6);
        s1.addFields(s2).addFields(s3);
        s2.addFields(s4);
        s4.addFields(s1);
        s1.addField(Integer.valueOf(14));
        s2.addFields(78, 13, 66, 3);
        Assert.assertEquals(0, s1.size());
        Assert.assertEquals(1, s2.size());
        Assert.assertEquals(1, s3.size());
        Assert.assertEquals(4, s4.size());
    }

    @Test
    public void testAddSetToList() {
        FieldListTest.check(new FieldList().addFields(new FieldSet(1)).addFields(2), 1, 2);
        FieldListTest.check(new FieldList().addFields(1).addFields(new FieldSet(2)), 1, 2);
        FieldListTest.check(new FieldList().addFields(new FieldSet(2)), 2);
    }
}

