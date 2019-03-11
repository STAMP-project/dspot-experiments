/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.domain;


import ModelType.LIST;
import ModelType.OBJECT;
import ModelType.PROPERTY;
import PathAddress.EMPTY_ADDRESS;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.test.integration.domain.management.util.DomainLifecycleUtil;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.logging.Logger;
import org.junit.Test;


/**
 * Smoke test of expression support.
 *
 * @author Brian Stansberry (c) 2012 Red Hat Inc.
 */
public class ExpressionSupportSmokeTestCase extends BuildConfigurationTestBase {
    private static final Logger LOGGER = Logger.getLogger(ExpressionSupportSmokeTestCase.class);

    private static final Set<ModelType> COMPLEX_TYPES = Collections.unmodifiableSet(EnumSet.of(LIST, OBJECT, PROPERTY));

    private DomainLifecycleUtil domainMasterLifecycleUtil;

    private int conflicts;

    private int noSimple;

    private int noSimpleCollection;

    private int noComplexList;

    private int noObject;

    private int noComplexProperty;

    private int supportedUndefined;

    private int simple;

    private int simpleCollection;

    private int complexList;

    private int object;

    private int complexProperty;

    private final boolean immediateValidation = Boolean.getBoolean("immediate.expression.validation");

    private final boolean logHandling = Boolean.getBoolean("expression.logging");

    /**
     * Launch a master HC in --admin-only. Iterate through all resources, converting any writable attribute that
     * support expressions and has a value or a default value to an expression (if it isn't already one), using the
     * value/default value as the expression default (so setting a system property isn't required). Then reload out of
     * --admin-only and confirm that the host's servers start correctly. Finally, read the resources from the host
     * and confirm that the expected values are there.
     *
     * @throws Exception
     * 		if there is one
     */
    @Test
    public void test() throws Exception {
        // Add some extra resources to get some more coverage
        addTestResources();
        Map<PathAddress, Map<String, ModelNode>> expectedValues = new HashMap<PathAddress, Map<String, ModelNode>>();
        setExpressions(EMPTY_ADDRESS, "master", expectedValues);
        ExpressionSupportSmokeTestCase.LOGGER.trace("Update statistics:");
        ExpressionSupportSmokeTestCase.LOGGER.trace("==================");
        ExpressionSupportSmokeTestCase.LOGGER.trace(("conflicts: " + (conflicts)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("no expression simple: " + (noSimple)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("no expression simple collection: " + (noSimpleCollection)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("no expression complex list: " + (noComplexList)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("no expression object: " + (noObject)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("no expression complex property: " + (noComplexProperty)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("supported but undefined: " + (supportedUndefined)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("simple: " + (simple)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("simple collection: " + (simpleCollection)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("complex list: " + (complexList)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("object: " + (object)));
        ExpressionSupportSmokeTestCase.LOGGER.trace(("complex property: " + (complexProperty)));
        // restart back to normal mode
        ModelNode op = new ModelNode();
        op.get(OP_ADDR).add(HOST, "master");
        op.get(OP).set("reload");
        op.get("admin-only").set(false);
        domainMasterLifecycleUtil.executeAwaitConnectionClosed(op);
        // Try to reconnect to the hc
        domainMasterLifecycleUtil.connect();
        // check that the servers are up
        domainMasterLifecycleUtil.awaitServers(System.currentTimeMillis());
        validateExpectedValues(EMPTY_ADDRESS, expectedValues, "master");
    }
}

