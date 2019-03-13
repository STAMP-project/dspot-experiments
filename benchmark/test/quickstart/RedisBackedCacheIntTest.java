package quickstart;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;


public class RedisBackedCacheIntTest {
    private RedisBackedCache underTest;

    // rule {
    @Rule
    public GenericContainer redis = new GenericContainer("redis:5.0.3-alpine").withExposedPorts(6379);

    @Test
    public void testSimplePutAndGet() {
        underTest.put("test", "example");
        String retrieved = underTest.get("test");
        Assert.assertEquals("example", retrieved);
    }
}

