/**
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.weld.ejb.interceptor.ordering;


import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that session bean interceptors are executed in the following order:
 *
 * 1) Interceptors bound using {@link Interceptors} (referred to as "legacy interceptors" hereafter)
 * 2) CDI interceptors
 * 3) Target class around invoke method
 *
 * @see https://issues.jboss.org/browse/AS7-6015
 * @author Jozef Hartinger
 */
@RunWith(Arquillian.class)
public class SessionBeanInterceptorOrderingTest {
    @Inject
    private InterceptedBean bean;

    @Test
    public void testSessionBeanInterceptorOrdering() {
        List<String> expectedSequence = new ArrayList<String>();
        expectedSequence.add("LegacyInterceptor");
        expectedSequence.add("CdiInterceptor");
        expectedSequence.add("TargetClassInterceptor");
        expectedSequence.add("InterceptedBean");
        List<String> actualSequence = new ArrayList<String>();
        bean.ping(actualSequence);
        Assert.assertEquals(expectedSequence, actualSequence);
    }
}

