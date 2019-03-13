/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.boot.model.source;


import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Guillaume Smet
 */
public class AttributePathTest {
    @Test
    @TestForIssue(jiraKey = "HHH-10863")
    public void testCollectionElement() {
        AttributePath attributePath = AttributePath.parse("items.collection&&element.name");
        Assert.assertFalse(attributePath.isCollectionElement());
        Assert.assertTrue(attributePath.getParent().isCollectionElement());
        Assert.assertFalse(attributePath.getParent().getParent().isCollectionElement());
    }
}

