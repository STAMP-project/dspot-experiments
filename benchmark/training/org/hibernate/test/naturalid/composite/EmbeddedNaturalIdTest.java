/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.naturalid.composite;


import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11255")
public class EmbeddedNaturalIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            PostalCarrier postalCarrier = new PostalCarrier();
            postalCarrier.setId(1L);
            postalCarrier.setPostalCode(new PostalCode());
            postalCarrier.getPostalCode().setCode("ABC123");
            postalCarrier.getPostalCode().setCountry("US");
            entityManager.persist(postalCarrier);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            PostalCarrier postalCarrier = entityManager.unwrap(.class).byNaturalId(.class).using("postalCode", new PostalCode("ABC123", "US")).load();
            assertEquals(Long.valueOf(1L), postalCarrier.getId());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            PostalCarrier postalCarrier = entityManager.unwrap(.class).bySimpleNaturalId(.class).load(new PostalCode("ABC123", "US"));
            assertEquals(Long.valueOf(1L), postalCarrier.getId());
        });
    }
}

