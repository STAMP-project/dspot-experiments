/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.tool;


import java.io.File;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.activemq.tool.properties.ReflectionConfigurable;
import org.apache.activemq.tool.properties.ReflectionUtil;


public class ReflectionUtilTest extends TestCase {
    public void testConfigurableOption() {
        ReflectionUtilTest.TestClass5 data = new ReflectionUtilTest.TestClass5();
        data.willIntercept = true;
        ReflectionUtil.configureClass(data, "this-should-not-matter", "this-should-not-matter");
        TestCase.assertTrue(data.intercepted);
        data.willIntercept = false;
        data.nest = new ReflectionUtilTest.TestClass5();
        data.nest.willIntercept = true;
        ReflectionUtil.configureClass(data, "nest.this-should-not-matter", "this-should-not-matter");
        TestCase.assertTrue(data.intercepted);
        TestCase.assertTrue(data.nest.intercepted);
        data.willIntercept = false;
        data.nest = new ReflectionUtilTest.TestClass5();
        data.nest.willIntercept = false;
        data.nest.nest = new ReflectionUtilTest.TestClass5();
        data.nest.nest.willIntercept = true;
        ReflectionUtil.configureClass(data, "nest.nest.this-should-not-matter", "this-should-not-matter");
        TestCase.assertTrue(data.intercepted);
        TestCase.assertTrue(data.nest.intercepted);
        TestCase.assertTrue(data.nest.nest.intercepted);
        ReflectionUtilTest.TestClass6 data2 = new ReflectionUtilTest.TestClass6();
        data2.nestConfig = new ReflectionUtilTest.TestClass5();
        data2.nestConfig.willIntercept = true;
        ReflectionUtil.configureClass(data2, "nestConfig.this-should-not-matter", "this-should-not-matter");
        TestCase.assertTrue(data2.nestConfig.intercepted);
        data2.nestNotConfig = new ReflectionUtilTest.TestClass6();
        data2.nestNotConfig.nestConfig = new ReflectionUtilTest.TestClass5();
        data2.nestNotConfig.nestConfig.willIntercept = true;
        ReflectionUtil.configureClass(data2, "nestNotConfig.nestConfig.this-should-not-matter", "this-should-not-matter");
        TestCase.assertTrue(data2.nestNotConfig.nestConfig.intercepted);
    }

    public void testDataTypeConfig() {
        ReflectionUtilTest.TestClass3 targetObj = new ReflectionUtilTest.TestClass3();
        // Initialize variables;
        targetObj.setBooleanData(false);
        targetObj.setIntData(0);
        targetObj.setLongData(0);
        targetObj.setShortData(((short) (0)));
        targetObj.setDoubleData(0.0);
        targetObj.setFloatData(0.0F);
        targetObj.setByteData(((byte) (0)));
        targetObj.setCharData('0');
        targetObj.setStringData("false");
        // Set properties
        Properties props = new Properties();
        props.setProperty("booleanData", "true");
        props.setProperty("intData", "1000");
        props.setProperty("longData", "2000");
        props.setProperty("shortData", "3000");
        props.setProperty("doubleData", "1234.567");
        props.setProperty("floatData", "9876.543");
        props.setProperty("byteData", "127");
        props.setProperty("charData", "A");
        props.setProperty("stringData", "true");
        ReflectionUtil.configureClass(targetObj, props);
        // Check config
        TestCase.assertEquals(true, targetObj.isBooleanData());
        TestCase.assertEquals(1000, targetObj.getIntData());
        TestCase.assertEquals(2000, targetObj.getLongData());
        TestCase.assertEquals(3000, targetObj.getShortData());
        TestCase.assertEquals(1234.567, targetObj.getDoubleData(), 1.0E-4);
        TestCase.assertEquals(9876.543, targetObj.getFloatData(), 1.0E-4);
        TestCase.assertEquals(127, targetObj.getByteData());
        TestCase.assertEquals('A', targetObj.getCharData());
        TestCase.assertEquals("true", targetObj.getStringData());
    }

