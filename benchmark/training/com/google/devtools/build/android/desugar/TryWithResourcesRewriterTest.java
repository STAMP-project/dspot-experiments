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


import TryWithResourcesRewriter.TARGET_METHODS;
import com.google.devtools.build.android.desugar.io.BitFlags;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtensionTestUtility;
import com.google.devtools.build.android.desugar.testdata.ClassUsingTryWithResources;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * This is the unit test for {@link TryWithResourcesRewriter}
 */
@RunWith(JUnit4.class)
public class TryWithResourcesRewriterTest {
    private final TryWithResourcesRewriterTest.DesugaringClassLoader classLoader = new TryWithResourcesRewriterTest.DesugaringClassLoader(ClassUsingTryWithResources.class.getName());

    private Class<?> desugaredClass;

    @Test
    public void testMethodsAreDesugared() {
        // verify whether the desugared class is indeed desugared.
        TryWithResourcesRewriterTest.DesugaredThrowableMethodCallCounter origCounter = TryWithResourcesRewriterTest.countDesugaredThrowableMethodCalls(ClassUsingTryWithResources.class);
        TryWithResourcesRewriterTest.DesugaredThrowableMethodCallCounter desugaredCounter = TryWithResourcesRewriterTest.countDesugaredThrowableMethodCalls(classLoader.classContent, classLoader);
        /**
         * In java9, javac creates a helper method {@code $closeResource(Throwable, AutoCloseable)
         * to close resources. So, the following number 3 is highly dependant on the version of javac.
         */
        assertThat(TryWithResourcesRewriterTest.hasAutoCloseable(classLoader.classContent)).isFalse();
        assertThat(classLoader.numOfTryWithResourcesInvoked.intValue()).isAtLeast(2);
        assertThat(classLoader.visitedExceptionTypes).containsExactly("java/lang/Exception", "java/lang/Throwable", "java/io/UnsupportedEncodingException");
        TryWithResourcesRewriterTest.assertDesugaringBehavior(origCounter, desugaredCounter);
    }

