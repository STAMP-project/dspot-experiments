/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jca.statistics.xa;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * XA Data source statistics testCase
 *
 * @author dsimko@redhat.com
 */
@RunWith(Arquillian.class)
@RunAsClient
@ServerSetup(XaDataSourceSetupStep.class)
public class XaDataSourcePoolStatisticsTestCase {
    private static final String ARCHIVE_NAME = "xa_transactions";

    private static final String APP_NAME = "xa-datasource-pool-statistics-test";

    private static final String ATTRIBUTE_XA_COMMIT_COUNT = "XACommitCount";

    private static final String ATTRIBUTE_XA_ROLLBACK_COUNT = "XARollbackCount";

    private static final String ATTRIBUTE_XA_START_COUNT = "XAStartCount";

    private static final int COUNT = 10;

    @ContainerResource
    private ManagementClient managementClient;

    /**
     * Tests increasing XACommitCount, XACommitAverageTime and XAStartCount
     * statistical attributes.
     */
    @Test
    public void testXACommit() throws Exception {
        int xaStartCountBefore = readStatisticalAttribute(XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_START_COUNT);
        int xaCommitCount = readStatisticalAttribute(XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_COMMIT_COUNT);
        Assert.assertEquals(((((XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_COMMIT_COUNT) + " is ") + xaCommitCount) + " but should be 0"), 0, xaCommitCount);
        SLSB slsb = getBean();
        for (int i = 0; i < (XaDataSourcePoolStatisticsTestCase.COUNT); i++) {
            slsb.commit();
        }
        xaCommitCount = readStatisticalAttribute(XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_COMMIT_COUNT);
        int xaStartCountAfter = readStatisticalAttribute(XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_START_COUNT);
        int total = xaStartCountBefore + (XaDataSourcePoolStatisticsTestCase.COUNT);
        Assert.assertEquals((((((XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_COMMIT_COUNT) + " is ") + xaCommitCount) + " but should be ") + (XaDataSourcePoolStatisticsTestCase.COUNT)), XaDataSourcePoolStatisticsTestCase.COUNT, xaCommitCount);
        Assert.assertEquals((((((XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_START_COUNT) + " is ") + xaStartCountAfter) + " but should be ") + total), total, xaStartCountAfter);
    }

    /**
     * Tests increasing XARollbackCount statistical attribute.
     */
    @Test
    public void testXARollback() throws Exception {
        int xaRollbackCount = readStatisticalAttribute(XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_ROLLBACK_COUNT);
        Assert.assertEquals(((((XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_ROLLBACK_COUNT) + " is ") + xaRollbackCount) + " but should be 0"), 0, xaRollbackCount);
        SLSB slsb = getBean();
        for (int i = 0; i < (XaDataSourcePoolStatisticsTestCase.COUNT); i++) {
            slsb.rollback();
        }
        xaRollbackCount = readStatisticalAttribute(XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_ROLLBACK_COUNT);
        Assert.assertEquals((((((XaDataSourcePoolStatisticsTestCase.ATTRIBUTE_XA_ROLLBACK_COUNT) + " is ") + xaRollbackCount) + " but should be ") + (XaDataSourcePoolStatisticsTestCase.COUNT)), XaDataSourcePoolStatisticsTestCase.COUNT, xaRollbackCount);
    }
}