    public void testValueOfMethod() {
        ReflectionUtilTest.TestClass4 targetObj = new ReflectionUtilTest.TestClass4();
        ReflectionUtil.configureClass(targetObj, "testFile", "TEST.FOO.BAR");
        TestCase.assertEquals("TEST.FOO.BAR", targetObj.testFile.toString());
    }

    public void testGetProperties() {
        ReflectionUtilTest.TestClass3 testData = new ReflectionUtilTest.TestClass3();
        testData.setBooleanData(false);
        testData.setByteData(((byte) (15)));
        testData.setCharData('G');
        testData.setDoubleData(765.43);
        testData.setFloatData(543.21F);
        testData.setIntData(654321);
        testData.setLongData(987654321);
        testData.setShortData(((short) (4321)));
        testData.setStringData("BAR.TEST.FOO");
        ReflectionUtilTest.TestClass3 targetObj = new ReflectionUtilTest.TestClass3();
        targetObj.setBooleanData(true);
        targetObj.setByteData(((byte) (10)));
        targetObj.setCharData('D');
        targetObj.setDoubleData(1234.567);
        targetObj.setFloatData(4567.89F);
        targetObj.setIntData(123456);
        targetObj.setLongData(1234567890);
        targetObj.setShortData(((short) (1234)));
        targetObj.setStringData("Test.FOO.BAR");
        targetObj.setTestData(testData);
        Properties p = ReflectionUtil.retrieveObjectProperties(targetObj);
        TestCase.assertEquals("true", p.getProperty("booleanData"));
        TestCase.assertEquals("10", p.getProperty("byteData"));
        TestCase.assertEquals("D", p.getProperty("charData"));
        TestCase.assertEquals("1234.567", p.getProperty("doubleData"));
        TestCase.assertEquals("4567.89", p.getProperty("floatData"));
        TestCase.assertEquals("123456", p.getProperty("intData"));
        TestCase.assertEquals("1234567890", p.getProperty("longData"));
        TestCase.assertEquals("1234", p.getProperty("shortData"));
        TestCase.assertEquals("Test.FOO.BAR", p.getProperty("stringData"));
        TestCase.assertEquals("false", p.getProperty("testData.booleanData"));
        TestCase.assertEquals("15", p.getProperty("testData.byteData"));
        TestCase.assertEquals("G", p.getProperty("testData.charData"));
        TestCase.assertEquals("765.43", p.getProperty("testData.doubleData"));
        TestCase.assertEquals("543.21", p.getProperty("testData.floatData"));
        TestCase.assertEquals("654321", p.getProperty("testData.intData"));
        TestCase.assertEquals("987654321", p.getProperty("testData.longData"));
        TestCase.assertEquals("4321", p.getProperty("testData.shortData"));
        TestCase.assertEquals("BAR.TEST.FOO", p.getProperty("testData.stringData"));
    }

    public void testNestedConfig() {
        ReflectionUtilTest.TestClass3 t1 = new ReflectionUtilTest.TestClass3();
        ReflectionUtilTest.TestClass3 t2 = new ReflectionUtilTest.TestClass3();
        ReflectionUtilTest.TestClass3 t3 = new ReflectionUtilTest.TestClass3();
        ReflectionUtilTest.TestClass3 t4 = new ReflectionUtilTest.TestClass3();
        ReflectionUtilTest.TestClass3 t5 = new ReflectionUtilTest.TestClass3();
        ReflectionUtil.configureClass(t1, "stringData", "t1");
        TestCase.assertEquals("t1", t1.getStringData());
        t1.setTestData(t2);
        ReflectionUtil.configureClass(t1, "testData.stringData", "t2");
        TestCase.assertEquals("t2", t2.getStringData());
        t2.setTestData(t3);
        ReflectionUtil.configureClass(t1, "testData.testData.stringData", "t3");
        TestCase.assertEquals("t3", t3.getStringData());
        t3.setTestData(t4);
        ReflectionUtil.configureClass(t1, "testData.testData.testData.stringData", "t4");
        TestCase.assertEquals("t4", t4.getStringData());
        t4.setTestData(t5);
        ReflectionUtil.configureClass(t1, "testData.testData.testData.testData.stringData", "t5");
        TestCase.assertEquals("t5", t5.getStringData());
    }

