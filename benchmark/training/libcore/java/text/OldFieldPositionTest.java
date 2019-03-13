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
package libcore.java.text;


import java.text.DateFormat;
import java.text.FieldPosition;
import junit.framework.TestCase;

import static java.text.DateFormat.Field.DAY_OF_WEEK_IN_MONTH;


public class OldFieldPositionTest extends TestCase {
    public void test_hashCode() {
        // Test for method int java.text.FieldPosition.hashCode()
        FieldPosition fpos1 = new FieldPosition(1);
        FieldPosition fpos2 = new FieldPosition(1);
        TestCase.assertTrue("test 1: hash codes are not equal for equal objects.", ((fpos1.hashCode()) == (fpos2.hashCode())));
        fpos1.setBeginIndex(5);
        fpos1.setEndIndex(110);
        TestCase.assertTrue("test 2: hash codes are equal for non equal objects.", ((fpos1.hashCode()) != (fpos2.hashCode())));
        fpos2.setBeginIndex(5);
        fpos2.setEndIndex(110);
        TestCase.assertTrue("test 3: hash codes are not equal for equal objects.", ((fpos1.hashCode()) == (fpos2.hashCode())));
        FieldPosition fpos3 = new FieldPosition(DAY_OF_WEEK_IN_MONTH);
        TestCase.assertTrue("test 4: hash codes are equal for non equal objects.", ((fpos2.hashCode()) != (fpos3.hashCode())));
    }

    public void test_setBeginIndexI() {
        // Test for method void java.text.FieldPosition.setBeginIndex(int)
        FieldPosition fpos = new FieldPosition(1);
        fpos.setBeginIndex(2);
        fpos.setEndIndex(3);
        TestCase.assertEquals("beginIndex should have been set to 2", 2, fpos.getBeginIndex());
        fpos.setBeginIndex(Integer.MAX_VALUE);
        TestCase.assertEquals("beginIndex should have been set to Integer.MAX_VALUE", Integer.MAX_VALUE, fpos.getBeginIndex());
        fpos.setBeginIndex((-1));
        TestCase.assertEquals("beginIndex should have been set to -1", (-1), fpos.getBeginIndex());
    }

    public void test_setEndIndexI() {
        // Test for method void java.text.FieldPosition.setEndIndex(int)
        FieldPosition fpos = new FieldPosition(1);
        fpos.setEndIndex(3);
        fpos.setBeginIndex(2);
        TestCase.assertEquals("EndIndex should have been set to 3", 3, fpos.getEndIndex());
        fpos.setEndIndex(Integer.MAX_VALUE);
        TestCase.assertEquals("endIndex should have been set to Integer.MAX_VALUE", Integer.MAX_VALUE, fpos.getEndIndex());
        fpos.setEndIndex((-1));
        TestCase.assertEquals("endIndex should have been set to -1", (-1), fpos.getEndIndex());
    }
}

