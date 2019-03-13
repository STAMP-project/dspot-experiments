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
import org.hamcrest.core.Is;
import org.junit.Test;


@ClusterScope(numDataNodes = 2, numClientNodes = 0, supportsDedicatedMasters = false)
public class UserSessionIntegrationTest extends BaseUsersIntegrationTest {
    @Test
    public void testSystemExecutorUsesSuperuserSession() {
        systemExecute("select username from sys.jobs", "sys", getNodeByEnterpriseNode(true));
        assertThat(response.rows()[0][0], Is.is("crate"));
    }

    @Test
    public void testSystemExecutorNullUser() {
        systemExecute("select username from sys.jobs", "sys", getNodeByEnterpriseNode(false));
        assertThat(response.rows()[0][0], Is.is("crate"));
    }
}

