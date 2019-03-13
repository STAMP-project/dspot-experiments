/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.inject.hk2;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that {@link java.util.function.Supplier} can be registered as a instance-factory.
 *
 * @author Petr Bouda
 */
public class SupplierInstanceBindingTest {
    private static class MySupplier implements Supplier<Integer> {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public Integer get() {
            return counter.incrementAndGet();
        }
    }

    private InjectionManager injectionManager;

    @Test
    public void testInstanceFactory() {
        SupplierInstanceBindingTest.MySupplier supplier = new SupplierInstanceBindingTest.MySupplier();
        supplier.get();
        supplier.get();
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(supplier).to(.class));
        Assert.assertEquals(((Integer) (3)), injectionManager.getInstance(Integer.class));
        Assert.assertEquals(((Integer) (4)), injectionManager.getInstance(Integer.class));
    }

    @Test
    public void testMessages() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(new SupplierGreeting()).to(.class);
            binder.bindAsContract(.class);
        });
        Conversation conversation = injectionManager.getInstance(Conversation.class);
        Assert.assertEquals(CzechGreeting.GREETING, conversation.greeting.getGreeting());
        Assert.assertEquals(CzechGreeting.GREETING, conversation.greetingSupplier.get().getGreeting());
    }

    @Test
    public void testSupplierSingletonInstancePerLookup() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(new SupplierGreeting()).to(.class);
            binder.bindAsContract(.class);
        });
        Greeting greeting1 = injectionManager.getInstance(Conversation.class).greeting;
        Greeting greeting2 = injectionManager.getInstance(Conversation.class).greeting;
        Greeting greeting3 = injectionManager.getInstance(Conversation.class).greeting;
        Assert.assertNotSame(greeting1, greeting2);
        Assert.assertNotSame(greeting2, greeting3);
        Supplier<Greeting> supplier1 = injectionManager.getInstance(Conversation.class).greetingSupplier;
        Supplier<Greeting> supplier2 = injectionManager.getInstance(Conversation.class).greetingSupplier;
        Supplier<Greeting> supplier3 = injectionManager.getInstance(Conversation.class).greetingSupplier;
        Assert.assertSame(supplier1, supplier2);
        Assert.assertSame(supplier2, supplier3);
    }

    @Test
    public void testSupplierSingletonInstanceSingleton() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(.class, .class).to(.class).in(.class);
            binder.bindAsContract(.class);
        });
        Greeting greeting1 = injectionManager.getInstance(Conversation.class).greeting;
        Greeting greeting2 = injectionManager.getInstance(Conversation.class).greeting;
        Greeting greeting3 = injectionManager.getInstance(Conversation.class).greeting;
        Assert.assertSame(greeting1, greeting2);
        Assert.assertSame(greeting2, greeting3);
        Supplier<Greeting> supplier1 = injectionManager.getInstance(Conversation.class).greetingSupplier;
        Supplier<Greeting> supplier2 = injectionManager.getInstance(Conversation.class).greetingSupplier;
        Supplier<Greeting> supplier3 = injectionManager.getInstance(Conversation.class).greetingSupplier;
        Assert.assertSame(supplier1, supplier2);
        Assert.assertSame(supplier2, supplier3);
    }

    @Test
    public void testSupplierBeanNamed() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(new TestSuppliers.TestSupplier()).named(TestSuppliers.TEST).to(.class);
            binder.bindFactory(new TestSuppliers.OtherTestSupplier()).named(TestSuppliers.OTHER_TEST).to(.class);
            binder.bindAsContract(.class);
        });
        TestSuppliers.TargetSupplierBean instance = injectionManager.getInstance(TestSuppliers.TargetSupplierBean.class);
        Assert.assertEquals(TestSuppliers.OTHER_TEST, instance.obj);
    }

    @Test
    public void testSupplierNamed() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(new TestSuppliers.TestSupplier()).named(TestSuppliers.TEST).to(.class);
            binder.bindFactory(new TestSuppliers.OtherTestSupplier()).named(TestSuppliers.OTHER_TEST).to(.class);
            binder.bindAsContract(.class);
        });
        TestSuppliers.TargetSupplier instance = injectionManager.getInstance(TestSuppliers.TargetSupplier.class);
        Assert.assertEquals(TestSuppliers.OTHER_TEST, instance.supplier.get());
    }
}

