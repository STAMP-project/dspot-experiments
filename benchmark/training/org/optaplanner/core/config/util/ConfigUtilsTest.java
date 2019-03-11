/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.config.util;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ConfigUtilsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void mergeProperty() {
        Integer a = null;
        Integer b = null;
        Assert.assertEquals(null, ConfigUtils.mergeProperty(a, b));
        a = Integer.valueOf(1);
        Assert.assertEquals(null, ConfigUtils.mergeProperty(a, b));
        b = Integer.valueOf(10);
        Assert.assertEquals(null, ConfigUtils.mergeProperty(a, b));
        b = Integer.valueOf(1);
        Assert.assertEquals(Integer.valueOf(1), ConfigUtils.mergeProperty(a, b));
        a = null;
        Assert.assertEquals(null, ConfigUtils.mergeProperty(a, b));
    }

    @Test
    public void meldProperty() {
        Integer a = null;
        Integer b = null;
        Assert.assertEquals(null, ConfigUtils.meldProperty(a, b));
        a = Integer.valueOf(1);
        Assert.assertEquals(Integer.valueOf(1), ConfigUtils.meldProperty(a, b));
        b = Integer.valueOf(10);
        Assert.assertEquals(ConfigUtils.mergeProperty(Integer.valueOf(1), Integer.valueOf(10)), ConfigUtils.meldProperty(a, b));
        a = null;
        Assert.assertEquals(Integer.valueOf(10), ConfigUtils.meldProperty(a, b));
    }

    @Test
    public void ceilDivide() {
        Assert.assertEquals(10, ConfigUtils.ceilDivide(19, 2));
        Assert.assertEquals(10, ConfigUtils.ceilDivide(20, 2));
        Assert.assertEquals(11, ConfigUtils.ceilDivide(21, 2));
        Assert.assertEquals((-9), ConfigUtils.ceilDivide(19, (-2)));
        Assert.assertEquals((-10), ConfigUtils.ceilDivide(20, (-2)));
        Assert.assertEquals((-10), ConfigUtils.ceilDivide(21, (-2)));
        Assert.assertEquals((-9), ConfigUtils.ceilDivide((-19), 2));
        Assert.assertEquals((-10), ConfigUtils.ceilDivide((-20), 2));
        Assert.assertEquals((-10), ConfigUtils.ceilDivide((-21), 2));
        Assert.assertEquals(10, ConfigUtils.ceilDivide((-19), (-2)));
        Assert.assertEquals(10, ConfigUtils.ceilDivide((-20), (-2)));
        Assert.assertEquals(11, ConfigUtils.ceilDivide((-21), (-2)));
    }

    @Test(expected = ArithmeticException.class)
    public void ceilDivideByZero() {
        ConfigUtils.ceilDivide(20, (-0));
    }

    @Test
    public void applyCustomProperties() {
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("primitiveBoolean", "true");
        customProperties.put("objectBoolean", "true");
        customProperties.put("primitiveInt", "1");
        customProperties.put("objectInteger", "2");
        customProperties.put("primitiveLong", "3");
        customProperties.put("objectLong", "4");
        customProperties.put("primitiveFloat", "5.5");
        customProperties.put("objectFloat", "6.6");
        customProperties.put("primitiveDouble", "7.7");
        customProperties.put("objectDouble", "8.8");
        customProperties.put("bigDecimal", "9.9");
        customProperties.put("string", "This is a sentence.");
        customProperties.put("configUtilsTestBeanEnum", "BETA");
        ConfigUtilsTest.ConfigUtilsTestBean bean = new ConfigUtilsTest.ConfigUtilsTestBean();
        ConfigUtils.applyCustomProperties(bean, "bean", customProperties, "customProperties");
        Assert.assertEquals(true, bean.primitiveBoolean);
        Assert.assertEquals(Boolean.TRUE, bean.objectBoolean);
        Assert.assertEquals(1, bean.primitiveInt);
        Assert.assertEquals(Integer.valueOf(2), bean.objectInteger);
        Assert.assertEquals(3L, bean.primitiveLong);
        Assert.assertEquals(Long.valueOf(4L), bean.objectLong);
        Assert.assertEquals(5.5F, bean.primitiveFloat, 0.0F);
        Assert.assertEquals(Float.valueOf(6.6F), bean.objectFloat);
        Assert.assertEquals(7.7, bean.primitiveDouble, 0.0);
        Assert.assertEquals(Double.valueOf(8.8), bean.objectDouble);
        Assert.assertEquals(new BigDecimal("9.9"), bean.bigDecimal);
        Assert.assertEquals("This is a sentence.", bean.string);
        Assert.assertEquals(ConfigUtilsTest.ConfigUtilsTestBeanEnum.BETA, bean.configUtilsTestBeanEnum);
    }

    @Test
    public void applyCustomPropertiesSubset() {
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("string", "This is a sentence.");
        ConfigUtilsTest.ConfigUtilsTestBean bean = new ConfigUtilsTest.ConfigUtilsTestBean();
        ConfigUtils.applyCustomProperties(bean, "bean", customProperties, "customProperties");
        Assert.assertEquals("This is a sentence.", bean.string);
    }

    @Test(expected = IllegalStateException.class)
    public void applyCustomPropertiesNonExistingCustomProperty() {
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("doesNotExist", "This is a sentence.");
        ConfigUtilsTest.ConfigUtilsTestBean bean = new ConfigUtilsTest.ConfigUtilsTestBean();
        ConfigUtils.applyCustomProperties(bean, "bean", customProperties, "customProperties");
    }

    private static class ConfigUtilsTestBean {
        private boolean primitiveBoolean;

        private Boolean objectBoolean;

        private int primitiveInt;

        private Integer objectInteger;

        private long primitiveLong;

        private Long objectLong;

        private float primitiveFloat;

        private Float objectFloat;

        private double primitiveDouble;

        private Double objectDouble;

        private BigDecimal bigDecimal;

        private String string;

        private ConfigUtilsTest.ConfigUtilsTestBeanEnum configUtilsTestBeanEnum;

        public void setPrimitiveBoolean(boolean primitiveBoolean) {
            this.primitiveBoolean = primitiveBoolean;
        }

        public void setObjectBoolean(Boolean objectBoolean) {
            this.objectBoolean = objectBoolean;
        }

        public void setPrimitiveInt(int primitiveInt) {
            this.primitiveInt = primitiveInt;
        }

        public void setObjectInteger(Integer objectInteger) {
            this.objectInteger = objectInteger;
        }

        public void setPrimitiveLong(long primitiveLong) {
            this.primitiveLong = primitiveLong;
        }

        public void setObjectLong(Long objectLong) {
            this.objectLong = objectLong;
        }

        public void setPrimitiveFloat(float primitiveFloat) {
            this.primitiveFloat = primitiveFloat;
        }

        public void setObjectFloat(Float objectFloat) {
            this.objectFloat = objectFloat;
        }

        public void setPrimitiveDouble(double primitiveDouble) {
            this.primitiveDouble = primitiveDouble;
        }

        public void setObjectDouble(Double objectDouble) {
            this.objectDouble = objectDouble;
        }

        public void setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public void setString(String string) {
            this.string = string;
        }

        public void setConfigUtilsTestBeanEnum(ConfigUtilsTest.ConfigUtilsTestBeanEnum configUtilsTestBeanEnum) {
            this.configUtilsTestBeanEnum = configUtilsTestBeanEnum;
        }
    }

    private enum ConfigUtilsTestBeanEnum {

        ALPHA,
        BETA,
        GAMMA;}

    @Test
    public void newInstanceStaticInnerClass() {
        Assert.assertNotNull(ConfigUtils.newInstance(this, "testProperty", ConfigUtilsTest.StaticInnerClass.class));
    }

    public static class StaticInnerClass {}

    @Test
    public void newInstanceStaticInnerClassWithArgsConstructor() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("no-arg constructor.");
        Assert.assertNotNull(ConfigUtils.newInstance(this, "testProperty", ConfigUtilsTest.StaticInnerClassWithArgsConstructor.class));
    }

    public static class StaticInnerClassWithArgsConstructor {
        public StaticInnerClassWithArgsConstructor(int i) {
        }
    }

    @Test
    public void newInstanceNonStaticInnerClass() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("inner class");
        Assert.assertNotNull(ConfigUtils.newInstance(this, "testProperty", ConfigUtilsTest.NonStaticInnerClass.class));
    }

    public class NonStaticInnerClass {}

    @Test
    public void newInstanceLocalClass() {
        class LocalClass {}
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("inner class");
        Assert.assertNotNull(ConfigUtils.newInstance(this, "testProperty", LocalClass.class));
    }
}

