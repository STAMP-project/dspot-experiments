/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.xml;


import javax.persistence.EntityManager;
import junit.framework.Assert;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class XmlTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testXmlMappingCorrectness() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.close();
    }

    @Test
    public void testXmlMappingWithCacheable() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        SharedSessionContractImplementor session = em.unwrap(SharedSessionContractImplementor.class);
        EntityPersister entityPersister = session.getFactory().getEntityPersister(Lighter.class.getName());
        Assert.assertTrue(entityPersister.hasCache());
    }
}

