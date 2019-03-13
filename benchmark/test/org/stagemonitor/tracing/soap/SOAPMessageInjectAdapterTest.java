package org.stagemonitor.tracing.soap;


import java.util.Collections;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SOAPMessageInjectAdapterTest {
    private SOAPMessageInjectAdapter soapMessageInjectAdapter;

    private SOAPMessageContext soapMessageContext;

    @Test
    public void iterator() throws Exception {
        assertThatThrownBy(() -> soapMessageInjectAdapter.iterator()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void put() throws Exception {
        soapMessageInjectAdapter.put("foo", "bar");
        Mockito.verify(soapMessageContext).put(ArgumentMatchers.eq(SOAPMessageContext.HTTP_REQUEST_HEADERS), ArgumentMatchers.eq(Collections.singletonMap("foo", Collections.singletonList("bar"))));
    }
}

