/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.basic;


import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-9529")
@RunWith(BytecodeEnhancerRunner.class)
public class CrossEnhancementTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        sessionFactory().close();
        buildSessionFactory();
    }

    // --- //
    @Entity
    @Table(name = "PARENT")
    private static class Parent {
        @Id
        String id;
    }

    @Embeddable
    private static class ChildKey implements Serializable {
        String parent;

        String type;
    }

    @Entity
    @Table(name = "CHILD")
    private static class Child {
        @EmbeddedId
        CrossEnhancementTest.ChildKey id;

        @MapsId("parent")
        @ManyToOne
        CrossEnhancementTest.Parent parent;

        public String getfieldOnChildKeyParent() {
            // Note that there are two GETFIELD ops here, one on the field 'id' that should be enhanced and another
            // on the field 'parent' that may be or not (depending if 'extended enhancement' is enabled)
            // Either way, the field 'parent' on ChildKey should not be confused with the field 'parent' on Child
            return id.parent;
        }
    }
}

