/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.context.event;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.StaticApplicationContext;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 */
public class LifecycleEventTests {
    @Test
    public void contextStartedEvent() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerSingleton("lifecycle", LifecycleEventTests.LifecycleTestBean.class);
        context.registerSingleton("listener", LifecycleEventTests.LifecycleListener.class);
        context.refresh();
        LifecycleEventTests.LifecycleTestBean lifecycleBean = ((LifecycleEventTests.LifecycleTestBean) (context.getBean("lifecycle")));
        LifecycleEventTests.LifecycleListener listener = ((LifecycleEventTests.LifecycleListener) (context.getBean("listener")));
        Assert.assertFalse(lifecycleBean.isRunning());
        Assert.assertEquals(0, listener.getStartedCount());
        context.start();
        Assert.assertTrue(lifecycleBean.isRunning());
        Assert.assertEquals(1, listener.getStartedCount());
        Assert.assertSame(context, listener.getApplicationContext());
    }

    @Test
    public void contextStoppedEvent() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerSingleton("lifecycle", LifecycleEventTests.LifecycleTestBean.class);
        context.registerSingleton("listener", LifecycleEventTests.LifecycleListener.class);
        context.refresh();
        LifecycleEventTests.LifecycleTestBean lifecycleBean = ((LifecycleEventTests.LifecycleTestBean) (context.getBean("lifecycle")));
        LifecycleEventTests.LifecycleListener listener = ((LifecycleEventTests.LifecycleListener) (context.getBean("listener")));
        Assert.assertFalse(lifecycleBean.isRunning());
        context.start();
        Assert.assertTrue(lifecycleBean.isRunning());
        Assert.assertEquals(0, listener.getStoppedCount());
        context.stop();
        Assert.assertFalse(lifecycleBean.isRunning());
        Assert.assertEquals(1, listener.getStoppedCount());
        Assert.assertSame(context, listener.getApplicationContext());
    }

    private static class LifecycleListener implements ApplicationListener<ApplicationEvent> {
        private ApplicationContext context;

        private int startedCount;

        private int stoppedCount;

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            if (event instanceof ContextStartedEvent) {
                this.context = ((ContextStartedEvent) (event)).getApplicationContext();
                (this.startedCount)++;
            } else
                if (event instanceof ContextStoppedEvent) {
                    this.context = ((ContextStoppedEvent) (event)).getApplicationContext();
                    (this.stoppedCount)++;
                }

        }

        public ApplicationContext getApplicationContext() {
            return this.context;
        }

        public int getStartedCount() {
            return this.startedCount;
        }

        public int getStoppedCount() {
            return this.stoppedCount;
        }
    }

    private static class LifecycleTestBean implements Lifecycle {
        private boolean running;

        @Override
        public boolean isRunning() {
            return this.running;
        }

        @Override
        public void start() {
            this.running = true;
        }

        @Override
        public void stop() {
            this.running = false;
        }
    }
}

