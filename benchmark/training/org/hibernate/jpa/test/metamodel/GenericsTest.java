/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metamodel;


import javax.persistence.EntityManager;
import javax.persistence.metamodel.EmbeddableType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Christian Beikov
 */
@TestForIssue(jiraKey = "HHH-11540")
public class GenericsTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testEmbeddableTypeExists() {
        EntityManager em = getOrCreateEntityManager();
        EmbeddableType<PersonId> idType = em.getMetamodel().embeddable(PersonId.class);
        Assert.assertNotNull(idType);
        em.close();
    }
}

