/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.desugar;


import com.google.common.io.Closer;
import java.io.Serializable;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static SubtypeComparator.INSTANCE;


/**
 * Unit Test for {@link DefaultMethodClassFixer}
 */
@RunWith(JUnit4.class)
public class DefaultMethodClassFixerTest {
    private ClassReaderFactory classpathReader;

    private ClassReaderFactory bootclassPath;

    private ClassLoader classLoader;

    private Closer closer;

    @Test
    public void testDesugaringDirectImplementation() {
        byte[] desugaredClass = desugar(("com.google.devtools.build.android.desugar.testdata.java8." + "DefaultInterfaceMethodWithStaticInitializer$TestInterfaceSetOne$C").replace('.', '/'));
        checkClinitForDefaultInterfaceMethodWithStaticInitializerTestInterfaceSetOneC(desugaredClass);
        byte[] desugaredClassAgain = desugar(desugaredClass);
        checkClinitForDefaultInterfaceMethodWithStaticInitializerTestInterfaceSetOneC(desugaredClassAgain);
        desugaredClassAgain = desugar(desugaredClassAgain);
        checkClinitForDefaultInterfaceMethodWithStaticInitializerTestInterfaceSetOneC(desugar(desugaredClassAgain));
    }

    @Test
    public void testInterfaceComparator() {
        assertThat(SubtypeComparator.INSTANCE.compare(Runnable.class, Runnable.class)).isEqualTo(0);
        assertThat(SubtypeComparator.INSTANCE.compare(Runnable.class, DefaultMethodClassFixerTest.MyRunnable1.class)).isEqualTo(1);
        assertThat(SubtypeComparator.INSTANCE.compare(DefaultMethodClassFixerTest.MyRunnable2.class, Runnable.class)).isEqualTo((-1));
        assertThat(SubtypeComparator.INSTANCE.compare(DefaultMethodClassFixerTest.MyRunnable3.class, Runnable.class)).isEqualTo((-1));
        assertThat(SubtypeComparator.INSTANCE.compare(DefaultMethodClassFixerTest.MyRunnable1.class, DefaultMethodClassFixerTest.MyRunnable3.class)).isEqualTo(1);
        assertThat(SubtypeComparator.INSTANCE.compare(DefaultMethodClassFixerTest.MyRunnable3.class, DefaultMethodClassFixerTest.MyRunnable2.class)).isEqualTo((-1));
        assertThat(SubtypeComparator.INSTANCE.compare(DefaultMethodClassFixerTest.MyRunnable2.class, DefaultMethodClassFixerTest.MyRunnable1.class)).isGreaterThan(0);
        assertThat(SubtypeComparator.INSTANCE.compare(Runnable.class, Serializable.class)).isGreaterThan(0);
        assertThat(SubtypeComparator.INSTANCE.compare(Serializable.class, Runnable.class)).isLessThan(0);
        TreeSet<Class<?>> orderedSet = new TreeSet(INSTANCE);
        orderedSet.add(Serializable.class);
        orderedSet.add(Runnable.class);
        orderedSet.add(DefaultMethodClassFixerTest.MyRunnable2.class);
        orderedSet.add(Callable.class);
        orderedSet.add(Serializable.class);
        orderedSet.add(DefaultMethodClassFixerTest.MyRunnable1.class);
        orderedSet.add(DefaultMethodClassFixerTest.MyRunnable3.class);
        // subtype before supertype(s)
        // java... comes textually after com.google...
        assertThat(orderedSet).containsExactly(DefaultMethodClassFixerTest.MyRunnable3.class, DefaultMethodClassFixerTest.MyRunnable1.class, DefaultMethodClassFixerTest.MyRunnable2.class, Serializable.class, Runnable.class, Callable.class).inOrder();
    }

    private static interface MyRunnable1 extends Runnable {}

    private static interface MyRunnable2 extends Runnable {}

    private static interface MyRunnable3 extends DefaultMethodClassFixerTest.MyRunnable1 , DefaultMethodClassFixerTest.MyRunnable2 {}
}

