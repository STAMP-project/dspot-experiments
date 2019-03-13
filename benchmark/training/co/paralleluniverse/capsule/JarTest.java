/**
 * Capsule
 * Copyright (c) 2014-2015, Parallel Universe Software Co. and Contributors. All rights reserved.
 *
 * This program and the accompanying materials are licensed under the terms
 * of the Eclipse Public License v1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package co.paralleluniverse.capsule;


import co.paralleluniverse.common.JarClassLoader;
import com.google.common.jimfs.Jimfs;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.jar.Manifest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pron
 */
// </editor-fold>
public class JarTest {
    @Test
    public void testCreateJar() throws Exception {
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").setAttribute("Bar", "5678").addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("foo.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "bar.txt"), StandardCharsets.UTF_8));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
        Assert.assertEquals("5678", man2.getMainAttributes().getValue("Bar"));
    }

    @Test
    public void testUpdateJar() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path jarPath = fs.getPath("test.jar");
        try {
            // create
            new Jar().setAttribute("Foo", "1234").setAttribute("Bar", "5678").setListAttribute("List", Arrays.asList("a", "b")).setMapAttribute("Map", new HashMap<String, String>() {
                {
                    put("x", "1");
                    put("y", "2");
                }
            }).addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(jarPath);
            // update
            Jar jar = new Jar(jarPath);
            ByteArrayOutputStream res = jar.setAttribute("Baz", "hi!").setAttribute("Bar", "8765").setListAttribute("List", JarTest.addLast(JarTest.addFirst(jar.getListAttribute("List"), "0"), "c")).setMapAttribute("Map", JarTest.put(JarTest.put(jar.getMapAttribute("Map", null), "z", "3"), "x", "0")).addEntry(Paths.get("dir", "baz.txt"), Jar.toInputStream("And I am baz!\n", StandardCharsets.UTF_8)).write(new ByteArrayOutputStream());
            // test
            // printEntries(toInput(res));
            Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("foo.txt"), StandardCharsets.UTF_8));
            Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "bar.txt"), StandardCharsets.UTF_8));
            Assert.assertEquals("And I am baz!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "baz.txt"), StandardCharsets.UTF_8));
            Manifest man2 = JarTest.toInput(res).getManifest();
            Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
            Assert.assertEquals("8765", man2.getMainAttributes().getValue("Bar"));
            Assert.assertEquals("hi!", man2.getMainAttributes().getValue("Baz"));
            Assert.assertEquals(Arrays.asList("0", "a", "b", "c"), new Jar(JarTest.toInput(res)).getListAttribute("List"));
            Assert.assertEquals(new HashMap<String, String>() {
                {
                    put("x", "0");
                    put("y", "2");
                    put("z", "3");
                }
            }, new Jar(JarTest.toInput(res)).getMapAttribute("Map", null));
        } finally {
            Files.delete(jarPath);
        }
    }

    @Test
    public void testUpdateJar2() throws Exception {
        // create
        ByteArrayOutputStream baos = new Jar().setAttribute("Foo", "1234").setAttribute("Bar", "5678").addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(new ByteArrayOutputStream());
        // update
        ByteArrayOutputStream res = new Jar(JarTest.toInput(baos)).setAttribute("Baz", "hi!").setAttribute("Bar", "8765").addEntry(Paths.get("dir", "baz.txt"), Jar.toInputStream("And I am baz!\n", StandardCharsets.UTF_8)).write(new ByteArrayOutputStream());
        // test
        // printEntries(toInput(res));
        Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("foo.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "bar.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("And I am baz!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "baz.txt"), StandardCharsets.UTF_8));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
        Assert.assertEquals("8765", man2.getMainAttributes().getValue("Bar"));
        Assert.assertEquals("hi!", man2.getMainAttributes().getValue("Baz"));
    }

    @Test
    public void testUpdateJar3() throws Exception {
        // create
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new Jar().setOutputStream(baos).setAttribute("Foo", "1234").setAttribute("Bar", "5678").addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).close();
        // update
        ByteArrayOutputStream res = new Jar(JarTest.toInput(baos)).setAttribute("Baz", "hi!").setAttribute("Bar", "8765").addEntry(Paths.get("dir", "baz.txt"), Jar.toInputStream("And I am baz!\n", StandardCharsets.UTF_8)).write(new ByteArrayOutputStream());
        // test
        // printEntries(toInput(res));
        Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("foo.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "bar.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("And I am baz!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "baz.txt"), StandardCharsets.UTF_8));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
        Assert.assertEquals("8765", man2.getMainAttributes().getValue("Bar"));
        Assert.assertEquals("hi!", man2.getMainAttributes().getValue("Baz"));
    }

    @Test
    public void testAddDirectory1() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path myDir = fs.getPath("dir1", "dir2");
        Files.createDirectories(myDir.resolve("da"));
        Files.createDirectories(myDir.resolve("db"));
        Files.createFile(myDir.resolve("da").resolve("x"));
        Files.createFile(myDir.resolve("da").resolve("y"));
        Files.createFile(myDir.resolve("db").resolve("w"));
        Files.createFile(myDir.resolve("db").resolve("z"));
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addEntries(((Path) (null)), myDir, Jar.notMatches("db/w")).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("da", "x"))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("da", "y"))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("db", "w"))) == null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("db", "z"))) != null));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testAddDirectory2() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path myDir = fs.getPath("dir1", "dir2");
        Files.createDirectories(myDir.resolve("da"));
        Files.createDirectories(myDir.resolve("db"));
        Files.createFile(myDir.resolve("da").resolve("x"));
        Files.createFile(myDir.resolve("da").resolve("y"));
        Files.createFile(myDir.resolve("db").resolve("w"));
        Files.createFile(myDir.resolve("db").resolve("z"));
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addEntries(Paths.get("d1", "d2"), myDir, Jar.notMatches("d1/d2/db/w")).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("d1", "d2", "da", "x"))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("d1", "d2", "da", "y"))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("d1", "d2", "db", "w"))) == null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), Paths.get("d1", "d2", "db", "z"))) != null));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testAddZip1() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path myZip = fs.getPath("zip1");
        new Jar().addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(myZip);
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addEntries(((String) (null)), myZip).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("foo.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "bar.txt"), StandardCharsets.UTF_8));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testAddZip2() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path myZip = fs.getPath("zip1");
        new Jar().addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(myZip);
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addEntries(Paths.get("d1", "d2"), myZip).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("d1", "d2", "foo.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("d1", "d2", "dir", "bar.txt"), StandardCharsets.UTF_8));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testAddZip3() throws Exception {
        ByteArrayOutputStream myZip = new Jar().addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(new ByteArrayOutputStream());
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addEntries(((Path) (null)), JarTest.toInput(myZip)).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("foo.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("dir", "bar.txt"), StandardCharsets.UTF_8));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testAddZip4() throws Exception {
        ByteArrayOutputStream myZip = new Jar().addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(new ByteArrayOutputStream());
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addEntries(Paths.get("d1", "d2"), JarTest.toInput(myZip)).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        Assert.assertEquals("I am foo!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("d1", "d2", "foo.txt"), StandardCharsets.UTF_8));
        Assert.assertEquals("I am bar!\n", JarTest.getEntryAsString(JarTest.toInput(res), Paths.get("d1", "d2", "dir", "bar.txt"), StandardCharsets.UTF_8));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testAddPackage() throws Exception {
        final Class clazz = JarClassLoader.class;
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addPackageOf(clazz, new Jar.Filter() {
            @Override
            public boolean filter(String entryName) {
                return !("co/paralleluniverse/common/FlexibleClassLoader.class".equals(entryName));
            }
        }).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        final Path pp = Paths.get(clazz.getPackage().getName().replace('.', '/'));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), pp.resolve(((clazz.getSimpleName()) + ".class")))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), pp.resolve("ProcessUtil.class"))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), pp.resolve("FlexibleClassLoader.class"))) == null));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testAddPackage2() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path jarPath = fs.getPath("test.jar");
        final Class clazz = JarClassLoader.class;
        new Jar().addPackageOf(clazz).write(jarPath);
        ByteArrayOutputStream res = new Jar().setAttribute("Foo", "1234").addPackageOf(new JarClassLoader(jarPath, true).loadClass(clazz.getName()), new Jar.Filter() {
            @Override
            public boolean filter(String entryName) {
                return !("co/paralleluniverse/common/FlexibleClassLoader.class".equals(entryName));
            }
        }).write(new ByteArrayOutputStream());
        // printEntries(toInput(res));
        final Path pp = Paths.get(clazz.getPackage().getName().replace('.', '/'));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), pp.resolve(((clazz.getSimpleName()) + ".class")))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), pp.resolve("ProcessUtil.class"))) != null));
        Assert.assertTrue(((JarTest.getEntry(JarTest.toInput(res), pp.resolve("FlexibleClassLoader.class"))) == null));
        Manifest man2 = JarTest.toInput(res).getManifest();
        Assert.assertEquals("1234", man2.getMainAttributes().getValue("Foo"));
    }

    @Test
    public void testReallyExecutableJar() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path jarPath = fs.getPath("test.jar");
        new Jar().setAttribute("Foo", "1234").setAttribute("Bar", "5678").setReallyExecutable(true).addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(jarPath);
        // printEntries(toInput(res));
        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(jarPath), StandardCharsets.UTF_8), 10);// Files.newBufferedReader(jarPath, UTF_8);

        String firstLine = reader.readLine();
        Assert.assertEquals("#!/bin/sh", firstLine);
    }

    @Test
    public void testStringPrefix() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path jarPath = fs.getPath("test.jar");
        new Jar().setAttribute("Foo", "1234").setAttribute("Bar", "5678").setJarPrefix("I'm the prefix!").addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(jarPath);
        // printEntries(toInput(res));
        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(jarPath), StandardCharsets.UTF_8), 10);// Files.newBufferedReader(jarPath, UTF_8);

        String firstLine = reader.readLine();
        Assert.assertEquals("I'm the prefix!", firstLine);
    }

    @Test
    public void testFilePrefix() throws Exception {
        FileSystem fs = Jimfs.newFileSystem();
        Path jarPath = fs.getPath("test.jar");
        Path prefixPath = fs.getPath("prefix.dat");
        Files.copy(JarTest.toInputStream("I'm the prefix!", StandardCharsets.UTF_8), prefixPath);
        new Jar().setAttribute("Foo", "1234").setAttribute("Bar", "5678").setJarPrefix(prefixPath).addEntry(Paths.get("foo.txt"), Jar.toInputStream("I am foo!\n", StandardCharsets.UTF_8)).addEntry(Paths.get("dir", "bar.txt"), Jar.toInputStream("I am bar!\n", StandardCharsets.UTF_8)).write(jarPath);
        // printEntries(toInput(res));
        BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(jarPath), StandardCharsets.UTF_8), 10);// Files.newBufferedReader(jarPath, UTF_8);

        String firstLine = reader.readLine();
        Assert.assertEquals("I'm the prefix!", firstLine);
    }
}

