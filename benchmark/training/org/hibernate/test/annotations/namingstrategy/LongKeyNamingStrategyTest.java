/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations.namingstrategy;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test harness for HHH-11089.
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11089")
public class LongKeyNamingStrategyTest extends BaseUnitTestCase {
    private ServiceRegistry serviceRegistry;

    @Test
    public void testWithCustomNamingStrategy() throws Exception {
        Metadata metadata = addAnnotatedClass(LongKeyNamingStrategyTest.Person.class).getMetadataBuilder().applyImplicitNamingStrategy(new LongIdentifierNamingStrategy()).build();
        ForeignKey foreignKey = ((ForeignKey) (metadata.getEntityBinding(LongKeyNamingStrategyTest.Address.class.getName()).getTable().getForeignKeyIterator().next()));
        Assert.assertEquals("FK_way_longer_than_the_30_char", foreignKey.getName());
        UniqueKey uniqueKey = metadata.getEntityBinding(LongKeyNamingStrategyTest.Address.class.getName()).getTable().getUniqueKeyIterator().next();
        Assert.assertEquals("UK_way_longer_than_the_30_char", uniqueKey.getName());
        Index index = metadata.getEntityBinding(LongKeyNamingStrategyTest.Address.class.getName()).getTable().getIndexIterator().next();
        Assert.assertEquals("IDX_way_longer_than_the_30_cha", index.getName());
    }

    @Entity(name = "Address")
    @Table(uniqueConstraints = @UniqueConstraint(name = "UK_way_longer_than_the_30_characters_limit", columnNames = { "city", "streetName", "streetNumber" }), indexes = @javax.persistence.Index(name = "IDX_way_longer_than_the_30_characters_limit", columnList = "city, streetName, streetNumber"))
    public class Address {
        @Id
        private Long id;

        private String city;

        private String streetName;

        private String streetNumber;

        @ManyToOne
        @JoinColumn(name = "person_id", foreignKey = @javax.persistence.ForeignKey(name = "FK_way_longer_than_the_30_characters_limit"))
        private LongKeyNamingStrategyTest.Person person;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreetName() {
            return streetName;
        }

        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }

        public String getStreetNumber() {
            return streetNumber;
        }

        public void setStreetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
        }
    }

    @Entity
    public class Person {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}

