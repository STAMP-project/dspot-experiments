/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@RequiresDialect(H2Dialect.class)
public class SmallIntToShortClassMappingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12115")
    public void testShortType() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.type.Event event = new org.hibernate.test.type.Event();
            event.id = 1;
            event.registrationNumber = "123";
            entityManager.persist(event);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.type.Event event = entityManager.find(.class, ((short) (1)));
            assertEquals("123", event.registrationNumber);
        });
    }

    @Entity(name = "Event")
    @Table(name = "event")
    public static class Event {
        @Id
        @Column(columnDefinition = "SMALLINT")
        private Short id;

        @Column(columnDefinition = "SMALLINT")
        @Convert(converter = SmallIntToShortClassMappingTest.ShortToString.class)
        private String registrationNumber;
    }

    @Converter
    public static class ShortToString implements AttributeConverter<String, Short> {
        @Override
        public Short convertToDatabaseColumn(String attribute) {
            if (attribute == null) {
                return null;
            }
            return Short.valueOf(attribute);
        }

        @Override
        public String convertToEntityAttribute(Short dbData) {
            if (dbData == null) {
                return null;
            }
            return String.valueOf(dbData);
        }
    }
}

