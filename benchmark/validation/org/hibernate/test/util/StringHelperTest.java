/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.util;


import org.hibernate.internal.util.StringHelper;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class StringHelperTest extends BaseUnitTestCase {
    private static final String BASE_PACKAGE = "org.hibernate";

    private static final String STRING_HELPER_FQN = "org.hibernate.internal.util.StringHelper";

    private static final String STRING_HELPER_NAME = StringHelper.unqualify(StringHelperTest.STRING_HELPER_FQN);

    @Test
    public void testNameCollapsing() {
        Assert.assertNull(StringHelper.collapse(null));
        Assert.assertEquals(StringHelperTest.STRING_HELPER_NAME, StringHelper.collapse(StringHelperTest.STRING_HELPER_NAME));
        Assert.assertEquals("o.h.i.u.StringHelper", StringHelper.collapse(StringHelperTest.STRING_HELPER_FQN));
    }

    @Test
    public void testPartialNameUnqualification() {
        Assert.assertNull(StringHelper.partiallyUnqualify(null, StringHelperTest.BASE_PACKAGE));
        Assert.assertEquals(StringHelperTest.STRING_HELPER_NAME, StringHelper.partiallyUnqualify(StringHelperTest.STRING_HELPER_NAME, StringHelperTest.BASE_PACKAGE));
        Assert.assertEquals("internal.util.StringHelper", StringHelper.partiallyUnqualify(StringHelperTest.STRING_HELPER_FQN, StringHelperTest.BASE_PACKAGE));
    }

    @Test
    public void testBasePackageCollapsing() {
        Assert.assertNull(StringHelper.collapseQualifierBase(null, StringHelperTest.BASE_PACKAGE));
        Assert.assertEquals(StringHelperTest.STRING_HELPER_NAME, StringHelper.collapseQualifierBase(StringHelperTest.STRING_HELPER_NAME, StringHelperTest.BASE_PACKAGE));
        Assert.assertEquals("o.h.internal.util.StringHelper", StringHelper.collapseQualifierBase(StringHelperTest.STRING_HELPER_FQN, StringHelperTest.BASE_PACKAGE));
    }

    @Test
    public void testFindIdentifierWord() {
        Assert.assertEquals(StringHelper.indexOfIdentifierWord("", "word"), (-1));
        Assert.assertEquals(StringHelper.indexOfIdentifierWord(null, "word"), (-1));
        Assert.assertEquals(StringHelper.indexOfIdentifierWord("sentence", null), (-1));
        Assert.assertEquals(StringHelper.indexOfIdentifierWord("where name=?13 and description=?1", "?1"), 31);
        Assert.assertEquals(StringHelper.indexOfIdentifierWord("where name=?13 and description=?1 and category_id=?4", "?1"), 31);
        Assert.assertEquals(StringHelper.indexOfIdentifierWord("?1", "?1"), 0);
        Assert.assertEquals(StringHelper.indexOfIdentifierWord("no identifier here", "?1"), (-1));
        Assert.assertEquals(StringHelper.indexOfIdentifierWord("some text ?", "?"), 10);
    }
}

