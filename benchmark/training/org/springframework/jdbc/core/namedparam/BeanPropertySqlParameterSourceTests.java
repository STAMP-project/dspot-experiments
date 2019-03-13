/**
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jdbc.core.namedparam;


import java.sql.Types;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 */
public class BeanPropertySqlParameterSourceTests {
    @Test(expected = IllegalArgumentException.class)
    public void withNullBeanPassedToCtor() throws Exception {
        new BeanPropertySqlParameterSource(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getValueWhereTheUnderlyingBeanHasNoSuchProperty() throws Exception {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean());
        source.getValue("thisPropertyDoesNotExist");
    }

    @Test
    public void successfulPropertyAccess() {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean("tb", 99));
        Assert.assertTrue(Arrays.asList(source.getReadablePropertyNames()).contains("name"));
        Assert.assertTrue(Arrays.asList(source.getReadablePropertyNames()).contains("age"));
        Assert.assertEquals("tb", source.getValue("name"));
        Assert.assertEquals(99, source.getValue("age"));
        Assert.assertEquals(Types.VARCHAR, source.getSqlType("name"));
        Assert.assertEquals(Types.INTEGER, source.getSqlType("age"));
    }

    @Test
    public void successfulPropertyAccessWithOverriddenSqlType() {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean("tb", 99));
        source.registerSqlType("age", Types.NUMERIC);
        Assert.assertEquals("tb", source.getValue("name"));
        Assert.assertEquals(99, source.getValue("age"));
        Assert.assertEquals(Types.VARCHAR, source.getSqlType("name"));
        Assert.assertEquals(Types.NUMERIC, source.getSqlType("age"));
    }

    @Test
    public void hasValueWhereTheUnderlyingBeanHasNoSuchProperty() throws Exception {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new TestBean());
        Assert.assertFalse(source.hasValue("thisPropertyDoesNotExist"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getValueWhereTheUnderlyingBeanPropertyIsNotReadable() throws Exception {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new BeanPropertySqlParameterSourceTests.NoReadableProperties());
        source.getValue("noOp");
    }

    @Test
    public void hasValueWhereTheUnderlyingBeanPropertyIsNotReadable() throws Exception {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(new BeanPropertySqlParameterSourceTests.NoReadableProperties());
        Assert.assertFalse(source.hasValue("noOp"));
    }

    @SuppressWarnings("unused")
    private static final class NoReadableProperties {
        public void setNoOp(String noOp) {
        }
    }
}

