package com.ctrip.framework.apollo.portal.spi.defaultImpl;


import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.portal.AbstractIntegrationTest;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.po.Permission;
import com.ctrip.framework.apollo.portal.entity.po.Role;
import com.ctrip.framework.apollo.portal.entity.po.RolePermission;
import com.ctrip.framework.apollo.portal.entity.po.UserRole;
import com.ctrip.framework.apollo.portal.repository.PermissionRepository;
import com.ctrip.framework.apollo.portal.repository.RolePermissionRepository;
import com.ctrip.framework.apollo.portal.repository.RoleRepository;
import com.ctrip.framework.apollo.portal.repository.UserRoleRepository;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class RolePermissionServiceTest extends AbstractIntegrationTest {
    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private String someCreatedBy;

    private String someLastModifiedBy;

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreatePermission() throws Exception {
        String someTargetId = "someTargetId";
        String somePermissionType = "somePermissionType";
        Permission somePermission = assemblePermission(somePermissionType, someTargetId);
        Permission created = rolePermissionService.createPermission(somePermission);
        Permission createdFromDB = permissionRepository.findById(created.getId()).orElse(null);
        Assert.assertEquals(somePermissionType, createdFromDB.getPermissionType());
        Assert.assertEquals(someTargetId, createdFromDB.getTargetId());
    }

    @Test(expected = IllegalStateException.class)
    @Sql(scripts = "/sql/permission/insert-test-permissions.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreatePermissionWithPermissionExisted() throws Exception {
        String someTargetId = "someTargetId";
        String somePermissionType = "somePermissionType";
        Permission somePermission = assemblePermission(somePermissionType, someTargetId);
        rolePermissionService.createPermission(somePermission);
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreatePermissions() throws Exception {
        String someTargetId = "someTargetId";
        String anotherTargetId = "anotherTargetId";
        String somePermissionType = "somePermissionType";
        String anotherPermissionType = "anotherPermissionType";
        Permission somePermission = assemblePermission(somePermissionType, someTargetId);
        Permission anotherPermission = assemblePermission(anotherPermissionType, anotherTargetId);
        Set<Permission> created = rolePermissionService.createPermissions(Sets.newHashSet(somePermission, anotherPermission));
        Set<Long> permissionIds = created.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        Iterable<Permission> permissionsFromDB = permissionRepository.findAllById(permissionIds);
        Set<String> targetIds = Sets.newHashSet();
        Set<String> permissionTypes = Sets.newHashSet();
        for (Permission permission : permissionsFromDB) {
            targetIds.add(permission.getTargetId());
            permissionTypes.add(permission.getPermissionType());
        }
        Assert.assertEquals(2, targetIds.size());
        Assert.assertEquals(2, permissionTypes.size());
        Assert.assertTrue(targetIds.containsAll(Sets.newHashSet(someTargetId, anotherTargetId)));
        Assert.assertTrue(permissionTypes.containsAll(Sets.newHashSet(somePermissionType, anotherPermissionType)));
    }

    @Test(expected = IllegalStateException.class)
    @Sql(scripts = "/sql/permission/insert-test-permissions.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreatePermissionsWithPermissionsExisted() throws Exception {
        String someTargetId = "someTargetId";
        String anotherTargetId = "anotherTargetId";
        String somePermissionType = "somePermissionType";
        String anotherPermissionType = "anotherPermissionType";
        Permission somePermission = assemblePermission(somePermissionType, someTargetId);
        Permission anotherPermission = assemblePermission(anotherPermissionType, anotherTargetId);
        rolePermissionService.createPermissions(Sets.newHashSet(somePermission, anotherPermission));
    }

    @Test
    @Sql(scripts = "/sql/permission/insert-test-permissions.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreateRoleWithPermissions() throws Exception {
        String someRoleName = "someRoleName";
        Role role = assembleRole(someRoleName);
        Set<Long> permissionIds = Sets.newHashSet(990L, 991L);
        Role created = rolePermissionService.createRoleWithPermissions(role, permissionIds);
        Role createdFromDB = roleRepository.findById(created.getId()).orElse(null);
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleIdIn(Sets.newHashSet(createdFromDB.getId()));
        Set<Long> rolePermissionIds = rolePermissions.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        Assert.assertEquals(someRoleName, createdFromDB.getRoleName());
        Assert.assertEquals(2, rolePermissionIds.size());
        Assert.assertTrue(rolePermissionIds.containsAll(permissionIds));
    }

    @Test(expected = IllegalStateException.class)
    @Sql(scripts = "/sql/permission/insert-test-roles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreateRoleWithPermissionsWithRoleExisted() throws Exception {
        String someRoleName = "someRoleName";
        Role role = assembleRole(someRoleName);
        rolePermissionService.createRoleWithPermissions(role, null);
    }

    @Test
    @Sql(scripts = "/sql/permission/insert-test-roles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testAssignRoleToUsers() throws Exception {
        String someRoleName = "someRoleName";
        String someUser = "someUser";
        String anotherUser = "anotherUser";
        String operator = "operator";
        Set<String> users = Sets.newHashSet(someUser, anotherUser);
        rolePermissionService.assignRoleToUsers(someRoleName, users, operator);
        List<UserRole> userRoles = userRoleRepository.findByRoleId(990);
        Set<String> usersWithRole = Sets.newHashSet();
        for (UserRole userRole : userRoles) {
            Assert.assertEquals(operator, userRole.getDataChangeCreatedBy());
            Assert.assertEquals(operator, userRole.getDataChangeLastModifiedBy());
            usersWithRole.add(userRole.getUserId());
        }
        Assert.assertEquals(2, usersWithRole.size());
        Assert.assertTrue(usersWithRole.containsAll(users));
    }

    @Test(expected = IllegalStateException.class)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testAssignRoleToUsersWithRoleNotExists() throws Exception {
        String someRoleName = "someRoleName";
        String someUser = "someUser";
        String operator = "operator";
        Set<String> users = Sets.newHashSet(someUser);
        rolePermissionService.assignRoleToUsers(someRoleName, users, operator);
    }

    @Test
    @Sql(scripts = "/sql/permission/insert-test-roles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-userroles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testAssignRoleToUsersWithUserRolesExisted() throws Exception {
        String someRoleName = "someRoleName";
        String someUser = "someUser";
        String anotherUser = "anotherUser";
        String operator = "operator";
        Set<String> users = Sets.newHashSet(someUser, anotherUser);
        rolePermissionService.assignRoleToUsers(someRoleName, users, operator);
        List<UserRole> userRoles = userRoleRepository.findByRoleId(990);
        Set<String> usersWithRole = Sets.newHashSet();
        for (UserRole userRole : userRoles) {
            Assert.assertEquals("someOperator", userRole.getDataChangeCreatedBy());
            Assert.assertEquals("someOperator", userRole.getDataChangeLastModifiedBy());
            usersWithRole.add(userRole.getUserId());
        }
        Assert.assertEquals(2, usersWithRole.size());
        Assert.assertTrue(usersWithRole.containsAll(users));
    }

    @Test
    @Sql(scripts = "/sql/permission/insert-test-roles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-userroles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testRemoveRoleFromUsers() throws Exception {
        String someRoleName = "someRoleName";
        String someUser = "someUser";
        String anotherUser = "anotherUser";
        String operator = "operator";
        Set<String> users = Sets.newHashSet(someUser, anotherUser);
        List<UserRole> userRoles = userRoleRepository.findByRoleId(990);
        Assert.assertFalse(userRoles.isEmpty());
        rolePermissionService.removeRoleFromUsers(someRoleName, users, operator);
        List<UserRole> userRolesAfterRemoval = userRoleRepository.findByRoleId(990);
        Assert.assertTrue(userRolesAfterRemoval.isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    @Sql(scripts = "/sql/permission/insert-test-userroles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testRemoveRoleFromUsersWithRoleNotExisted() throws Exception {
        String someRoleName = "someRoleName";
        String someUser = "someUser";
        String operator = "operator";
        Set<String> users = Sets.newHashSet(someUser);
        rolePermissionService.removeRoleFromUsers(someRoleName, users, operator);
    }

    @Test
    @Sql(scripts = "/sql/permission/insert-test-roles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-userroles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testQueryUsersWithRole() throws Exception {
        String someRoleName = "someRoleName";
        Set<UserInfo> users = rolePermissionService.queryUsersWithRole(someRoleName);
        Set<String> userIds = users.stream().map(UserInfo::getUserId).collect(Collectors.toSet());
        Assert.assertTrue(userIds.containsAll(Sets.newHashSet("someUser", "anotherUser")));
    }

    @Test
    @Sql(scripts = "/sql/permission/insert-test-roles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-permissions.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-userroles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-rolepermissions.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testUserHasPermission() throws Exception {
        String someTargetId = "someTargetId";
        String anotherTargetId = "anotherTargetId";
        String somePermissionType = "somePermissionType";
        String anotherPermissionType = "anotherPermissionType";
        String someUser = "someUser";
        String anotherUser = "anotherUser";
        String someUserWithNoPermission = "someUserWithNoPermission";
        Assert.assertTrue(rolePermissionService.userHasPermission(someUser, somePermissionType, someTargetId));
        Assert.assertTrue(rolePermissionService.userHasPermission(someUser, anotherPermissionType, anotherTargetId));
        Assert.assertTrue(rolePermissionService.userHasPermission(anotherUser, somePermissionType, someTargetId));
        Assert.assertTrue(rolePermissionService.userHasPermission(anotherUser, anotherPermissionType, anotherTargetId));
        Assert.assertFalse(rolePermissionService.userHasPermission(someUserWithNoPermission, somePermissionType, someTargetId));
        Assert.assertFalse(rolePermissionService.userHasPermission(someUserWithNoPermission, anotherPermissionType, anotherTargetId));
    }
}

