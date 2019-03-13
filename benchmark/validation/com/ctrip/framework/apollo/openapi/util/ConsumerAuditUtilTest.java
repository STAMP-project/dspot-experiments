package com.ctrip.framework.apollo.openapi.util;


import com.ctrip.framework.apollo.openapi.entity.ConsumerAudit;
import com.ctrip.framework.apollo.openapi.service.ConsumerService;
import com.google.common.util.concurrent.SettableFuture;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ConsumerAuditUtilTest {
    private ConsumerAuditUtil consumerAuditUtil;

    @Mock
    private ConsumerService consumerService;

    @Mock
    private HttpServletRequest request;

    private long batchTimeout = 50;

    private TimeUnit batchTimeUnit = TimeUnit.MILLISECONDS;

    @Test
    public void audit() throws Exception {
        long someConsumerId = 1;
        String someUri = "someUri";
        String someQuery = "someQuery";
        String someMethod = "someMethod";
        Mockito.when(request.getRequestURI()).thenReturn(someUri);
        Mockito.when(request.getQueryString()).thenReturn(someQuery);
        Mockito.when(request.getMethod()).thenReturn(someMethod);
        SettableFuture<List<ConsumerAudit>> result = SettableFuture.create();
        Mockito.doAnswer(((Answer<Void>) (( invocation) -> {
            Object[] args = invocation.getArguments();
            result.set(((List<ConsumerAudit>) (args[0])));
            return null;
        }))).when(consumerService).createConsumerAudits(ArgumentMatchers.anyCollection());
        consumerAuditUtil.audit(request, someConsumerId);
        List<ConsumerAudit> audits = result.get(((batchTimeout) * 5), batchTimeUnit);
        Assert.assertEquals(1, audits.size());
        ConsumerAudit audit = audits.get(0);
        Assert.assertEquals(String.format("%s?%s", someUri, someQuery), audit.getUri());
        Assert.assertEquals(someMethod, audit.getMethod());
        Assert.assertEquals(someConsumerId, audit.getConsumerId());
    }
}

