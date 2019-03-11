/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jca.statistics;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Data source statistics testCase
 *
 * @author <a href="mailto:vrastsel@redhat.com">Vladimir Rastseluev</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DataSourcePoolStatisticsTestCase extends JcaStatisticsBase {
    static int dsCount = 0;

    static int xaDsCount = 0;

    private AutoCloseable snapshot;

    @Test
    public void testOneDs() throws Exception {
        ModelNode ds1 = createDataSource(false, 0, 20, false);
        testStatistics(ds1);
        testStatisticsDouble(ds1);
    }

    @Test
    public void testTwoDs() throws Exception {
        ModelNode ds1 = createDataSource(false, 0, 10, false);
        ModelNode ds2 = createDataSource(false, 0, 6, true);
        testStatistics(ds1);
        testStatistics(ds2);
        testStatisticsDouble(ds1);
        testStatisticsDouble(ds2);
        testInterference(ds1, ds2);
        testInterference(ds2, ds1);
    }

    @Test
    public void testOneXaDs() throws Exception {
        ModelNode ds1 = createDataSource(true, 0, 10, true);
        testStatistics(ds1);
        testStatisticsDouble(ds1);
    }

    @Test
    public void testTwoXaDs() throws Exception {
        ModelNode ds1 = createDataSource(true, 0, 1, false);
        ModelNode ds2 = createDataSource(true, 0, 1, true);
        testStatistics(ds1);
        testStatistics(ds2);
        testStatisticsDouble(ds1);
        testStatisticsDouble(ds2);
        testInterference(ds1, ds2);
        testInterference(ds2, ds1);
    }

    @Test
    public void testXaPlusDs() throws Exception {
        ModelNode ds1 = createDataSource(false, 0, 3, false);
        ModelNode ds2 = createDataSource(true, 0, 4, true);
        testStatistics(ds1);
        testStatistics(ds2);
        testStatisticsDouble(ds1);
        testStatisticsDouble(ds2);
        testInterference(ds1, ds2);
        testInterference(ds2, ds1);
    }
}

