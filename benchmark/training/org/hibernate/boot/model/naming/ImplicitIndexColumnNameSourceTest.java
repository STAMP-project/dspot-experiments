/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot.model.naming;


import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dmytro Bondar
 */
public class ImplicitIndexColumnNameSourceTest {
    @Test
    @TestForIssue(jiraKey = "HHH-10810")
    public void testExtensionImplicitNameSource() {
        Assert.assertTrue(ImplicitNameSource.class.isAssignableFrom(ImplicitIndexColumnNameSource.class));
    }
}

