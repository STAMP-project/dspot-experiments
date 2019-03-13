/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.inheritance.discriminator.joinedsubclass;


import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-9302")
public class JoinedSubclassTest extends BaseCoreFunctionalTestCase {
    private Long subSubEntityId;

    @Test
    public void shouldRetrieveSubEntity() {
        session = openSession();
        try {
            RootEntity loaded = session.get(SubEntity.class, subSubEntityId);
            Assert.assertNotNull(loaded);
            Assert.assertTrue((loaded instanceof SubSubEntity));
        } finally {
            session.close();
        }
    }

    // Criteria
    @Test
    public void shouldRetrieveSubSubEntityWithCriteria() {
        session = openSession();
        try {
            SubSubEntity loaded = ((SubSubEntity) (session.createCriteria(SubSubEntity.class).add(Restrictions.idEq(subSubEntityId)).uniqueResult()));
            Assert.assertNotNull(loaded);
        } finally {
            session.close();
        }
    }

    @Test
    public void shouldNotRetrieveSubSubSubEntityWithCriteria() {
        session = openSession();
        try {
            SubSubSubEntity loaded = ((SubSubSubEntity) (session.createCriteria(SubSubSubEntity.class).add(Restrictions.idEq(subSubEntityId)).uniqueResult()));
            Assert.assertNull(loaded);
        } finally {
            session.close();
        }
    }

    // HQL
    @Test
    public void shouldRetrieveSubSubEntityWithHQL() {
        session = openSession();
        try {
            SubSubEntity loaded = ((SubSubEntity) (session.createQuery("select se from SubSubEntity se where se.id = :id").setLong("id", subSubEntityId).uniqueResult()));
            Assert.assertNotNull(loaded);
        } finally {
            session.close();
        }
    }

    @Test
    public void shouldNotRetrieveSubSubSubEntityWithHQL() {
        session = openSession();
        try {
            SubSubSubEntity loaded = ((SubSubSubEntity) (session.createQuery("select se from SubSubSubEntity se where se.id = :id").setLong("id", subSubEntityId).uniqueResult()));
            Assert.assertNull(loaded);
        } finally {
            session.close();
        }
    }
}

