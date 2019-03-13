package com.navercorp.pinpoint.plugin.dubbo;


import DubboConstants.DUBBO_ARGS_ANNOTATION_KEY;
import DubboConstants.DUBBO_CONSUMER_SERVICE_TYPE;
import DubboConstants.DUBBO_PROVIDER_SERVICE_NO_STATISTICS_TYPE;
import DubboConstants.DUBBO_PROVIDER_SERVICE_TYPE;
import DubboConstants.DUBBO_RESULT_ANNOTATION_KEY;
import DubboConstants.DUBBO_RPC_ANNOTATION_KEY;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DubboTraceMetadataProviderTest {
    @Mock
    TraceMetadataSetupContext context;

    @Test
    public void setup() {
        DubboTraceMetadataProvider provider = new DubboTraceMetadataProvider();
        provider.setup(context);
        Mockito.verify(context).addServiceType(DUBBO_PROVIDER_SERVICE_TYPE);
        Mockito.verify(context).addServiceType(DUBBO_CONSUMER_SERVICE_TYPE);
        Mockito.verify(context).addServiceType(DUBBO_PROVIDER_SERVICE_NO_STATISTICS_TYPE);
        Mockito.verify(context).addAnnotationKey(DUBBO_ARGS_ANNOTATION_KEY);
        Mockito.verify(context).addAnnotationKey(DUBBO_RESULT_ANNOTATION_KEY);
        Mockito.verify(context).addAnnotationKey(DUBBO_RPC_ANNOTATION_KEY);
    }
}

