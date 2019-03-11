/**
 * Copyright (C) 2013 Brett Wooldridge
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
package com.zaxxer.hikari.osgi;


import com.zaxxer.hikari.pool.TestElf;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 *
 *
 * @author lburgazzoli
 */
@RunWith(OSGiBundleTest.ConditionalPaxExam.class)
public class OSGiBundleTest {
    @Test
    public void checkInject() {
        Assert.assertNotNull(context);
    }

    @Test
    public void checkBundle() {
        Boolean bundleFound = false;
        Boolean bundleActive = false;
        Bundle[] bundles = context.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle != null) {
                if (bundle.getSymbolicName().equals("com.zaxxer.HikariCP")) {
                    bundleFound = true;
                    if ((bundle.getState()) == (Bundle.ACTIVE)) {
                        bundleActive = true;
                    }
                }
            }
        }
        Assert.assertTrue(bundleFound);
        Assert.assertTrue(bundleActive);
    }

    @Inject
    BundleContext context;

    public static class ConditionalPaxExam extends PaxExam {
        public ConditionalPaxExam(Class<?> klass) throws InitializationError {
            super(klass);
        }

        @Override
        public void run(RunNotifier notifier) {
            if (!(TestElf.isJava9())) {
                super.run(notifier);
            }
        }

        @Override
        public void filter(Filter filter) throws NoTestsRemainException {
            if (TestElf.isJava9()) {
                throw new NoTestsRemainException();
            }
            super.filter(filter);
        }
    }
}

