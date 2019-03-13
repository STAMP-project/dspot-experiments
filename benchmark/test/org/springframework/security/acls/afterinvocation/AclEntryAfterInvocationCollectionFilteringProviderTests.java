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
package org.springframework.security.acls.afterinvocation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;


/**
 *
 *
 * @author Luke Taylor
 */
@SuppressWarnings({ "unchecked" })
public class AclEntryAfterInvocationCollectionFilteringProviderTests {
    @Test
    public void objectsAreRemovedIfPermissionDenied() throws Exception {
        AclService service = Mockito.mock(AclService.class);
        Acl acl = Mockito.mock(Acl.class);
        Mockito.when(acl.isGranted(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(false);
        Mockito.when(service.readAclById(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(acl);
        AclEntryAfterInvocationCollectionFilteringProvider provider = new AclEntryAfterInvocationCollectionFilteringProvider(service, Arrays.asList(Mockito.mock(Permission.class)));
        provider.setObjectIdentityRetrievalStrategy(Mockito.mock(ObjectIdentityRetrievalStrategy.class));
        provider.setProcessDomainObjectClass(Object.class);
        provider.setSidRetrievalStrategy(Mockito.mock(SidRetrievalStrategy.class));
        Object returned = provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("AFTER_ACL_COLLECTION_READ"), new ArrayList(Arrays.asList(new Object(), new Object())));
        assertThat(returned).isInstanceOf(List.class);
        assertThat(((List) (returned))).isEmpty();
        returned = provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("UNSUPPORTED", "AFTER_ACL_COLLECTION_READ"), new Object[]{ new Object(), new Object() });
        assertThat((returned instanceof Object[])).isTrue();
        assertThat(((((Object[]) (returned)).length) == 0)).isTrue();
    }

    @Test
    public void accessIsGrantedIfNoAttributesDefined() throws Exception {
        AclEntryAfterInvocationCollectionFilteringProvider provider = new AclEntryAfterInvocationCollectionFilteringProvider(Mockito.mock(AclService.class), Arrays.asList(Mockito.mock(Permission.class)));
        Object returned = new Object();
        assertThat(returned).isSameAs(provider.decide(Mockito.mock(Authentication.class), new Object(), Collections.<ConfigAttribute>emptyList(), returned));
    }

    @Test
    public void nullReturnObjectIsIgnored() throws Exception {
        AclService service = Mockito.mock(AclService.class);
        AclEntryAfterInvocationCollectionFilteringProvider provider = new AclEntryAfterInvocationCollectionFilteringProvider(service, Arrays.asList(Mockito.mock(Permission.class)));
        assertThat(provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("AFTER_ACL_COLLECTION_READ"), null)).isNull();
        Mockito.verify(service, Mockito.never()).readAclById(ArgumentMatchers.any(ObjectIdentity.class), ArgumentMatchers.any(List.class));
    }
}

