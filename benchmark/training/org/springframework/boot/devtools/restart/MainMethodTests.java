/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.restart;


import java.lang.reflect.Method;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;


/**
 * Tests for {@link MainMethod}.
 *
 * @author Phillip Webb
 */
public class MainMethodTests {
    private static ThreadLocal<MainMethod> mainMethod = new ThreadLocal<>();

    private Method actualMain;

    @Test
    public void threadMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new MainMethod(null)).withMessageContaining("Thread must not be null");
    }

    @Test
    public void validMainMethod() throws Exception {
        MainMethod method = new MainMethodTests.TestThread(MainMethodTests.Valid::main).test();
        assertThat(method.getMethod()).isEqualTo(this.actualMain);
        assertThat(method.getDeclaringClassName()).isEqualTo(this.actualMain.getDeclaringClass().getName());
    }

    @Test
    public void missingArgsMainMethod() throws Exception {
        assertThatIllegalStateException().isThrownBy(() -> new org.springframework.boot.devtools.restart.TestThread(org.springframework.boot.devtools.restart.MissingArgs::main).test()).withMessageContaining("Unable to find main method");
    }

    @Test
    public void nonStatic() throws Exception {
        assertThatIllegalStateException().isThrownBy(() -> new org.springframework.boot.devtools.restart.TestThread(() -> new org.springframework.boot.devtools.restart.NonStaticMain().main()).test()).withMessageContaining("Unable to find main method");
    }

    private static class TestThread extends Thread {
        private final Runnable runnable;

        private Exception exception;

        private MainMethod mainMethod;

        TestThread(Runnable runnable) {
            this.runnable = runnable;
        }

        public MainMethod test() throws InterruptedException {
            start();
            join();
            if ((this.exception) != null) {
                ReflectionUtils.rethrowRuntimeException(this.exception);
            }
            return this.mainMethod;
        }

        @Override
        public void run() {
            try {
                this.runnable.run();
                this.mainMethod = MainMethodTests.mainMethod.get();
            } catch (Exception ex) {
                this.exception = ex;
            }
        }
    }

    public static class Valid {
        public static void main(String... args) {
            MainMethodTests.Valid.someOtherMethod();
        }

        private static void someOtherMethod() {
            MainMethodTests.mainMethod.set(new MainMethod());
        }
    }

    public static class MissingArgs {
        public static void main() {
            MainMethodTests.mainMethod.set(new MainMethod());
        }
    }

    private static class NonStaticMain {
        public void main(String... args) {
            MainMethodTests.mainMethod.set(new MainMethod());
        }
    }
}

