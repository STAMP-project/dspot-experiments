/**
 * Copyright 2006-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.internal.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;


/**
 *
 *
 * @author Jeff Butler
 */
public class JavaBeansUtilTest {
    /**
     *
     */
    public JavaBeansUtilTest() {
        super();
    }

    @Test
    public void testGetValidPropertyName() {
        Assertions.assertEquals("eMail", JavaBeansUtil.getValidPropertyName("eMail"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("firstName", JavaBeansUtil.getValidPropertyName("firstName"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("URL", JavaBeansUtil.getValidPropertyName("URL"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("XAxis", JavaBeansUtil.getValidPropertyName("XAxis"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("a", JavaBeansUtil.getValidPropertyName("a"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("b", JavaBeansUtil.getValidPropertyName("B"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("yaxis", JavaBeansUtil.getValidPropertyName("Yaxis"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("i_PARAM_INT_1", JavaBeansUtil.getValidPropertyName("I_PARAM_INT_1"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("_fred", JavaBeansUtil.getValidPropertyName("_fred"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("accountType", JavaBeansUtil.getValidPropertyName("AccountType"));// $NON-NLS-1$ //$NON-NLS-2$

    }

    @Test
    public void testGetGetterMethodName() {
        Assertions.assertEquals("geteMail", JavaBeansUtil.getGetterMethodName("eMail", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("getFirstName", JavaBeansUtil.getGetterMethodName("firstName", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("getURL", JavaBeansUtil.getGetterMethodName("URL", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("getXAxis", JavaBeansUtil.getGetterMethodName("XAxis", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("getA", JavaBeansUtil.getGetterMethodName("a", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("isActive", JavaBeansUtil.getGetterMethodName("active", FullyQualifiedJavaType.getBooleanPrimitiveInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("getI_PARAM_INT_1", JavaBeansUtil.getGetterMethodName("i_PARAM_INT_1", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("get_fred", JavaBeansUtil.getGetterMethodName("_fred", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("getAccountType", JavaBeansUtil.getGetterMethodName("AccountType", FullyQualifiedJavaType.getStringInstance()));// $NON-NLS-1$ //$NON-NLS-2$

    }

    @Test
    public void testGetSetterMethodName() {
        Assertions.assertEquals("seteMail", JavaBeansUtil.getSetterMethodName("eMail"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("setFirstName", JavaBeansUtil.getSetterMethodName("firstName"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("setURL", JavaBeansUtil.getSetterMethodName("URL"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("setXAxis", JavaBeansUtil.getSetterMethodName("XAxis"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("setA", JavaBeansUtil.getSetterMethodName("a"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("setI_PARAM_INT_1", JavaBeansUtil.getSetterMethodName("i_PARAM_INT_1"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("set_fred", JavaBeansUtil.getSetterMethodName("_fred"));// $NON-NLS-1$ //$NON-NLS-2$

        Assertions.assertEquals("setAccountType", JavaBeansUtil.getSetterMethodName("AccountType"));// $NON-NLS-1$ //$NON-NLS-2$

    }
}

