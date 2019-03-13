package io.fabric8.maven.docker.config.handler.property;


import ConfigKey.BUILD_OPTIONS;
import ConfigKey.ENTRYPOINT;
import ConfigKey.ENV_RUN;
import ConfigKey.NAME;
import ConfigKey.PORTS;
import ConfigKey.SHMSIZE;
import PropertyMode.Fallback;
import PropertyMode.Only;
import PropertyMode.Override;
import PropertyMode.Skip;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ValueProviderTest {
    private ValueProvider provider;

    private Properties props;

    @Test
    public void testGetString_Only() {
        configure(Only);
        Assert.assertEquals(null, provider.getString(NAME, ((String) (null))));
        Assert.assertEquals(null, provider.getString(NAME, "ignored"));
        props.put("docker.name", "myname");
        Assert.assertEquals("myname", provider.getString(NAME, ((String) (null))));
        Assert.assertEquals("myname", provider.getString(NAME, "ignored"));
    }

    @Test
    public void testGetString_Skip() {
        configure(Skip);
        Assert.assertEquals(null, provider.getString(NAME, ((String) (null))));
        Assert.assertEquals("fromconfig", provider.getString(NAME, "fromconfig"));
        props.put("docker.name", "ignored");
        Assert.assertEquals(null, provider.getString(NAME, ((String) (null))));
        Assert.assertEquals("fromconfig", provider.getString(NAME, "fromconfig"));
    }

    @Test
    public void testGetString_Fallback() {
        configure(Fallback);
        Assert.assertEquals(null, provider.getString(NAME, ((String) (null))));
        Assert.assertEquals("fromconfig", provider.getString(NAME, "fromconfig"));
        props.put("docker.name", "fromprop");
        Assert.assertEquals("fromprop", provider.getString(NAME, ((String) (null))));
        Assert.assertEquals("fromconfig", provider.getString(NAME, "fromconfig"));
    }

    @Test
    public void testGetString_Override() {
        configure(Override);
        Assert.assertEquals(null, provider.getString(NAME, ((String) (null))));
        Assert.assertEquals("fromconfig", provider.getString(NAME, "fromconfig"));
        props.put("docker.name", "fromprop");
        Assert.assertEquals("fromprop", provider.getString(NAME, ((String) (null))));
        Assert.assertEquals("fromprop", provider.getString(NAME, "fromconfig"));
    }

    @Test
    public void testGetInt() {
        configure(Only);
        Assert.assertEquals(null, provider.getInteger(SHMSIZE, null));
        Assert.assertEquals(null, provider.getInteger(SHMSIZE, 100));
        props.put("docker.shmsize", "200");
        Assert.assertEquals(200, ((int) (provider.getInteger(SHMSIZE, null))));
        Assert.assertEquals(200, ((int) (provider.getInteger(SHMSIZE, 100))));
    }

    @Test
    public void testGetList() {
        configure(Only);
        Assert.assertEquals(null, provider.getList(PORTS, null));
        Assert.assertEquals(null, provider.getList(PORTS, Collections.singletonList("8080")));
        props.put("docker.ports.1", "200");
        Assert.assertThat(provider.getList(PORTS, null), Matchers.contains("200"));
        Assert.assertThat(provider.getList(PORTS, Collections.singletonList("8080")), Matchers.contains("200"));
        props.put("docker.ports.1", "200");
        props.put("docker.ports.2", "8080");
        Assert.assertThat(provider.getList(PORTS, null), Matchers.contains("200", "8080"));
        Assert.assertThat(provider.getList(PORTS, Collections.singletonList("123")), Matchers.contains("200", "8080"));
        configure(Fallback);
        Assert.assertThat(provider.getList(PORTS, null), Matchers.contains("200", "8080"));
        Assert.assertThat(provider.getList(PORTS, Collections.singletonList("123")), Matchers.contains("123", "200", "8080"));
        configure(Override);
        Assert.assertThat(provider.getList(PORTS, null), Matchers.contains("200", "8080"));
        Assert.assertThat(provider.getList(PORTS, Collections.singletonList("123")), Matchers.contains("200", "8080", "123"));
        // Test with another property that does not have CombinePolicy Merge
        props.put("docker.entrypoint.1", "ep1");
        props.put("docker.entrypoint.2", "ep2");
        Assert.assertThat(provider.getList(ENTRYPOINT, null), Matchers.contains("ep1", "ep2"));
        Assert.assertThat(provider.getList(ENTRYPOINT, Collections.singletonList("asd")), Matchers.contains("ep1", "ep2"));
        configure(Fallback);
        Assert.assertThat(provider.getList(ENTRYPOINT, null), Matchers.contains("ep1", "ep2"));
        Assert.assertThat(provider.getList(ENTRYPOINT, Collections.singletonList("asd")), Matchers.contains("asd"));
        // Override combine policy
        props.put("docker.entrypoint._combine", "merge");
        Assert.assertThat(provider.getList(ENTRYPOINT, Collections.singletonList("asd")), Matchers.contains("asd", "ep1", "ep2"));
    }

    @Test
    public void testGetMap() {
        configure(Only);
        Assert.assertEquals(null, provider.getMap(ENV_RUN, null));
        Assert.assertEquals(null, provider.getMap(ENV_RUN, getTestMap("key", "value")));
        props.put("docker.envRun.myprop1", "pvalue1");
        props.put("docker.envRun.myprop2", "pvalue2");
        Map m = provider.getMap(ENV_RUN, null);
        Assert.assertEquals(2, m.size());
        Assert.assertEquals("pvalue1", m.get("myprop1"));
        Assert.assertEquals("pvalue2", m.get("myprop2"));
        m = provider.getMap(ENV_RUN, getTestMap("mycfg", "cvalue"));
        Assert.assertEquals(2, m.size());
        Assert.assertEquals("pvalue1", m.get("myprop1"));
        Assert.assertEquals("pvalue2", m.get("myprop2"));
        configure(Override);
        m = provider.getMap(ENV_RUN, null);
        Assert.assertEquals(2, m.size());
        Assert.assertEquals("pvalue1", m.get("myprop1"));
        Assert.assertEquals("pvalue2", m.get("myprop2"));
        m = provider.getMap(ENV_RUN, getTestMap("ckey", "cvalue", "myprop1", "ignored"));
        Assert.assertEquals(3, m.size());
        Assert.assertEquals("pvalue1", m.get("myprop1"));
        Assert.assertEquals("pvalue2", m.get("myprop2"));
        Assert.assertEquals("cvalue", m.get("ckey"));
        configure(Fallback);
        m = provider.getMap(ENV_RUN, getTestMap("ckey", "cvalue", "myprop1", "overrides"));
        Assert.assertEquals(3, m.size());
        Assert.assertEquals("overrides", m.get("myprop1"));
        Assert.assertEquals("pvalue2", m.get("myprop2"));
        Assert.assertEquals("cvalue", m.get("ckey"));
        // Test with another property that does not have CombinePolicy Merge
        props.put("docker.buildOptions.boprop1", "popt1");
        props.put("docker.buildOptions.boprop2", "popt2");
        configure(Override);
        m = provider.getMap(BUILD_OPTIONS, null);
        Assert.assertEquals(2, m.size());
        Assert.assertEquals("popt1", m.get("boprop1"));
        Assert.assertEquals("popt2", m.get("boprop2"));
        m = provider.getMap(BUILD_OPTIONS, getTestMap("ckey", "ignored", "myprop1", "ignored"));
        Assert.assertEquals(2, m.size());
        Assert.assertEquals("popt1", m.get("boprop1"));
        Assert.assertEquals("popt2", m.get("boprop2"));
        configure(Fallback);
        m = provider.getMap(BUILD_OPTIONS, getTestMap("ckey", "notignored1", "myprop1", "notignored2"));
        Assert.assertEquals(2, m.size());
        Assert.assertEquals("notignored1", m.get("ckey"));
        Assert.assertEquals("notignored2", m.get("myprop1"));
        // Override combine policy
        props.put("docker.buildOptions._combine", "merge");
        m = provider.getMap(BUILD_OPTIONS, getTestMap("ckey", "notignored1", "boprop2", "notignored2"));
        Assert.assertEquals(3, m.size());
        Assert.assertEquals("popt1", m.get("boprop1"));
        Assert.assertEquals("notignored2", m.get("boprop2"));
        Assert.assertEquals("notignored1", m.get("ckey"));
    }
}

