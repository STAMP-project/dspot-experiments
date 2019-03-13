/**
 * Copyright 2012-2019 the original author or authors.
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


import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ThreadFactory;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFile.Kind;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFiles;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link Restarter}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class RestarterTests {
    @Rule
    public OutputCapture out = new OutputCapture();

    @Test
    public void cantGetInstanceBeforeInitialize() {
        Restarter.clearInstance();
        assertThatIllegalStateException().isThrownBy(Restarter::getInstance).withMessageContaining("Restarter has not been initialized");
    }

    @Test
    public void testRestart() throws Exception {
        Restarter.clearInstance();
        Thread thread = new Thread(RestarterTests.SampleApplication::main);
        thread.start();
        Thread.sleep(2600);
        String output = this.out.toString();
        assertThat(StringUtils.countOccurrencesOf(output, "Tick 0")).isGreaterThan(1);
        assertThat(StringUtils.countOccurrencesOf(output, "Tick 1")).isGreaterThan(1);
        assertThat(RestarterTests.CloseCountingApplicationListener.closed).isGreaterThan(0);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getOrAddAttributeWithNewAttribute() {
        ObjectFactory objectFactory = Mockito.mock(ObjectFactory.class);
        BDDMockito.given(objectFactory.getObject()).willReturn("abc");
        Object attribute = Restarter.getInstance().getOrAddAttribute("x", objectFactory);
        assertThat(attribute).isEqualTo("abc");
    }

    @Test
    public void addUrlsMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> Restarter.getInstance().addUrls(null)).withMessageContaining("Urls must not be null");
    }

    @Test
    public void addUrls() throws Exception {
        URL url = new URL("file:/proj/module-a.jar!/");
        Collection<URL> urls = Collections.singleton(url);
        Restarter restarter = Restarter.getInstance();
        restarter.addUrls(urls);
        restarter.restart();
        ClassLoader classLoader = ((RestarterTests.TestableRestarter) (restarter)).getRelaunchClassLoader();
        assertThat(((URLClassLoader) (classLoader)).getURLs()[0]).isEqualTo(url);
    }

    @Test
    public void addClassLoaderFilesMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> Restarter.getInstance().addClassLoaderFiles(null)).withMessageContaining("ClassLoaderFiles must not be null");
    }

    @Test
    public void addClassLoaderFiles() {
        ClassLoaderFiles classLoaderFiles = new ClassLoaderFiles();
        classLoaderFiles.addFile("f", new org.springframework.boot.devtools.restart.classloader.ClassLoaderFile(Kind.ADDED, "abc".getBytes()));
        Restarter restarter = Restarter.getInstance();
        restarter.addClassLoaderFiles(classLoaderFiles);
        restarter.restart();
        ClassLoader classLoader = ((RestarterTests.TestableRestarter) (restarter)).getRelaunchClassLoader();
        assertThat(classLoader.getResourceAsStream("f")).hasContent("abc");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void getOrAddAttributeWithExistingAttribute() {
        Restarter.getInstance().getOrAddAttribute("x", () -> "abc");
        ObjectFactory objectFactory = Mockito.mock(ObjectFactory.class);
        Object attribute = Restarter.getInstance().getOrAddAttribute("x", objectFactory);
        assertThat(attribute).isEqualTo("abc");
        Mockito.verifyZeroInteractions(objectFactory);
    }

    @Test
    public void getThreadFactory() throws Exception {
        final ClassLoader parentLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader contextClassLoader = new URLClassLoader(new URL[0]);
        Thread thread = new Thread(() -> {
            Runnable runnable = Mockito.mock(Runnable.class);
            Thread regular = new Thread();
            ThreadFactory factory = Restarter.getInstance().getThreadFactory();
            Thread viaFactory = factory.newThread(runnable);
            // Regular threads will inherit the current thread
            assertThat(regular.getContextClassLoader()).isEqualTo(contextClassLoader);
            // Factory threads should inherit from the initial thread
            assertThat(viaFactory.getContextClassLoader()).isEqualTo(parentLoader);
        });
        thread.setContextClassLoader(contextClassLoader);
        thread.start();
        thread.join();
    }

    @Test
    public void getInitialUrls() throws Exception {
        Restarter.clearInstance();
        RestartInitializer initializer = Mockito.mock(RestartInitializer.class);
        URL[] urls = new URL[]{ new URL("file:/proj/module-a.jar!/") };
        BDDMockito.given(initializer.getInitialUrls(ArgumentMatchers.any(Thread.class))).willReturn(urls);
        Restarter.initialize(new String[0], false, initializer, false);
        assertThat(Restarter.getInstance().getInitialUrls()).isEqualTo(urls);
    }

    @Component
    @EnableScheduling
    public static class SampleApplication {
        private int count = 0;

        private static volatile boolean quit = false;

        @Scheduled(fixedDelay = 200)
        public void tickBean() {
            System.out.println(((("Tick " + ((this.count)++)) + " ") + (Thread.currentThread())));
        }

        @Scheduled(initialDelay = 500, fixedDelay = 500)
        public void restart() {
            System.out.println(("Restart " + (Thread.currentThread())));
            if (!(RestarterTests.SampleApplication.quit)) {
                Restarter.getInstance().restart();
            }
        }

        public static void main(String... args) {
            Restarter.initialize(args, false, new MockRestartInitializer(), true);
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RestarterTests.SampleApplication.class);
            context.addApplicationListener(new RestarterTests.CloseCountingApplicationListener());
            Restarter.getInstance().prepare(context);
            System.out.println(("Sleep " + (Thread.currentThread())));
            RestarterTests.SampleApplication.sleep();
            RestarterTests.SampleApplication.quit = true;
        }

        private static void sleep() {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException ex) {
                // Ignore
            }
        }
    }

    private static class CloseCountingApplicationListener implements ApplicationListener<ContextClosedEvent> {
        static int closed = 0;

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            (RestarterTests.CloseCountingApplicationListener.closed)++;
        }
    }

    private static class TestableRestarter extends Restarter {
        private ClassLoader relaunchClassLoader;

        TestableRestarter() {
            this(Thread.currentThread(), new String[]{  }, false, new MockRestartInitializer());
        }

        protected TestableRestarter(Thread thread, String[] args, boolean forceReferenceCleanup, RestartInitializer initializer) {
            super(thread, args, forceReferenceCleanup, initializer);
        }

        @Override
        public void restart(FailureHandler failureHandler) {
            try {
                stop();
                start(failureHandler);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        protected Throwable relaunch(ClassLoader classLoader) {
            this.relaunchClassLoader = classLoader;
            return null;
        }

        @Override
        protected void stop() {
        }

        public ClassLoader getRelaunchClassLoader() {
            return this.relaunchClassLoader;
        }
    }

    static class RestarterInitializer {
        static void setRestarterInstance() {
            RestarterTests.RestarterInitializer.main(new String[0]);
        }

        static void main(String[] args) {
            Restarter.setInstance(new RestarterTests.TestableRestarter());
        }
    }
}

