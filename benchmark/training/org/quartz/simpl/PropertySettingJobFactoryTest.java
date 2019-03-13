/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz.simpl;


import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;


/**
 * Unit test for PropertySettingJobFactory.
 */
public class PropertySettingJobFactoryTest extends TestCase {
    private PropertySettingJobFactory factory;

    public void testSetBeanPropsPrimatives() throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("intValue", Integer.valueOf(1));
        jobDataMap.put("longValue", Long.valueOf(2L));
        jobDataMap.put("floatValue", Float.valueOf(3.0F));
        jobDataMap.put("doubleValue", Double.valueOf(4.0));
        jobDataMap.put("booleanValue", Boolean.TRUE);
        jobDataMap.put("shortValue", Short.valueOf(((short) (5))));
        jobDataMap.put("charValue", 'a');
        jobDataMap.put("byteValue", Byte.valueOf(((byte) (6))));
        jobDataMap.put("stringValue", "S1");
        jobDataMap.put("mapValue", Collections.singletonMap("A", "B"));
        PropertySettingJobFactoryTest.TestBean myBean = new PropertySettingJobFactoryTest.TestBean();
        factory.setBeanProps(myBean, jobDataMap);
        TestCase.assertEquals(1, myBean.getIntValue());
        TestCase.assertEquals(2L, myBean.getLongValue());
        TestCase.assertEquals(3.0F, myBean.getFloatValue(), 1.0E-4);
        TestCase.assertEquals(4.0, myBean.getDoubleValue(), 1.0E-4);
        TestCase.assertTrue(myBean.getBooleanValue());
        TestCase.assertEquals(5, myBean.getShortValue());
        TestCase.assertEquals('a', myBean.getCharValue());
        TestCase.assertEquals(((byte) (6)), myBean.getByteValue());
        TestCase.assertEquals("S1", myBean.getStringValue());
        TestCase.assertTrue(myBean.getMapValue().containsKey("A"));
    }

    public void testSetBeanPropsUnknownProperty() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("bogusValue", Integer.valueOf(1));
        try {
            factory.setBeanProps(new PropertySettingJobFactoryTest.TestBean(), jobDataMap);
            TestCase.fail();
        } catch (SchedulerException ignore) {
            // ignore
        }
    }

    public void testSetBeanPropsNullPrimative() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("intValue", null);
        try {
            factory.setBeanProps(new PropertySettingJobFactoryTest.TestBean(), jobDataMap);
            TestCase.fail();
        } catch (SchedulerException ignore) {
            // ignore
        }
    }

    public void testSetBeanPropsNullNonPrimative() throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("mapValue", null);
        PropertySettingJobFactoryTest.TestBean testBean = new PropertySettingJobFactoryTest.TestBean();
        testBean.setMapValue(Collections.singletonMap("A", "B"));
        factory.setBeanProps(testBean, jobDataMap);
        TestCase.assertNull(testBean.getMapValue());
    }

    public void testSetBeanPropsWrongPrimativeType() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("intValue", new Float(7));
        try {
            factory.setBeanProps(new PropertySettingJobFactoryTest.TestBean(), jobDataMap);
            TestCase.fail();
        } catch (SchedulerException ignore) {
            // ignore
        }
    }

    public void testSetBeanPropsWrongNonPrimativeType() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("mapValue", new Float(7));
        try {
            factory.setBeanProps(new PropertySettingJobFactoryTest.TestBean(), jobDataMap);
            TestCase.fail();
        } catch (SchedulerException ignore) {
            // ignore
        }
    }

    public void testSetBeanPropsCharStringTooShort() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("charValue", "");
        try {
            factory.setBeanProps(new PropertySettingJobFactoryTest.TestBean(), jobDataMap);
            TestCase.fail();
        } catch (SchedulerException ignore) {
            // ignroe
        }
    }

    public void testSetBeanPropsCharStringTooLong() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("charValue", "abba");
        try {
            factory.setBeanProps(new PropertySettingJobFactoryTest.TestBean(), jobDataMap);
            TestCase.fail();
        } catch (SchedulerException ignore) {
            // ignore
        }
    }

    public void testSetBeanPropsFromStrings() throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("intValue", "1");
        jobDataMap.put("longValue", "2");
        jobDataMap.put("floatValue", "3.0");
        jobDataMap.put("doubleValue", "4.0");
        jobDataMap.put("booleanValue", "true");
        jobDataMap.put("shortValue", "5");
        jobDataMap.put("charValue", "a");
        jobDataMap.put("byteValue", "6");
        PropertySettingJobFactoryTest.TestBean myBean = new PropertySettingJobFactoryTest.TestBean();
        factory.setBeanProps(myBean, jobDataMap);
        TestCase.assertEquals(1, myBean.getIntValue());
        TestCase.assertEquals(2L, myBean.getLongValue());
        TestCase.assertEquals(3.0F, myBean.getFloatValue(), 1.0E-4);
        TestCase.assertEquals(4.0, myBean.getDoubleValue(), 1.0E-4);
        TestCase.assertEquals(true, myBean.getBooleanValue());
        TestCase.assertEquals(5, myBean.getShortValue());
        TestCase.assertEquals('a', myBean.getCharValue());
        TestCase.assertEquals(((byte) (6)), myBean.getByteValue());
    }

    private static final class TestBean {
        private int intValue;

        private long longValue;

        private float floatValue;

        private double doubleValue;

        private boolean booleanValue;

        private byte byteValue;

        private short shortValue;

        private char charValue;

        private String stringValue;

        private Map<?, ?> mapValue;

        public boolean getBooleanValue() {
            return booleanValue;
        }

        @SuppressWarnings("unused")
        public void setBooleanValue(boolean booleanValue) {
            this.booleanValue = booleanValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        @SuppressWarnings("unused")
        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public float getFloatValue() {
            return floatValue;
        }

        @SuppressWarnings("unused")
        public void setFloatValue(float floatValue) {
            this.floatValue = floatValue;
        }

        public int getIntValue() {
            return intValue;
        }

        @SuppressWarnings("unused")
        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public long getLongValue() {
            return longValue;
        }

        @SuppressWarnings("unused")
        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public Map<?, ?> getMapValue() {
            return mapValue;
        }

        public void setMapValue(Map<?, ?> mapValue) {
            this.mapValue = mapValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        @SuppressWarnings("unused")
        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public byte getByteValue() {
            return byteValue;
        }

        @SuppressWarnings("unused")
        public void setByteValue(byte byteValue) {
            this.byteValue = byteValue;
        }

        public char getCharValue() {
            return charValue;
        }

        @SuppressWarnings("unused")
        public void setCharValue(char charValue) {
            this.charValue = charValue;
        }

        public short getShortValue() {
            return shortValue;
        }

        @SuppressWarnings("unused")
        public void setShortValue(short shortValue) {
            this.shortValue = shortValue;
        }
    }
}

