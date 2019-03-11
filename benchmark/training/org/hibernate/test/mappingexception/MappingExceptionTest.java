/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id: SQLExceptionConversionTest.java 6847 2005-05-21 15:46:41Z oneovthafew $
 */
package org.hibernate.test.mappingexception;


import SourceType.FILE;
import SourceType.INPUT_STREAM;
import SourceType.RESOURCE;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.hibernate.Hibernate;
import org.hibernate.InvalidMappingException;
import org.hibernate.MappingException;
import org.hibernate.MappingNotFoundException;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.util.ConfigHelper;
import org.hibernate.org.hibernate.boot.InvalidMappingException;
import org.hibernate.org.hibernate.boot.MappingException;
import org.hibernate.org.hibernate.boot.MappingNotFoundException;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for various mapping exceptions thrown when mappings are not found or invalid.
 *
 * @author Max Rydahl Andersen
 */
public class MappingExceptionTest extends BaseUnitTestCase {
    @Test
    public void testNotFound() throws MalformedURLException, MappingException {
        Configuration cfg = new Configuration();
        try {
            cfg.addCacheableFile("completelybogus.hbm.xml");
            Assert.fail();
        } catch (MappingNotFoundException e) {
            Assert.assertEquals(e.getType(), "file");
            Assert.assertEquals(e.getPath(), "completelybogus.hbm.xml");
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), FILE);
            Assert.assertEquals(e.getOrigin().getName(), "completelybogus.hbm.xml");
        }
        try {
            cfg.addCacheableFile(new File("completelybogus.hbm.xml"));
            Assert.fail();
        } catch (MappingNotFoundException e) {
            Assert.assertEquals(e.getType(), "file");
            Assert.assertEquals(e.getPath(), "completelybogus.hbm.xml");
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), FILE);
            Assert.assertEquals(e.getOrigin().getName(), "completelybogus.hbm.xml");
        }
        try {
            cfg.addClass(Hibernate.class);// TODO: String.class result in npe, because no classloader exists for it

            Assert.fail();
        } catch (MappingNotFoundException inv) {
            Assert.assertEquals(inv.getType(), "resource");
            Assert.assertEquals(inv.getPath(), "org/hibernate/Hibernate.hbm.xml");
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), RESOURCE);
            Assert.assertEquals(e.getOrigin().getName(), "org/hibernate/Hibernate.hbm.xml");
        }
        try {
            cfg.addFile("completelybogus.hbm.xml");
            Assert.fail();
        } catch (MappingNotFoundException e) {
            Assert.assertEquals(e.getType(), "file");
            Assert.assertEquals(e.getPath(), "completelybogus.hbm.xml");
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), FILE);
            Assert.assertEquals(e.getOrigin().getName(), "completelybogus.hbm.xml");
        }
        try {
            cfg.addFile(new File("completelybogus.hbm.xml"));
            Assert.fail();
        } catch (MappingNotFoundException inv) {
            Assert.assertEquals(inv.getType(), "file");
            Assert.assertEquals(inv.getPath(), "completelybogus.hbm.xml");
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), FILE);
            Assert.assertEquals(e.getOrigin().getName(), "completelybogus.hbm.xml");
        }
        try {
            cfg.addInputStream(new ByteArrayInputStream(new byte[0]));
            Assert.fail();
        } catch (org.hibernate.boot.InvalidMappingException e) {
            Assert.assertEquals(INPUT_STREAM, e.getOrigin().getType());
            Assert.assertEquals(null, e.getOrigin().getName());
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "input stream");
            Assert.assertEquals(inv.getPath(), null);
        }
        try {
            cfg.addResource("nothere");
            Assert.fail();
        } catch (MappingNotFoundException inv) {
            Assert.assertEquals(inv.getType(), "resource");
            Assert.assertEquals(inv.getPath(), "nothere");
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), RESOURCE);
            Assert.assertEquals(e.getOrigin().getName(), "nothere");
        }
        try {
            cfg.addResource("nothere", getClass().getClassLoader());
            Assert.fail();
        } catch (MappingNotFoundException inv) {
            Assert.assertEquals(inv.getType(), "resource");
            Assert.assertEquals(inv.getPath(), "nothere");
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), RESOURCE);
            Assert.assertEquals(e.getOrigin().getName(), "nothere");
        }
        try {
            cfg.addURL(new URL("file://nothere"));
            Assert.fail();
        } catch (org.hibernate.boot.MappingNotFoundException e) {
            Assert.assertEquals(e.getOrigin().getType(), SourceType.URL);
            Assert.assertEquals(e.getOrigin().getName(), "file://nothere");
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "URL");
            Assert.assertEquals(inv.getPath(), "file://nothere");
        } catch (org.hibernate.boot.MappingException me) {
            Assert.assertEquals(me.getOrigin().getType(), SourceType.URL);
            Assert.assertEquals(me.getOrigin().getName(), "file://nothere");
        }
    }

    @Test
    public void testInvalidMapping() throws IOException, MappingException {
        String resourceName = "org/hibernate/test/mappingexception/InvalidMapping.hbm.xml";
        File file = File.createTempFile("TempInvalidMapping", ".hbm.xml");
        file.deleteOnExit();
        copy(ConfigHelper.getConfigStream(resourceName), file);
        Configuration cfg = new Configuration();
        try {
            cfg.addCacheableFile(file.getAbsolutePath());
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "file");
            Assert.assertNotNull(inv.getPath());
            Assert.assertTrue(inv.getPath().endsWith(".hbm.xml"));
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addCacheableFile(file);
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "file");
            Assert.assertNotNull(inv.getPath());
            Assert.assertTrue(inv.getPath().endsWith(".hbm.xml"));
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addClass(InvalidMapping.class);
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "resource");
            Assert.assertEquals(inv.getPath(), "org/hibernate/test/mappingexception/InvalidMapping.hbm.xml");
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addFile(file.getAbsolutePath());
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "file");
            Assert.assertEquals(inv.getPath(), file.getPath());
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addFile(file);
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "file");
            Assert.assertEquals(inv.getPath(), file.getPath());
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addInputStream(ConfigHelper.getResourceAsStream(resourceName));
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "input stream");
            Assert.assertEquals(inv.getPath(), null);
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addResource(resourceName);
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "resource");
            Assert.assertEquals(inv.getPath(), resourceName);
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addResource(resourceName, getClass().getClassLoader());
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "resource");
            Assert.assertEquals(inv.getPath(), resourceName);
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
        try {
            cfg.addURL(ConfigHelper.findAsResource(resourceName));
            Assert.fail();
        } catch (InvalidMappingException inv) {
            Assert.assertEquals(inv.getType(), "URL");
            Assert.assertTrue(inv.getPath().endsWith("InvalidMapping.hbm.xml"));
            Assert.assertTrue((!((inv.getCause()) instanceof MappingNotFoundException)));
        }
    }
}

