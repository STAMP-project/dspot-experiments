/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.sqlobject;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.extension.HandleSupplier;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestSqlMethodDecorators {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Handle handle;

    private static final ThreadLocal<List<String>> INVOCATIONS = ThreadLocal.withInitial(ArrayList::new);

    @Test
    public void testUnordered() {
        TestSqlMethodDecorators.Dao dao = handle.attach(TestSqlMethodDecorators.Dao.class);
        dao.unordered();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).isIn(Arrays.asList("foo", "bar", "method"), Arrays.asList("bar", "foo", "method"));
    }

    @Test
    public void testOrderedFooBar() {
        TestSqlMethodDecorators.Dao dao = handle.attach(TestSqlMethodDecorators.Dao.class);
        dao.orderedFooBar();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).containsExactly("foo", "bar", "method");
    }

    @Test
    public void testOrderedBarFoo() {
        TestSqlMethodDecorators.Dao dao = handle.attach(TestSqlMethodDecorators.Dao.class);
        dao.orderedBarFoo();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).containsExactly("bar", "foo", "method");
    }

    @Test
    public void testOrderedFooBarOnType() {
        TestSqlMethodDecorators.OrderedOnType dao = handle.attach(TestSqlMethodDecorators.OrderedOnType.class);
        dao.orderedFooBarOnType();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).containsExactly("foo", "bar", "method");
    }

    @Test
    public void testOrderedFooBarOnTypeOverriddenToBarFooOnMethod() {
        TestSqlMethodDecorators.OrderedOnType dao = handle.attach(TestSqlMethodDecorators.OrderedOnType.class);
        dao.orderedBarFooOnMethod();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).containsExactly("bar", "foo", "method");
    }

    @Test
    public void testAbortingDecorator() {
        TestSqlMethodDecorators.Dao dao = handle.attach(TestSqlMethodDecorators.Dao.class);
        dao.abortingDecorator();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).containsExactly("foo", "abort");
    }

    @Test
    public void testRegisteredDecorator() {
        handle.getConfig(HandlerDecorators.class).register(( base, sqlObjectType, method) -> ( obj, args, handle) -> {
            invoked("custom");
            return base.invoke(obj, args, handle);
        });
        handle.attach(TestSqlMethodDecorators.Dao.class).orderedFooBar();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).containsExactly("custom", "foo", "bar", "method");
    }

    @Test
    public void testRegisteredDecoratorReturnsBase() {
        handle.getConfig(HandlerDecorators.class).register(( base, sqlObjectType, method) -> base);
        handle.attach(TestSqlMethodDecorators.Dao.class).orderedFooBar();
        assertThat(TestSqlMethodDecorators.INVOCATIONS.get()).containsExactly("foo", "bar", "method");
    }

    public interface Dao {
        @TestSqlMethodDecorators.Foo
        @TestSqlMethodDecorators.Bar
        @TestSqlMethodDecorators.CustomSqlOperation
        void unordered();

        @TestSqlMethodDecorators.Foo
        @TestSqlMethodDecorators.Bar
        @TestSqlMethodDecorators.CustomSqlOperation
        @DecoratorOrder({ TestSqlMethodDecorators.Foo.class, TestSqlMethodDecorators.Bar.class })
        void orderedFooBar();

        @TestSqlMethodDecorators.Foo
        @TestSqlMethodDecorators.Bar
        @TestSqlMethodDecorators.CustomSqlOperation
        @DecoratorOrder({ TestSqlMethodDecorators.Bar.class, TestSqlMethodDecorators.Foo.class })
        void orderedBarFoo();

        @TestSqlMethodDecorators.Foo
        @TestSqlMethodDecorators.Abort
        @TestSqlMethodDecorators.Bar
        @TestSqlMethodDecorators.CustomSqlOperation
        @DecoratorOrder({ TestSqlMethodDecorators.Foo.class, TestSqlMethodDecorators.Abort.class, TestSqlMethodDecorators.Bar.class })
        void abortingDecorator();
    }

    @DecoratorOrder({ TestSqlMethodDecorators.Foo.class, TestSqlMethodDecorators.Bar.class })
    public interface OrderedOnType {
        @TestSqlMethodDecorators.Foo
        @TestSqlMethodDecorators.Bar
        @TestSqlMethodDecorators.CustomSqlOperation
        void orderedFooBarOnType();

        @TestSqlMethodDecorators.Foo
        @TestSqlMethodDecorators.Bar
        @TestSqlMethodDecorators.CustomSqlOperation
        @DecoratorOrder({ TestSqlMethodDecorators.Bar.class, TestSqlMethodDecorators.Foo.class })
        void orderedBarFooOnMethod();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @SqlMethodDecoratingAnnotation(TestSqlMethodDecorators.Foo.Factory.class)
    public @interface Foo {
        class Factory implements HandlerDecorator {
            @Override
            public Handler decorateHandler(Handler base, Class<?> sqlObjectType, Method method) {
                return ( obj, args, handle) -> {
                    invoked("foo");
                    return base.invoke(obj, args, handle);
                };
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @SqlMethodDecoratingAnnotation(TestSqlMethodDecorators.Bar.Factory.class)
    public @interface Bar {
        class Factory implements HandlerDecorator {
            @Override
            public Handler decorateHandler(Handler base, Class<?> sqlObjectType, Method method) {
                return ( obj, args, handle) -> {
                    invoked("bar");
                    return base.invoke(obj, args, handle);
                };
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @SqlMethodDecoratingAnnotation(TestSqlMethodDecorators.Abort.Factory.class)
    public @interface Abort {
        class Factory implements HandlerDecorator {
            @Override
            public Handler decorateHandler(Handler base, Class<?> sqlObjectType, Method method) {
                return ( obj, args, handle) -> {
                    invoked("abort");
                    return null;
                };
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @SqlOperation(TestSqlMethodDecorators.CustomSqlOperation.Impl.class)
    public @interface CustomSqlOperation {
        class Impl implements Handler {
            @Override
            public Object invoke(Object target, Object[] args, HandleSupplier handle) {
                TestSqlMethodDecorators.invoked("method");
                return null;
            }
        }
    }
}

