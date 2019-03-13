/**
 * Created on 2018/3/27.
 */
package com.alicp.jetcache.anno.support;


import com.alicp.jetcache.CacheConfigException;
import com.alicp.jetcache.support.JavaValueDecoder;
import com.alicp.jetcache.support.JavaValueEncoder;
import com.alicp.jetcache.support.KryoValueDecoder;
import com.alicp.jetcache.support.KryoValueEncoder;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 *
 *
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class ConfigProviderTest {
    @Test
    public void parseQueryParameters() {
        Assertions.assertEquals(0, ConfigProvider.parseQueryParameters(null).size());
        Assertions.assertEquals("b", ConfigProvider.parseQueryParameters("a=b").get("a"));
        Map<String, String> m = ConfigProvider.parseQueryParameters("a=b&c=d");
        Assertions.assertEquals("b", m.get("a"));
        Assertions.assertEquals("d", m.get("c"));
        m = ConfigProvider.parseQueryParameters("a&b=");
        Assertions.assertFalse(m.containsKey("a"));
        Assertions.assertFalse(m.containsKey("b"));
    }

    @Test
    public void parseValueEncoder() {
        ConfigProvider cp = new ConfigProvider();
        AbstractValueEncoder encoder = ((AbstractValueEncoder) (cp.parseValueEncoder("kryo")));
        Assertions.assertEquals(KryoValueEncoder.class, encoder.getClass());
        Assertions.assertTrue(encoder.isUseIdentityNumber());
        encoder = ((AbstractValueEncoder) (cp.parseValueEncoder("java?useIdentityNumber=false")));
        Assertions.assertEquals(JavaValueEncoder.class, encoder.getClass());
        Assertions.assertFalse(encoder.isUseIdentityNumber());
        Assertions.assertThrows(CacheConfigException.class, () -> cp.parseValueEncoder(null));
        Assertions.assertThrows(CacheConfigException.class, () -> cp.parseValueEncoder("xxx"));
    }

    @Test
    public void parseValueDecoder() {
        ConfigProvider cp = new ConfigProvider();
        AbstractValueDecoder decoder = ((AbstractValueDecoder) (cp.parseValueDecoder("kryo")));
        Assertions.assertEquals(KryoValueDecoder.class, decoder.getClass());
        Assertions.assertTrue(decoder.isUseIdentityNumber());
        decoder = ((AbstractValueDecoder) (cp.parseValueDecoder("java?useIdentityNumber=false")));
        Assertions.assertEquals(JavaValueDecoder.class, decoder.getClass());
        Assertions.assertFalse(decoder.isUseIdentityNumber());
        Assertions.assertThrows(CacheConfigException.class, () -> cp.parseValueDecoder(null));
        Assertions.assertThrows(CacheConfigException.class, () -> cp.parseValueDecoder("xxx"));
    }
}

