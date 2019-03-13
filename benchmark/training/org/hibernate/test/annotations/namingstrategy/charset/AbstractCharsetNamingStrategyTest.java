/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations.namingstrategy.charset;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.test.annotations.namingstrategy.LongIdentifierNamingStrategy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12357")
public abstract class AbstractCharsetNamingStrategyTest extends BaseUnitTestCase {
    protected ServiceRegistry serviceRegistry;

    @Test
    public void testWithCustomNamingStrategy() throws Exception {
        Metadata metadata = addAnnotatedClass(AbstractCharsetNamingStrategyTest.Person.class).getMetadataBuilder().applyImplicitNamingStrategy(new LongIdentifierNamingStrategy()).build();
        UniqueKey uniqueKey = metadata.getEntityBinding(AbstractCharsetNamingStrategyTest.Address.class.getName()).getTable().getUniqueKeyIterator().next();
        Assert.assertEquals(expectedUniqueKeyName(), uniqueKey.getName());
        ForeignKey foreignKey = ((ForeignKey) (metadata.getEntityBinding(AbstractCharsetNamingStrategyTest.Address.class.getName()).getTable().getForeignKeyIterator().next()));
        Assert.assertEquals(expectedForeignKeyName(), foreignKey.getName());
        Index index = metadata.getEntityBinding(AbstractCharsetNamingStrategyTest.Address.class.getName()).getTable().getIndexIterator().next();
        Assert.assertEquals(expectedIndexName(), index.getName());
    }

    @Entity(name = "Address")
    @Table(uniqueConstraints = @UniqueConstraint(columnNames = { "city", "strad?" }), indexes = @javax.persistence.Index(columnList = "city, strad?"))
    public class Address {
        @Id
        private Long id;

        private String city;

        private String strad?;

        @ManyToOne
        private AbstractCharsetNamingStrategyTest.Person person?;
    }

    @Entity(name = "Person")
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

