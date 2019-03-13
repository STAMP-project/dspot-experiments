/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.jdr;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.jboss.as.jdr.commands.JdrEnvironment;
import org.jboss.as.jdr.util.JdrZipFile;
import org.jboss.as.jdr.util.PatternSanitizer;
import org.jboss.as.jdr.util.XMLSanitizer;
import org.jboss.as.jdr.vfs.Filters;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileFilter;
import org.junit.Assert;
import org.junit.Test;


public class JdrTestCase {
    @Test
    public void testJdrZipName() throws Exception {
        JdrEnvironment env = new JdrEnvironment();
        env.setJbossHome("/foo/bar/baz");
        env.setHostControllerName("host");
        env.setOutputDirectory("target");
        String name;
        JdrZipFile zf = new JdrZipFile(env);
        try {
            name = zf.name();
            zf.close();
        } finally {
            safeClose(zf);
            File f = new File(zf.name());
            f.delete();
        }
        Assert.assertTrue(name.endsWith(".zip"));
        Assert.assertTrue(name.contains("host"));
        Assert.assertTrue(name.startsWith("target"));
    }

    @Test
    public void testBlackListFilter() {
        VirtualFileFilter blf = Filters.regexBlackList();
        Assert.assertFalse(blf.accepts(VFS.getChild("/foo/bar/baz/mgmt-users.properties")));
        Assert.assertFalse(blf.accepts(VFS.getChild("/foo/bar/baz/application-users.properties")));
    }

    @Test
    public void testXMLSanitizer() throws Exception {
        String xml = "<test><password>foobar</password></test>";
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        XMLSanitizer s = new XMLSanitizer("//password", Filters.TRUE);
        InputStream res = s.sanitize(is);
        byte[] buf = new byte[res.available()];
        res.read(buf);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><test><password/></test>", new String(buf, StandardCharsets.UTF_8));
    }

    @Test
    public void testPatternSanitizer() throws Exception {
        String propf = "password=123456";
        InputStream is = new ByteArrayInputStream(propf.getBytes(StandardCharsets.UTF_8));
        PatternSanitizer s = new PatternSanitizer("password=.*", "password=*", Filters.TRUE);
        InputStream res = s.sanitize(is);
        byte[] buf = new byte[res.available()];
        res.read(buf);
        Assert.assertEquals("password=*", new String(buf, StandardCharsets.UTF_8));
    }

    @Test
    public void testWildcardFilterAcceptAnything() throws Exception {
        VirtualFileFilter filter = Filters.wildcard("*");
        VirtualFile good = VFS.getChild("/this/is/a/test.txt");
        Assert.assertTrue(filter.accepts(good));
    }

    @Test
    public void testWildcardFilterPrefixGlob() throws Exception {
        VirtualFileFilter filter = Filters.wildcard("*.txt");
        VirtualFile good = VFS.getChild("/this/is/a/test.txt");
        VirtualFile bad = VFS.getChild("/this/is/a/test.xml");
        VirtualFile wingood = VFS.getChild("/C:/this/is/a/test.txt");
        VirtualFile winbad = VFS.getChild("/C:/this/is/a/test.xml");
        Assert.assertTrue(filter.accepts(good));
        Assert.assertFalse(filter.accepts(bad));
        Assert.assertTrue(filter.accepts(wingood));
        Assert.assertFalse(filter.accepts(winbad));
    }

    @Test
    public void testWildcardFilterSuffixGlob() throws Exception {
        VirtualFileFilter filter = Filters.wildcard("/this/is*");
        VirtualFile good = VFS.getChild("/this/is/a/test.txt");
        VirtualFile bad = VFS.getChild("/that/is/a/test.txt");
        VirtualFile wingood = VFS.getChild("/C:/this/is/a/test.txt");
        VirtualFile winbad = VFS.getChild("/C:/that/is/a/test.txt");
        Assert.assertTrue(filter.accepts(good));
        Assert.assertFalse(filter.accepts(bad));
        Assert.assertTrue(filter.accepts(wingood));
        Assert.assertFalse(filter.accepts(winbad));
    }

    @Test
    public void testWildcardFilterMiddleGlob() throws Exception {
        VirtualFileFilter filter = Filters.wildcard("/this*test.txt");
        VirtualFile good = VFS.getChild("/this/is/a/test.txt");
        VirtualFile bad1 = VFS.getChild("/that/is/a/test.txt");
        VirtualFile bad2 = VFS.getChild("/this/is/a/test.xml");
        VirtualFile win = VFS.getChild("/C:/this/is/a/test.txt");
        VirtualFile winbad = VFS.getChild("/C:/this/is/a/test.xml");
        Assert.assertTrue(filter.accepts(good));
        Assert.assertTrue(filter.accepts(win));
        Assert.assertFalse(filter.accepts(bad1));
        Assert.assertFalse(filter.accepts(bad2));
        Assert.assertFalse(filter.accepts(winbad));
    }
}

