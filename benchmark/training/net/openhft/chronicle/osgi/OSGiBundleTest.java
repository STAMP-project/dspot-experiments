/**
 * Copyright 2014 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
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
package net.openhft.chronicle.osgi;


import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 *
 *
 * @author lburgazzoli
 */
@Ignore
@RunWith(PaxExam.class)
public class OSGiBundleTest extends OSGiTestBase {
    @Inject
    BundleContext context;

    @Test
    public void checkInject() {
        Assert.assertNotNull(context);
    }

    @Test
    public void checkBundle() {
        Boolean bundleFound = false;
        Bundle[] bundles = context.getBundles();
        for (Bundle bundle : bundles) {
            if (bundle != null) {
                if (bundle.getSymbolicName().equals("net.openhft.chronicle")) {
                    bundleFound = true;
                    Assert.assertEquals(bundle.getState(), Bundle.ACTIVE);
                }
            }
        }
        Assert.assertTrue(bundleFound);
    }
}