    public class TestClass1 {
        private boolean booleanData;

        private int intData;

        private long longData;

        public boolean isBooleanData() {
            return booleanData;
        }

        public void setBooleanData(boolean booleanData) {
            this.booleanData = booleanData;
        }

        public int getIntData() {
            return intData;
        }

        public void setIntData(int intData) {
            this.intData = intData;
        }

        public long getLongData() {
            return longData;
        }

        public void setLongData(long longData) {
            this.longData = longData;
        }
    }

    public class TestClass2 extends ReflectionUtilTest.TestClass1 {
        private float floatData;

        private byte byteData;

        private char charData;

        public float getFloatData() {
            return floatData;
        }

        public void setFloatData(float floatData) {
            this.floatData = floatData;
        }

        public byte getByteData() {
            return byteData;
        }

        public void setByteData(byte byteData) {
            this.byteData = byteData;
        }

        public char getCharData() {
            return charData;
        }

        public void setCharData(char charData) {
            this.charData = charData;
        }
    }

    public class TestClass3 extends ReflectionUtilTest.TestClass2 {
        private short shortData;

        private double doubleData;

        private String stringData;

        private ReflectionUtilTest.TestClass3 testData;

        public short getShortData() {
            return shortData;
        }

        public void setShortData(short shortData) {
            this.shortData = shortData;
        }

        public double getDoubleData() {
            return doubleData;
        }

        public void setDoubleData(double doubleData) {
            this.doubleData = doubleData;
        }

        public String getStringData() {
            return stringData;
        }

        public void setStringData(String stringData) {
            this.stringData = stringData;
        }

        public ReflectionUtilTest.TestClass3 getTestData() {
            return testData;
        }

        public void setTestData(ReflectionUtilTest.TestClass3 testData) {
            this.testData = testData;
        }

        public ReflectionUtilTest.TestClass3 getRecursiveData() {
            return this;
        }
    }

    public class TestClass4 {
        private File testFile;

        public String getTestFile() {
            return testFile.toString();
        }

        public void setTestFile(String testFile) {
            this.testFile = new File(testFile);
        }
    }

    public class TestClass5 implements ReflectionConfigurable {
        public boolean intercepted;

        public boolean willIntercept = true;

        public ReflectionUtilTest.TestClass5 nest;

        public void configureProperties(Properties props) {
            // Do nothing
        }

        public Properties retrieveProperties(Properties props) {
            return null;
        }

        public boolean acceptConfig(String key, String val) {
            intercepted = true;
            return !(willIntercept);
        }

        public ReflectionUtilTest.TestClass5 getNest() {
            return nest;
        }

        public void setNest(ReflectionUtilTest.TestClass5 nest) {
            this.nest = nest;
        }
    }

    public class TestClass6 {
        public ReflectionUtilTest.TestClass6 nestNotConfig;

        public ReflectionUtilTest.TestClass5 nestConfig;

        public ReflectionUtilTest.TestClass6 getNestNotConfig() {
            return nestNotConfig;
        }

        public void setNestNotConfig(ReflectionUtilTest.TestClass6 nestNotConfig) {
            this.nestNotConfig = nestNotConfig;
        }

        public ReflectionUtilTest.TestClass5 getNestConfig() {
            return nestConfig;
        }

        public void setNestConfig(ReflectionUtilTest.TestClass5 nestConfig) {
            this.nestConfig = nestConfig;
        }
    }
}

