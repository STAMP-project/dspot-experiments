package spark.resource;


import org.junit.Assert;
import org.junit.Test;


public class UriPathTest {
    @Test
    public void canonical() throws Exception {
        String[][] canonical = new String[][]{ new String[]{ "/aaa/bbb/", "/aaa/bbb/" }, new String[]{ "/aaa//bbb/", "/aaa//bbb/" }, new String[]{ "/aaa///bbb/", "/aaa///bbb/" }, new String[]{ "/aaa/./bbb/", "/aaa/bbb/" }, new String[]{ "/aaa/../bbb/", "/bbb/" }, new String[]{ "/aaa/./../bbb/", "/bbb/" }, new String[]{ "/aaa/bbb/ccc/../../ddd/", "/aaa/ddd/" }, new String[]{ "./bbb/", "bbb/" }, new String[]{ "./aaa/../bbb/", "bbb/" }, new String[]{ "./", "" }, new String[]{ ".//", ".//" }, new String[]{ ".///", ".///" }, new String[]{ "/.", "/" }, new String[]{ "//.", "//" }, new String[]{ "///.", "///" }, new String[]{ "/", "/" }, new String[]{ "aaa/bbb", "aaa/bbb" }, new String[]{ "aaa/", "aaa/" }, new String[]{ "aaa", "aaa" }, new String[]{ "/aaa/bbb", "/aaa/bbb" }, new String[]{ "/aaa//bbb", "/aaa//bbb" }, new String[]{ "/aaa/./bbb", "/aaa/bbb" }, new String[]{ "/aaa/../bbb", "/bbb" }, new String[]{ "/aaa/./../bbb", "/bbb" }, new String[]{ "./bbb", "bbb" }, new String[]{ "./aaa/../bbb", "bbb" }, new String[]{ "aaa/bbb/..", "aaa/" }, new String[]{ "aaa/bbb/../", "aaa/" }, new String[]{ "/aaa//../bbb", "/aaa/bbb" }, new String[]{ "/aaa/./../bbb", "/bbb" }, new String[]{ "./", "" }, new String[]{ ".", "" }, new String[]{ "", "" }, new String[]{ "..", null }, new String[]{ "./..", null }, new String[]{ "aaa/../..", null }, new String[]{ "/foo/bar/../../..", null }, new String[]{ "/../foo", null }, new String[]{ "/foo/.", "/foo/" }, new String[]{ "a", "a" }, new String[]{ "a/", "a/" }, new String[]{ "a/.", "a/" }, new String[]{ "a/..", "" }, new String[]{ "a/../..", null }, new String[]{ "/foo/../../bar", null }, new String[]{ "/foo/../bar//", "/bar//" } };
        for (String[] aCanonical : canonical) {
            Assert.assertEquals(("canonical " + (aCanonical[0])), aCanonical[1], UriPath.canonical(aCanonical[0]));
        }
    }
}

