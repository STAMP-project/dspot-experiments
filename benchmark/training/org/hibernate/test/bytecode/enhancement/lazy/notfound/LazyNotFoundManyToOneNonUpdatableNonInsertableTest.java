/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 *
 *
 * @author Gail Badner
 */
package org.hibernate.test.bytecode.enhancement.lazy.notfound;


import javax.persistence.CascadeType;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-12226")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyNotFoundManyToOneNonUpdatableNonInsertableTest extends BaseCoreFunctionalTestCase {
    private static int ID = 1;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.bytecode.enhancement.lazy.notfound.Lazy p = new org.hibernate.test.bytecode.enhancement.lazy.notfound.Lazy();
            p.id = LazyNotFoundManyToOneNonUpdatableNonInsertableTest.ID;
            org.hibernate.test.bytecode.enhancement.lazy.notfound.User u = new org.hibernate.test.bytecode.enhancement.lazy.notfound.User();
            u.id = LazyNotFoundManyToOneNonUpdatableNonInsertableTest.ID;
            u.setLazy(p);
            session.persist(u);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(session.get(.class, LazyNotFoundManyToOneNonUpdatableNonInsertableTest.ID));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.bytecode.enhancement.lazy.notfound.User user = session.find(.class, LazyNotFoundManyToOneNonUpdatableNonInsertableTest.ID);
            assertFalse(Hibernate.isPropertyInitialized(user, "lazy"));
            assertNull(user.getLazy());
        });
    }

    @Entity(name = "User")
    @Table(name = "USER_TABLE")
    public static class User {
        @Id
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
        @LazyToOne(LazyToOneOption.NO_PROXY)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), name = "id", referencedColumnName = "id", insertable = false, updatable = false)
        private LazyNotFoundManyToOneNonUpdatableNonInsertableTest.Lazy lazy;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public LazyNotFoundManyToOneNonUpdatableNonInsertableTest.Lazy getLazy() {
            return lazy;
        }

        public void setLazy(LazyNotFoundManyToOneNonUpdatableNonInsertableTest.Lazy lazy) {
            this.lazy = lazy;
        }
    }

    @Entity(name = "Lazy")
    @Table(name = "LAZY")
    public static class Lazy {
        @Id
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}

