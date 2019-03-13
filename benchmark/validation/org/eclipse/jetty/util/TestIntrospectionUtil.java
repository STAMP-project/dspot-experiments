/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * TestInjection
 */
public class TestIntrospectionUtil {
    public static final Class<?>[] __INTEGER_ARG = new Class[]{ Integer.class };

    static Field privateAField;

    static Field protectedAField;

    static Field publicAField;

    static Field defaultAField;

    static Field privateBField;

    static Field protectedBField;

    static Field publicBField;

    static Field defaultBField;

    static Method privateCMethod;

    static Method protectedCMethod;

    static Method publicCMethod;

    static Method defaultCMethod;

    static Method privateDMethod;

    static Method protectedDMethod;

    static Method publicDMethod;

    static Method defaultDMethod;

    public class ServletA {
        private Integer privateA;

        protected Integer protectedA;

        Integer defaultA;

        public Integer publicA;
    }

    public class ServletB extends TestIntrospectionUtil.ServletA {
        private String privateB;

        protected String protectedB;

        public String publicB;

        String defaultB;
    }

    public class ServletC {
        private void setPrivateC(Integer c) {
        }

        protected void setProtectedC(Integer c) {
        }

        public void setPublicC(Integer c) {
        }

        void setDefaultC(Integer c) {
        }
    }

    public class ServletD extends TestIntrospectionUtil.ServletC {
        private void setPrivateD(Integer d) {
        }

        protected void setProtectedD(Integer d) {
        }

        public void setPublicD(Integer d) {
        }

        void setDefaultD(Integer d) {
        }
    }

    @Test
    public void testFieldPrivate() throws Exception {
        // direct
        Field f = IntrospectionUtil.findField(TestIntrospectionUtil.ServletA.class, "privateA", Integer.class, true, false);
        Assertions.assertEquals(TestIntrospectionUtil.privateAField, f);
        // inheritance
        Assertions.assertThrows(NoSuchFieldException.class, () -> {
            // Private fields should not be inherited
            IntrospectionUtil.findField(TestIntrospectionUtil.ServletB.class, "privateA", Integer.class, true, false);
        });
    }

    @Test
    public void testFieldProtected() throws Exception {
        // direct
        Field f = IntrospectionUtil.findField(TestIntrospectionUtil.ServletA.class, "protectedA", Integer.class, true, false);
        Assertions.assertEquals(f, TestIntrospectionUtil.protectedAField);
        // inheritance
        f = IntrospectionUtil.findField(TestIntrospectionUtil.ServletB.class, "protectedA", Integer.class, true, false);
        Assertions.assertEquals(f, TestIntrospectionUtil.protectedAField);
    }

    @Test
    public void testFieldPublic() throws Exception {
        // direct
        Field f = IntrospectionUtil.findField(TestIntrospectionUtil.ServletA.class, "publicA", Integer.class, true, false);
        Assertions.assertEquals(f, TestIntrospectionUtil.publicAField);
        // inheritance
        f = IntrospectionUtil.findField(TestIntrospectionUtil.ServletB.class, "publicA", Integer.class, true, false);
        Assertions.assertEquals(f, TestIntrospectionUtil.publicAField);
    }

    @Test
    public void testFieldDefault() throws Exception {
        // direct
        Field f = IntrospectionUtil.findField(TestIntrospectionUtil.ServletA.class, "defaultA", Integer.class, true, false);
        Assertions.assertEquals(f, TestIntrospectionUtil.defaultAField);
        // inheritance
        f = IntrospectionUtil.findField(TestIntrospectionUtil.ServletB.class, "defaultA", Integer.class, true, false);
        Assertions.assertEquals(f, TestIntrospectionUtil.defaultAField);
    }

    @Test
    public void testMethodPrivate() throws Exception {
        // direct
        Method m = IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletC.class, "setPrivateC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        Assertions.assertEquals(m, TestIntrospectionUtil.privateCMethod);
        // inheritance
        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletD.class, "setPrivateC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        });
    }

    @Test
    public void testMethodProtected() throws Exception {
        // direct
        Method m = IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletC.class, "setProtectedC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        Assertions.assertEquals(m, TestIntrospectionUtil.protectedCMethod);
        // inherited
        m = IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletD.class, "setProtectedC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        Assertions.assertEquals(m, TestIntrospectionUtil.protectedCMethod);
    }

    @Test
    public void testMethodPublic() throws Exception {
        // direct
        Method m = IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletC.class, "setPublicC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        Assertions.assertEquals(m, TestIntrospectionUtil.publicCMethod);
        // inherited
        m = IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletD.class, "setPublicC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        Assertions.assertEquals(m, TestIntrospectionUtil.publicCMethod);
    }

    @Test
    public void testMethodDefault() throws Exception {
        // direct
        Method m = IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletC.class, "setDefaultC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        Assertions.assertEquals(m, TestIntrospectionUtil.defaultCMethod);
        // inherited
        m = IntrospectionUtil.findMethod(TestIntrospectionUtil.ServletD.class, "setDefaultC", TestIntrospectionUtil.__INTEGER_ARG, true, false);
        Assertions.assertEquals(m, TestIntrospectionUtil.defaultCMethod);
    }
}

