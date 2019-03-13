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
package org.mybatis.generator.api.dom.java;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class InnerInterfaceTest {
    @Test
    public void testConstructor() {
        InnerInterface innerInterface = new InnerInterface("com.foo.InnerUserInterface");
        Assertions.assertNotNull(innerInterface);
    }

    @Test
    public void testAddSuperInterface() {
        InnerInterface innerInterface = new InnerInterface("com.foo.InnerUserInterface");
        FullyQualifiedJavaType superInterType = new FullyQualifiedJavaType("com.foo.SuperUserInterface");
        innerInterface.addSuperInterface(superInterType);
        Assertions.assertNotNull(innerInterface.getSuperInterfaceTypes());
        Assertions.assertTrue(innerInterface.getSuperInterfaceTypes().contains(superInterType));
    }

    @Test
    public void testAddMethod() {
        InnerInterface interfaze = new InnerInterface("com.foo.UserInterface");
        Method method = new Method("foo");
        interfaze.addMethod(method);
        Assertions.assertNotNull(interfaze.getMethods());
        Assertions.assertEquals(interfaze.getMethods().size(), 1);
        Assertions.assertSame(interfaze.getMethods().get(0), method);
    }

    @Test
    public void testGetType() {
        InnerInterface innerInterface = new InnerInterface("com.foo.InnerUserInterface");
        Assertions.assertNotNull(innerInterface.getType());
        Assertions.assertEquals(innerInterface.getType().getFullyQualifiedName(), "com.foo.InnerUserInterface");
    }

    @Test
    public void testAddInnerInterfaces() {
        InnerInterface interfaze = new InnerInterface("com.foo.UserInterface");
        InnerInterface innerInterfaze = new InnerInterface("com.foo.InnerUserInterface");
        interfaze.addInnerInterface(innerInterfaze);
        Assertions.assertNotNull(interfaze.getInnerInterfaces());
        Assertions.assertEquals(interfaze.getInnerInterfaces().size(), 1);
        Assertions.assertSame(interfaze.getInnerInterfaces().get(0), innerInterfaze);
    }
}

