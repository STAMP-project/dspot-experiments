/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.wf.ddl.bmt.emf;


import java.net.URL;
import java.util.Collections;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Steve Ebersole
 */
@RunWith(Arquillian.class)
@Ignore("WildFly has not released a version supporting JPA 2.2 and CDI 2.0")
public class DdlInWildFlyUsingBmtAndEmfTest {
    public static final String PERSISTENCE_XML_RESOURCE_NAME = "pu-wf-ddl/persistence.xml";

    public static final String PERSISTENCE_UNIT_NAME = "pu-wf-ddl";

    @ArquillianResource
    private InitialContext initialContext;

    @Test
    public void testCreateThenDrop() throws Exception {
        URL persistenceXmlUrl = Thread.currentThread().getContextClassLoader().getResource(DdlInWildFlyUsingBmtAndEmfTest.PERSISTENCE_XML_RESOURCE_NAME);
        if (persistenceXmlUrl == null) {
            persistenceXmlUrl = Thread.currentThread().getContextClassLoader().getResource(('/' + (DdlInWildFlyUsingBmtAndEmfTest.PERSISTENCE_XML_RESOURCE_NAME)));
        }
        Assert.assertNotNull(persistenceXmlUrl);
        ParsedPersistenceXmlDescriptor persistenceUnit = PersistenceXmlParser.locateIndividualPersistenceUnit(persistenceXmlUrl);
        // creating the EMF causes SchemaCreator to be run...
        EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder(persistenceUnit, Collections.emptyMap()).build();
        // closing the EMF causes the delayed SchemaDropper to be run...
        // wrap in a transaction just to see if we can get this to fail in the way the WF report says;
        // in my experience however this succeeds with or without the transaction
        final TransactionManager tm = emf.unwrap(SessionFactoryImplementor.class).getServiceRegistry().getService(JtaPlatform.class).retrieveTransactionManager();
        tm.begin();
        Transaction txn = tm.getTransaction();
        emf.close();
        txn.commit();
    }
}

