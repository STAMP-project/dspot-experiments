/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.security.authorization;


import ExceptionMessage.INVALID_MODE;
import ExceptionMessage.INVALID_MODE_SEGMENT;
import Mode.Bits.ALL;
import Mode.Bits.EXECUTE;
import Mode.Bits.NONE;
import Mode.Bits.READ;
import Mode.Bits.READ_EXECUTE;
import Mode.Bits.READ_WRITE;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for the {@code ModeParser}.
 *
 * @author rvesse
 */
public final class ModeParserTest {
    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    @Test
    public void numerics() {
        Mode parsed = ModeParser.parse("777");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(ALL, parsed.getGroupBits());
        Assert.assertEquals(ALL, parsed.getOtherBits());
        parsed = ModeParser.parse("755");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(READ_EXECUTE, parsed.getGroupBits());
        Assert.assertEquals(READ_EXECUTE, parsed.getOtherBits());
        parsed = ModeParser.parse("644");
        Assert.assertEquals(READ_WRITE, parsed.getOwnerBits());
        Assert.assertEquals(READ, parsed.getGroupBits());
        Assert.assertEquals(READ, parsed.getOtherBits());
    }

    @Test
    public void symbolicsSeparated() {
        Mode parsed = ModeParser.parse("u=rwx,g=rwx,o=rwx");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(ALL, parsed.getGroupBits());
        Assert.assertEquals(ALL, parsed.getOtherBits());
        parsed = ModeParser.parse("u=rwx,g=rx,o=rx");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(READ_EXECUTE, parsed.getGroupBits());
        Assert.assertEquals(READ_EXECUTE, parsed.getOtherBits());
        parsed = ModeParser.parse("u=rw,g=r,o=r");
        Assert.assertEquals(READ_WRITE, parsed.getOwnerBits());
        Assert.assertEquals(READ, parsed.getGroupBits());
        Assert.assertEquals(READ, parsed.getOtherBits());
    }

    @Test
    public void symbolicsCombined() {
        Mode parsed = ModeParser.parse("a=rwx");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(ALL, parsed.getGroupBits());
        Assert.assertEquals(ALL, parsed.getOtherBits());
        parsed = ModeParser.parse("ugo=rwx");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(ALL, parsed.getGroupBits());
        Assert.assertEquals(ALL, parsed.getOtherBits());
        parsed = ModeParser.parse("u=rwx,go=rx");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(READ_EXECUTE, parsed.getGroupBits());
        Assert.assertEquals(READ_EXECUTE, parsed.getOtherBits());
        parsed = ModeParser.parse("u=rw,go=r");
        Assert.assertEquals(READ_WRITE, parsed.getOwnerBits());
        Assert.assertEquals(READ, parsed.getGroupBits());
        Assert.assertEquals(READ, parsed.getOtherBits());
    }

    @Test
    public void symbolicsCumulative() {
        Mode parsed = ModeParser.parse("u=r,u=w,u=x");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(NONE, parsed.getGroupBits());
        Assert.assertEquals(NONE, parsed.getOtherBits());
        parsed = ModeParser.parse("g=r,g=w,g=x");
        Assert.assertEquals(NONE, parsed.getOwnerBits());
        Assert.assertEquals(ALL, parsed.getGroupBits());
        Assert.assertEquals(NONE, parsed.getOtherBits());
        parsed = ModeParser.parse("o=r,o=w,o=x");
        Assert.assertEquals(NONE, parsed.getOwnerBits());
        Assert.assertEquals(NONE, parsed.getGroupBits());
        Assert.assertEquals(ALL, parsed.getOtherBits());
    }

    @Test
    public void symbolicsPartial() {
        Mode parsed = ModeParser.parse("u=rwx");
        Assert.assertEquals(ALL, parsed.getOwnerBits());
        Assert.assertEquals(NONE, parsed.getGroupBits());
        Assert.assertEquals(NONE, parsed.getOtherBits());
        parsed = ModeParser.parse("go=rw");
        Assert.assertEquals(NONE, parsed.getOwnerBits());
        Assert.assertEquals(READ_WRITE, parsed.getGroupBits());
        Assert.assertEquals(READ_WRITE, parsed.getOtherBits());
        parsed = ModeParser.parse("o=x");
        Assert.assertEquals(NONE, parsed.getOwnerBits());
        Assert.assertEquals(NONE, parsed.getGroupBits());
        Assert.assertEquals(EXECUTE, parsed.getOtherBits());
    }

    @Test
    public void symbolicsBadEmpty() {
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_MODE.getMessage(""));
        ModeParser.parse("");
    }

    @Test
    public void symbolicsBadNull() {
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_MODE.getMessage(((Object) (null))));
        ModeParser.parse(null);
    }

    @Test
    public void symbolicsBadWhitespace() {
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_MODE.getMessage("  "));
        ModeParser.parse("  ");
    }

    @Test
    public void symbolicsBadNoSeparator() {
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_MODE_SEGMENT.getMessage("u=rwx,foo", "foo"));
        ModeParser.parse("u=rwx,foo");
    }

    @Test
    public void symbolicsBadTargets() {
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_MODE_SEGMENT.getMessage("f=r", "f=r", "f"));
        ModeParser.parse("f=r");
    }

    @Test
    public void symbolicsBadPermissions() {
        mThrown.expect(IllegalArgumentException.class);
        mThrown.expectMessage(INVALID_MODE_SEGMENT.getMessage("u=Xst", "u=Xst", "Xst"));
        ModeParser.parse("u=Xst");
    }
}

