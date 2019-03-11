package org.jak_linux.dns66.db;


import Configuration.Item;
import android.content.Context;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import org.jak_linux.dns66.Configuration;
import org.jak_linux.dns66.FileHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class RuleDatabaseTest {
    @Test
    public void testGetInstance() throws Exception {
        RuleDatabase instance = RuleDatabase.getInstance();
        Assert.assertNotNull(instance);
        Assert.assertTrue(instance.isEmpty());
        Assert.assertFalse(instance.isBlocked("example.com"));
    }

    @Test
    public void testParseLine() throws Exception {
        // Standard format lines
        Assert.assertEquals("example.com", RuleDatabase.parseLine("0.0.0.0 example.com"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("127.0.0.1 example.com"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("::1 example.com"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("example.com"));
        // Comments
        Assert.assertEquals("example.com", RuleDatabase.parseLine("example.com # foo"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("0.0.0.0 example.com # foo"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("::1 example.com # foo"));
        // Check lower casing
        Assert.assertEquals("example.com", RuleDatabase.parseLine("example.cOm"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("127.0.0.1 example.cOm"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("::1 example.cOm"));
        // Space trimming
        Assert.assertNull(RuleDatabase.parseLine(" 127.0.0.1 example.com"));
        Assert.assertEquals("127.0.0.1.example.com", RuleDatabase.parseLine("127.0.0.1.example.com "));
        Assert.assertEquals("::1.example.com", RuleDatabase.parseLine("::1.example.com "));
        Assert.assertEquals("0.0.0.0.example.com", RuleDatabase.parseLine("0.0.0.0.example.com "));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("127.0.0.1 example.com "));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("127.0.0.1 example.com\t"));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("127.0.0.1   example.com "));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("127.0.0.1\t example.com "));
        Assert.assertEquals("example.com", RuleDatabase.parseLine("::1\t example.com "));
        // Space between values
        // Invalid lines
        Assert.assertNull(RuleDatabase.parseLine("127.0.0.1 "));
        Assert.assertNull(RuleDatabase.parseLine("127.0.0.1"));
        Assert.assertNull(RuleDatabase.parseLine("0.0.0.0"));
        Assert.assertNull(RuleDatabase.parseLine("0.0.0.0 "));
        Assert.assertNull(RuleDatabase.parseLine("::1 "));
        Assert.assertNull(RuleDatabase.parseLine("::1"));
        Assert.assertNull(RuleDatabase.parseLine("invalid example.com"));
        Assert.assertNull(RuleDatabase.parseLine("invalid\texample.com"));
        Assert.assertNull(RuleDatabase.parseLine("invalid long line"));
        Assert.assertNull(RuleDatabase.parseLine("# comment line"));
        Assert.assertNull(RuleDatabase.parseLine(""));
        Assert.assertNull(RuleDatabase.parseLine("\t"));
        Assert.assertNull(RuleDatabase.parseLine(" "));
    }

    @Test
    public void testLoadReader() throws Exception {
        RuleDatabase db = new RuleDatabase();
        db.nextBlockedHosts = db.blockedHosts.get();
        Configuration.Item item = new Configuration.Item();
        item.location = "<some random file>";
        item.state = Item.STATE_IGNORE;
        // Ignore. Does nothing
        Assert.assertTrue(db.loadReader(item, new StringReader("example.com")));
        Assert.assertTrue(db.isEmpty());
        Assert.assertFalse(db.isBlocked("example.com"));
        // Deny, the host should be blocked now.
        item.state = Item.STATE_DENY;
        Assert.assertTrue(db.loadReader(item, new StringReader("example.com")));
        Assert.assertFalse(db.isEmpty());
        Assert.assertTrue(db.isBlocked("example.com"));
        // Reallow again, the entry should disappear.
        item.state = Item.STATE_ALLOW;
        Assert.assertTrue(db.loadReader(item, new StringReader("example.com")));
        Assert.assertTrue(db.isEmpty());
        Assert.assertFalse(db.isBlocked("example.com"));
        // Check multiple lines
        item.state = Item.STATE_DENY;
        Assert.assertFalse(db.isBlocked("example.com"));
        Assert.assertFalse(db.isBlocked("foo.com"));
        Assert.assertTrue(db.loadReader(item, new StringReader("example.com\n127.0.0.1 foo.com")));
        Assert.assertFalse(db.isEmpty());
        Assert.assertTrue(db.isBlocked("example.com"));
        Assert.assertTrue(db.isBlocked("foo.com"));
        // Interrupted test
        Thread.currentThread().interrupt();
        try {
            db.loadReader(item, new StringReader("example.com"));
            Assert.fail("Interrupted thread did not cause reader to be interrupted");
        } catch (InterruptedException e) {
        }
        // Test with an invalid line before a valid one.
        item.state = Item.STATE_DENY;
        Assert.assertTrue(db.loadReader(item, new StringReader("invalid line\notherhost.com")));
        Assert.assertTrue(db.isBlocked("otherhost.com"));
        // Allow again
        item.state = Item.STATE_ALLOW;
        Assert.assertTrue(db.loadReader(item, new StringReader("invalid line\notherhost.com")));
        Assert.assertFalse(db.isBlocked("otherhost.com"));
        // Reader can't read, we are aborting.
        Reader reader = Mockito.mock(Reader.class);
        doThrow(new IOException()).when(reader).read(((char[]) (ArgumentMatchers.any())));
        doThrow(new IOException()).when(reader).read(((char[]) (ArgumentMatchers.any())), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        doThrow(new IOException()).when(reader).read(ArgumentMatchers.any(CharBuffer.class));
        Assert.assertFalse(db.loadReader(item, reader));
    }

    @Test
    @PrepareForTest({ Log.class, FileHelper.class })
    public void testInitialize_host() throws Exception {
        RuleDatabase ruleDatabase = spy(new RuleDatabase());
        Configuration.Item item = new Configuration.Item();
        item.location = "ahost.com";
        item.state = Item.STATE_DENY;
        Configuration configuration = new Configuration();
        configuration.hosts = new Configuration.Hosts();
        configuration.hosts.enabled = true;
        configuration.hosts.items = new ArrayList();
        configuration.hosts.items.add(item);
        Context context = mock(Context.class);
        mockStatic(FileHelper.class);
        when(FileHelper.loadCurrentSettings(context)).thenReturn(configuration);
        when(FileHelper.openItemFile(context, item)).thenReturn(null);
        ruleDatabase.initialize(context);
        Assert.assertTrue(ruleDatabase.isBlocked("ahost.com"));
        configuration.hosts.enabled = false;
        ruleDatabase.initialize(context);
        Assert.assertFalse(ruleDatabase.isBlocked("ahost.com"));
        Assert.assertTrue(ruleDatabase.isEmpty());
    }

    @Test
    @PrepareForTest({ Log.class, FileHelper.class })
    public void testInitialize_file() throws Exception {
        RuleDatabase ruleDatabase = spy(new RuleDatabase());
        Configuration.Item item = new Configuration.Item();
        item.location = "protocol://some-weird-file-uri";
        item.state = Item.STATE_DENY;
        Configuration configuration = new Configuration();
        configuration.hosts = new Configuration.Hosts();
        configuration.hosts.enabled = true;
        configuration.hosts.items = new ArrayList();
        configuration.hosts.items.add(item);
        Context context = mock(Context.class);
        mockStatic(FileHelper.class);
        when(FileHelper.loadCurrentSettings(context)).thenReturn(configuration);
        when(FileHelper.openItemFile(context, item)).thenReturn(new InputStreamReader(new ByteArrayInputStream("example.com".getBytes("utf-8"))));
        ruleDatabase.initialize(context);
        Assert.assertTrue(ruleDatabase.isBlocked("example.com"));
        item.state = Item.STATE_IGNORE;
        ruleDatabase.initialize(context);
        Assert.assertTrue(ruleDatabase.isEmpty());
    }

    @Test
    @PrepareForTest({ Log.class, FileHelper.class })
    public void testInitialize_fileNotFound() throws Exception {
        RuleDatabase ruleDatabase = spy(new RuleDatabase());
        Configuration.Item item = new Configuration.Item();
        item.location = "protocol://some-weird-file-uri";
        item.state = Item.STATE_DENY;
        Configuration configuration = new Configuration();
        configuration.hosts = new Configuration.Hosts();
        configuration.hosts.enabled = true;
        configuration.hosts.items = new ArrayList();
        configuration.hosts.items.add(item);
        Context context = mock(Context.class);
        mockStatic(FileHelper.class);
        when(FileHelper.loadCurrentSettings(context)).thenReturn(configuration);
        when(FileHelper.openItemFile(context, item)).thenThrow(new FileNotFoundException("foobar"));
        ruleDatabase.initialize(context);
        Assert.assertTrue(ruleDatabase.isEmpty());
    }

    public static class FooException extends RuntimeException {}
}

