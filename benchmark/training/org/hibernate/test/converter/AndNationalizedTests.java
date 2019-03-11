/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import java.sql.Types;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Nationalized;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the combination of @Nationalized and @Convert
 *
 * @author Steve Ebersole
 */
// @SkipForDialect(value = PostgreSQL81Dialect.class, comment = "Postgres does not support ")
public class AndNationalizedTests extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9599")
    public void basicTest() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            Metadata metadata = addAnnotatedClass(AndNationalizedTests.TestEntity.class).buildMetadata();
            validate();
            final PersistentClass entityBinding = metadata.getEntityBinding(AndNationalizedTests.TestEntity.class.getName());
            if (((metadata.getDatabase().getDialect()) instanceof PostgreSQL81Dialect) || ((metadata.getDatabase().getDialect()) instanceof DB2Dialect)) {
                // See issue HHH-10693 for PostgreSQL, HHH-12753 for DB2
                Assert.assertEquals(Types.VARCHAR, entityBinding.getProperty("name").getType().sqlTypes(metadata)[0]);
            } else {
                Assert.assertEquals(Types.NVARCHAR, entityBinding.getProperty("name").getType().sqlTypes(metadata)[0]);
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity(name = "TestEntity")
    @Table(name = "TestEntity")
    public static class TestEntity {
        @Id
        public Integer id;

        @Nationalized
        @Convert(converter = AndNationalizedTests.NameConverter.class)
        public AndNationalizedTests.Name name;
    }

    public static class Name {
        private final String text;

        public Name(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class NameConverter implements AttributeConverter<AndNationalizedTests.Name, String> {
        @Override
        public String convertToDatabaseColumn(AndNationalizedTests.Name attribute) {
            return attribute.getText();
        }

        @Override
        public AndNationalizedTests.Name convertToEntityAttribute(String dbData) {
            return new AndNationalizedTests.Name(dbData);
        }
    }
}

