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


import com.google.common.collect.Lists;
import io.crate.exceptions.SchemaUnknownException;
import io.crate.exceptions.UnhandledServerException;
import io.crate.exceptions.UnsupportedFeatureException;
import io.crate.metadata.RelationName;
import io.crate.test.integration.CrateUnitTest;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Test;


public class ExceptionPrivilegeValidatorTest extends CrateUnitTest {
    private List<List<Object>> validationCallArguments;

    private User user;

    private ExceptionAuthorizedValidator validator;

    @Test
    public void testTableScopeException() throws Exception {
        validator.ensureExceptionAuthorized(new io.crate.exceptions.RelationValidationException(Lists.newArrayList(RelationName.fromIndexName("users"), RelationName.fromIndexName("my_schema.foo")), "bla"));
        assertAskedAnyForTable("doc.users");
        assertAskedAnyForTable("my_schema.foo");
    }

    @Test
    public void testSchemaScopeException() throws Exception {
        validator.ensureExceptionAuthorized(new SchemaUnknownException("my_schema"));
        assertAskedAnyForSchema("my_schema");
    }

    @Test
    public void testClusterScopeException() throws Exception {
        validator.ensureExceptionAuthorized(new UnsupportedFeatureException("unsupported"));
        assertAskedAnyForCluster();
    }

    @Test
    public void testUnscopedException() throws Exception {
        validator.ensureExceptionAuthorized(new UnhandledServerException("unhandled"));
        assertThat(validationCallArguments.size(), Is.is(0));
    }
}

