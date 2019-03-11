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
package org.jboss.as.test.integration.jpa.cfgfile;


import java.util.Map;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test Hibernate configuration in hibernate.cfg.xml file
 *
 * @author Zbynek Roubalik
 */
@RunWith(Arquillian.class)
public class CfgFileTestCase {
    private static final String ARCHIVE_NAME = "jpa_cfgfile";

    private static final String hibernate_cfg_xml = "<?xml version=\'1.0\' encoding=\'utf-8\'?>\n " + (((((((("<!DOCTYPE hibernate-configuration>\n" + "<hibernate-configuration>\n") + "<session-factory>\n") + "    <property name=\"connection.driver_class\">org.hsqldb.jdbcDriver</property>\n") + "    <property name=\"hibernate.connection.datasource\">java:jboss/datasources/ExampleDS</property>\n") + "    <property name=\"dialect\">org.hibernate.dialect.HSQLDialect</property>\n") + "    <property name=\"hibernate.hbm2ddl.auto\">create-drop</property>\n") + "  </session-factory>\n") + "</hibernate-configuration>");

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    public void testEntityManagerInvocation() throws Exception {
        SFSB1 sfsb1 = lookup("SFSB1", SFSB1.class);
        sfsb1.getEmployeeNoTX(1);
    }

    @Test
    public void testProperties() throws Exception {
        SFSB1 sfsb1 = lookup("SFSB1", SFSB1.class);
        Map<String, Object> props = sfsb1.getEMFProperties();
        Assert.assertEquals("Value for org.hsqldb.jdbcDriver", "org.hsqldb.jdbcDriver", props.get("hibernate.connection.driver_class").toString());
        Assert.assertEquals("Value for hibernate.connection.datasource", "java:jboss/datasources/ExampleDS", props.get("hibernate.connection.datasource").toString());
        Assert.assertEquals("Value for dialect", "org.hibernate.dialect.HSQLDialect", props.get("hibernate.dialect").toString());
    }
}

