package hudson.util;


import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class PackedMapTest {
    static class Holder {
        PackedMap pm;
    }

    private XStream2 xs = new XStream2();

    @Test
    public void basic() {
        Map<String, String> o = new TreeMap<String, String>();
        o.put("a", "b");
        o.put("c", "d");
        PackedMap<String, String> p = PackedMap.of(o);
        Assert.assertEquals("b", p.get("a"));
        Assert.assertEquals("d", p.get("c"));
        Assert.assertEquals(p.size(), 2);
        for (Map.Entry<String, String> e : p.entrySet()) {
            System.out.println((((e.getKey()) + '=') + (e.getValue())));
        }
        PackedMapTest.Holder h = new PackedMapTest.Holder();
        h.pm = p;
        String xml = xs.toXML(h);
        Assert.assertEquals(("<hudson.util.PackedMapTest_-Holder>\n" + (((((((((("  <pm>\n" + "    <entry>\n") + "      <string>a</string>\n") + "      <string>b</string>\n") + "    </entry>\n") + "    <entry>\n") + "      <string>c</string>\n") + "      <string>d</string>\n") + "    </entry>\n") + "  </pm>\n") + "</hudson.util.PackedMapTest_-Holder>")), xml);
        xs.fromXML(xml);
    }
}

