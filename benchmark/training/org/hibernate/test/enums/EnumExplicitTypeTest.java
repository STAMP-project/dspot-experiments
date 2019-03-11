/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.enums;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class EnumExplicitTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10766")
    public void hbmEnumWithExplicitTypeTest() {
        long id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person person = Person.person(Gender.MALE, HairColor.BROWN);
            person.setOriginalHairColor(HairColor.BLONDE);
            session.persist(person);
            return person.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Number personId = ((Number) (session.createNativeQuery("select id from Person where originalHairColor = :color").setParameter("color", HairColor.BLONDE.name()).getSingleResult()));
            assertEquals(id, personId.longValue());
        });
    }
}

