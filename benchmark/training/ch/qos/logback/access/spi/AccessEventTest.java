package ch.qos.logback.access.spi;


import ch.qos.logback.access.dummy.DummyAccessEventBuilder;
import ch.qos.logback.access.dummy.DummyRequest;
import ch.qos.logback.core.testUtil.RandomUtil;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AccessEventTest {
    int diff = RandomUtil.getPositiveInt();

    // See LOGBACK-1189
    @Test
    public void callingPrepareForDeferredProcessingShouldBeIdempotent() {
        String key = "key-" + (diff);
        String val = "val-" + (diff);
        IAccessEvent ae = DummyAccessEventBuilder.buildNewAccessEvent();
        DummyRequest request = ((DummyRequest) (ae.getRequest()));
        Map<String, String> headersMap = request.getHeaders();
        Map<String, String[]> parametersMap = request.getParameterMap();
        headersMap.put(key, val);
        request.setAttribute(key, val);
        parametersMap.put(key, new String[]{ val });
        ae.prepareForDeferredProcessing();
        Assert.assertEquals(val, ae.getAttribute(key));
        Assert.assertEquals(val, ae.getRequestHeader(key));
        Assert.assertEquals(val, ae.getRequestParameter(key)[0]);
        request.setAttribute(key, "change");
        headersMap.put(key, "change");
        parametersMap.put(key, new String[]{ "change" });
        ae.prepareForDeferredProcessing();
        Assert.assertEquals(val, ae.getAttribute(key));
        Assert.assertEquals(val, ae.getRequestHeader(key));
        Assert.assertEquals(val, ae.getRequestParameter(key)[0]);
    }
}

