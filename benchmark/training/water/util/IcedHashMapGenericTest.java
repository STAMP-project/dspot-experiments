package water.util;


import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import water.AutoBuffer;


public class IcedHashMapGenericTest {
    @Test
    public void testJavaNativeValues() {
        Map<String, Object> map = Collections.unmodifiableMap(new HashMap<String, Object>() {
            {
                put("integer", 42);
                put("float", 0.5F);
                put("double", Math.E);
                put("boolean-true", true);
                put("boolean-false", false);
            }
        });
        final IcedHashMapGeneric<String, Object> icedMapOrig = new IcedHashMapGeneric.IcedHashMapStringObject();
        icedMapOrig.putAll(map);
        Assert.assertEquals(map, icedMapOrig);
        final IcedHashMapGeneric<String, Object> icedMapDeser = new AutoBuffer().put(icedMapOrig).flipForReading().get();
        Assert.assertEquals(map, icedMapDeser);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AutoBuffer ab = new AutoBuffer(baos, false);
        icedMapOrig.writeJSON_impl(ab);
        ab.close();
        Assert.assertEquals("\u0000\"integer\":42, \"boolean-false\":false, \"double\":2.718281828459045, \"float\":0.5, \"boolean-true\":true", baos.toString());
        System.out.println(baos.toString());
    }
}

