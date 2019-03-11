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
package org.apache.harmony.tests.java.text;


import com.google.j2objc.util.ReflectionUtil;
import java.text.DateFormat;
import java.text.FieldPosition;
import junit.framework.TestCase;

import static java.text.DateFormat.Field.AM_PM;
import static java.text.DateFormat.Field.DAY_OF_WEEK_IN_MONTH;
import static java.text.DateFormat.Field.ERA;
import static java.text.DateFormat.Field.HOUR1;
import static java.text.DateFormat.Field.MINUTE;
import static java.text.DateFormat.Field.MONTH;
import static java.text.DateFormat.Field.TIME_ZONE;


public class FieldPositionTest extends TestCase {
    /**
     *
     *
     * @unknown java.text.FieldPosition#FieldPosition(int)
     */
    public void test_ConstructorI() {
        // Test for constructor java.text.FieldPosition(int)
        FieldPosition fpos = new FieldPosition(DateFormat.MONTH_FIELD);
        TestCase.assertEquals("Test1: Constructor failed to set field identifier!", DateFormat.MONTH_FIELD, fpos.getField());
        TestCase.assertNull("Constructor failed to set field attribute!", fpos.getFieldAttribute());
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#FieldPosition(java.text.Format$Field)
     */
    public void test_ConstructorLjava_text_Format$Field() {
        // Test for constructor java.text.FieldPosition(Format.Field)
        FieldPosition fpos = new FieldPosition(MONTH);
        TestCase.assertSame("Constructor failed to set field attribute!", MONTH, fpos.getFieldAttribute());
        TestCase.assertEquals("Test1: Constructor failed to set field identifier!", (-1), fpos.getField());
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#FieldPosition(java.text.Format$Field, int)
     */
    public void test_ConstructorLjava_text_Format$FieldI() {
        // Test for constructor java.text.FieldPosition(Format.Field, int)
        FieldPosition fpos = new FieldPosition(MONTH, DateFormat.MONTH_FIELD);
        TestCase.assertSame("Constructor failed to set field attribute!", MONTH, fpos.getFieldAttribute());
        TestCase.assertEquals("Test1: Constructor failed to set field identifier!", DateFormat.MONTH_FIELD, fpos.getField());
        // test special cases
        FieldPosition fpos2 = new FieldPosition(HOUR1, DateFormat.HOUR1_FIELD);
        TestCase.assertSame("Constructor failed to set field attribute!", HOUR1, fpos2.getFieldAttribute());
        TestCase.assertEquals("Test2: Constructor failed to set field identifier!", DateFormat.HOUR1_FIELD, fpos2.getField());
        FieldPosition fpos3 = new FieldPosition(TIME_ZONE, DateFormat.MONTH_FIELD);
        TestCase.assertSame("Constructor failed to set field attribute!", TIME_ZONE, fpos3.getFieldAttribute());
        TestCase.assertEquals("Test3: Constructor failed to set field identifier!", DateFormat.MONTH_FIELD, fpos3.getField());
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        // Test for method boolean
        // java.text.FieldPosition.equals(java.lang.Object)
        FieldPosition fpos = new FieldPosition(1);
        FieldPosition fpos1 = new FieldPosition(1);
        TestCase.assertTrue("Identical objects were not equal!", fpos.equals(fpos1));
        FieldPosition fpos2 = new FieldPosition(2);
        TestCase.assertTrue("Objects with a different ID should not be equal!", (!(fpos.equals(fpos2))));
        fpos.setBeginIndex(1);
        fpos1.setBeginIndex(2);
        TestCase.assertTrue("Objects with a different beginIndex were still equal!", (!(fpos.equals(fpos1))));
        fpos1.setBeginIndex(1);
        fpos1.setEndIndex(2);
        TestCase.assertTrue("Objects with a different endIndex were still equal!", (!(fpos.equals(fpos1))));
        FieldPosition fpos3 = new FieldPosition(ERA, 1);
        TestCase.assertTrue("Objects with a different attribute should not be equal!", (!(fpos.equals(fpos3))));
        FieldPosition fpos4 = new FieldPosition(AM_PM, 1);
        TestCase.assertTrue("Objects with a different attribute should not be equal!", (!(fpos3.equals(fpos4))));
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#getBeginIndex()
     */
    public void test_getBeginIndex() {
        // Test for method int java.text.FieldPosition.getBeginIndex()
        FieldPosition fpos = new FieldPosition(1);
        fpos.setEndIndex(3);
        fpos.setBeginIndex(2);
        TestCase.assertEquals("getBeginIndex should have returned 2", 2, fpos.getBeginIndex());
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#getEndIndex()
     */
    public void test_getEndIndex() {
        // Test for method int java.text.FieldPosition.getEndIndex()
        FieldPosition fpos = new FieldPosition(1);
        fpos.setBeginIndex(2);
        fpos.setEndIndex(3);
        TestCase.assertEquals("getEndIndex should have returned 3", 3, fpos.getEndIndex());
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#getField()
     */
    public void test_getField() {
        // Test for method int java.text.FieldPosition.getField()
        FieldPosition fpos = new FieldPosition(65);
        TestCase.assertEquals("FieldPosition(65) should have caused getField to return 65", 65, fpos.getField());
        FieldPosition fpos2 = new FieldPosition(MINUTE);
        TestCase.assertEquals("FieldPosition(DateFormat.Field.MINUTE) should have caused getField to return -1", (-1), fpos2.getField());
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#getFieldAttribute()
     */
    public void test_getFieldAttribute() {
        // Test for method int java.text.FieldPosition.getFieldAttribute()
        FieldPosition fpos = new FieldPosition(TIME_ZONE);
        TestCase.assertTrue("FieldPosition(DateFormat.Field.TIME_ZONE) should have caused getFieldAttribute to return DateFormat.Field.TIME_ZONE", ((fpos.getFieldAttribute()) == (TIME_ZONE)));
        FieldPosition fpos2 = new FieldPosition(DateFormat.TIMEZONE_FIELD);
        TestCase.assertNull("FieldPosition(DateFormat.TIMEZONE_FIELD) should have caused getFieldAttribute to return null", fpos2.getFieldAttribute());
    }

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
        FieldPosition fpos = new FieldPosition(1);
        fpos.setEndIndex(3);
        fpos.setBeginIndex(2);
        TestCase.assertEquals("EndIndex should have been set to 3", 3, fpos.getEndIndex());
        fpos.setEndIndex(Integer.MAX_VALUE);
        TestCase.assertEquals("endIndex should have been set to Integer.MAX_VALUE", Integer.MAX_VALUE, fpos.getEndIndex());
        fpos.setEndIndex((-1));
        TestCase.assertEquals("endIndex should have been set to -1", (-1), fpos.getEndIndex());
    }

    /**
     *
     *
     * @unknown java.text.FieldPosition#toString()
     */
    public void test_toString() {
        // Test for method java.lang.String java.text.FieldPosition.toString()
        FieldPosition fpos = new FieldPosition(1);
        fpos.setBeginIndex(2);
        fpos.setEndIndex(3);
        // J2ObjC reflection-stripping change.
        String expected = "java.text.FieldPosition" + "[field=1,attribute=null,beginIndex=2,endIndex=3]";
        TestCase.assertTrue("ToString returned the wrong value:", ReflectionUtil.matchClassNamePrefix(fpos.toString(), expected));
        FieldPosition fpos2 = new FieldPosition(ERA);
        fpos2.setBeginIndex(4);
        fpos2.setEndIndex(5);
        // J2ObjC reflection-stripping change.
        expected = ("java.text.FieldPosition[field=-1,attribute=" + (ERA.toString())) + ",beginIndex=4,endIndex=5]";
        TestCase.assertTrue("ToString returned the wrong value:", ReflectionUtil.matchClassNamePrefix(fpos2.toString(), expected));
    }
}

