/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.auth.user;


import Privilege.Clazz.CLUSTER;
import Privilege.Clazz.SCHEMA;
import Privilege.Clazz.TABLE;
import Privilege.Type.DQL;
import Schemas.DOC_SCHEMA_NAME;
import io.crate.exceptions.MissingPrivilegeException;
import io.crate.test.integration.CrateUnitTest;
import org.junit.Test;


public class PrivilegesTest extends CrateUnitTest {
    private static User user = User.of("ford");

    @Test
    public void testExceptionIsThrownIfUserHasNotRequiredPrivilege() throws Exception {
        expectedException.expect(MissingPrivilegeException.class);
        expectedException.expectMessage("Missing 'DQL' privilege for user 'ford'");
        Privileges.ensureUserHasPrivilege(DQL, CLUSTER, null, PrivilegesTest.user, DOC_SCHEMA_NAME);
    }

    @Test
    public void testNoExceptionIsThrownIfUserHasNotRequiredPrivilegeOnInformationSchema() throws Exception {
        // ensureUserHasPrivilege will not throw an exception if the schema is `information_schema`
        Privileges.ensureUserHasPrivilege(DQL, SCHEMA, "information_schema", PrivilegesTest.user, DOC_SCHEMA_NAME);
    }

    @Test
    public void testExceptionIsThrownIfUserHasNotAnyPrivilege() throws Exception {
        expectedException.expect(MissingPrivilegeException.class);
        expectedException.expectMessage("Missing privilege for user 'ford'");
        Privileges.ensureUserHasPrivilege(CLUSTER, null, PrivilegesTest.user);
    }

    @Test
    public void testUserWithNoPrivilegeCanAccessInformationSchema() throws Exception {
        // ensureUserHasPrivilege will not throw an exception if the schema is `information_schema`
        Privileges.ensureUserHasPrivilege(SCHEMA, "information_schema", PrivilegesTest.user);
        Privileges.ensureUserHasPrivilege(TABLE, "information_schema.table", PrivilegesTest.user);
        Privileges.ensureUserHasPrivilege(TABLE, "information_schema.views", PrivilegesTest.user);
    }
}

