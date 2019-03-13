/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.acls.jdbc;


import java.util.UUID;
import org.junit.Test;
import org.springframework.security.acls.TargetObjectWithUUID;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests the ACL system using ACL class id type of UUID and using an in-memory database.
 *
 * @author Paul Wheeler
 */
@ContextConfiguration(locations = { "/jdbcMutableAclServiceTestsWithAclClass-context.xml" })
public class JdbcMutableAclServiceTestsWithAclClassId extends JdbcMutableAclServiceTests {
    private static final String TARGET_CLASS_WITH_UUID = TargetObjectWithUUID.class.getName();

    private final ObjectIdentity topParentOid = new ObjectIdentityImpl(JdbcMutableAclServiceTestsWithAclClassId.TARGET_CLASS_WITH_UUID, UUID.randomUUID());

    private final ObjectIdentity middleParentOid = new ObjectIdentityImpl(JdbcMutableAclServiceTestsWithAclClassId.TARGET_CLASS_WITH_UUID, UUID.randomUUID());

    private final ObjectIdentity childOid = new ObjectIdentityImpl(JdbcMutableAclServiceTestsWithAclClassId.TARGET_CLASS_WITH_UUID, UUID.randomUUID());

    @Test
    @Transactional
    public void identityWithUuidIdIsSupportedByCreateAcl() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getAuth());
        UUID id = UUID.randomUUID();
        ObjectIdentity oid = new ObjectIdentityImpl(JdbcMutableAclServiceTestsWithAclClassId.TARGET_CLASS_WITH_UUID, id);
        getJdbcMutableAclService().createAcl(oid);
        assertThat(getJdbcMutableAclService().readAclById(new ObjectIdentityImpl(JdbcMutableAclServiceTestsWithAclClassId.TARGET_CLASS_WITH_UUID, id))).isNotNull();
    }
}

