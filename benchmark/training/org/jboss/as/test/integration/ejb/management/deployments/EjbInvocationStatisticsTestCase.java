/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.management.deployments;


import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.ejb.remote.common.EJBManagementUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests whether the invocation statistics actually make sense.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(EjbInvocationStatisticsTestCaseSetup.class)
public class EjbInvocationStatisticsTestCase {
    private static final String SUBSYSTEM_NAME = "ejb3";

    @ContainerResource
    private ManagementClient managementClient;

    private Boolean statisticsEnabled;

    private static InitialContext context;

    @Test
    public void testSingleton() throws Exception {
        validateBean(EJBManagementUtil.SINGLETON, ManagedSingletonBean.class);
    }

    // TODO Elytron - ejb-client4 integration
    @Test
    public void testSFSB() throws Exception {
        validateBean(EJBManagementUtil.STATEFUL, ManagedStatefulBean.class);
    }

    @Test
    public void testSLSB() throws Exception {
        validateBean(EJBManagementUtil.STATELESS, ManagedStatelessBean.class);
    }

    // this needs to run after testSingleton because testSingleton doesn't expect any previously made invocations
    @Test
    @InSequence(1)
    public void testSingletonWaitTime() throws Exception {
        validateWaitTimeStatistic(EJBManagementUtil.SINGLETON, ManagedSingletonBean.class);
    }
}

