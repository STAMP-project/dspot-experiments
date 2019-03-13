/**
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.beans.factory.annotation;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.wiring.BeanWiringInfo;

import static Autowire.NO;


/**
 *
 *
 * @author Rick Evans
 * @author Chris Beams
 */
public class AnnotationBeanWiringInfoResolverTests {
    @Test
    public void testResolveWiringInfo() throws Exception {
        try {
            new AnnotationBeanWiringInfoResolver().resolveWiringInfo(null);
            Assert.fail("Must have thrown an IllegalArgumentException by this point (null argument)");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testResolveWiringInfoWithAnInstanceOfANonAnnotatedClass() {
        AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
        BeanWiringInfo info = resolver.resolveWiringInfo("java.lang.String is not @Configurable");
        Assert.assertNull("Must be returning null for a non-@Configurable class instance", info);
    }

    @Test
    public void testResolveWiringInfoWithAnInstanceOfAnAnnotatedClass() {
        AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
        BeanWiringInfo info = resolver.resolveWiringInfo(new AnnotationBeanWiringInfoResolverTests.Soap());
        Assert.assertNotNull("Must *not* be returning null for a non-@Configurable class instance", info);
    }

    @Test
    public void testResolveWiringInfoWithAnInstanceOfAnAnnotatedClassWithAutowiringTurnedOffExplicitly() {
        AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
        BeanWiringInfo info = resolver.resolveWiringInfo(new AnnotationBeanWiringInfoResolverTests.WirelessSoap());
        Assert.assertNotNull("Must *not* be returning null for an @Configurable class instance even when autowiring is NO", info);
        Assert.assertFalse(info.indicatesAutowiring());
        Assert.assertEquals(AnnotationBeanWiringInfoResolverTests.WirelessSoap.class.getName(), info.getBeanName());
    }

    @Test
    public void testResolveWiringInfoWithAnInstanceOfAnAnnotatedClassWithAutowiringTurnedOffExplicitlyAndCustomBeanName() {
        AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
        BeanWiringInfo info = resolver.resolveWiringInfo(new AnnotationBeanWiringInfoResolverTests.NamedWirelessSoap());
        Assert.assertNotNull("Must *not* be returning null for an @Configurable class instance even when autowiring is NO", info);
        Assert.assertFalse(info.indicatesAutowiring());
        Assert.assertEquals("DerBigStick", info.getBeanName());
    }

    @Configurable
    private static class Soap {}

    @Configurable(autowire = NO)
    private static class WirelessSoap {}

    @Configurable(autowire = Autowire.NO, value = "DerBigStick")
    private static class NamedWirelessSoap {}
}

