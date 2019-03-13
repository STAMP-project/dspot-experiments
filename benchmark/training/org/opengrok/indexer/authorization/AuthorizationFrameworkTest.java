/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2016, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.authorization;


import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opengrok.indexer.configuration.Group;
import org.opengrok.indexer.configuration.Nameable;
import org.opengrok.indexer.configuration.Project;


@RunWith(Parameterized.class)
public class AuthorizationFrameworkTest {
    private static final Random RANDOM = new Random();

    private final AuthorizationFrameworkTest.StackSetup setup;

    public AuthorizationFrameworkTest(AuthorizationFrameworkTest.StackSetup setup) {
        this.setup = setup;
    }

    @Test
    public void testPluginsGeneric() {
        AuthorizationFramework framework = new AuthorizationFramework(null, setup.stack);
        framework.loadAllPlugins(setup.stack);
        boolean actual;
        String format = "%s <%s> was <%s> for entity %s";
        for (AuthorizationFrameworkTest.TestCase innerSetup : setup.setup) {
            String entityName = ((innerSetup.entity) == null) ? "null" : innerSetup.entity.getName();
            try {
                actual = framework.isAllowed(innerSetup.request, ((Group) (innerSetup.entity)));
                Assert.assertEquals(String.format(format, setup.toString(), innerSetup.expected, actual, entityName), innerSetup.expected, actual);
            } catch (ClassCastException ex) {
                actual = framework.isAllowed(innerSetup.request, ((Project) (innerSetup.entity)));
                Assert.assertEquals(String.format(format, setup.toString(), innerSetup.expected, actual, entityName), innerSetup.expected, actual);
            }
        }
    }

    public static class TestCase {
        public boolean expected;

        public HttpServletRequest request;

        public Nameable entity;

        public TestCase(boolean expected, HttpServletRequest request, Nameable entity) {
            this.expected = expected;
            this.request = request;
            this.entity = entity;
        }

        @Override
        public String toString() {
            return (("expected <" + (expected)) + "> for entity ") + ((entity) == null ? "null" : entity.getName());
        }
    }

    public static class StackSetup {
        public AuthorizationStack stack;

        public List<AuthorizationFrameworkTest.TestCase> setup;

        public StackSetup(AuthorizationStack stack, AuthorizationFrameworkTest.TestCase... setups) {
            this.stack = stack;
            this.setup = Arrays.asList(setups);
        }

        @Override
        public String toString() {
            return (((((((stack.getFlag().toString().toUpperCase(Locale.ROOT)) + "[") + (printStack(stack))) + "] ") + "-> {\n") + (setup.stream().map(( t) -> t.toString()).collect(Collectors.joining(",\n")))) + "\n") + "}";
        }

        private String printStack(AuthorizationStack s) {
            String x = new String();
            for (AuthorizationEntity entity : s.getStack()) {
                if (entity instanceof AuthorizationPlugin) {
                    x += (getPlugin().toString()) + ", ";
                } else {
                    x += (((entity.getFlag().toString().toUpperCase(Locale.ROOT)) + "[") + (printStack(((AuthorizationStack) (entity))))) + "], ";
                }
            }
            return x.replaceAll(", $", "");
        }
    }
}

