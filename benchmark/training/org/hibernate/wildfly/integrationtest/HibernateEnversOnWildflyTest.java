/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.wildfly.integrationtest;


import java.util.Arrays;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Chris Cranford
 */
@RunWith(Arquillian.class)
public class HibernateEnversOnWildflyTest {
    private static final String ORM_VERSION = Session.class.getPackage().getImplementationVersion();

    private static final String ORM_MINOR_VERSION = HibernateEnversOnWildflyTest.ORM_VERSION.substring(0, HibernateEnversOnWildflyTest.ORM_VERSION.indexOf(".", ((HibernateEnversOnWildflyTest.ORM_VERSION.indexOf(".")) + 1)));

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction userTransaction;

    @Test
    public void testEnversCompatibility() throws Exception {
        // revision 1
        userTransaction.begin();
        entityManager.joinTransaction();
        AuditedEntity entity = new AuditedEntity(1, "Marco Polo");
        entityManager.persist(entity);
        userTransaction.commit();
        // revision 2
        userTransaction.begin();
        entityManager.joinTransaction();
        entity.setName("George Washington");
        entityManager.merge(entity);
        userTransaction.commit();
        entityManager.clear();
        // verify audit history revision counts
        userTransaction.begin();
        final AuditReader auditReader = AuditReaderFactory.get(entityManager);
        Assert.assertEquals(Arrays.asList(1, 2), auditReader.getRevisions(AuditedEntity.class, 1));
        userTransaction.commit();
    }
}

