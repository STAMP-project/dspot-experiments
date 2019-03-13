package com.github.dockerjava.api.model;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class DeviceTest {
    public static List<String> validPaths = Arrays.asList("/home", "/home:/home", "/home:/something/else", "/with space", "/home:/with space", "relative:/absolute-path", "hostPath:/containerPath:r", "/hostPath:/containerPath:rw", "/hostPath:/containerPath:mrw");

    public static HashMap<String, String> badPaths = new LinkedHashMap<String, String>() {
        {
            put("", "bad format for path: ");
            // TODO implement ValidatePath
            // put("./", "./ is not an absolute path");
            // put("../", "../ is not an absolute path");
            // put("/:../", "../ is not an absolute path");
            // put("/:path", "path is not an absolute path");
            // put(":", "bad format for path: :");
            // put("/tmp:", " is not an absolute path");
            // put(":test", "bad format for path: :test");
            // put(":/test", "bad format for path: :/test");
            // put("tmp:", " is not an absolute path");
            // put(":test:", "bad format for path: :test:");
            // put("::", "bad format for path: ::");
            // put(":::", "bad format for path: :::");
            // put("/tmp:::", "bad format for path: /tmp:::");
            // put(":/tmp::", "bad format for path: :/tmp::");
            // put("path:ro", "ro is not an absolute path");
            // put("path:rr", "rr is not an absolute path");
            put("a:/b:ro", "bad mode specified: ro");
            put("a:/b:rr", "bad mode specified: rr");
        }
    };

    @Test
    public void testParse() throws Exception {
        MatcherAssert.assertThat(Device.parse("/dev/sda:/dev/xvdc:r"), Matchers.equalTo(new Device("r", "/dev/xvdc", "/dev/sda")));
        MatcherAssert.assertThat(Device.parse("/dev/snd:rw"), Matchers.equalTo(new Device("rw", "/dev/snd", "/dev/snd")));
        MatcherAssert.assertThat(Device.parse("/dev/snd:/something"), Matchers.equalTo(new Device("rwm", "/something", "/dev/snd")));
        MatcherAssert.assertThat(Device.parse("/dev/snd:/something:rw"), Matchers.equalTo(new Device("rw", "/something", "/dev/snd")));
    }

    @Test
    public void testParseBadPaths() {
        for (Map.Entry<String, String> entry : DeviceTest.badPaths.entrySet()) {
            final String deviceStr = entry.getKey();
            try {
                Device.parse(deviceStr);
                Assert.fail((((("Should fail because: " + (entry.getValue())) + " '") + deviceStr) + "'"));
            } catch (IllegalArgumentException ex) {
                MatcherAssert.assertThat(ex.getMessage(), Matchers.containsString("Invalid device specification:"));
            }
        }
    }

    @Test
    public void testParseValidPaths() {
        for (String path : DeviceTest.validPaths) {
            Device.parse(path);
        }
    }
}

