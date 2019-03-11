/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.acls;


import java.util.Locale;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;


/**
 *
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class AclPermissionEvaluatorTests {
    @Test
    public void hasPermissionReturnsTrueIfAclGrantsPermission() throws Exception {
        AclService service = Mockito.mock(AclService.class);
        AclPermissionEvaluator pe = new AclPermissionEvaluator(service);
        ObjectIdentity oid = Mockito.mock(ObjectIdentity.class);
        ObjectIdentityRetrievalStrategy oidStrategy = Mockito.mock(ObjectIdentityRetrievalStrategy.class);
        Mockito.when(oidStrategy.getObjectIdentity(ArgumentMatchers.any(Object.class))).thenReturn(oid);
        pe.setObjectIdentityRetrievalStrategy(oidStrategy);
        pe.setSidRetrievalStrategy(Mockito.mock(SidRetrievalStrategy.class));
        Acl acl = Mockito.mock(Acl.class);
        Mockito.when(service.readAclById(ArgumentMatchers.any(ObjectIdentity.class), ArgumentMatchers.anyList())).thenReturn(acl);
        Mockito.when(acl.isGranted(ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(false))).thenReturn(true);
        assertThat(pe.hasPermission(Mockito.mock(Authentication.class), new Object(), "READ")).isTrue();
    }

    @Test
    public void resolvePermissionNonEnglishLocale() {
        Locale systemLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));
        AclService service = Mockito.mock(AclService.class);
        AclPermissionEvaluator pe = new AclPermissionEvaluator(service);
        ObjectIdentity oid = Mockito.mock(ObjectIdentity.class);
        ObjectIdentityRetrievalStrategy oidStrategy = Mockito.mock(ObjectIdentityRetrievalStrategy.class);
        Mockito.when(oidStrategy.getObjectIdentity(ArgumentMatchers.any(Object.class))).thenReturn(oid);
        pe.setObjectIdentityRetrievalStrategy(oidStrategy);
        pe.setSidRetrievalStrategy(Mockito.mock(SidRetrievalStrategy.class));
        Acl acl = Mockito.mock(Acl.class);
        Mockito.when(service.readAclById(ArgumentMatchers.any(ObjectIdentity.class), ArgumentMatchers.anyList())).thenReturn(acl);
        Mockito.when(acl.isGranted(ArgumentMatchers.anyList(), ArgumentMatchers.anyList(), ArgumentMatchers.eq(false))).thenReturn(true);
        assertThat(pe.hasPermission(Mockito.mock(Authentication.class), new Object(), "write")).isTrue();
        Locale.setDefault(systemLocale);
    }
}

