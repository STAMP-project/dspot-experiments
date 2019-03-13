package io.fabric8.maven.docker.util;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import junitparams.JUnitParamsRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static EnvUtil.PROPERTY_COMBINE_POLICY_SUFFIX;


/**
 *
 *
 * @author roland
 * @since 14.10.14
 */
@RunWith(JUnitParamsRunner.class)
public class EnvUtilTest {
    @Test
    public void splitPath() {
        Iterator<String[]> it = EnvUtil.splitOnLastColon(Arrays.asList("db", "postgres:9:db", "postgres:db", "atlast:")).iterator();
        String[][] expected = new String[][]{ new String[]{ "db", "db" }, new String[]{ "postgres:9", "db" }, new String[]{ "postgres", "db" }, new String[]{ "atlast", "" } };
        for (int i = 0; i < (expected.length); i++) {
            String[] got = it.next();
            Assert.assertEquals(2, got.length);
            Assert.assertEquals(expected[i][0], got[0]);
            Assert.assertEquals(expected[i][1], got[1]);
        }
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void removeEmptyEntries() {
        Assert.assertEquals(ImmutableList.of("ein"), EnvUtil.removeEmptyEntries(Arrays.asList(null, "", " ein", " ")));
        Assert.assertEquals(ImmutableList.of(), EnvUtil.removeEmptyEntries(null));
    }

    @Test
    public void splitAtCommas() {
        Iterable<String> it = EnvUtil.splitAtCommasAndTrim(Arrays.asList("db,postgres:9:db", "postgres:db"));
        Iterable<String> expected = ImmutableList.of("db", "postgres:9:db", "postgres:db");
        Assert.assertTrue(Iterables.elementsEqual(it, expected));
    }

    @Test
    public void splitAtCommasEmpty() {
        assertEmptyList(EnvUtil.splitAtCommasAndTrim(Collections.<String>emptyList()));
    }

    @Test
    public void splitAtCommasSingleEmpty() {
        assertEmptyList(EnvUtil.splitAtCommasAndTrim(Arrays.asList("")));
    }

    @Test
    public void splitAtCommasNullList() {
        assertEmptyList(EnvUtil.splitAtCommasAndTrim(null));
    }

    // null occurs when <links><link></link></links>
    @Test
    public void splitAtCommasNullInList() {
        assertEmptyList(EnvUtil.splitAtCommasAndTrim(Collections.<String>singletonList(null)));
    }

    @Test
    public void extractMapFromProperties() {
        Properties props = getTestProperties("bla.hello", "world", "bla.max", "morlock", ("bla." + (PROPERTY_COMBINE_POLICY_SUFFIX)), "ignored-since-it-is-reserved", "blub.not", "aMap");
        Map<String, String> result = EnvUtil.extractFromPropertiesAsMap("bla", props);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals("world", result.get("hello"));
        Assert.assertEquals("morlock", result.get("max"));
    }

    @Test
    public void extractListFromProperties() {
        Properties props = getTestProperties("bla.2", "world", "bla.1", "hello", "bla.blub", "last", ("bla." + (PROPERTY_COMBINE_POLICY_SUFFIX)), "ignored-since-it-is-reserved", "blub.1", "unknown");
        List<String> result = EnvUtil.extractFromPropertiesAsList("bla", props);
        Assert.assertEquals(3, result.size());
        Assert.assertArrayEquals(new String[]{ "hello", "world", "last" }, new ArrayList(result).toArray());
    }

    @Test
    public void fixupPath() throws Exception {
        String[] data = new String[]{ "my/regular/path", "my/regular/path", "c:\\windows\\path", "/c/windows/path", "Z:\\yet another\\path", "/z/yet another/path" };
        for (int i = 0; i < (data.length); i += 2) {
            Assert.assertEquals(data[(i + 1)], EnvUtil.fixupPath(data[i]));
        }
    }

    @Test
    public void isValidWindowsFileName() {
        Assert.assertFalse(EnvUtil.isValidWindowsFileName("/Dockerfile"));
        Assert.assertTrue(EnvUtil.isValidWindowsFileName("Dockerfile"));
        Assert.assertFalse(EnvUtil.isValidWindowsFileName("Dockerfile/"));
    }

    @Test
    public void ensureRegistryHttpUrl() {
        String[] data = new String[]{ "https://index.docker.io/v1/", "https://index.docker.io/v1/", "index.docker.io/v1/", "https://index.docker.io/v1/", "http://index.docker.io/v1/", "http://index.docker.io/v1/", "registry.fuse-ignite.openshift.com", "https://registry.fuse-ignite.openshift.com" };
        for (int i = 0; i < (data.length); i += 2) {
            Assert.assertEquals((">> " + (data[i])), data[(i + 1)], EnvUtil.ensureRegistryHttpUrl(data[i]));
        }
    }
}

