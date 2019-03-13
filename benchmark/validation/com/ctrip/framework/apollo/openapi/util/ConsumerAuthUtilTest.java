package com.ctrip.framework.apollo.openapi.util;


import ConsumerAuthUtil.CONSUMER_ID;
import com.ctrip.framework.apollo.openapi.service.ConsumerService;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsumerAuthUtilTest {
    private ConsumerAuthUtil consumerAuthUtil;

    @Mock
    private ConsumerService consumerService;

    @Mock
    private HttpServletRequest request;

    @Test
    public void testGetConsumerId() throws Exception {
        String someToken = "someToken";
        Long someConsumerId = 1L;
        Mockito.when(consumerService.getConsumerIdByToken(someToken)).thenReturn(someConsumerId);
        Assert.assertEquals(someConsumerId, consumerAuthUtil.getConsumerId(someToken));
        Mockito.verify(consumerService, Mockito.times(1)).getConsumerIdByToken(someToken);
    }

    @Test
    public void testStoreConsumerId() throws Exception {
        long someConsumerId = 1L;
        consumerAuthUtil.storeConsumerId(request, someConsumerId);
        Mockito.verify(request, Mockito.times(1)).setAttribute(CONSUMER_ID, someConsumerId);
    }

    @Test
    public void testRetrieveConsumerId() throws Exception {
        long someConsumerId = 1;
        Mockito.when(request.getAttribute(CONSUMER_ID)).thenReturn(someConsumerId);
        Assert.assertEquals(someConsumerId, consumerAuthUtil.retrieveConsumerId(request));
        Mockito.verify(request, Mockito.times(1)).getAttribute(CONSUMER_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testRetrieveConsumerIdWithConsumerIdNotSet() throws Exception {
        consumerAuthUtil.retrieveConsumerId(request);
    }

    @Test(expected = IllegalStateException.class)
    public void testRetrieveConsumerIdWithConsumerIdInvalid() throws Exception {
        String someInvalidConsumerId = "abc";
        Mockito.when(request.getAttribute(CONSUMER_ID)).thenReturn(someInvalidConsumerId);
        consumerAuthUtil.retrieveConsumerId(request);
    }
}

