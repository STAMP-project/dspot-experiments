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
package org.jboss.as.test.integration.hibernate.secondlevelcache;


import java.sql.Connection;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test that Hibernate second level cache is working native Hibernate
 *
 * @author Scott Marlow (based on Madhumita's Hibernate test)
 */
@RunWith(Arquillian.class)
public class HibernateSecondLevelCacheTestCase {
    private static final String FACTORY_CLASS = "<property name=\"hibernate.cache.region.factory_class\">org.infinispan.hibernate.cache.v51.InfinispanRegionFactory</property>";

    private static final String MODULE_DEPENDENCIES = "Dependencies: org.hibernate.envers export,org.hibernate\n";

    private static final String ARCHIVE_NAME = "hibernateSecondLevel_test";

    public static final String hibernate_cfg = (((("<?xml version='1.0' encoding='utf-8'?>" + (((((("<!DOCTYPE hibernate-configuration PUBLIC " + "\"//Hibernate/Hibernate Configuration DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd\">") + "<hibernate-configuration><session-factory>") + "<property name=\"show_sql\">false</property>") + "<property name=\"hibernate.cache.use_second_level_cache\">true</property>") + "<property name=\"hibernate.show_sql\">false</property>")) + (HibernateSecondLevelCacheTestCase.FACTORY_CLASS)) + "<property name=\"hibernate.cache.infinispan.shared\">false</property>") + "<mapping resource=\"testmapping.hbm.xml\"/>") + "</session-factory></hibernate-configuration>";

    public static final String testmapping = (((((((((("<?xml version=\"1.0\"?>" + (((("<!DOCTYPE hibernate-mapping PUBLIC " + "\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">") + "<hibernate-mapping package=\"org.jboss.as.test.integration.hibernate\">") + "<class name=\"")) + (Student.class.getName())) + "\" lazy=\"false\" table=\"STUDENT\">") + "<cache usage=\"transactional\"/>") + "<id name=\"studentId\" column=\"student_id\">") + "<generator class=\"native\"/>") + "</id>") + "<property name=\"firstName\" column=\"first_name\"/>") + "<property name=\"lastName\" column=\"last_name\"/>") + "<property name=\"address\"/>") + "</class></hibernate-mapping>";

    @ArquillianResource
    private static InitialContext iniCtx;

    @Test
    public void testSecondLevelCache() throws Exception {
        SFSB sfsb = HibernateSecondLevelCacheTestCase.lookup("SFSB", SFSB.class);
        // setup Configuration and SessionFactory
        sfsb.setupConfig();
        DataSource ds = rawLookup("java:jboss/datasources/ExampleDS", DataSource.class);
        try {
            Student s1 = sfsb.createStudent("MADHUMITA", "SADHUKHAN", "99 Purkynova REDHAT BRNO CZ", 1);
            Student s2 = sfsb.getStudent(1);
            Connection conn = ds.getConnection();
            int updated = conn.prepareStatement("update Student set first_name='hacked' where student_id=1").executeUpdate();
            Assert.assertTrue(("was able to update added Student.  update count=" + updated), (updated > 0));
            conn.close();
            // read updated (dirty) data from second level cache
            s2 = sfsb.getStudent(1);
            Assert.assertTrue("was able to read updated Student entity", (s2 != null));
            Assert.assertEquals(("Student first name was read from second level cache = " + (s2.getFirstName())), "MADHUMITA", s2.getFirstName());
            // clear dirty data from second level cache and verify that updated data is read from database
            sfsb.clearCache();
            s2 = sfsb.getStudent(1);
            Assert.assertTrue("was able to read updated Student entity from database after clearing cache", (s2 != null));
            Assert.assertEquals(("Updated Student first name was read from database = " + (s2.getFirstName())), "hacked", s2.getFirstName());
        } finally {
            Connection conn = ds.getConnection();
            conn.prepareStatement("delete from Student").executeUpdate();
            conn.close();
            try {
                sfsb.cleanup();
            } catch (Throwable ignore) {
            }
        }
    }

    @Test
    public void testReadDeletedRowFrom2lc() throws Exception {
        SFSB sfsb = HibernateSecondLevelCacheTestCase.lookup("SFSB", SFSB.class);
        // setup Configuration and SessionFactory
        sfsb.setupConfig();
        DataSource ds = rawLookup("java:jboss/datasources/ExampleDS", DataSource.class);
        try {
            Student s1 = sfsb.createStudent("Hope", "Solo", "6415 NE 138th Pl. Kirkland, WA 98034 USA", 1);
            Student s2 = sfsb.getStudent(1);
            Connection conn = ds.getConnection();
            int updated = conn.prepareStatement("delete from Student where student_id=1").executeUpdate();
            Assert.assertTrue(("was able to delete row from Student.  update count=" + updated), (updated > 0));
            conn.close();
            // read updated (dirty) data from second level cache
            s2 = sfsb.getStudent(1);
            Assert.assertTrue("was able to read deleted Student entity", (s2 != null));
            Assert.assertEquals(("deleted Student first name was read from second level cache = " + (s2.getFirstName())), "Hope", s2.getFirstName());
        } finally {
            Connection conn = ds.getConnection();
            conn.prepareStatement("delete from Student").executeUpdate();
            conn.close();
            try {
                sfsb.cleanup();
            } catch (Throwable ignore) {
            }
        }
    }
}

