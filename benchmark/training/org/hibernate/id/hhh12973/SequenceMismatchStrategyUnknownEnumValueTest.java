/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id.hhh12973;


import org.hibernate.HibernateException;
import org.hibernate.id.SequenceMismatchStrategy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.util.ExceptionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12973")
public class SequenceMismatchStrategyUnknownEnumValueTest extends BaseUnitTestCase {
    @Test
    public void test() {
        try {
            SequenceMismatchStrategy.interpret("acme");
            Assert.fail("Should throw HibernateException!");
        } catch (Exception e) {
            Throwable rootCause = ExceptionUtil.rootCause(e);
            Assert.assertTrue((rootCause instanceof HibernateException));
            Assert.assertEquals("Unrecognized sequence.increment_size_mismatch_strategy value : [acme].  Supported values include [log], [exception], and [fix].", rootCause.getMessage());
        }
    }
}

