package retrofit2;


import android.net.Uri;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import retrofit2.http.GET;
import retrofit2.http.Url;


@RunWith(RobolectricTestRunner.class)
@SuppressWarnings({ "UnusedParameters", "unused" })
public final class AmplRequestFactoryAndroidTest {
    @Test(timeout = 10000)
    public void getWithAndroidUriUrlAbsolute_add2991litString3105_failAssert51() throws Exception {
        try {
            class Example {
                @GET
                Call<ResponseBody> method(@Url
                Uri url) {
                    return null;
                }
            }
            Request request = RequestFactoryTest.buildRequest(Example.class, Uri.parse("httIs://example2.com/foo/bar/"));
            int o_getWithAndroidUriUrlAbsolute_add2991__9 = request.headers().size();
            org.junit.Assert.fail("getWithAndroidUriUrlAbsolute_add2991litString3105 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Malformed URL. Base: http://example.com/, Relative: httIs://example2.com/foo/bar/", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void getWithAndroidUriUrlAbsolute_add2990_rv3295litString4141_failAssert66() throws Exception {
        try {
            class Example {
                @GET
                Call<ResponseBody> method(@Url
                Uri url) {
                    return null;
                }
            }
            Request request = RequestFactoryTest.buildRequest(Example.class, Uri.parse("uttps://example2.com/foo/bar/"));
            String o_getWithAndroidUriUrlAbsolute_add2990__9 = request.method();
            org.junit.Assert.fail("getWithAndroidUriUrlAbsolute_add2990_rv3295litString4141 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("Malformed URL. Base: http://example.com/, Relative: uttps://example2.com/foo/bar/", expected.getMessage());
        }
    }
}

