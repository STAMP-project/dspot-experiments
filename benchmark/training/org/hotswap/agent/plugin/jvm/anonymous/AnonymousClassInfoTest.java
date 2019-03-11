/**
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package org.hotswap.agent.plugin.jvm.anonymous;


import org.junit.Assert;
import org.junit.Test;


/**
 * Check generated signatures same with java Class and javassist CtClass.
 */
public class AnonymousClassInfoTest {
    @Test
    public void testGetClassSignature() throws Exception {
        String classSignature = "java.lang.Object;" + (AnonymousTestInterface1.class.getName());
        Assert.assertEquals(classSignature, getAnonymousClassInfo().getClassSignature());
        Assert.assertEquals(classSignature, getAnonymousCtClassInfo().getClassSignature());
    }

    @Test
    public void testGetMethodSignature() throws Exception {
        String methodsSignature = "java.lang.String test1();";
        Assert.assertEquals(methodsSignature, getAnonymousClassInfo().getMethodSignature());
        Assert.assertEquals(methodsSignature, getAnonymousCtClassInfo().getMethodSignature());
    }

    @Test
    public void testGetFieldsSignature() throws Exception {
        // default field this
        String fieldsSignature = "org.hotswap.agent.plugin.jvm.anonymous.AnonymousTestClass1 this$0;";
        Assert.assertEquals(fieldsSignature, getAnonymousClassInfo().getFieldsSignature());
        Assert.assertEquals(fieldsSignature, getAnonymousCtClassInfo().getFieldsSignature());
    }

    @Test
    public void testGetEnclosingMethodSignature() throws Exception {
        String enclosingMethodSignature = "java.lang.String enclosing1();";
        Assert.assertEquals(enclosingMethodSignature, getAnonymousClassInfo().getEnclosingMethodSignature());
        Assert.assertEquals(enclosingMethodSignature, getAnonymousCtClassInfo().getEnclosingMethodSignature());
    }
}

