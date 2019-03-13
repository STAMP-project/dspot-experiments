/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations.backquotes;


import java.io.PrintWriter;
import java.io.StringWriter;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testcase for ANN-718 - @JoinTable / @JoinColumn fail when using backquotes in PK field name.
 *
 * @author Hardy Ferentschik
 */
public class BackquoteTest extends BaseUnitTestCase {
    private ServiceRegistry serviceRegistry;

    private SessionFactory sessionFactory;

    @Test
    @TestForIssue(jiraKey = "ANN-718")
    public void testBackquotes() {
        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(Bug.class);
            config.addAnnotatedClass(Category.class);
            sessionFactory = config.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            log.debug(writer.toString());
            Assert.fail(e.getMessage());
        } finally {
            if ((sessionFactory) != null) {
                sessionFactory.close();
                sessionFactory = null;
            }
        }
    }

    /**
     * HHH-4647 : Problems with @JoinColumn referencedColumnName and quoted column and table names
     *
     *  An invalid referencedColumnName to an entity having a quoted table name results in an
     *  infinite loop in o.h.c.Configuration$MappingsImpl#getPhysicalColumnName().
     *  The same issue exists with getLogicalColumnName()
     */
    @Test
    @TestForIssue(jiraKey = "HHH-4647")
    public void testInvalidReferenceToQuotedTableName() {
        try {
            Configuration config = new Configuration();
            config.addAnnotatedClass(Printer.class);
            config.addAnnotatedClass(PrinterCable.class);
            sessionFactory = config.buildSessionFactory(serviceRegistry);
            Assert.fail("expected MappingException to be thrown");
        }// we WANT MappingException to be thrown
         catch (MappingException e) {
            Assert.assertTrue("MappingException was thrown", true);
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            log.debug(writer.toString());
            Assert.fail(e.getMessage());
        } finally {
            if ((sessionFactory) != null) {
                sessionFactory.close();
                sessionFactory = null;
            }
        }
    }
}

