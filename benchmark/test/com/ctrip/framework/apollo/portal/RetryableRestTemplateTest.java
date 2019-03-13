package com.ctrip.framework.apollo.portal;


import Env.DEV;
import com.ctrip.framework.apollo.common.exception.ServiceException;
import com.ctrip.framework.apollo.portal.component.AdminServiceAddressLocator;
import com.ctrip.framework.apollo.portal.component.RetryableRestTemplate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;


public class RetryableRestTemplateTest extends AbstractUnitTest {
    @Mock
    private AdminServiceAddressLocator serviceAddressLocator;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RetryableRestTemplate retryableRestTemplate;

    private String path = "app";

    private String serviceOne = "http://10.0.0.1";

    private String serviceTwo = "http://10.0.0.2";

    private String serviceThree = "http://10.0.0.3";

    private ResourceAccessException socketTimeoutException = new ResourceAccessException("");

    private ResourceAccessException httpHostConnectException = new ResourceAccessException("");

    private ResourceAccessException connectTimeoutException = new ResourceAccessException("");

    private Object request = new Object();

    private ResponseEntity<Object> entity = new ResponseEntity(HttpStatus.OK);

    @Test(expected = ServiceException.class)
    public void testNoAdminServer() {
        Mockito.when(serviceAddressLocator.getServiceList(ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        retryableRestTemplate.get(DEV, path, Object.class);
    }

    @Test(expected = ServiceException.class)
    public void testAllServerDown() {
        Mockito.when(serviceAddressLocator.getServiceList(ArgumentMatchers.any())).thenReturn(Arrays.asList(mockService(serviceOne), mockService(serviceTwo), mockService(serviceThree)));
        Mockito.when(restTemplate.getForObject((((serviceOne) + "/") + (path)), Object.class)).thenThrow(socketTimeoutException);
        Mockito.when(restTemplate.getForObject((((serviceTwo) + "/") + (path)), Object.class)).thenThrow(httpHostConnectException);
        Mockito.when(restTemplate.getForObject((((serviceThree) + "/") + (path)), Object.class)).thenThrow(connectTimeoutException);
        retryableRestTemplate.get(DEV, path, Object.class);
        Mockito.verify(restTemplate).getForObject((((serviceOne) + "/") + (path)), Object.class);
        Mockito.verify(restTemplate).getForObject((((serviceTwo) + "/") + (path)), Object.class);
        Mockito.verify(restTemplate).getForObject((((serviceThree) + "/") + (path)), Object.class);
    }

    @Test
    public void testOneServerDown() {
        Object result = new Object();
        Mockito.when(serviceAddressLocator.getServiceList(ArgumentMatchers.any())).thenReturn(Arrays.asList(mockService(serviceOne), mockService(serviceTwo), mockService(serviceThree)));
        Mockito.when(restTemplate.getForObject((((serviceOne) + "/") + (path)), Object.class)).thenThrow(socketTimeoutException);
        Mockito.when(restTemplate.getForObject((((serviceTwo) + "/") + (path)), Object.class)).thenReturn(result);
        Mockito.when(restTemplate.getForObject((((serviceThree) + "/") + (path)), Object.class)).thenThrow(connectTimeoutException);
        Object o = retryableRestTemplate.get(DEV, path, Object.class);
        Mockito.verify(restTemplate).getForObject((((serviceOne) + "/") + (path)), Object.class);
        Mockito.verify(restTemplate).getForObject((((serviceTwo) + "/") + (path)), Object.class);
        Mockito.verify(restTemplate, Mockito.times(0)).getForObject((((serviceThree) + "/") + (path)), Object.class);
        Assert.assertEquals(result, o);
    }

    @Test(expected = ResourceAccessException.class)
    public void testPostSocketTimeoutNotRetry() {
        Mockito.when(serviceAddressLocator.getServiceList(ArgumentMatchers.any())).thenReturn(Arrays.asList(mockService(serviceOne), mockService(serviceTwo), mockService(serviceThree)));
        Mockito.when(restTemplate.postForEntity((((serviceOne) + "/") + (path)), request, Object.class)).thenThrow(socketTimeoutException);
        Mockito.when(restTemplate.postForEntity((((serviceTwo) + "/") + (path)), request, Object.class)).thenReturn(entity);
        retryableRestTemplate.post(DEV, path, request, Object.class);
        Mockito.verify(restTemplate).postForEntity((((serviceOne) + "/") + (path)), request, Object.class);
        Mockito.verify(restTemplate, Mockito.times(0)).postForEntity((((serviceTwo) + "/") + (path)), request, Object.class);
    }

    @Test
    public void testDelete() {
        Mockito.when(serviceAddressLocator.getServiceList(ArgumentMatchers.any())).thenReturn(Arrays.asList(mockService(serviceOne), mockService(serviceTwo), mockService(serviceThree)));
        retryableRestTemplate.delete(DEV, path);
        Mockito.verify(restTemplate).delete((((serviceOne) + "/") + (path)));
    }

    @Test
    public void testPut() {
        Mockito.when(serviceAddressLocator.getServiceList(ArgumentMatchers.any())).thenReturn(Arrays.asList(mockService(serviceOne), mockService(serviceTwo), mockService(serviceThree)));
        retryableRestTemplate.put(DEV, path, request);
        Mockito.verify(restTemplate).put((((serviceOne) + "/") + (path)), request);
    }
}

