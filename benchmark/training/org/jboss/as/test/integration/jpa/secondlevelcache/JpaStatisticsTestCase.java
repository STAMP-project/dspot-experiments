/**
 * JBoss, Home of Professional Open Source.
 *  Copyright 2018, Red Hat, Inc., and individual contributors
 *  as indicated by the @author tags. See the copyright.txt file in the
 *  distribution for a full listing of individual contributors.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.jpa.secondlevelcache;


import ModelDescriptionConstants.INCLUDE_RUNTIME;
import ModelDescriptionConstants.RECURSIVE;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.operations.common.Util;
import org.jboss.as.test.integration.management.base.ContainerResourceMgmtTestBase;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * JPA statistics test
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JpaStatisticsTestCase extends ContainerResourceMgmtTestBase {
    private static final String ARCHIVE_NAME = "JpaStatisticsTestCase";

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    public void testJpaStatistics() throws Exception {
        ModelNode op = Util.createOperation(READ_RESOURCE_OPERATION, PathAddress.pathAddress(DEPLOYMENT, ((JpaStatisticsTestCase.ARCHIVE_NAME) + ".jar")).append(SUBSYSTEM, "jpa").append("hibernate-persistence-unit", ((JpaStatisticsTestCase.ARCHIVE_NAME) + ".jar#mypc")));
        op.get(INCLUDE_RUNTIME).set(true);
        op.get(RECURSIVE).set(true);
        // ensure that the WFLY-10964 regression doesn't occur,
        // "org.hibernate.MappingException: Unknown entity: entity-update-count" was being thrown due to
        // a bug in the (WildFly) Hibernate integration code.  This causes JPA statistics to not be shown
        // in WildFly management console.
        ModelNode result = executeOperation(op);
        Assert.assertFalse((("Subsystem is empty (result=" + result) + ")"), ((result.keys().size()) == 0));
    }
}

