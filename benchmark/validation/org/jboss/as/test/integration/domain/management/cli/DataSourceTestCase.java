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
package org.jboss.as.test.integration.domain.management.cli;


import org.jboss.as.test.integration.management.base.AbstractCliTestBase;
import org.junit.Test;


/**
 *
 *
 * @author Dominik Pospisil <dpospisi@redhat.com>
 */
public class DataSourceTestCase extends AbstractCliTestBase {
    private static String[] profileNames;

    private static final String[][] DS_PROPS = new String[][]{ new String[]{ "idle-timeout-minutes", "5" } };

    @Test
    public void testDataSource() throws Exception {
        testAddDataSource("h2");
        testModifyDataSource("h2");
        testRemoveDataSource("h2");
    }

    @Test
    public void testXaDataSource() throws Exception {
        testAddXaDataSource();
        testModifyXaDataSource();
        testRemoveXaDataSource();
    }

    @Test
    public void testDataSourcewithHotDeployedJar() throws Exception {
        cli.sendLine(("deploy --all-server-groups " + (createDriverJarFile().getAbsolutePath())));
        testAddDataSource("foodriver.jar");
        testModifyDataSource("foodriver.jar");
        testRemoveDataSource("foodriver.jar");
    }
}

