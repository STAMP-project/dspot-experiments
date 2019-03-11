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
package org.springframework.security.acls;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;


/**
 *
 *
 * @author Luke Taylor
 */
@SuppressWarnings({ "unchecked" })
public class AclPermissionCacheOptimizerTests {
    @Test
    public void eagerlyLoadsRequiredAcls() throws Exception {
        AclService service = Mockito.mock(AclService.class);
        AclPermissionCacheOptimizer pco = new AclPermissionCacheOptimizer(service);
        ObjectIdentityRetrievalStrategy oidStrat = Mockito.mock(ObjectIdentityRetrievalStrategy.class);
        SidRetrievalStrategy sidStrat = Mockito.mock(SidRetrievalStrategy.class);
        pco.setObjectIdentityRetrievalStrategy(oidStrat);
        pco.setSidRetrievalStrategy(sidStrat);
        Object[] dos = new Object[]{ new Object(), null, new Object() };
        ObjectIdentity[] oids = new ObjectIdentity[]{ new ObjectIdentityImpl("A", "1"), new ObjectIdentityImpl("A", "2") };
        Mockito.when(oidStrat.getObjectIdentity(dos[0])).thenReturn(oids[0]);
        Mockito.when(oidStrat.getObjectIdentity(dos[2])).thenReturn(oids[1]);
        pco.cachePermissionsFor(Mockito.mock(Authentication.class), Arrays.asList(dos));
        // AclService should be invoked with the list of required Oids
        Mockito.verify(service).readAclsById(ArgumentMatchers.eq(Arrays.asList(oids)), ArgumentMatchers.any(List.class));
    }

    @Test
    public void ignoresEmptyCollection() {
        AclService service = Mockito.mock(AclService.class);
        AclPermissionCacheOptimizer pco = new AclPermissionCacheOptimizer(service);
        ObjectIdentityRetrievalStrategy oids = Mockito.mock(ObjectIdentityRetrievalStrategy.class);
        SidRetrievalStrategy sids = Mockito.mock(SidRetrievalStrategy.class);
        pco.setObjectIdentityRetrievalStrategy(oids);
        pco.setSidRetrievalStrategy(sids);
        pco.cachePermissionsFor(Mockito.mock(Authentication.class), Collections.emptyList());
        Mockito.verifyZeroInteractions(service, sids, oids);
    }
}

