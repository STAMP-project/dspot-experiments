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
package org.jboss.as.test.integration.hibernate.naturalid;


import javax.ejb.EJB;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test that naturalId API used with Hibernate sessionfactory can be initiated from hibernate.cfg.xml and properties added to
 * Hibernate Configuration in AS7 container
 *
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class HibernateNativeAPINaturalIdTestCase {
    private static final String ARCHIVE_NAME = "hibernate4naturalid_test";

    public static final String hibernate_cfg = "<?xml version='1.0' encoding='utf-8'?>" + (((((((("<!DOCTYPE hibernate-configuration PUBLIC " + "\"//Hibernate/Hibernate Configuration DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd\">") + "<hibernate-configuration><session-factory>") + "<property name=\"show_sql\">false</property>") + "<property name=\"current_session_context_class\">thread</property>") + /* Hibernate 5.2+ (see https://hibernate.atlassian.net/browse/HHH-10877 +
    https://hibernate.atlassian.net/browse/HHH-12665) no longer defaults
    to allowing a DML operation outside of a started transaction.
    The application workaround is to configure new property hibernate.allow_update_outside_transaction=true.
     */
    "<property name=\"hibernate.allow_update_outside_transaction\">true</property>") + "<mapping resource=\"testmapping.hbm.xml\"/>") + "</session-factory></hibernate-configuration>");

    public static final String testmapping = "<?xml version=\"1.0\"?>" + ((((((((((("<!DOCTYPE hibernate-mapping PUBLIC " + "\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">") + "<hibernate-mapping package=\"org.jboss.as.test.integration.hibernate.naturalid\">") + "<class name=\"org.jboss.as.test.integration.hibernate.naturalid.Person\" table=\"PERSON\">") + "<id name=\"personId\" column=\"person_id\">") + "<generator class=\"native\"/>") + "</id>") + "<natural-id><property name=\"personVoterId\" column=\"personVoter_id\"/><property name=\"firstName\" column=\"first_name\"/></natural-id>") + "<property name=\"lastName\" column=\"last_name\"/>") + "<property name=\"address\"/>") + // + "<set name=\"courses\" table=\"student_courses\">"
    // + "<key column=\"student_id\"/>"
    // + "<many-to-many column=\"course_id\" class=\"org.jboss.as.test.integration.nonjpa.hibernate.Course\"/>"
    // + "</set>" +
    "</class></hibernate-mapping>");

    @ArquillianResource
    private static InitialContext iniCtx;

    @EJB
    private SFSBHibernateSFNaturalId sfsb;

    @Test
    public void testNaturalIdload() throws Exception {
        // setup Configuration and SessionFactory
        sfsb.setupConfig();
        try {
            Person s1 = sfsb.createPerson("MADHUMITA", "SADHUKHAN", "99 Purkynova REDHAT BRNO CZ", 123, 1);
            Person s2 = sfsb.createPerson("REDHAT", "LINUX", "Worldwide", 435, 3);
            Person p1 = sfsb.getPersonReference("REDHAT", 435);
            Person p2 = sfsb.loadPerson("MADHUMITA", 123);
            Assert.assertEquals(p2.getAddress(), "99 Purkynova REDHAT BRNO CZ");
        } finally {
            sfsb.cleanup();
        }
    }
}

