/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.schemagen;


import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.hamcrest.core.Is;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10104")
public class SchemaCreateDropTest extends BaseEntityManagerFunctionalTestCase {
    private EntityManager em;

    @Test
    public void testQueryWithoutTransaction() {
        TypedQuery<String> query = em.createQuery("SELECT d.name FROM Document d", String.class);
        List<String> results = query.getResultList();
        Assert.assertThat(results.size(), Is.is(1));
    }
}

