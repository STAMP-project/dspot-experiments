/**
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sk89q.minecraft.util.commands;


import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


public class CommandContextTest {
    private static final Logger log = Logger.getLogger(CommandContextTest.class.getCanonicalName());

    private static final String firstCmdString = "herpderp -opw testers \"mani world\" \'another thing\'  because something";

    CommandContext firstCommand;

    @Test(expected = CommandException.class)
    public void testInvalidFlags() throws CommandException {
        final String failingCommand = "herpderp -opw testers";
        new CommandContext(failingCommand, new HashSet(Arrays.asList('o', 'w')));
    }

    @Test
    public void testBasicArgs() {
        String command = firstCommand.getCommand();
        String argOne = firstCommand.getString(0);
        String joinedArg = firstCommand.getJoinedStrings(0);
        Assert.assertEquals("herpderp", command);
        Assert.assertEquals("another thing", argOne);
        Assert.assertEquals("'another thing'  because something", joinedArg);
    }

    @Test
    public void testFlags() {
        Assert.assertTrue(firstCommand.hasFlag('p'));
        Assert.assertTrue(firstCommand.hasFlag('o'));
        Assert.assertTrue(firstCommand.hasFlag('w'));
        Assert.assertEquals("testers", firstCommand.getFlag('o'));
        Assert.assertEquals("mani world", firstCommand.getFlag('w'));
        Assert.assertFalse(firstCommand.hasFlag('u'));
    }

    @Test
    public void testOnlyQuotedString() {
        String cmd = "r \"hello goodbye have fun\"";
        String cmd2 = "r 'hellogeedby' nnnnnee";
        try {
            new CommandContext(cmd);
            new CommandContext(cmd2);
        } catch (CommandException e) {
            CommandContextTest.log.log(Level.WARNING, "Error", e);
            Assert.fail("Error creating CommandContext");
        }
    }

    @Test
    public void testUnmatchedQuote() {
        String cmd = "r \"hello goodbye have fun";
        try {
            new CommandContext(cmd);
        } catch (CommandException e) {
            CommandContextTest.log.log(Level.WARNING, "Error", e);
            Assert.fail("Error creating CommandContext");
        }
    }

    @Test
    public void testMultipleSpaces() {
        String cmd = "r hi   self";
        try {
            new CommandContext(cmd);
        } catch (CommandException e) {
            CommandContextTest.log.log(Level.WARNING, "Error", e);
            Assert.fail("Error creating CommandContext");
        }
    }

    @Test
    public void testFlagsAnywhere() {
        try {
            CommandContext context = new CommandContext("r hello -f");
            Assert.assertTrue(context.hasFlag('f'));
            CommandContext context2 = new CommandContext("r hello -f world");
            Assert.assertTrue(context2.hasFlag('f'));
        } catch (CommandException e) {
            CommandContextTest.log.log(Level.WARNING, "Error", e);
            Assert.fail("Error creating CommandContext");
        }
    }

    @Test
    public void testExactJoinedStrings() {
        try {
            CommandContext context = new CommandContext("r -f \"hello world\"   foo   bar");
            Assert.assertTrue(context.hasFlag('f'));
            Assert.assertEquals("\"hello world\"   foo   bar", context.getJoinedStrings(0));
            Assert.assertEquals("foo   bar", context.getJoinedStrings(1));
            CommandContext context2 = new CommandContext("pm name \"hello world\"   foo   bar");
            Assert.assertEquals("\"hello world\"   foo   bar", context2.getJoinedStrings(1));
        } catch (CommandException e) {
            CommandContextTest.log.log(Level.WARNING, "Error", e);
            Assert.fail("Error creating CommandContext");
        }
    }

    @Test
    public void testSlice() {
        try {
            CommandContext context = new CommandContext("foo bar baz");
            Assert.assertArrayEquals(new String[]{ "foo", "bar", "baz" }, context.getSlice(0));
        } catch (CommandException e) {
            CommandContextTest.log.log(Level.WARNING, "Error", e);
            Assert.fail("Error creating CommandContext");
        }
    }

    @Test
    public void testEmptyQuote() {
        try {
            CommandContext context = new CommandContext("region flag xmas blocked-cmds \"\"");
            Assert.assertEquals(context.argsLength(), 3);
        } catch (CommandException e) {
            CommandContextTest.log.log(Level.WARNING, "Error", e);
            Assert.fail("Error creating CommandContext");
        }
    }
}

