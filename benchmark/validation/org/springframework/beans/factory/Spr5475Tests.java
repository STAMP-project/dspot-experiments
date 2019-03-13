/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.beans.factory;


import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;


/**
 * SPR-5475 exposed the fact that the error message displayed when incorrectly
 * invoking a factory method is not instructive to the user and rather misleading.
 *
 * @author Chris Beams
 */
public class Spr5475Tests {
    @Test
    public void noArgFactoryMethodInvokedWithOneArg() {
        assertExceptionMessageForMisconfiguredFactoryMethod(rootBeanDefinition(Spr5475Tests.Foo.class).setFactoryMethod("noArgFactory").addConstructorArgValue("bogusArg").getBeanDefinition(), ("Error creating bean with name 'foo': No matching factory method found: factory method 'noArgFactory(String)'. " + "Check that a method with the specified name and arguments exists and that it is static."));
    }

    @Test
    public void noArgFactoryMethodInvokedWithTwoArgs() {
        assertExceptionMessageForMisconfiguredFactoryMethod(rootBeanDefinition(Spr5475Tests.Foo.class).setFactoryMethod("noArgFactory").addConstructorArgValue("bogusArg1").addConstructorArgValue("bogusArg2".getBytes()).getBeanDefinition(), ("Error creating bean with name 'foo': No matching factory method found: factory method 'noArgFactory(String,byte[])'. " + "Check that a method with the specified name and arguments exists and that it is static."));
    }

    @Test
    public void noArgFactoryMethodInvokedWithTwoArgsAndTypesSpecified() {
        RootBeanDefinition def = new RootBeanDefinition(Spr5475Tests.Foo.class);
        def.setFactoryMethodName("noArgFactory");
        ConstructorArgumentValues cav = new ConstructorArgumentValues();
        cav.addIndexedArgumentValue(0, "bogusArg1", CharSequence.class.getName());
        cav.addIndexedArgumentValue(1, "bogusArg2".getBytes());
        def.setConstructorArgumentValues(cav);
        assertExceptionMessageForMisconfiguredFactoryMethod(def, ("Error creating bean with name 'foo': No matching factory method found: factory method 'noArgFactory(CharSequence,byte[])'. " + "Check that a method with the specified name and arguments exists and that it is static."));
    }

    @Test
    public void singleArgFactoryMethodInvokedWithNoArgs() {
        // calling a factory method that accepts arguments without any arguments emits an exception unlike cases
        // where a no-arg factory method is called with arguments. Adding this test just to document the difference
        assertExceptionMessageForMisconfiguredFactoryMethod(rootBeanDefinition(Spr5475Tests.Foo.class).setFactoryMethod("singleArgFactory").getBeanDefinition(), ("Error creating bean with name 'foo': " + (("Unsatisfied dependency expressed through method 'singleArgFactory' parameter 0: " + "Ambiguous argument values for parameter of type [java.lang.String] - ") + "did you specify the correct bean references as arguments?")));
    }

    static class Foo {
        static Spr5475Tests.Foo noArgFactory() {
            return new Spr5475Tests.Foo();
        }

        static Spr5475Tests.Foo singleArgFactory(String arg) {
            return new Spr5475Tests.Foo();
        }
    }
}

