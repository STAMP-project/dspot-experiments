/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.access.method;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.util.SimpleMethodInvocation;


/**
 *
 *
 * @author Luke Taylor
 */
@SuppressWarnings({ "unchecked" })
public class DelegatingMethodSecurityMetadataSourceTests {
    DelegatingMethodSecurityMetadataSource mds;

    @Test
    public void returnsEmptyListIfDelegateReturnsNull() throws Exception {
        List sources = new ArrayList();
        MethodSecurityMetadataSource delegate = Mockito.mock(MethodSecurityMetadataSource.class);
        Mockito.when(delegate.getAttributes(Matchers.<Method>any(), Matchers.any(Class.class))).thenReturn(null);
        sources.add(delegate);
        mds = new DelegatingMethodSecurityMetadataSource(sources);
        assertThat(mds.getMethodSecurityMetadataSources()).isSameAs(sources);
        assertThat(mds.getAllConfigAttributes().isEmpty()).isTrue();
        MethodInvocation mi = new SimpleMethodInvocation(null, String.class.getMethod("toString"));
        assertThat(mds.getAttributes(mi)).isEqualTo(Collections.emptyList());
        // Exercise the cached case
        assertThat(mds.getAttributes(mi)).isEqualTo(Collections.emptyList());
    }

    @Test
    public void returnsDelegateAttributes() throws Exception {
        List sources = new ArrayList();
        MethodSecurityMetadataSource delegate = Mockito.mock(MethodSecurityMetadataSource.class);
        ConfigAttribute ca = Mockito.mock(ConfigAttribute.class);
        List attributes = Arrays.asList(ca);
        Method toString = String.class.getMethod("toString");
        Mockito.when(delegate.getAttributes(toString, String.class)).thenReturn(attributes);
        sources.add(delegate);
        mds = new DelegatingMethodSecurityMetadataSource(sources);
        assertThat(mds.getMethodSecurityMetadataSources()).isSameAs(sources);
        assertThat(mds.getAllConfigAttributes().isEmpty()).isTrue();
        MethodInvocation mi = new SimpleMethodInvocation("", toString);
        assertThat(mds.getAttributes(mi)).isSameAs(attributes);
        // Exercise the cached case
        assertThat(mds.getAttributes(mi)).isSameAs(attributes);
        assertThat(mds.getAttributes(new SimpleMethodInvocation(null, String.class.getMethod("length")))).isEmpty();
    }
}

