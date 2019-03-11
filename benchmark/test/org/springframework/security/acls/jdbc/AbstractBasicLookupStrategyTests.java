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


import BasePermission.READ;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.sf.ehcache.CacheManager;
import org.junit.Test;
import org.springframework.security.acls.TargetObject;
import org.springframework.security.acls.TargetObjectWithUUID;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;


/**
 * Tests {@link BasicLookupStrategy}
 *
 * @author Andrei Stefan
 */
public abstract class AbstractBasicLookupStrategyTests {
    protected static final Sid BEN_SID = new PrincipalSid("ben");

    protected static final String TARGET_CLASS = TargetObject.class.getName();

    protected static final String TARGET_CLASS_WITH_UUID = TargetObjectWithUUID.class.getName();

    protected static final UUID OBJECT_IDENTITY_UUID = UUID.randomUUID();

    protected static final Long OBJECT_IDENTITY_LONG_AS_UUID = 110L;

    // ~ Instance fields
    // ================================================================================================
    private BasicLookupStrategy strategy;

    private static CacheManager cacheManager;

    @Test
    public void testAclsRetrievalWithDefaultBatchSize() throws Exception {
        ObjectIdentity topParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(100));
        ObjectIdentity middleParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(101));
        // Deliberately use an integer for the child, to reproduce bug report in SEC-819
        ObjectIdentity childOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Integer.valueOf(102));
        Map<ObjectIdentity, Acl> map = this.strategy.readAclsById(Arrays.asList(topParentOid, middleParentOid, childOid), null);
        checkEntries(topParentOid, middleParentOid, childOid, map);
    }

    @Test
    public void testAclsRetrievalFromCacheOnly() throws Exception {
        ObjectIdentity topParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Integer.valueOf(100));
        ObjectIdentity middleParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(101));
        ObjectIdentity childOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(102));
        // Objects were put in cache
        strategy.readAclsById(Arrays.asList(topParentOid, middleParentOid, childOid), null);
        // Let's empty the database to force acls retrieval from cache
        emptyDatabase();
        Map<ObjectIdentity, Acl> map = this.strategy.readAclsById(Arrays.asList(topParentOid, middleParentOid, childOid), null);
        checkEntries(topParentOid, middleParentOid, childOid, map);
    }

    @Test
    public void testAclsRetrievalWithCustomBatchSize() throws Exception {
        ObjectIdentity topParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(100));
        ObjectIdentity middleParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Integer.valueOf(101));
        ObjectIdentity childOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(102));
        // Set a batch size to allow multiple database queries in order to retrieve all
        // acls
        this.strategy.setBatchSize(1);
        Map<ObjectIdentity, Acl> map = this.strategy.readAclsById(Arrays.asList(topParentOid, middleParentOid, childOid), null);
        checkEntries(topParentOid, middleParentOid, childOid, map);
    }

    @Test
    public void testAllParentsAreRetrievedWhenChildIsLoaded() throws Exception {
        String query = "INSERT INTO acl_object_identity(ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,PARENT_OBJECT,OWNER_SID,ENTRIES_INHERITING) VALUES (6,2,103,1,1,1);";
        getJdbcTemplate().execute(query);
        ObjectIdentity topParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Long.valueOf(100));
        ObjectIdentity middleParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Long.valueOf(101));
        ObjectIdentity childOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Long.valueOf(102));
        ObjectIdentity middleParent2Oid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Long.valueOf(103));
        // Retrieve the child
        Map<ObjectIdentity, Acl> map = this.strategy.readAclsById(Arrays.asList(childOid), null);
        // Check that the child and all its parents were retrieved
        assertThat(map.get(childOid)).isNotNull();
        assertThat(map.get(childOid).getObjectIdentity()).isEqualTo(childOid);
        assertThat(map.get(middleParentOid)).isNotNull();
        assertThat(map.get(middleParentOid).getObjectIdentity()).isEqualTo(middleParentOid);
        assertThat(map.get(topParentOid)).isNotNull();
        assertThat(map.get(topParentOid).getObjectIdentity()).isEqualTo(topParentOid);
        // The second parent shouldn't have been retrieved
        assertThat(map.get(middleParent2Oid)).isNull();
    }

    /**
     * Test created from SEC-590.
     */
    @Test
    public void testReadAllObjectIdentitiesWhenLastElementIsAlreadyCached() throws Exception {
        String query = "INSERT INTO acl_object_identity(ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,PARENT_OBJECT,OWNER_SID,ENTRIES_INHERITING) VALUES (6,2,105,null,1,1);" + ((("INSERT INTO acl_object_identity(ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,PARENT_OBJECT,OWNER_SID,ENTRIES_INHERITING) VALUES (7,2,106,6,1,1);" + "INSERT INTO acl_object_identity(ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,PARENT_OBJECT,OWNER_SID,ENTRIES_INHERITING) VALUES (8,2,107,6,1,1);") + "INSERT INTO acl_object_identity(ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,PARENT_OBJECT,OWNER_SID,ENTRIES_INHERITING) VALUES (9,2,108,7,1,1);") + "INSERT INTO acl_entry(ID,ACL_OBJECT_IDENTITY,ACE_ORDER,SID,MASK,GRANTING,AUDIT_SUCCESS,AUDIT_FAILURE) VALUES (7,6,0,1,1,1,0,0)");
        getJdbcTemplate().execute(query);
        ObjectIdentity grandParentOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(104));
        ObjectIdentity parent1Oid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(105));
        ObjectIdentity parent2Oid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Integer.valueOf(106));
        ObjectIdentity childOid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, Integer.valueOf(107));
        // First lookup only child, thus populating the cache with grandParent,
        // parent1
        // and child
        List<Permission> checkPermission = Arrays.asList(READ);
        List<Sid> sids = Arrays.asList(AbstractBasicLookupStrategyTests.BEN_SID);
        List<ObjectIdentity> childOids = Arrays.asList(childOid);
        strategy.setBatchSize(6);
        Map<ObjectIdentity, Acl> foundAcls = strategy.readAclsById(childOids, sids);
        Acl foundChildAcl = foundAcls.get(childOid);
        assertThat(foundChildAcl).isNotNull();
        assertThat(foundChildAcl.isGranted(checkPermission, sids, false)).isTrue();
        // Search for object identities has to be done in the following order:
        // last
        // element have to be one which
        // is already in cache and the element before it must not be stored in
        // cache
        List<ObjectIdentity> allOids = Arrays.asList(grandParentOid, parent1Oid, parent2Oid, childOid);
        try {
            foundAcls = strategy.readAclsById(allOids, sids);
        } catch (NotFoundException notExpected) {
            fail("It shouldn't have thrown NotFoundException");
        }
        Acl foundParent2Acl = foundAcls.get(parent2Oid);
        assertThat(foundParent2Acl).isNotNull();
        assertThat(foundParent2Acl.isGranted(checkPermission, sids, false)).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullOwnerIsNotSupported() {
        String query = "INSERT INTO acl_object_identity(ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,PARENT_OBJECT,OWNER_SID,ENTRIES_INHERITING) VALUES (6,2,104,null,null,1);";
        getJdbcTemplate().execute(query);
        ObjectIdentity oid = new ObjectIdentityImpl(AbstractBasicLookupStrategyTests.TARGET_CLASS, new Long(104));
        strategy.readAclsById(Arrays.asList(oid), Arrays.asList(AbstractBasicLookupStrategyTests.BEN_SID));
    }

    @Test
    public void testCreatePrincipalSid() {
        Sid result = strategy.createSid(true, "sid");
        assertThat(result.getClass()).isEqualTo(PrincipalSid.class);
        assertThat(getPrincipal()).isEqualTo("sid");
    }

    @Test
    public void testCreateGrantedAuthority() {
        Sid result = strategy.createSid(false, "sid");
        assertThat(result.getClass()).isEqualTo(GrantedAuthoritySid.class);
        assertThat(getGrantedAuthority()).isEqualTo("sid");
    }
}

