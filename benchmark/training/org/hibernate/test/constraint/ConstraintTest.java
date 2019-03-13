/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.constraint;


import java.util.Iterator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.UniqueConstraint;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Meyer
 */
public class ConstraintTest extends BaseNonConfigCoreFunctionalTestCase {
    private static final int MAX_NAME_LENGTH = 30;

    private static final String EXPLICIT_FK_NAME_NATIVE = "fk_explicit_native";

    private static final String EXPLICIT_FK_NAME_JPA = "fk_explicit_jpa";

    private static final String EXPLICIT_UK_NAME = "uk_explicit";

    @Test
    @TestForIssue(jiraKey = "HHH-7797")
    public void testUniqueConstraints() {
        Column column = ((Column) (metadata().getEntityBinding(ConstraintTest.DataPoint.class.getName()).getProperty("foo1").getColumnIterator().next()));
        Assert.assertFalse(column.isNullable());
        Assert.assertTrue(column.isUnique());
        column = ((Column) (metadata().getEntityBinding(ConstraintTest.DataPoint.class.getName()).getProperty("foo2").getColumnIterator().next()));
        Assert.assertTrue(column.isNullable());
        Assert.assertTrue(column.isUnique());
        column = ((Column) (metadata().getEntityBinding(ConstraintTest.DataPoint.class.getName()).getProperty("id").getColumnIterator().next()));
        Assert.assertFalse(column.isNullable());
        Assert.assertTrue(column.isUnique());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1904")
    public void testConstraintNameLength() {
        int foundCount = 0;
        for (Namespace namespace : metadata().getDatabase().getNamespaces()) {
            for (Table table : namespace.getTables()) {
                Iterator fkItr = table.getForeignKeyIterator();
                while (fkItr.hasNext()) {
                    ForeignKey fk = ((ForeignKey) (fkItr.next()));
                    Assert.assertTrue(((fk.getName().length()) <= (ConstraintTest.MAX_NAME_LENGTH)));
                    // ensure the randomly generated constraint name doesn't
                    // happen if explicitly given
                    Column column = fk.getColumn(0);
                    if (column.getName().equals("explicit_native")) {
                        foundCount++;
                        Assert.assertEquals(fk.getName(), ConstraintTest.EXPLICIT_FK_NAME_NATIVE);
                    } else
                        if (column.getName().equals("explicit_jpa")) {
                            foundCount++;
                            Assert.assertEquals(fk.getName(), ConstraintTest.EXPLICIT_FK_NAME_JPA);
                        }

                } 
                Iterator ukItr = table.getUniqueKeyIterator();
                while (ukItr.hasNext()) {
                    UniqueKey uk = ((UniqueKey) (ukItr.next()));
                    Assert.assertTrue(((uk.getName().length()) <= (ConstraintTest.MAX_NAME_LENGTH)));
                    // ensure the randomly generated constraint name doesn't
                    // happen if explicitly given
                    Column column = uk.getColumn(0);
                    if (column.getName().equals("explicit")) {
                        foundCount++;
                        Assert.assertEquals(uk.getName(), ConstraintTest.EXPLICIT_UK_NAME);
                    }
                } 
            }
        }
        Assert.assertEquals("Could not find the necessary columns.", 3, foundCount);
    }

    @Entity
    @javax.persistence.Table(name = "DataPoint", uniqueConstraints = { @UniqueConstraint(name = ConstraintTest.EXPLICIT_UK_NAME, columnNames = { "explicit" }) })
    public static class DataPoint {
        @Id
        @GeneratedValue
        @Column(nullable = false, unique = true)
        public long id;

        @Column(nullable = false, unique = true)
        public String foo1;

        @Column(nullable = true, unique = true)
        public String foo2;

        public String explicit;
    }

    @Entity
    @javax.persistence.Table(name = "DataPoint2")
    public static class DataPoint2 {
        @Id
        @GeneratedValue
        public long id;

        @OneToOne
        public ConstraintTest.DataPoint dp;

        @OneToOne
        @ForeignKey(name = ConstraintTest.EXPLICIT_FK_NAME_NATIVE)
        @JoinColumn(name = "explicit_native")
        public ConstraintTest.DataPoint explicit_native;

        @OneToOne
        @JoinColumn(name = "explicit_jpa", foreignKey = @ForeignKey(name = ConstraintTest.EXPLICIT_FK_NAME_JPA))
        public ConstraintTest.DataPoint explicit_jpa;
    }
}

