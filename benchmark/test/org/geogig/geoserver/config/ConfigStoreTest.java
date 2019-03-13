/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.config;


import Type.RESOURCE;
import Type.UNDEFINED;
import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.ResourceStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ConfigStoreTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ResourceStore dataDir;

    private ConfigStore store;

    @Test
    public void saveNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("null RepositoryInfo");
        store.save(null);
    }

    @Test
    public void saveNoId() throws Exception {
        RepositoryInfo info = new RepositoryInfo();
        info.setLocation(URI.create("file:/home/test/repo"));
        info.setId(null);
        Assert.assertNull(info.getId());
        store.save(info);
        Assert.assertNotNull(info.getId());
    }

    @Test
    public void saveDeprecatedFormat() throws Exception {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        RepositoryInfo info = new RepositoryInfo(dummyId);
        info.setName("repo");
        info.setParentDirectory("/home/test");
        store.save(info);
        String path = ConfigStore.path(info.getId());
        Resource resource = dataDir.get(path);
        Assert.assertEquals(RESOURCE, resource.getType());
        String expected = (((((("<RepositoryInfo>"// 
         + "<id>") + dummyId) + "</id>")// 
         + "<location>") + (info.getLocation().toString())) + "</location>")// 
         + "</RepositoryInfo>";
        XMLAssert.assertXMLEqual(new StringReader(expected), new InputStreamReader(resource.in(), Charsets.UTF_8));
    }

    @Test
    public void save() throws Exception {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        RepositoryInfo info = new RepositoryInfo(dummyId);
        info.setLocation(URI.create("file:/home/test/repo"));
        store.save(info);
        String path = ConfigStore.path(info.getId());
        Resource resource = dataDir.get(path);
        Assert.assertEquals(RESOURCE, resource.getType());
        String expected = (((("<RepositoryInfo>"// 
         + "<id>") + dummyId) + "</id>")// 
         + "<location>file:/home/test/repo</location>")// 
         + "</RepositoryInfo>";
        XMLAssert.assertXMLEqual(new StringReader(expected), new InputStreamReader(resource.in(), Charsets.UTF_8));
    }

    @Test
    public void checkIdFormatOnSave() {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        RepositoryInfo info = new RepositoryInfo((dummyId + "a"));
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Id doesn't match UUID format");
        store.save(info);
    }

    @Test
    public void loadNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("null id");
        store.get(null);
    }

    @Test
    public void loadNonExistent() throws Exception {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966989";
        try {
            store.get(dummyId);
            Assert.fail("Expected FileNotFoundException");
        } catch (NoSuchElementException e) {
            Assert.assertTrue(e.getMessage().startsWith("Repository not found: "));
        }
        String path = ConfigStore.path(dummyId);
        Resource resource = dataDir.get(path);
        Assert.assertEquals(UNDEFINED, resource.getType());
    }

    @Test
    public void loadDeprecatedFormat() throws Exception {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        String expected = ((((("<RepositoryInfo>"// 
         + "<id>") + dummyId) + "</id>")// 
         + "<parentDirectory>/home/test</parentDirectory>")// 
         + "<name>repo</name>")// 
         + "</RepositoryInfo>";
        String path = ConfigStore.path(dummyId);
        Resource resource = dataDir.get(path);
        try (OutputStream out = resource.out()) {
            out.write(expected.getBytes(Charsets.UTF_8));
        }
        RepositoryInfo info = store.get(dummyId);
        Assert.assertNotNull(info);
        Assert.assertEquals(dummyId, info.getId());
        Assert.assertEquals("file", info.getLocation().getScheme());
        Assert.assertTrue(info.getLocation().toString().endsWith("/home/test/repo"));
    }

    @Test
    public void get() throws Exception {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        String expected = (((("<RepositoryInfo>"// 
         + "<id>") + dummyId) + "</id>")// 
         + "<location>file:/home/test/repo</location>")// 
         + "</RepositoryInfo>";
        String path = ConfigStore.path(dummyId);
        Resource resource = dataDir.get(path);
        try (OutputStream out = resource.out()) {
            out.write(expected.getBytes(Charsets.UTF_8));
        }
        RepositoryInfo info = store.get(dummyId);
        Assert.assertNotNull(info);
        Assert.assertEquals(dummyId, info.getId());
        Assert.assertEquals(new URI("file:/home/test/repo"), info.getLocation());
    }

    @Test
    public void loadMalformed() throws Exception {
        // this xml has a missing > character at the end
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        String expected = ((((("<RepositoryInfo>"// 
         + "<id>") + dummyId) + "</id>")// 
         + "<parentDirectory>/home/test</parentDirectory>")// 
         + "<name>repo</name>")// 
         + "</RepositoryInfo";
        String path = ConfigStore.path(dummyId);
        Resource resource = dataDir.get(path);
        try (OutputStream out = resource.out()) {
            out.write(expected.getBytes(Charsets.UTF_8));
        }
        thrown.expect(NoSuchElementException.class);
        thrown.expectMessage(("Unable to load repo config " + dummyId));
        store.get(dummyId);
    }

    @Test
    public void getRepositoriesEmpty() {
        List<RepositoryInfo> all = store.getRepositories();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.isEmpty());
    }

    @Test
    public void getRepositories() {
        store.save(dummy(1));
        store.save(dummy(2));
        store.save(dummy(3));
        store.save(dummy(4));
        List<RepositoryInfo> all = store.getRepositories();
        Assert.assertNotNull(all);
        Assert.assertEquals(4, all.size());
        Set<RepositoryInfo> expected = Sets.newHashSet(dummy(1), dummy(2), dummy(3), dummy(4));
        Assert.assertEquals(expected, new HashSet<RepositoryInfo>(all));
    }

    @Test
    public void getRepositoriesIgnoresMalformed() throws Exception {
        store.save(dummy(1));
        store.save(dummy(2));
        store.save(dummy(3));
        RepositoryInfo dummy = dummy(4);
        store.save(dummy);
        Resource breakIt = dataDir.get(ConfigStore.path(dummy.getId()));
        byte[] bytes = IOUtils.toByteArray(breakIt.in());
        byte[] from = new byte[(bytes.length) - 10];
        System.arraycopy(bytes, 0, from, 0, from.length);
        // make sure the test doesn't run so fast that the lastModified timestamp doesn't change and
        // hence the ConfigStore ignores the change event issued by the ResourceStore when the
        // Resource output stream is closed...
        Thread.sleep(50);
        try (OutputStream out = breakIt.out()) {
            out.write(from);
        }
        List<RepositoryInfo> all = store.getRepositories();
        Assert.assertNotNull(all);
        Assert.assertEquals(3, all.size());
        Set<RepositoryInfo> expected = Sets.newHashSet(dummy(1), dummy(2), dummy(3));
        Assert.assertEquals(expected, new HashSet<RepositoryInfo>(all));
    }

    @Test
    public void delete() throws Exception {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        String expected = ((((("<RepositoryInfo>"// 
         + "<id>") + dummyId) + "</id>")// 
         + "<parentDirectory>/home/test</parentDirectory>")// 
         + "<name>repo</name>")// 
         + "</RepositoryInfo>";
        String path = ConfigStore.path(dummyId);
        Resource resource = dataDir.get(path);
        try (OutputStream out = resource.out()) {
            out.write(expected.getBytes(Charsets.UTF_8));
        }
        Assert.assertNotNull(store.get(dummyId));
        Assert.assertTrue(store.delete(dummyId));
        thrown.expect(NoSuchElementException.class);
        Assert.assertNull(store.get(dummyId));
    }

    @Test
    public void deleteNull() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("null id");
        store.delete(null);
    }

    @Test
    public void deleteMalformedId() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Id doesn't match UUID format");
        store.delete("not-a-uuid");
    }

    @Test
    public void deleteNonExistent() throws Exception {
        final String dummyId = "94bcb762-9ee9-4b43-a912-063509966988";
        Assert.assertFalse(store.delete(dummyId));
    }

    @Test
    public void testPreload() throws Exception {
        store.save(dummy(1));
        store.save(dummy(2));
        store.save(dummy(3));
        ConfigStore store2 = new ConfigStore(dataDir);
        Assert.assertEquals(3, store2.getRepositories().size());
    }

    @Test
    public void testPreloadIgnoresMalformed() throws Exception {
        store.save(dummy(7));
        store.save(dummy(8));
        store.save(dummy(9));
        RepositoryInfo dummy = dummy(5);
        store.save(dummy);
        Resource breakIt = dataDir.get(ConfigStore.path(dummy.getId()));
        byte[] bytes = IOUtils.toByteArray(breakIt.in());
        byte[] from = new byte[(bytes.length) - 5];
        System.arraycopy(bytes, 0, from, 0, from.length);
        try (OutputStream out = breakIt.out()) {
            out.write(from);
        }
        ConfigStore store2 = new ConfigStore(dataDir);
        Assert.assertEquals(3, store2.getRepositories().size());
    }

    @Test
    public void testGetByName() throws Exception {
        store.save(dummy(1));
        store.save(dummy(2));
        Assert.assertNull(store.getByName("name-3"));
        RepositoryInfo info = dummy(3);
        store.save(info);
        Assert.assertEquals(info, store.getByName("name-3"));
    }

    @Test
    public void testRepoExistsByName() throws Exception {
        store.save(dummy(1));
        store.save(dummy(2));
        Assert.assertFalse(store.repoExistsByName("name-3"));
        RepositoryInfo info = dummy(3);
        store.save(info);
        Assert.assertTrue(store.repoExistsByName("name-3"));
    }

    @Test
    public void testGetByLocation() throws Exception {
        store.save(dummy(1));
        store.save(dummy(2));
        URI uri = URI.create("file:/parent/directory/3/name-3");
        Assert.assertNull(store.getByLocation(uri));
        RepositoryInfo info = dummy(3);
        store.save(info);
        Assert.assertEquals(info, store.getByLocation(uri));
    }

    @Test
    public void testRepoExistsByLocation() throws Exception {
        store.save(dummy(1));
        store.save(dummy(2));
        URI uri = URI.create("file:/parent/directory/3/name-3");
        Assert.assertFalse(store.repoExistsByLocation(uri));
        RepositoryInfo info = dummy(3);
        store.save(info);
        Assert.assertTrue(store.repoExistsByLocation(uri));
    }
}

