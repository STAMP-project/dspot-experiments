package quickstart;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("This test class is deliberately invalid, as it relies on a non-existent local Redis")
public class RedisBackedCacheIntTestStep0 {
    private RedisBackedCache underTest;

    @Test
    public void testSimplePutAndGet() {
        underTest.put("test", "example");
        String retrieved = underTest.get("test");
        Assert.assertEquals("example", retrieved);
    }
}

