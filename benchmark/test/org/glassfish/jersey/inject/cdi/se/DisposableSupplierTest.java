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
package org.glassfish.jersey.inject.cdi.se;


import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import org.glassfish.jersey.internal.inject.DisposableSupplier;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.process.internal.RequestScope;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that {@link DisposableSupplier} is properly processed by {@link BeanHelper}.
 *
 * @author Petr Bouda
 */
@Vetoed
public class DisposableSupplierTest {
    private static final Type DISPOSABLE_SUPPLIER_TYPE = getType();

    private static final Type PROXIABLE_DISPOSABLE_SUPPLIER_TYPE = getType();

    private static final Type SUPPLIER_TYPE = getType();

    private InjectionManager injectionManager;

    @Test
    public void testBindSingletonClassDisposableSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class, .class).to(.class));
        Object supplier = injectionManager.getInstance(DisposableSupplierTest.SUPPLIER_TYPE);
        Object disposableSupplier = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertNotNull(supplier);
        Assert.assertNotNull(disposableSupplier);
        Assert.assertSame(supplier, disposableSupplier);
    }

    @Test
    public void testBindPerLookupClassDisposableSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class).to(.class));
        Object supplier = injectionManager.getInstance(DisposableSupplierTest.SUPPLIER_TYPE);
        Object disposableSupplier = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertNotNull(supplier);
        Assert.assertNotNull(disposableSupplier);
        Assert.assertNotSame(supplier, disposableSupplier);
    }

    @Test
    public void testBindInstanceDisposableSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(new org.glassfish.jersey.inject.cdi.se.DisposableSupplierImpl()).to(.class));
        Object supplier = injectionManager.getInstance(DisposableSupplierTest.SUPPLIER_TYPE);
        Object disposableSupplier = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertNotNull(supplier);
        Assert.assertNotNull(disposableSupplier);
        Assert.assertSame(supplier, disposableSupplier);
    }

    @Test
    public void testNotBindClassDisposableSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class).to(.class));
        Assert.assertNull(injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE));
    }

    @Test
    public void testNotBindInstanceDisposableSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(new SupplierGreeting()).to(.class));
        Assert.assertNull(injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE));
    }

    @Test
    public void testOnlyIncrementSingletonSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class, .class).to(.class));
        Object instance1 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("1", ((DisposableSupplier<?>) (instance1)).get());
        Object instance2 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("2", ((DisposableSupplier<?>) (instance2)).get());
        Object instance3 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("3", ((DisposableSupplier<?>) (instance3)).get());
    }

    @Test
    public void testOnlyIncrementInstanceSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(new org.glassfish.jersey.inject.cdi.se.DisposableSupplierImpl()).to(.class));
        Object instance1 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("1", ((DisposableSupplier<?>) (instance1)).get());
        Object instance2 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("2", ((DisposableSupplier<?>) (instance2)).get());
        Object instance3 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("3", ((DisposableSupplier<?>) (instance3)).get());
    }

    @Test
    public void testOnlyIncrementPerLookupSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class).to(.class));
        Object instance1 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("1", ((DisposableSupplier<?>) (instance1)).get());
        Object instance2 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("1", ((DisposableSupplier<?>) (instance2)).get());
        Object instance3 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        Assert.assertEquals("1", ((DisposableSupplier<?>) (instance3)).get());
    }

    @Test
    public void testOnlyIncrementSingletonInstances() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class, .class).to(.class));
        Object instance1 = injectionManager.getInstance(String.class);
        Assert.assertEquals("1", instance1);
        Object instance2 = injectionManager.getInstance(String.class);
        Assert.assertEquals("2", instance2);
        Object instance3 = injectionManager.getInstance(String.class);
        Assert.assertEquals("3", instance3);
    }

    @Test
    public void testOnlyIncrementInstanceInstance() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(new org.glassfish.jersey.inject.cdi.se.DisposableSupplierImpl()).to(.class));
        Object instance1 = injectionManager.getInstance(String.class);
        Assert.assertEquals("1", instance1);
        Object instance2 = injectionManager.getInstance(String.class);
        Assert.assertEquals("2", instance2);
        Object instance3 = injectionManager.getInstance(String.class);
        Assert.assertEquals("3", instance3);
    }

    @Test
    public void testOnlyIncrementPerLookupInstance() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class).to(.class));
        Object instance1 = injectionManager.getInstance(String.class);
        Assert.assertEquals("1", instance1);
        Object instance2 = injectionManager.getInstance(String.class);
        Assert.assertEquals("1", instance2);
        Object instance3 = injectionManager.getInstance(String.class);
        Assert.assertEquals("1", instance3);
    }

    @Test
    public void testDisposeSingletonSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class, .class).to(.class));
        // 1-1
        DisposableSupplier<String> supplier1 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        String instance1 = supplier1.get();
        // 2-2
        DisposableSupplier<String> supplier2 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        String instance2 = supplier2.get();
        // 3-3
        DisposableSupplier<String> supplier3 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        supplier3.get();
        // 2-2
        supplier1.dispose(instance1);
        // 1-1
        supplier2.dispose(instance2);
        // 2-2
        Supplier<String> supplier4 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        String result = supplier4.get();
        Assert.assertEquals("2", result);
    }

    @Test
    public void testDisposePerLookupSupplier() {
        BindingTestHelper.bind(injectionManager, ( binder) -> binder.bindFactory(.class).to(.class));
        // 1
        DisposableSupplier<String> supplier1 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        String instance1 = supplier1.get();
        // 1
        DisposableSupplier<String> supplier2 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        String instance2 = supplier2.get();
        // 1
        DisposableSupplier<String> supplier3 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        supplier3.get();
        // 0
        supplier1.dispose(instance1);
        // 0
        supplier2.dispose(instance2);
        // 1
        Supplier<String> supplier4 = injectionManager.getInstance(DisposableSupplierTest.DISPOSABLE_SUPPLIER_TYPE);
        String result = supplier4.get();
        Assert.assertEquals("1", result);
    }

    @Test
    public void testDisposeSingletonSupplierRequestScopedInstance() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(.class, .class).to(.class).in(.class);
        });
        RequestScope request = injectionManager.getInstance(RequestScope.class);
        AtomicReference<Supplier<DisposableSupplierTest.ProxiableHolder>> atomicSupplier = new AtomicReference<>();
        request.runInScope(() -> {
            // Save Singleton Supplier for later check that the instance was disposed.
            Supplier<org.glassfish.jersey.inject.cdi.se.ProxiableHolder> supplier = injectionManager.getInstance(PROXIABLE_DISPOSABLE_SUPPLIER_TYPE);
            atomicSupplier.set(supplier);
            // All instances should be the same because they are request scoped.
            org.glassfish.jersey.inject.cdi.se.ProxiableHolder instance1 = injectionManager.getInstance(.class);
            assertEquals("1", instance1.getValue());
            org.glassfish.jersey.inject.cdi.se.ProxiableHolder instance2 = injectionManager.getInstance(.class);
            assertEquals("1", instance2.getValue());
        });
        Supplier<DisposableSupplierTest.ProxiableHolder> cleanedSupplier = atomicSupplier.get();
        // Next should be 1-1
        Assert.assertEquals("1", cleanedSupplier.get().getValue());
    }

    /**
     * Tests that object created in request scope is disposing at the time of ending the scope.
     */
    @Test
    public void testDisposePerLookupSupplierRequestScopedInstance() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(.class).to(.class).in(.class);
        });
        RequestScope request = injectionManager.getInstance(RequestScope.class);
        AtomicReference<Supplier<DisposableSupplierTest.ProxiableHolder>> atomicSupplier = new AtomicReference<>();
        request.runInScope(() -> {
            // Save Singleton Supplier for later check that the instance was disposed.
            Supplier<org.glassfish.jersey.inject.cdi.se.ProxiableHolder> supplier = injectionManager.getInstance(PROXIABLE_DISPOSABLE_SUPPLIER_TYPE);
            atomicSupplier.set(supplier);
            // All instances should be the same because they are request scoped.
            org.glassfish.jersey.inject.cdi.se.ProxiableHolder instance1 = injectionManager.getInstance(.class);
            assertEquals("1", instance1.getValue());
            org.glassfish.jersey.inject.cdi.se.ProxiableHolder instance2 = injectionManager.getInstance(.class);
            assertEquals("1", instance2.getValue());
        });
        Supplier<DisposableSupplierTest.ProxiableHolder> cleanedSupplier = atomicSupplier.get();
        // Next should be 1
        Assert.assertEquals("1", cleanedSupplier.get().getValue());
    }

    /**
     * Tests that inherited request scoped is also cleaned by disposing the objects.
     */
    @Test
    public void testDisposeSingletonSupplierMultiRequestScoped() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(.class).to(.class).in(.class);
        });
        RequestScope request = injectionManager.getInstance(RequestScope.class);
        AtomicReference<Supplier<DisposableSupplierTest.ProxiableHolder>> firstSupplier = new AtomicReference<>();
        AtomicReference<Supplier<DisposableSupplierTest.ProxiableHolder>> secondSupplier = new AtomicReference<>();
        request.runInScope(() -> {
            Supplier<org.glassfish.jersey.inject.cdi.se.ProxiableHolder> supplier1 = injectionManager.getInstance(PROXIABLE_DISPOSABLE_SUPPLIER_TYPE);
            firstSupplier.set(supplier1);
            org.glassfish.jersey.inject.cdi.se.ProxiableHolder instance1 = injectionManager.getInstance(.class);
            assertEquals("1", instance1.getValue());
            request.runInScope(() -> {
                // Save Singleton Supplier for later check that the instance was disposed.
                Supplier<org.glassfish.jersey.inject.cdi.se.ProxiableHolder> supplier2 = injectionManager.getInstance(PROXIABLE_DISPOSABLE_SUPPLIER_TYPE);
                secondSupplier.set(supplier2);
                org.glassfish.jersey.inject.cdi.se.ProxiableHolder instance2 = injectionManager.getInstance(.class);
                // 1-2 because the same static class is used in inherited runInScope
                assertEquals("1", instance2.getValue());
            });
        });
        Supplier<DisposableSupplierTest.ProxiableHolder> cleanedSupplier1 = firstSupplier.get();
        Supplier<DisposableSupplierTest.ProxiableHolder> cleanedSupplier2 = secondSupplier.get();
        // Next should be 1-1
        Assert.assertEquals("1", cleanedSupplier1.get().getValue());
        // 1-2 because the same static class is used but the instance is cleaned.
        Assert.assertEquals("1", cleanedSupplier2.get().getValue());
    }

    /**
     * PerLookup fields are not disposed therefore they should never be used as a DisposedSupplier because the field stay in
     * {@link org.glassfish.jersey.inject.cdi.se.bean.SupplierClassBean} forever.
     */
    @Test
    public void testDisposeComposedObjectWithPerLookupFields() {
        BindingTestHelper.bind(injectionManager, ( binder) -> {
            binder.bindFactory(.class, .class).to(.class);
            binder.bindAsContract(.class).in(.class);
        });
        RequestScope request = injectionManager.getInstance(RequestScope.class);
        AtomicReference<Supplier<String>> atomicSupplier = new AtomicReference<>();
        request.runInScope(() -> {
            // Save Singleton Supplier for later check that the instance was disposed.
            Supplier<String> supplier = injectionManager.getInstance(DISPOSABLE_SUPPLIER_TYPE);
            atomicSupplier.set(supplier);
            // All instances should be the same because they are request scoped.
            org.glassfish.jersey.inject.cdi.se.ComposedObject instance = injectionManager.getInstance(.class);
            assertEquals("1", instance.getFirst());
            assertEquals("2", instance.getSecond());
            assertEquals("3", instance.getThird());
        });
        Supplier<String> cleanedSupplier = atomicSupplier.get();
        // Next should be 1 - all instances are disposed and decremented back
        Assert.assertEquals("1", cleanedSupplier.get());
    }

    @Vetoed
    private static class ComposedObject {
        @Inject
        String first;

        @Inject
        String second;

        @Inject
        String third;

        public String getFirst() {
            return first;
        }

        public String getSecond() {
            return second;
        }

        public String getThird() {
            return third;
        }
    }

    @Vetoed
    private static class DisposableSupplierImpl implements DisposableSupplier<String> {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public String get() {
            // Create a new string - don't share the instances in the string pool.
            return new String(((counter.incrementAndGet()) + ""));
        }

        @Override
        public void dispose(final String instance) {
            counter.decrementAndGet();
        }
    }

    @Vetoed
    private static class ProxiableDisposableSupplierImpl implements DisposableSupplier<DisposableSupplierTest.ProxiableHolder> {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public DisposableSupplierTest.ProxiableHolder get() {
            // Create a new string - don't share the instances in the string pool.
            return new DisposableSupplierTest.ProxiableHolder(((counter.incrementAndGet()) + ""));
        }

        @Override
        public void dispose(DisposableSupplierTest.ProxiableHolder instance) {
            counter.decrementAndGet();
        }
    }

    @Vetoed
    private static class ProxiableHolder {
        private String value;

        public ProxiableHolder(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

