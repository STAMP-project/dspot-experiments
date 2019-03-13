package io.fabric8.maven.docker.util;


import io.fabric8.maven.docker.access.AuthConfig;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;


/**
 *
 *
 * @author roland
 * @since 30.07.14
 */
public class AuthConfigTest {
    @Test
    public void simpleConstructor() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("username", "roland");
        map.put("password", "#>secrets??");
        map.put("email", "roland@jolokia.org");
        AuthConfig config = new AuthConfig(map);
        check(config);
    }

    @Test
    public void mapConstructor() {
        AuthConfig config = new AuthConfig("roland", "#>secrets??", "roland@jolokia.org", null);
        check(config);
    }

    @Test
    public void dockerLoginConstructor() {
        AuthConfig config = new AuthConfig(Base64.encodeBase64String("roland:#>secrets??".getBytes()), "roland@jolokia.org");
        check(config);
    }
}

