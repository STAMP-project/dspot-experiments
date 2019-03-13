/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::basic-enums-attribute-converter-example[]
public class EnumerationConverterTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.Person person = new org.hibernate.userguide.mapping.basic.Person();
            person.setId(1L);
            person.setName("John Doe");
            person.setGender(Gender.MALE);
            entityManager.persist(person);
        });
    }

    // tag::basic-enums-attribute-converter-example[]
    // tag::basic-enums-attribute-converter-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        @Convert(converter = EnumerationConverterTest.GenderConverter.class)
        public Gender gender;

        // Getters and setters are omitted for brevity
        // end::basic-enums-attribute-converter-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }
    }

    @Converter
    public static class GenderConverter implements AttributeConverter<Gender, Character> {
        public Character convertToDatabaseColumn(Gender value) {
            if (value == null) {
                return null;
            }
            return value.getCode();
        }

        public Gender convertToEntityAttribute(Character value) {
            if (value == null) {
                return null;
            }
            return Gender.fromCode(value);
        }
    }
}

