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
package io.crate.integrationtests;


import ESIntegTestCase.ClusterScope;
import Privilege.Clazz;
import Privilege.State;
import Privilege.Type;
import io.crate.action.sql.SQLActionException;
import io.crate.analyze.user.Privilege;
import io.crate.testing.TestingHelpers;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.hamcrest.core.Is;
import org.junit.Test;


@ClusterScope(numClientNodes = 0, supportsDedicatedMasters = false, numDataNodes = 0)
public class SysPrivilegesIntegrationTest extends BaseUsersIntegrationTest {
    private static final Set<Privilege> PRIVILEGES = new java.util.HashSet(Arrays.asList(new Privilege(State.GRANT, Type.DQL, Clazz.CLUSTER, null, "crate"), new Privilege(State.GRANT, Type.DML, Clazz.CLUSTER, null, "crate")));

    private static final List<String> USERNAMES = Arrays.asList("ford", "arthur", "normal");

    private String nodeEnterpriseEnabled;

    private String nodeEnterpriseDisabled;

    @Test
    public void testTableColumns() throws Exception {
        executeAsSuperuser(("select column_name, data_type from information_schema.columns" + " where table_name='privileges' and table_schema='sys'"));
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("class| string\n" + (((("grantee| string\n" + "grantor| string\n") + "ident| string\n") + "state| string\n") + "type| string\n"))));
    }

    @Test
    public void testListingAsSuperUser() throws Exception {
        executeAsSuperuser("select * from sys.privileges order by grantee, type");
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("CLUSTER| arthur| crate| NULL| GRANT| DML\n" + (((("CLUSTER| arthur| crate| NULL| GRANT| DQL\n" + "CLUSTER| ford| crate| NULL| GRANT| DML\n") + "CLUSTER| ford| crate| NULL| GRANT| DQL\n") + "CLUSTER| normal| crate| NULL| GRANT| DML\n") + "CLUSTER| normal| crate| NULL| GRANT| DQL\n"))));
    }

    @Test
    public void testListingAsUserWithPrivilege() throws Exception {
        executeAsSuperuser("select * from sys.privileges order by grantee, type");
        assertThat(TestingHelpers.printedTable(response.rows()), Is.is(("CLUSTER| arthur| crate| NULL| GRANT| DML\n" + (((("CLUSTER| arthur| crate| NULL| GRANT| DQL\n" + "CLUSTER| ford| crate| NULL| GRANT| DML\n") + "CLUSTER| ford| crate| NULL| GRANT| DQL\n") + "CLUSTER| normal| crate| NULL| GRANT| DML\n") + "CLUSTER| normal| crate| NULL| GRANT| DQL\n"))));
    }

    @Test
    public void testTableNotAvailableIfEnterpriseIsOff() throws Exception {
        expectedException.expect(SQLActionException.class);
        expectedException.expectMessage("RelationUnknown: Relation 'sys.privileges' unknown");
        execute("select * from sys.privileges", null, createSuperUserSession(false));
    }
}

