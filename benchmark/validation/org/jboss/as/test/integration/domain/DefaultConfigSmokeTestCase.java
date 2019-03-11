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
package org.jboss.as.test.integration.domain;


import java.net.URL;
import java.net.URLConnection;
import org.jboss.as.test.integration.domain.management.util.DomainLifecycleUtil;
import org.jboss.as.test.integration.domain.management.util.WildFlyManagedConfiguration;
import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.jboss.logging.Logger;
import org.junit.Test;


/**
 * Ensures the default domain.xml and host.xml start.
 *
 * @author Brian Stansberry (c) 2013 Red Hat Inc.
 */
public class DefaultConfigSmokeTestCase extends BuildConfigurationTestBase {
    private static final Logger LOGGER = Logger.getLogger(DefaultConfigSmokeTestCase.class);

    public static final String slaveAddress = System.getProperty("jboss.test.host.slave.address", "127.0.0.1");

    @Test
    public void testStandardHost() throws Exception {
        final WildFlyManagedConfiguration config = BuildConfigurationTestBase.createConfiguration("domain.xml", "host.xml", getClass().getSimpleName());
        final DomainLifecycleUtil utils = new DomainLifecycleUtil(config);
        try {
            utils.start();
            // Double-check server status by confirming server-one can accept a web request to the root
            URLConnection connection = new URL((("http://" + (TestSuiteEnvironment.formatPossibleIpv6Address(BuildConfigurationTestBase.masterAddress))) + ":8080")).openConnection();
            connection.connect();
            if (Boolean.getBoolean("expression.audit")) {
                writeExpressionAudit(utils);
            }
        } finally {
            utils.stop();// Stop

        }
    }

    @Test
    public void testMasterAndSlave() throws Exception {
        final WildFlyManagedConfiguration masterConfig = BuildConfigurationTestBase.createConfiguration("domain.xml", "host-master.xml", getClass().getSimpleName());
        final DomainLifecycleUtil masterUtils = new DomainLifecycleUtil(masterConfig);
        final WildFlyManagedConfiguration slaveConfig = BuildConfigurationTestBase.createConfiguration("domain.xml", "host-slave.xml", getClass().getSimpleName(), "slave", DefaultConfigSmokeTestCase.slaveAddress, 19990);
        final DomainLifecycleUtil slaveUtils = new DomainLifecycleUtil(slaveConfig);
        try {
            masterUtils.start();
            slaveUtils.start();
            // Double-check server status by confirming server-one can accept a web request to the root
            URLConnection connection = new URL((("http://" + (TestSuiteEnvironment.formatPossibleIpv6Address(DefaultConfigSmokeTestCase.slaveAddress))) + ":8080")).openConnection();
            connection.connect();
        } finally {
            try {
                slaveUtils.stop();
            } finally {
                masterUtils.stop();
            }
        }
    }
}

