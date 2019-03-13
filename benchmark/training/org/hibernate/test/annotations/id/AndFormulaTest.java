/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.id;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hamcrest.CoreMatchers;
import org.hibernate.annotations.Formula;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.cfg.CannotForceNonNullableException;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Originally developed for HHH-9807 - better error message on combination of {@code @Id} + {@code @Formula}
 *
 * @author Steve Ebersole
 */
public class AndFormulaTest extends BaseUnitTestCase {
    private static StandardServiceRegistry ssr;

    @Test
    public void testBindingEntityWithIdAndFormula() {
        try {
            addAnnotatedClass(AndFormulaTest.EntityWithIdAndFormula.class).buildMetadata();
            Assert.fail("Expecting failure from invalid mapping");
        } catch (CannotForceNonNullableException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("Identifier property ["));
        }
    }

    @Entity
    public static class EntityWithIdAndFormula {
        @Id
        @Formula("VALUE")
        public Integer id;
    }
}

