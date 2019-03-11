/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.procedure;


import ParameterMode.IN;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Parameter;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialect(H2Dialect.class)
public class H2StoreProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testStoreProcedureGetParameters() {
        final EntityManager entityManager = getOrCreateEntityManager();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_all_entities", H2StoreProcedureTest.MyEntity.class);
            final Set<Parameter<?>> parameters = query.getParameters();
            Assert.assertThat(parameters.size(), Is.is(0));
            final List resultList = query.getResultList();
            Assert.assertThat(resultList.size(), Is.is(1));
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void testStoreProcedureGetParameterByPosition() {
        final EntityManager entityManager = getOrCreateEntityManager();
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("by_Id", H2StoreProcedureTest.MyEntity.class);
            query.registerStoredProcedureParameter(1, Long.class, IN);
            query.setParameter(1, 1L);
            final List resultList = query.getResultList();
            Assert.assertThat(resultList.size(), Is.is(1));
            final Set<Parameter<?>> parameters = query.getParameters();
            Assert.assertThat(parameters.size(), Is.is(1));
            final Parameter<?> parameter = query.getParameter(1);
            Assert.assertThat(parameter, IsNot.not(IsNull.nullValue()));
            try {
                query.getParameter(2);
                Assert.fail("IllegalArgumentException expected, parameter at position 2 does not exist");
            } catch (IllegalArgumentException iae) {
                // expected
            }
        } finally {
            entityManager.close();
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "MY_ENTITY")
    public static class MyEntity {
        @Id
        long id;

        String name;
    }
}

