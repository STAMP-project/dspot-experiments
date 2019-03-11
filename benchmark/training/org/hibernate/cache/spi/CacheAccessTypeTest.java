/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.cache.spi;


import AccessType.NONSTRICT_READ_WRITE;
import AccessType.READ_ONLY;
import AccessType.READ_WRITE;
import AccessType.TRANSACTIONAL;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.UnknownAccessTypeException;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class CacheAccessTypeTest {
    @Test
    @TestForIssue(jiraKey = "HHH-9844")
    public void testExplicitExternalNames() {
        Assert.assertSame(READ_ONLY, AccessType.fromExternalName("read-only"));
        Assert.assertSame(READ_WRITE, AccessType.fromExternalName("read-write"));
        Assert.assertSame(NONSTRICT_READ_WRITE, AccessType.fromExternalName("nonstrict-read-write"));
        Assert.assertSame(TRANSACTIONAL, AccessType.fromExternalName("transactional"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9844")
    public void testEnumNames() {
        Assert.assertSame(READ_ONLY, AccessType.fromExternalName("READ_ONLY"));
        Assert.assertSame(READ_WRITE, AccessType.fromExternalName("READ_WRITE"));
        Assert.assertSame(NONSTRICT_READ_WRITE, AccessType.fromExternalName("NONSTRICT_READ_WRITE"));
        Assert.assertSame(TRANSACTIONAL, AccessType.fromExternalName("TRANSACTIONAL"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9844")
    public void testLowerCaseEnumNames() {
        Assert.assertSame(READ_ONLY, AccessType.fromExternalName("read_only"));
        Assert.assertSame(READ_WRITE, AccessType.fromExternalName("read_write"));
        Assert.assertSame(NONSTRICT_READ_WRITE, AccessType.fromExternalName("nonstrict_read_write"));
        Assert.assertSame(TRANSACTIONAL, AccessType.fromExternalName("transactional"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9844")
    public void testUpperCaseWithHyphens() {
        try {
            AccessType.fromExternalName("READ-ONLY");
            Assert.fail("should have failed because upper-case using hyphans is not supported.");
        } catch (UnknownAccessTypeException ex) {
            // expected
        }
        try {
            AccessType.fromExternalName("READ-WRITE");
            Assert.fail("should have failed because upper-case using hyphans is not supported.");
        } catch (UnknownAccessTypeException ex) {
            // expected
        }
        try {
            AccessType.fromExternalName("NONSTRICT-READ-WRITE");
            Assert.fail("should have failed because upper-case using hyphans is not supported.");
        } catch (UnknownAccessTypeException ex) {
            // expected
        }
    }
}

