/**
 * Copyright 2010 the original author or authors.
 *
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
package powermock.classloading;


import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.core.classloader.MockClassLoader;
import org.powermock.core.classloader.javassist.JavassistMockClassLoader;
import powermock.classloading.classes.MyArgument;
import powermock.classloading.classes.MyClass;
import powermock.classloading.classes.MyEnum;
import powermock.classloading.classes.MyEnumHolder;
import powermock.classloading.classes.MyHierarchicalFieldHolder;
import powermock.classloading.classes.MyIntegerHolder;
import powermock.classloading.classes.MyPrimitiveArrayHolder;
import powermock.classloading.classes.MyReferenceFieldHolder;
import powermock.classloading.classes.MyReturnValue;
import powermock.classloading.classes.ReflectionMethodInvoker;


@Ignore("Test are failed on JDK more that 1.6. On Travis we can run only on JDK8 and JDK9")
public class ObjenesisClassloaderExecutorTest {
    @Test
    public void loadsObjectGraphInSpecifiedClassloaderAndReturnsResultInOriginalClassloader() throws Exception {
        MockClassLoader classloader = createClassloader();
        final MyReturnValue expectedConstructorValue = new MyReturnValue(new MyArgument("first value"));
        final MyClass myClass = new MyClass(expectedConstructorValue);
        final MyArgument expected = new MyArgument("A value");
        MyReturnValue[] actual = new org.powermock.classloading.SingleClassloaderExecutor(classloader).execute(new Callable<MyReturnValue[]>() {
            public MyReturnValue[] call() throws Exception {
                Assert.assertEquals(JavassistMockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                return myClass.myMethod(expected);
            }
        });
        Assert.assertFalse(MockClassLoader.class.getName().equals(this.getClass().getClassLoader().getClass().getName()));
        final MyReturnValue myReturnValue = actual[0];
        Assert.assertEquals(expectedConstructorValue.getMyArgument().getValue(), myReturnValue.getMyArgument().getValue());
        Assert.assertEquals(expected.getValue(), actual[1].getMyArgument().getValue());
    }

    @Test
    public void loadsObjectGraphThatIncludesPrimitiveValuesInSpecifiedClassloaderAndReturnsResultInOriginalClassloader() throws Exception {
        MockClassLoader classloader = createClassloader();
        final Integer expected = 42;
        final MyIntegerHolder myClass = new MyIntegerHolder(expected);
        Integer actual = new org.powermock.classloading.SingleClassloaderExecutor(classloader).execute(new Callable<Integer>() {
            public Integer call() throws Exception {
                Assert.assertEquals(JavassistMockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                final int myInteger = myClass.getMyInteger();
                Assert.assertEquals(((int) (expected)), myInteger);
                return myInteger;
            }
        });
        Assert.assertFalse(MockClassLoader.class.getName().equals(this.getClass().getClassLoader().getClass().getName()));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void loadsObjectGraphThatIncludesEnumsInSpecifiedClassloaderAndReturnsResultInOriginalClassloader() throws Exception {
        MockClassLoader classloader = createClassloader();
        final MyEnum expected = MyEnum.MyEnum1;
        final MyEnumHolder myClass = new MyEnumHolder(expected);
        MyEnum actual = new org.powermock.classloading.SingleClassloaderExecutor(classloader).execute(new Callable<MyEnum>() {
            public MyEnum call() throws Exception {
                Assert.assertEquals(JavassistMockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                MyEnum myEnum = myClass.getMyEnum();
                Assert.assertEquals(expected, myEnum);
                return myEnum;
            }
        });
        Assert.assertFalse(MockClassLoader.class.getName().equals(this.getClass().getClassLoader().getClass().getName()));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void loadsObjectGraphThatIncludesPrimitiveArraysInSpecifiedClassloaderAndReturnsResultInOriginalClassloader() throws Exception {
        MockClassLoader classloader = createClassloader();
        final int[] expected = new int[]{ 1, 2 };
        final MyPrimitiveArrayHolder myClass = new MyPrimitiveArrayHolder(expected);
        int[] actual = new org.powermock.classloading.SingleClassloaderExecutor(classloader).execute(new Callable<int[]>() {
            public int[] call() throws Exception {
                Assert.assertEquals(JavassistMockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                int[] myArray = myClass.getMyArray();
                Assert.assertArrayEquals(expected, myArray);
                return myArray;
            }
        });
        Assert.assertFalse(MockClassLoader.class.getName().equals(this.getClass().getClassLoader().getClass().getName()));
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void usesReferenceCloningWhenTwoFieldsPointToSameInstance() throws Exception {
        final MockClassLoader classloader = createClassloader();
        final MyReferenceFieldHolder tested = new MyReferenceFieldHolder();
        Assert.assertSame(tested.getMyArgument1(), tested.getMyArgument2());
        Assert.assertSame(tested.getMyArgument1(), MyReferenceFieldHolder.MY_ARGUMENT);
        new org.powermock.classloading.SingleClassloaderExecutor(classloader).execute(new Runnable() {
            public void run() {
                Assert.assertEquals(JavassistMockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                Assert.assertEquals(tested.getMyArgument1(), tested.getMyArgument2());
                Assert.assertEquals(tested.getMyArgument1(), MyReferenceFieldHolder.MY_ARGUMENT);
                Assert.assertSame(tested.getMyArgument1(), tested.getMyArgument2());
                // FIXME: This assertion should work:
                // assertSame(tested.getMyArgument1(), MyReferenceFieldHolder.MY_ARGUMENT);
            }
        });
    }

    @Test
    public void worksWithObjectHierarchy() throws Exception {
        final MockClassLoader classloader = createClassloader();
        final MyHierarchicalFieldHolder tested = new MyHierarchicalFieldHolder();
        Assert.assertSame(tested.getMyArgument1(), tested.getMyArgument2());
        Assert.assertEquals(tested.getMyArgument3(), tested.getMyArgument2());
        new org.powermock.classloading.SingleClassloaderExecutor(classloader).execute(new Runnable() {
            public void run() {
                Assert.assertEquals(JavassistMockClassLoader.class.getName(), this.getClass().getClassLoader().getClass().getName());
                Assert.assertSame(tested.getMyArgument1(), tested.getMyArgument2());
                Assert.assertEquals(tested.getMyArgument3(), tested.getMyArgument2());
            }
        });
    }

    @Test
    public void worksWithReflection() throws Exception {
        final MockClassLoader classloader = createClassloader();
        final MyArgument myArgument = new MyArgument("test");
        final MyReturnValue instance = new MyReturnValue(myArgument);
        Method method = instance.getClass().getMethod("getMyArgument");
        final ReflectionMethodInvoker tested = new ReflectionMethodInvoker(method, instance);
        execute(new Runnable() {
            public void run() {
                Object invoke = tested.invoke();
                Assert.assertSame(invoke, myArgument);
            }
        });
    }
}