    @Test
    public void testCheckSuppressedExceptionsReturningEmptySuppressedExceptions() {
        {
            Throwable[] suppressed = ClassUsingTryWithResources.checkSuppressedExceptions(false);
            assertThat(suppressed).isEmpty();
        }
        try {
            Throwable[] suppressed = ((Throwable[]) (desugaredClass.getMethod("checkSuppressedExceptions", boolean.class).invoke(null, Boolean.FALSE)));
            assertThat(suppressed).isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    @Test
    public void testPrintStackTraceOfCaughtException() {
        {
            String trace = ClassUsingTryWithResources.printStackTraceOfCaughtException();
            assertThat(trace.toLowerCase()).contains("suppressed");
        }
        try {
            String trace = ((String) (desugaredClass.getMethod("printStackTraceOfCaughtException").invoke(null)));
            if (ThrowableExtensionTestUtility.isMimicStrategy()) {
                assertThat(trace.toLowerCase()).contains("suppressed");
            } else
                if (ThrowableExtensionTestUtility.isReuseStrategy()) {
                    assertThat(trace.toLowerCase()).contains("suppressed");
                } else
                    if (ThrowableExtensionTestUtility.isNullStrategy()) {
                        assertThat(trace.toLowerCase()).doesNotContain("suppressed");
                    } else {
                        Assert.fail(("unexpected desugaring strategy " + (ThrowableExtension.getStrategy())));
                    }


        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    @Test
    public void testCheckSuppressedExceptionReturningOneSuppressedException() {
        {
            Throwable[] suppressed = ClassUsingTryWithResources.checkSuppressedExceptions(true);
            assertThat(suppressed).hasLength(1);
        }
        try {
            Throwable[] suppressed = ((Throwable[]) (desugaredClass.getMethod("checkSuppressedExceptions", boolean.class).invoke(null, Boolean.TRUE)));
            if (ThrowableExtensionTestUtility.isMimicStrategy()) {
                assertThat(suppressed).hasLength(1);
            } else
                if (ThrowableExtensionTestUtility.isReuseStrategy()) {
                    assertThat(suppressed).hasLength(1);
                } else
                    if (ThrowableExtensionTestUtility.isNullStrategy()) {
                        assertThat(suppressed).isEmpty();
                    } else {
                        Assert.fail(("unexpected desugaring strategy " + (ThrowableExtension.getStrategy())));
                    }


        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    @Test
    public void testSimpleTryWithResources() throws Throwable {
        {
            try {
                ClassUsingTryWithResources.simpleTryWithResources();
                Assert.fail("Expected RuntimeException");
            } catch (RuntimeException expected) {
                assertThat(expected.getClass()).isEqualTo(RuntimeException.class);
                assertThat(expected.getSuppressed()).hasLength(1);
                assertThat(expected.getSuppressed()[0].getClass()).isEqualTo(IOException.class);
            }
        }
        try {
            try {
                desugaredClass.getMethod("simpleTryWithResources").invoke(null);
                Assert.fail("Expected RuntimeException");
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        } catch (RuntimeException expected) {
            String expectedStrategyName = ThrowableExtensionTestUtility.getTwrStrategyClassNameSpecifiedInSystemProperty();
            assertThat(ThrowableExtensionTestUtility.getStrategyClassName()).isEqualTo(expectedStrategyName);
            if (ThrowableExtensionTestUtility.isMimicStrategy()) {
                assertThat(expected.getSuppressed()).isEmpty();
                assertThat(ThrowableExtension.getSuppressed(expected)).hasLength(1);
                assertThat(ThrowableExtension.getSuppressed(expected)[0].getClass()).isEqualTo(IOException.class);
            } else
                if (ThrowableExtensionTestUtility.isReuseStrategy()) {
                    assertThat(expected.getSuppressed()).hasLength(1);
                    assertThat(expected.getSuppressed()[0].getClass()).isEqualTo(IOException.class);
                    assertThat(ThrowableExtension.getSuppressed(expected)[0].getClass()).isEqualTo(IOException.class);
                } else
                    if (ThrowableExtensionTestUtility.isNullStrategy()) {
                        assertThat(expected.getSuppressed()).isEmpty();
                        assertThat(ThrowableExtension.getSuppressed(expected)).isEmpty();
                    } else {
                        Assert.fail(("unexpected desugaring strategy " + (ThrowableExtension.getStrategy())));
                    }


        }
    }

    private static class DesugaredThrowableMethodCallCounter extends ClassVisitor {
        private final ClassLoader classLoader;

        private final Map<String, AtomicInteger> counterMap;

        private int syntheticCloseResourceCount;

        public DesugaredThrowableMethodCallCounter(ClassLoader loader) {
            super(Opcodes.ASM5);
            classLoader = loader;
            counterMap = new HashMap<>();
            TARGET_METHODS.entries().forEach(( entry) -> counterMap.put(((entry.getKey()) + (entry.getValue())), new AtomicInteger()));
            TARGET_METHODS.entries().forEach(( entry) -> counterMap.put(((entry.getKey()) + (TryWithResourcesRewriter.METHOD_DESC_MAP.get(entry.getValue()))), new AtomicInteger()));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ((((BitFlags.isSet(access, ((Opcodes.ACC_SYNTHETIC) | (Opcodes.ACC_STATIC)))) && (name.equals("$closeResource"))) && ((Type.getArgumentTypes(desc).length) == 2)) && (Type.getArgumentTypes(desc)[0].getDescriptor().equals("Ljava/lang/Throwable;"))) {
                ++(syntheticCloseResourceCount);
            }
            return new TryWithResourcesRewriterTest.DesugaredThrowableMethodCallCounter.InvokeCounter();
        }

        private class InvokeCounter extends MethodVisitor {
            public InvokeCounter() {
                super(Opcodes.ASM5);
            }

            private boolean isAssignableToThrowable(String owner) {
                try {
                    Class<?> ownerClass = classLoader.loadClass(owner.replace('/', '.'));
                    return Throwable.class.isAssignableFrom(ownerClass);
                } catch (ClassNotFoundException e) {
                    throw new AssertionError(e);
                }
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                String signature = name + desc;
                if (((opcode == (Opcodes.INVOKEVIRTUAL)) && (isAssignableToThrowable(owner))) || ((opcode == (Opcodes.INVOKESTATIC)) && (Type.getInternalName(ThrowableExtension.class).equals(owner)))) {
                    AtomicInteger counter = counterMap.get(signature);
                    if (counter == null) {
                        return;
                    }
                    counter.incrementAndGet();
                }
            }
        }

        public int getSyntheticCloseResourceCount() {
            return syntheticCloseResourceCount;
        }

        public int countThrowableAddSuppressed() {
            return counterMap.get("addSuppressed(Ljava/lang/Throwable;)V").get();
        }

        public int countThrowableGetSuppressed() {
            return counterMap.get("getSuppressed()[Ljava/lang/Throwable;").get();
        }

        public int countThrowablePrintStackTrace() {
            return counterMap.get("printStackTrace()V").get();
        }

        public int countThrowablePrintStackTracePrintStream() {
            return counterMap.get("printStackTrace(Ljava/io/PrintStream;)V").get();
        }

        public int countThrowablePrintStackTracePrintWriter() {
            return counterMap.get("printStackTrace(Ljava/io/PrintWriter;)V").get();
        }

        public int countExtAddSuppressed() {
            return counterMap.get("addSuppressed(Ljava/lang/Throwable;Ljava/lang/Throwable;)V").get();
        }

        public int countExtGetSuppressed() {
            return counterMap.get("getSuppressed(Ljava/lang/Throwable;)[Ljava/lang/Throwable;").get();
        }

        public int countExtPrintStackTrace() {
            return counterMap.get("printStackTrace(Ljava/lang/Throwable;)V").get();
        }

        public int countExtPrintStackTracePrintStream() {
            return counterMap.get("printStackTrace(Ljava/lang/Throwable;Ljava/io/PrintStream;)V").get();
        }

        public int countExtPrintStackTracePrintWriter() {
            return counterMap.get("printStackTrace(Ljava/lang/Throwable;Ljava/io/PrintWriter;)V").get();
        }
    }

    private static class DesugaringClassLoader extends ClassLoader {
        private final String targetedClassName;

        private Class<?> klass;

        private byte[] classContent;

        private final Set<String> visitedExceptionTypes = new HashSet<>();

        private final AtomicInteger numOfTryWithResourcesInvoked = new AtomicInteger();

        public DesugaringClassLoader(String targetedClassName) {
            super(TryWithResourcesRewriterTest.DesugaringClassLoader.class.getClassLoader());
            this.targetedClassName = targetedClassName;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(targetedClassName)) {
                if ((klass) != null) {
                    return klass;
                }
                // desugar the class, and return the desugared one.
                classContent = desugarTryWithResources(name);
                klass = defineClass(name, classContent, 0, classContent.length);
                return klass;
            } else {
                return super.findClass(name);
            }
        }

        private byte[] desugarTryWithResources(String className) {
            try {
                ClassReader reader = new ClassReader(className);
                CloseResourceMethodScanner scanner = new CloseResourceMethodScanner();
                reader.accept(scanner, ClassReader.SKIP_DEBUG);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
                TryWithResourcesRewriter rewriter = new TryWithResourcesRewriter(writer, TryWithResourcesRewriterTest.class.getClassLoader(), visitedExceptionTypes, numOfTryWithResourcesInvoked, scanner.hasCloseResourceMethod());
                reader.accept(rewriter, 0);
                return writer.toByteArray();
            } catch (IOException e) {
                Assert.fail(e.toString());
                return null;// suppress compiler error.

            }
        }
    }
}

