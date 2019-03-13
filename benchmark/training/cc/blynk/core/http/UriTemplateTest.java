package cc.blynk.core.http;


import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 05.01.16.
 */
public class UriTemplateTest {
    @Test
    public void testCorrectMatch() {
        UriTemplate template1 = new UriTemplate("http://example.com/admin/users/{name}");
        UriTemplate template2 = new UriTemplate("http://example.com/admin/users/changePass/{name}");
        Assert.assertFalse(template1.matcher("http://example.com/admin/users/changePass/dmitriy@blynk.cc").matches());
        Assert.assertTrue(template1.matcher("http://example.com/admin/users/dmitriy@blynk.cc").matches());
        Assert.assertTrue(template2.matcher("http://example.com/admin/users/changePass/dmitriy@blynk.cc").matches());
        Assert.assertFalse(template2.matcher("http://example.com/admin/users/dmitriy@blynk.cc").matches());
    }
}

