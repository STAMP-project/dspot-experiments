/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.enumcollection;


import User_.roles;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.jpa.test.criteria.enumcollection.User.Role.Admin;


/**
 * Mote that these are simply performing syntax checking (can the criteria query
 * be properly compiled and executed)
 *
 * @author Steve Ebersole
 */
public class EnumIsMemberTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9605")
    public void testQueryEnumCollection() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        User user = new User();
        user.setId(1L);
        user.getRoles().add(Admin);
        em.persist(user);
        em.getTransaction().commit();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        Expression<Set<User.Role>> roles = root.get(roles);
        // Using the correct collection of enums and an enum parameter
        query.where(builder.isMember(Admin, roles));
        TypedQuery<User> typedQuery = em.createQuery(query);
        List<User> users = typedQuery.getResultList();
        Assert.assertEquals(1, users.size());
        em.getTransaction().commit();
        em.getTransaction().begin();
        // delete
        em.remove(user);
        em.getTransaction().commit();
    }
}

