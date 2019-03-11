package com.ctrip.framework.apollo.openapi.service;


import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.portal.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConsumerRolePermissionServiceTest extends AbstractIntegrationTest {
    @Autowired
    private ConsumerRolePermissionService consumerRolePermissionService;

    @Test
    @Sql(scripts = "/sql/permission/insert-test-roles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-permissions.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-consumerroles.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/permission/insert-test-rolepermissions.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testConsumerHasPermission() throws Exception {
        String someTargetId = "someTargetId";
        String anotherTargetId = "anotherTargetId";
        String somePermissionType = "somePermissionType";
        String anotherPermissionType = "anotherPermissionType";
        long someConsumerId = 1;
        long anotherConsumerId = 2;
        long someConsumerWithNoPermission = 3;
        Assert.assertTrue(consumerRolePermissionService.consumerHasPermission(someConsumerId, somePermissionType, someTargetId));
        Assert.assertTrue(consumerRolePermissionService.consumerHasPermission(someConsumerId, anotherPermissionType, anotherTargetId));
        Assert.assertTrue(consumerRolePermissionService.consumerHasPermission(anotherConsumerId, somePermissionType, someTargetId));
        Assert.assertTrue(consumerRolePermissionService.consumerHasPermission(anotherConsumerId, anotherPermissionType, anotherTargetId));
        Assert.assertFalse(consumerRolePermissionService.consumerHasPermission(someConsumerWithNoPermission, somePermissionType, someTargetId));
        Assert.assertFalse(consumerRolePermissionService.consumerHasPermission(someConsumerWithNoPermission, anotherPermissionType, anotherTargetId));
    }
}

