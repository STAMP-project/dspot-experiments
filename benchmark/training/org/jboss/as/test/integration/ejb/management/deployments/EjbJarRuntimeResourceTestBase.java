/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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


import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.test.integration.ejb.remote.common.EJBManagementUtil;
import org.junit.Test;


/**
 * Base class for tests of management resources exposed by runtime EJB components.
 *
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public class EjbJarRuntimeResourceTestBase {
    protected static final String SECURITY_DOMAIN = "security-domain";

    protected static final String MODULE_NAME = "ejb-management";

    protected static final String JAR_NAME = (EjbJarRuntimeResourceTestBase.MODULE_NAME) + ".jar";

    private static final String COMPONENT_CLASS_NAME = "component-class-name";

    private static final String DECLARED_ROLES = "declared-roles";

    private static final String POOL_NAME = "pool-name";

    private static final String RUN_AS_ROLE = "run-as-role";

    private static final String TIMER_ATTRIBUTE = "timers";

    private static final String[] POOL_ATTRIBUTES = new String[]{ "pool-available-count", "pool-create-count", "pool-current-size", EjbJarRuntimeResourceTestBase.POOL_NAME, "pool-max-size", "pool-remove-count" };

    private static final String[] TIMER_ATTRIBUTES = new String[]{ "time-remaining", "next-timeout", "calendar-timer" };

    private static final String[] SCHEDULE_ATTRIBUTES = new String[]{ "day-of-month", "day-of-week", "hour", "minute", "year", "timezone", "start", "end" };

    @ContainerResource
    private ManagementClient managementClient;

    private final PathAddress baseAddress;

    protected EjbJarRuntimeResourceTestBase(final PathAddress baseAddress) {
        this.baseAddress = baseAddress;
    }

    @Test
    public void testMDB() throws Exception {
        testComponent(EJBManagementUtil.MESSAGE_DRIVEN, ManagedMDB.class.getSimpleName(), true);
    }

    @Test
    public void testNoTimerMDB() throws Exception {
        testComponent(EJBManagementUtil.MESSAGE_DRIVEN, NoTimerMDB.class.getSimpleName(), false);
    }

    @Test
    public void testSLSB() throws Exception {
        testComponent(EJBManagementUtil.STATELESS, ManagedStatelessBean.class.getSimpleName(), true);
    }

    @Test
    public void testNoTimerSLSB() throws Exception {
        testComponent(EJBManagementUtil.STATELESS, NoTimerStatelessBean.class.getSimpleName(), false);
    }

    @Test
    public void testSingleton() throws Exception {
        testComponent(EJBManagementUtil.SINGLETON, ManagedSingletonBean.class.getSimpleName(), true);
    }

    @Test
    public void testNoTimerSingleton() throws Exception {
        testComponent(EJBManagementUtil.SINGLETON, NoTimerSingletonBean.class.getSimpleName(), false);
    }

    @Test
    public void testSFSB() throws Exception {
        testComponent(EJBManagementUtil.STATEFUL, ManagedStatefulBean.class.getSimpleName(), false);
    }
}

