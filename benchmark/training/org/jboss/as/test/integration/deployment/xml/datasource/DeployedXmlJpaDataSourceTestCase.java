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
package org.jboss.as.test.integration.deployment.xml.datasource;


import java.util.Set;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test deployment of -ds.xml files as JPA data sources
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class DeployedXmlJpaDataSourceTestCase {
    public static final String TEST_DS_XML = "test-ds.xml";

    public static final String JPA_DEPLOYMENT_NAME = "jpaDeployment";

    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void testJpaUsedWithXMLXaDataSource() throws Throwable {
        final JpaRemote remote = ((JpaRemote) (initialContext.lookup("java:module/JpaRemoteBean")));
        remote.addEmployee("Bob");
        remote.addEmployee("Sue");
        final Set<String> emps = remote.getEmployees();
        Assert.assertEquals(2, emps.size());
        Assert.assertTrue(emps.contains("Bob"));
        Assert.assertTrue(emps.contains("Sue"));
    }
}

