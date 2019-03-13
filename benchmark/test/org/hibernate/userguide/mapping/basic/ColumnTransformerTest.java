/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import java.util.Currency;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::mapping-column-read-and-write-composite-type-example[]
@RequiresDialect(H2Dialect.class)
public class ColumnTransformerTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        // tag::mapping-column-read-and-write-composite-type-persistence-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::basic-datetime-temporal-date-persist-example[]
            org.hibernate.userguide.mapping.basic.Savings savings = new org.hibernate.userguide.mapping.basic.Savings();
            savings.setId(1L);
            savings.setWallet(new MonetaryAmount(BigDecimal.TEN, Currency.getInstance(Locale.US)));
            entityManager.persist(savings);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.Savings savings = entityManager.find(.class, 1L);
            assertEquals(10, savings.getWallet().getAmount().intValue());
        });
        // end::mapping-column-read-and-write-composite-type-persistence-example[]
    }

    // tag::mapping-column-read-and-write-composite-type-example[]
    // tag::mapping-column-read-and-write-composite-type-example[]
    @Entity(name = "Savings")
    public static class Savings {
        @Id
        private Long id;

        @Type(type = "org.hibernate.userguide.mapping.basic.MonetaryAmountUserType")
        @Columns(columns = { @Column(name = "money"), @Column(name = "currency") })
        @ColumnTransformer(forColumn = "money", read = "money / 100", write = "? * 100")
        private MonetaryAmount wallet;

        // Getters and setters omitted for brevity
        // end::mapping-column-read-and-write-composite-type-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public MonetaryAmount getWallet() {
            return wallet;
        }

        public void setWallet(MonetaryAmount wallet) {
            this.wallet = wallet;
        }
    }
}

