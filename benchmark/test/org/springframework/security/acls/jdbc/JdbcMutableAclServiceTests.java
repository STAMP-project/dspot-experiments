/**
 * Copyright 2004, 2005, 2006, 2017 Acegi Technology Pty Limited
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


import BasePermission.ADMINISTRATION;
import BasePermission.CREATE;
import BasePermission.DELETE;
import BasePermission.READ;
import BasePermission.WRITE;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.TargetObject;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.sid.CustomSid;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration tests the ACL system using an in-memory database.
 *
 * @author Ben Alex
 * @author Andrei Stefan
 */
@ContextConfiguration(locations = { "/jdbcMutableAclServiceTests-context.xml" })
public class JdbcMutableAclServiceTests extends AbstractTransactionalJUnit4SpringContextTests {
    // ~ Constant fields
    // ================================================================================================
    private static final String TARGET_CLASS = TargetObject.class.getName();

    private final Authentication auth = new TestingAuthenticationToken("ben", "ignored", "ROLE_ADMINISTRATOR");

    public static final String SELECT_ALL_CLASSES = "SELECT * FROM acl_class WHERE class = ?";

    // ~ Instance fields
    // ================================================================================================
    private final ObjectIdentity topParentOid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(100));

    private final ObjectIdentity middleParentOid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(101));

    private final ObjectIdentity childOid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(102));

    @Autowired
    private JdbcMutableAclService jdbcMutableAclService;

    @Autowired
    private AclCache aclCache;

    @Autowired
    private LookupStrategy lookupStrategy;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    public void testLifecycle() {
        SecurityContextHolder.getContext().setAuthentication(auth);
        MutableAcl topParent = jdbcMutableAclService.createAcl(getTopParentOid());
        MutableAcl middleParent = jdbcMutableAclService.createAcl(getMiddleParentOid());
        MutableAcl child = jdbcMutableAclService.createAcl(getChildOid());
        // Specify the inheritance hierarchy
        middleParent.setParent(topParent);
        child.setParent(middleParent);
        // Now let's add a couple of permissions
        topParent.insertAce(0, READ, new PrincipalSid(auth), true);
        topParent.insertAce(1, WRITE, new PrincipalSid(auth), false);
        middleParent.insertAce(0, DELETE, new PrincipalSid(auth), true);
        child.insertAce(0, DELETE, new PrincipalSid(auth), false);
        // Explicitly save the changed ACL
        jdbcMutableAclService.updateAcl(topParent);
        jdbcMutableAclService.updateAcl(middleParent);
        jdbcMutableAclService.updateAcl(child);
        // Let's check if we can read them back correctly
        Map<ObjectIdentity, Acl> map = jdbcMutableAclService.readAclsById(Arrays.asList(getTopParentOid(), getMiddleParentOid(), getChildOid()));
        assertThat(map).hasSize(3);
        // Replace our current objects with their retrieved versions
        topParent = ((MutableAcl) (map.get(getTopParentOid())));
        middleParent = ((MutableAcl) (map.get(getMiddleParentOid())));
        child = ((MutableAcl) (map.get(getChildOid())));
        // Check the retrieved versions has IDs
        assertThat(topParent.getId()).isNotNull();
        assertThat(middleParent.getId()).isNotNull();
        assertThat(child.getId()).isNotNull();
        // Check their parents were correctly persisted
        assertThat(topParent.getParentAcl()).isNull();
        assertThat(getObjectIdentity()).isEqualTo(getTopParentOid());
        assertThat(getObjectIdentity()).isEqualTo(getMiddleParentOid());
        // Check their ACEs were correctly persisted
        assertThat(topParent.getEntries()).hasSize(2);
        assertThat(middleParent.getEntries()).hasSize(1);
        assertThat(child.getEntries()).hasSize(1);
        // Check the retrieved rights are correct
        List<Permission> read = Arrays.asList(READ);
        List<Permission> write = Arrays.asList(WRITE);
        List<Permission> delete = Arrays.asList(DELETE);
        List<Sid> pSid = Arrays.asList(((Sid) (new PrincipalSid(auth))));
        assertThat(topParent.isGranted(read, pSid, false)).isTrue();
        assertThat(topParent.isGranted(write, pSid, false)).isFalse();
        assertThat(middleParent.isGranted(delete, pSid, false)).isTrue();
        assertThat(child.isGranted(delete, pSid, false)).isFalse();
        try {
            child.isGranted(Arrays.asList(ADMINISTRATION), pSid, false);
            fail("Should have thrown NotFoundException");
        } catch (NotFoundException expected) {
        }
        // Now check the inherited rights (when not explicitly overridden) also look OK
        assertThat(child.isGranted(read, pSid, false)).isTrue();
        assertThat(child.isGranted(write, pSid, false)).isFalse();
        assertThat(child.isGranted(delete, pSid, false)).isFalse();
        // Next change the child so it doesn't inherit permissions from above
        child.setEntriesInheriting(false);
        jdbcMutableAclService.updateAcl(child);
        child = ((MutableAcl) (jdbcMutableAclService.readAclById(getChildOid())));
        assertThat(child.isEntriesInheriting()).isFalse();
        // Check the child permissions no longer inherit
        assertThat(child.isGranted(delete, pSid, true)).isFalse();
        try {
            child.isGranted(read, pSid, true);
            fail("Should have thrown NotFoundException");
        } catch (NotFoundException expected) {
        }
        try {
            child.isGranted(write, pSid, true);
            fail("Should have thrown NotFoundException");
        } catch (NotFoundException expected) {
        }
        // Let's add an identical permission to the child, but it'll appear AFTER the
        // current permission, so has no impact
        child.insertAce(1, DELETE, new PrincipalSid(auth), true);
        // Let's also add another permission to the child
        child.insertAce(2, CREATE, new PrincipalSid(auth), true);
        // Save the changed child
        jdbcMutableAclService.updateAcl(child);
        child = ((MutableAcl) (jdbcMutableAclService.readAclById(getChildOid())));
        assertThat(child.getEntries()).hasSize(3);
        // Output permissions
        for (int i = 0; i < (child.getEntries().size()); i++) {
            System.out.println(child.getEntries().get(i));
        }
        // Check the permissions are as they should be
        assertThat(child.isGranted(delete, pSid, true)).isFalse();// as earlier permission

        // overrode
        assertThat(child.isGranted(Arrays.asList(CREATE), pSid, true)).isTrue();
        // Now check the first ACE (index 0) really is DELETE for our Sid and is
        // non-granting
        AccessControlEntry entry = child.getEntries().get(0);
        assertThat(entry.getPermission().getMask()).isEqualTo(DELETE.getMask());
        assertThat(entry.getSid()).isEqualTo(new PrincipalSid(auth));
        assertThat(entry.isGranting()).isFalse();
        assertThat(entry.getId()).isNotNull();
        // Now delete that first ACE
        child.deleteAce(0);
        // Save and check it worked
        child = jdbcMutableAclService.updateAcl(child);
        assertThat(child.getEntries()).hasSize(2);
        assertThat(child.isGranted(delete, pSid, false)).isTrue();
        SecurityContextHolder.clearContext();
    }

    /**
     * Test method that demonstrates eviction failure from cache - SEC-676
     */
    @Test
    @Transactional
    public void deleteAclAlsoDeletesChildren() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth);
        jdbcMutableAclService.createAcl(getTopParentOid());
        MutableAcl middleParent = jdbcMutableAclService.createAcl(getMiddleParentOid());
        MutableAcl child = jdbcMutableAclService.createAcl(getChildOid());
        child.setParent(middleParent);
        jdbcMutableAclService.updateAcl(middleParent);
        jdbcMutableAclService.updateAcl(child);
        // Check the childOid really is a child of middleParentOid
        Acl childAcl = jdbcMutableAclService.readAclById(getChildOid());
        assertThat(getObjectIdentity()).isEqualTo(getMiddleParentOid());
        // Delete the mid-parent and test if the child was deleted, as well
        jdbcMutableAclService.deleteAcl(getMiddleParentOid(), true);
        try {
            jdbcMutableAclService.readAclById(getMiddleParentOid());
            fail("It should have thrown NotFoundException");
        } catch (NotFoundException expected) {
        }
        try {
            jdbcMutableAclService.readAclById(getChildOid());
            fail("It should have thrown NotFoundException");
        } catch (NotFoundException expected) {
        }
        Acl acl = jdbcMutableAclService.readAclById(getTopParentOid());
        assertThat(acl).isNotNull();
        assertThat(getTopParentOid()).isEqualTo(getObjectIdentity());
    }

    @Test
    public void constructorRejectsNullParameters() throws Exception {
        try {
            new JdbcMutableAclService(null, lookupStrategy, aclCache);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            new JdbcMutableAclService(dataSource, null, aclCache);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        try {
            new JdbcMutableAclService(dataSource, lookupStrategy, null);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void createAclRejectsNullParameter() throws Exception {
        try {
            jdbcMutableAclService.createAcl(null);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    @Transactional
    public void createAclForADuplicateDomainObject() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth);
        ObjectIdentity duplicateOid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(100));
        jdbcMutableAclService.createAcl(duplicateOid);
        // Try to add the same object second time
        try {
            jdbcMutableAclService.createAcl(duplicateOid);
            fail("It should have thrown AlreadyExistsException");
        } catch (AlreadyExistsException expected) {
        }
    }

    @Test
    @Transactional
    public void deleteAclRejectsNullParameters() throws Exception {
        try {
            jdbcMutableAclService.deleteAcl(null, true);
            fail("It should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    @Transactional
    public void deleteAclWithChildrenThrowsException() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth);
        MutableAcl parent = jdbcMutableAclService.createAcl(getTopParentOid());
        MutableAcl child = jdbcMutableAclService.createAcl(getMiddleParentOid());
        // Specify the inheritance hierarchy
        child.setParent(parent);
        jdbcMutableAclService.updateAcl(child);
        try {
            jdbcMutableAclService.setForeignKeysInDatabase(false);// switch on FK

            // checking in the
            // class, not database
            jdbcMutableAclService.deleteAcl(getTopParentOid(), false);
            fail("It should have thrown ChildrenExistException");
        } catch (ChildrenExistException expected) {
        } finally {
            jdbcMutableAclService.setForeignKeysInDatabase(true);// restore to the

            // default
        }
    }

    @Test
    @Transactional
    public void deleteAclRemovesRowsFromDatabase() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth);
        MutableAcl child = jdbcMutableAclService.createAcl(getChildOid());
        child.insertAce(0, DELETE, new PrincipalSid(auth), false);
        jdbcMutableAclService.updateAcl(child);
        // Remove the child and check all related database rows were removed accordingly
        jdbcMutableAclService.deleteAcl(getChildOid(), false);
        assertThat(jdbcTemplate.queryForList(JdbcMutableAclServiceTests.SELECT_ALL_CLASSES, new Object[]{ getTargetClass() })).hasSize(1);
        assertThat(jdbcTemplate.queryForList("select * from acl_object_identity")).isEmpty();
        assertThat(jdbcTemplate.queryForList("select * from acl_entry")).isEmpty();
        // Check the cache
        assertThat(aclCache.getFromCache(getChildOid())).isNull();
        assertThat(aclCache.getFromCache(Long.valueOf(102))).isNull();
    }

    /**
     * SEC-1107
     */
    @Test
    @Transactional
    public void identityWithIntegerIdIsSupportedByCreateAcl() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(auth);
        ObjectIdentity oid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Integer.valueOf(101));
        jdbcMutableAclService.createAcl(oid);
        assertThat(jdbcMutableAclService.readAclById(new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(101)))).isNotNull();
    }

    /**
     * SEC-655
     */
    @Test
    @Transactional
    public void childrenAreClearedFromCacheWhenParentIsUpdated() throws Exception {
        Authentication auth = new TestingAuthenticationToken("ben", "ignored", "ROLE_ADMINISTRATOR");
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
        ObjectIdentity parentOid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(104));
        ObjectIdentity childOid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(105));
        MutableAcl parent = jdbcMutableAclService.createAcl(parentOid);
        MutableAcl child = jdbcMutableAclService.createAcl(childOid);
        child.setParent(parent);
        jdbcMutableAclService.updateAcl(child);
        parent = ((AclImpl) (jdbcMutableAclService.readAclById(parentOid)));
        parent.insertAce(0, READ, new PrincipalSid("ben"), true);
        jdbcMutableAclService.updateAcl(parent);
        parent = ((AclImpl) (jdbcMutableAclService.readAclById(parentOid)));
        parent.insertAce(1, READ, new PrincipalSid("scott"), true);
        jdbcMutableAclService.updateAcl(parent);
        child = ((MutableAcl) (jdbcMutableAclService.readAclById(childOid)));
        parent = ((MutableAcl) (child.getParentAcl()));
        assertThat(parent.getEntries()).hasSize(2).withFailMessage("Fails because child has a stale reference to its parent");
        assertThat(parent.getEntries().get(0).getPermission().getMask()).isEqualTo(1);
        assertThat(parent.getEntries().get(0).getSid()).isEqualTo(new PrincipalSid("ben"));
        assertThat(parent.getEntries().get(1).getPermission().getMask()).isEqualTo(1);
        assertThat(parent.getEntries().get(1).getSid()).isEqualTo(new PrincipalSid("scott"));
    }

    /**
     * SEC-655
     */
    @Test
    @Transactional
    public void childrenAreClearedFromCacheWhenParentisUpdated2() throws Exception {
        Authentication auth = new TestingAuthenticationToken("system", "secret", "ROLE_IGNORED");
        SecurityContextHolder.getContext().setAuthentication(auth);
        ObjectIdentityImpl rootObject = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(1));
        MutableAcl parent = jdbcMutableAclService.createAcl(rootObject);
        MutableAcl child = jdbcMutableAclService.createAcl(new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(2)));
        child.setParent(parent);
        jdbcMutableAclService.updateAcl(child);
        parent.insertAce(0, ADMINISTRATION, new GrantedAuthoritySid("ROLE_ADMINISTRATOR"), true);
        jdbcMutableAclService.updateAcl(parent);
        parent.insertAce(1, DELETE, new PrincipalSid("terry"), true);
        jdbcMutableAclService.updateAcl(parent);
        child = ((MutableAcl) (jdbcMutableAclService.readAclById(new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(2)))));
        parent = ((MutableAcl) (child.getParentAcl()));
        assertThat(parent.getEntries()).hasSize(2);
        assertThat(parent.getEntries().get(0).getPermission().getMask()).isEqualTo(16);
        assertThat(parent.getEntries().get(0).getSid()).isEqualTo(new GrantedAuthoritySid("ROLE_ADMINISTRATOR"));
        assertThat(parent.getEntries().get(1).getPermission().getMask()).isEqualTo(8);
        assertThat(parent.getEntries().get(1).getSid()).isEqualTo(new PrincipalSid("terry"));
    }

    @Test
    @Transactional
    public void cumulativePermissions() {
        Authentication auth = new TestingAuthenticationToken("ben", "ignored", "ROLE_ADMINISTRATOR");
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
        ObjectIdentity topParentOid = new ObjectIdentityImpl(JdbcMutableAclServiceTests.TARGET_CLASS, Long.valueOf(110));
        MutableAcl topParent = jdbcMutableAclService.createAcl(topParentOid);
        // Add an ACE permission entry
        Permission cm = new CumulativePermission().set(READ).set(ADMINISTRATION);
        assertThat(cm.getMask()).isEqualTo(17);
        Sid benSid = new PrincipalSid(auth);
        topParent.insertAce(0, cm, benSid, true);
        assertThat(topParent.getEntries()).hasSize(1);
        // Explicitly save the changed ACL
        topParent = jdbcMutableAclService.updateAcl(topParent);
        // Check the mask was retrieved correctly
        assertThat(topParent.getEntries().get(0).getPermission().getMask()).isEqualTo(17);
        assertThat(topParent.isGranted(Arrays.asList(cm), Arrays.asList(benSid), true)).isTrue();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testProcessingCustomSid() {
        JdbcMutableAclServiceTests.CustomJdbcMutableAclService customJdbcMutableAclService = Mockito.spy(new JdbcMutableAclServiceTests.CustomJdbcMutableAclService(dataSource, lookupStrategy, aclCache));
        CustomSid customSid = new CustomSid("Custom sid");
        Mockito.when(customJdbcMutableAclService.createOrRetrieveSidPrimaryKey("Custom sid", false, false)).thenReturn(1L);
        Long result = customJdbcMutableAclService.createOrRetrieveSidPrimaryKey(customSid, false);
        assertThat(new Long(1L)).isEqualTo(result);
    }

    /**
     * This class needed to show how to extend {@link JdbcMutableAclService} for
     * processing custom {@link Sid} implementations
     */
    private class CustomJdbcMutableAclService extends JdbcMutableAclService {
        private CustomJdbcMutableAclService(DataSource dataSource, LookupStrategy lookupStrategy, AclCache aclCache) {
            super(dataSource, lookupStrategy, aclCache);
        }

        @Override
        protected Long createOrRetrieveSidPrimaryKey(Sid sid, boolean allowCreate) {
            String sidName;
            boolean isPrincipal = false;
            if (sid instanceof CustomSid) {
                sidName = ((CustomSid) (sid)).getSid();
            } else
                if (sid instanceof GrantedAuthoritySid) {
                    sidName = getGrantedAuthority();
                } else {
                    sidName = getPrincipal();
                    isPrincipal = true;
                }

            return createOrRetrieveSidPrimaryKey(sidName, isPrincipal, allowCreate);
        }
    }
}

