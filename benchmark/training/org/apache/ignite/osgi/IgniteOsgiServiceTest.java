/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.osgi;


import javax.inject.Inject;
import org.apache.ignite.Ignite;
import org.apache.ignite.osgi.activators.TestOsgiFlags;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * Pax Exam test class to check whether the Ignite service is exposed properly and whether lifecycle callbacks
 * are invoked.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class IgniteOsgiServiceTest extends AbstractIgniteKarafTest {
    /**
     * Injects the Ignite OSGi service.
     */
    @Inject
    @Filter("(ignite.name=testGrid)")
    private Ignite ignite;

    @Inject
    private BundleContext bundleCtx;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testServiceExposedAndCallbacksInvoked() throws Exception {
        Assert.assertNotNull(ignite);
        Assert.assertEquals("testGrid", ignite.name());
        TestOsgiFlags flags = ((TestOsgiFlags) (bundleCtx.getService(bundleCtx.getAllServiceReferences(TestOsgiFlags.class.getName(), null)[0])));
        Assert.assertNotNull(flags);
        Assert.assertEquals(Boolean.TRUE, flags.getOnBeforeStartInvoked());
        Assert.assertEquals(Boolean.TRUE, flags.getOnAfterStartInvoked());
        // The bundle is still not stopped, therefore these callbacks cannot be tested.
        Assert.assertNull(flags.getOnBeforeStopInvoked());
        Assert.assertNull(flags.getOnAfterStopInvoked());
        // No exceptions.
        Assert.assertNull(flags.getOnAfterStartThrowable());
        Assert.assertNull(flags.getOnAfterStopThrowable());
    }
}

