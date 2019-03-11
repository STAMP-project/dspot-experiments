/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.notfound;


import java.io.Serializable;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 * @author Gail Badner
 */
public class NotFoundLogicalOneToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLogicalOneToOne() throws Exception {
        NotFoundLogicalOneToOneTest.Currency euro = new NotFoundLogicalOneToOneTest.Currency();
        euro.setName("Euro");
        NotFoundLogicalOneToOneTest.Coin fiveC = new NotFoundLogicalOneToOneTest.Coin();
        fiveC.setName("Five cents");
        fiveC.setCurrency(euro);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(euro);
            session.persist(fiveC);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(euro);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.notfound.Coin coin = session.get(.class, fiveC.getId());
            assertNull(coin.getCurrency());
            session.delete(coin);
        });
    }

    @Entity(name = "Coin")
    public static class Coin {
        private Integer id;

        private String name;

        private NotFoundLogicalOneToOneTest.Currency currency;

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

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @NotFound(action = NotFoundAction.IGNORE)
        public NotFoundLogicalOneToOneTest.Currency getCurrency() {
            return currency;
        }

        public void setCurrency(NotFoundLogicalOneToOneTest.Currency currency) {
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

