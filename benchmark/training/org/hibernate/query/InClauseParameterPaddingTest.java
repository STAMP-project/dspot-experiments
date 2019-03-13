/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12469")
public class InClauseParameterPaddingTest extends BaseEntityManagerFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testInClauseParameterPadding() {
        validateInClauseParameterPadding("in (?)", 1);
        validateInClauseParameterPadding("in (? , ?)", 1, 2);
        validateInClauseParameterPadding("in (? , ? , ? , ?)", 1, 2, 3);
        validateInClauseParameterPadding("in (? , ? , ? , ?)", 1, 2, 3, 4);
        validateInClauseParameterPadding("in (? , ? , ? , ? , ? , ? , ? , ?)", 1, 2, 3, 4, 5);
        validateInClauseParameterPadding("in (? , ? , ? , ? , ? , ? , ? , ?)", 1, 2, 3, 4, 5, 6);
        validateInClauseParameterPadding("in (? , ? , ? , ? , ? , ? , ? , ?)", 1, 2, 3, 4, 5, 6, 7);
        validateInClauseParameterPadding("in (? , ? , ? , ? , ? , ? , ? , ?)", 1, 2, 3, 4, 5, 6, 7, 8);
        validateInClauseParameterPadding("in (? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)", 1, 2, 3, 4, 5, 6, 7, 8, 9);
        validateInClauseParameterPadding("in (? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Integer id;

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

