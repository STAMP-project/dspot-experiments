/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.notfound;


import DialectChecks.SupportsIdentityColumns;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class NotFoundTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToOne() throws Exception {
        final NotFoundTest.Currency euro = new NotFoundTest.Currency();
        euro.setName("Euro");
        final NotFoundTest.Coin fiveCents = new NotFoundTest.Coin();
        fiveCents.setName("Five cents");
        fiveCents.setCurrency(euro);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(euro);
            session.persist(fiveCents);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.notfound.Currency _euro = session.get(.class, euro.getId());
            session.delete(_euro);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.notfound.Coin _fiveCents = session.get(.class, fiveCents.getId());
            assertNull(_fiveCents.getCurrency());
            session.delete(_fiveCents);
        });
    }

    @Entity(name = "Coin")
    public static class Coin {
        private Integer id;

        private String name;

        private NotFoundTest.Currency currency;

        @Id
        @GeneratedValue
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ManyToOne
        @JoinColumn(name = "currency", referencedColumnName = "name")
        @NotFound(action = NotFoundAction.IGNORE)
        public NotFoundTest.Currency getCurrency() {
            return currency;
        }

        public void setCurrency(NotFoundTest.Currency currency) {
            this.currency = currency;
        }
    }

    @Entity(name = "Currency")
    public static class Currency implements Serializable {
        private Integer id;

        private String name;

        @Id
        @GeneratedValue
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

