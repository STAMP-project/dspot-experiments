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


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;


/**
 *
 *
 * @author Luke Taylor
 */
@SuppressWarnings({ "unchecked" })
public class AclEntryAfterInvocationProviderTests {
    @Test(expected = IllegalArgumentException.class)
    public void rejectsMissingPermissions() throws Exception {
        try {
            new AclEntryAfterInvocationProvider(Mockito.mock(AclService.class), null);
            fail("Exception expected");
        } catch (IllegalArgumentException expected) {
        }
        new AclEntryAfterInvocationProvider(Mockito.mock(AclService.class), Collections.<Permission>emptyList());
    }

    @Test
    public void accessIsAllowedIfPermissionIsGranted() {
        AclService service = Mockito.mock(AclService.class);
        Acl acl = Mockito.mock(Acl.class);
        Mockito.when(acl.isGranted(ArgumentMatchers.any(List.class), ArgumentMatchers.any(List.class), ArgumentMatchers.anyBoolean())).thenReturn(true);
        Mockito.when(service.readAclById(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(acl);
        AclEntryAfterInvocationProvider provider = new AclEntryAfterInvocationProvider(service, Arrays.asList(Mockito.mock(Permission.class)));
        provider.setMessageSource(new SpringSecurityMessageSource());
        provider.setObjectIdentityRetrievalStrategy(Mockito.mock(ObjectIdentityRetrievalStrategy.class));
        provider.setProcessDomainObjectClass(Object.class);
        provider.setSidRetrievalStrategy(Mockito.mock(SidRetrievalStrategy.class));
        Object returned = new Object();
        assertThat(returned).isSameAs(provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("AFTER_ACL_READ"), returned));
    }

    @Test
    public void accessIsGrantedIfNoAttributesDefined() throws Exception {
        AclEntryAfterInvocationProvider provider = new AclEntryAfterInvocationProvider(Mockito.mock(AclService.class), Arrays.asList(Mockito.mock(Permission.class)));
        Object returned = new Object();
        assertThat(returned).isSameAs(provider.decide(Mockito.mock(Authentication.class), new Object(), Collections.<ConfigAttribute>emptyList(), returned));
    }

    @Test
    public void accessIsGrantedIfObjectTypeNotSupported() throws Exception {
        AclEntryAfterInvocationProvider provider = new AclEntryAfterInvocationProvider(Mockito.mock(AclService.class), Arrays.asList(Mockito.mock(Permission.class)));
        provider.setProcessDomainObjectClass(String.class);
        // Not a String
        Object returned = new Object();
        assertThat(returned).isSameAs(provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("AFTER_ACL_READ"), returned));
    }

    @Test(expected = AccessDeniedException.class)
    public void accessIsDeniedIfPermissionIsNotGranted() {
        AclService service = Mockito.mock(AclService.class);
        Acl acl = Mockito.mock(Acl.class);
        Mockito.when(acl.isGranted(ArgumentMatchers.any(List.class), ArgumentMatchers.any(List.class), ArgumentMatchers.anyBoolean())).thenReturn(false);
        // Try a second time with no permissions found
        Mockito.when(acl.isGranted(ArgumentMatchers.any(), ArgumentMatchers.any(List.class), ArgumentMatchers.anyBoolean())).thenThrow(new NotFoundException(""));
        Mockito.when(service.readAclById(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(acl);
        AclEntryAfterInvocationProvider provider = new AclEntryAfterInvocationProvider(service, Arrays.asList(Mockito.mock(Permission.class)));
        provider.setProcessConfigAttribute("MY_ATTRIBUTE");
        provider.setMessageSource(new SpringSecurityMessageSource());
        provider.setObjectIdentityRetrievalStrategy(Mockito.mock(ObjectIdentityRetrievalStrategy.class));
        provider.setProcessDomainObjectClass(Object.class);
        provider.setSidRetrievalStrategy(Mockito.mock(SidRetrievalStrategy.class));
        try {
            provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("UNSUPPORTED", "MY_ATTRIBUTE"), new Object());
            fail("Expected Exception");
        } catch (AccessDeniedException expected) {
        }
        // Second scenario with no acls found
        provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("UNSUPPORTED", "MY_ATTRIBUTE"), new Object());
    }

    @Test
    public void nullReturnObjectIsIgnored() throws Exception {
        AclService service = Mockito.mock(AclService.class);
        AclEntryAfterInvocationProvider provider = new AclEntryAfterInvocationProvider(service, Arrays.asList(Mockito.mock(Permission.class)));
        assertThat(provider.decide(Mockito.mock(Authentication.class), new Object(), SecurityConfig.createList("AFTER_ACL_COLLECTION_READ"), null)).isNull();
        Mockito.verify(service, Mockito.never()).readAclById(ArgumentMatchers.any(ObjectIdentity.class), ArgumentMatchers.any(List.class));
    }
}

