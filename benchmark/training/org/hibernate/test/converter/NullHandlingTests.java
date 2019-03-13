/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class NullHandlingTests extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8697")
    public void testNullReplacementOnBinding() {
        NullHandlingTests.TheEntity theEntity = new NullHandlingTests.TheEntity(1);
        Session session = openSession();
        session.beginTransaction();
        // at this point TheEntity.sex is null
        // lets make sure that the converter is given a chance to adjust that to UNKNOWN...
        session.save(theEntity);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                ResultSet rs = connection.createStatement().executeQuery("select sex from the_entity where id=1");
                try {
                    if (!(rs.next())) {
                        throw new RuntimeException("Could not locate inserted row");
                    }
                    String sexDbValue = rs.getString(1);
                    if (rs.next()) {
                        throw new RuntimeException("Found more than one row");
                    }
                    Assert.assertEquals(NullHandlingTests.Sex.UNKNOWN.name().toLowerCase(Locale.ENGLISH), sexDbValue);
                } finally {
                    rs.close();
                }
            }
        });
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        session.delete(theEntity);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9320")
    public void testNullReplacementOnExtraction() {
        Session session = openSession();
        session.beginTransaction();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                connection.createStatement().execute("insert into the_entity(id, sex) values (1, null)");
            }
        });
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        // at this point TheEntity.sex is null in the database
        // lets load it and make sure that the converter is given a chance to adjust that to UNKNOWN...
        NullHandlingTests.TheEntity theEntity = ((NullHandlingTests.TheEntity) (session.get(NullHandlingTests.TheEntity.class, 1)));
        session.getTransaction().commit();
        session.close();
        Assert.assertEquals(NullHandlingTests.Sex.UNKNOWN, theEntity.sex);
        session = openSession();
        session.beginTransaction();
        session.delete(theEntity);
        session.getTransaction().commit();
        session.close();
    }

    @Entity(name = "TheEntity")
    @Table(name = "the_entity")
    public static class TheEntity {
        @Id
        public Integer id;

        @Convert(converter = NullHandlingTests.SexConverter.class)
        public NullHandlingTests.Sex sex;

        public TheEntity() {
        }

        public TheEntity(Integer id) {
            this.id = id;
        }
    }

    public static enum Sex {

        MALE,
        FEMALE,
        UNKNOWN;}

    public static class SexConverter implements AttributeConverter<NullHandlingTests.Sex, String> {
        @Override
        public String convertToDatabaseColumn(NullHandlingTests.Sex attribute) {
            // HHH-8697
            if (attribute == null) {
                return NullHandlingTests.Sex.UNKNOWN.name().toLowerCase(Locale.ENGLISH);
            }
            return attribute.name().toLowerCase(Locale.ENGLISH);
        }

        @Override
        public NullHandlingTests.Sex convertToEntityAttribute(String dbData) {
            // HHH-9320
            if (dbData == null) {
                return NullHandlingTests.Sex.UNKNOWN;
            }
            return NullHandlingTests.Sex.valueOf(dbData.toUpperCase(Locale.ENGLISH));
        }
    }
}

