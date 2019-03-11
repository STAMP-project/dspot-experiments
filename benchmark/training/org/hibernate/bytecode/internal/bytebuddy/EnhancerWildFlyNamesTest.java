/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.bytecode.internal.bytebuddy;


import java.io.IOException;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * WildFly will use class names in "internal JVM format" when invoking the enhancer,
 * meaning the package separator is '/' rather than '.'.
 * We need to make sure this is handled.
 */
public class EnhancerWildFlyNamesTest {
    @Test
    @TestForIssue(jiraKey = "HHH-12545")
    public void test() {
        Enhancer enhancer = createByteBuddyEnhancer();
        String internalName = SimpleEntity.class.getName().replace('.', '/');
        String resourceName = internalName + ".class";
        byte[] buffer = new byte[0];
        try {
            buffer = readResource(resourceName);
        } catch (IOException e) {
            Assert.fail("Should not have an IOException here");
        }
        byte[] enhanced = enhancer.enhance(internalName, buffer);
        Assert.assertNotNull("This is null when there have been swallowed exceptions during enhancement. Check Logs!", enhanced);
    }
}

