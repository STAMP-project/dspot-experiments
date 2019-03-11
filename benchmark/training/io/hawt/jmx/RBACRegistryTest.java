package io.hawt.jmx;


import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RBACRegistryTest {
    private RBACRegistry rbacRegistry = new RBACRegistry();

    @Test
    public void list() throws Exception {
        Map<String, Object> result = rbacRegistry.list();
        Assert.assertThat(result.get("cache"), CoreMatchers.notNullValue());
        Map<String, Map<String, Object>> domains = ((Map<String, Map<String, Object>>) (result.get("domains")));
        Assert.assertThat(domains, CoreMatchers.notNullValue());
        Map<String, Object> hawtioDomain = domains.get("hawtio");
        Assert.assertThat(hawtioDomain, CoreMatchers.notNullValue());
        Assert.assertThat(hawtioDomain.get("type=security,name=RBACRegistry"), CoreMatchers.notNullValue());
    }
}

