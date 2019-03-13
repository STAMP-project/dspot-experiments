/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.opengrok.indexer.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;


public class ClassUtilTest {
    private static class TestObject {
        private String strField;

        private int intField;

        private ClassUtilTest.TestObject objField;

        public String getStrField() {
            return strField;
        }

        public void setStrField(String strField) {
            this.strField = strField;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        public ClassUtilTest.TestObject getObjField() {
            return objField;
        }

        public void setObjField(ClassUtilTest.TestObject objField) {
            this.objField = objField;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ClassUtilTest.TestObject testObject = ((ClassUtilTest.TestObject) (o));
            return (((intField) == (testObject.intField)) && (Objects.equals(strField, testObject.strField))) && (Objects.equals(objField, testObject.objField));
        }

        @Override
        public int hashCode() {
            return Objects.hash(strField, intField, objField);
        }
    }

    private ClassUtilTest.TestObject testObject;

    @Test
    public void setStrFieldTest() throws IOException {
        ClassUtil.setFieldValue(testObject, "strField", "value");
        Assert.assertEquals("value", testObject.strField);
    }

    @Test
    public void setIntFieldTest() throws IOException {
        ClassUtil.setFieldValue(testObject, "intField", "124");
        Assert.assertEquals(124, testObject.intField);
    }

    @Test
    public void setObjFieldTest() throws IOException {
        ClassUtilTest.TestObject t = new ClassUtilTest.TestObject();
        t.strField = "test";
        ObjectMapper mapper = new ObjectMapper();
        ClassUtil.setFieldValue(testObject, "objField", mapper.writeValueAsString(t));
        Assert.assertEquals(t, testObject.objField);
    }

    @Test
    public void getStrFieldTest() throws IOException {
        testObject.strField = "value2";
        Assert.assertEquals("value2", ClassUtil.getFieldValue(testObject, "strField"));
    }

    @Test
    public void getIntFieldTest() throws IOException {
        testObject.intField = 1;
        Assert.assertEquals(1, ClassUtil.getFieldValue(testObject, "intField"));
    }

    @Test
    public void getObjFieldTest() throws IOException {
        ClassUtilTest.TestObject t = new ClassUtilTest.TestObject();
        t.strField = "test";
        testObject.objField = t;
        Assert.assertEquals(t, ClassUtil.getFieldValue(testObject, "objField"));
    }
}

