/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.namescope;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class IdGeneratorNamesGlobalScopeTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNoSequenceGenratorNameClash() {
        buildSessionFactory();
    }

    @Entity(name = "FirstEntity")
    @TableGenerator(name = "table-generator", table = "table_identifier_2", pkColumnName = "identifier", valueColumnName = "value", allocationSize = 5)
    public static class FirstEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "table-generator")
        private Long id;
    }

    @Entity(name = "SecondEntity")
    @TableGenerator(name = "table-generator", table = "table_identifier", pkColumnName = "identifier", valueColumnName = "value", allocationSize = 5)
    public static class SecondEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "table-generator")
        private Long id;
    }
}

