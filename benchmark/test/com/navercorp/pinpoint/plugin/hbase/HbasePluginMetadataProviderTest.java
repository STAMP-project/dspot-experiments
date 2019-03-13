package com.navercorp.pinpoint.plugin.hbase;


import HbasePluginConstants.HBASE_ASYNC_CLIENT;
import HbasePluginConstants.HBASE_CLIENT;
import HbasePluginConstants.HBASE_CLIENT_ADMIN;
import HbasePluginConstants.HBASE_CLIENT_PARAMS;
import HbasePluginConstants.HBASE_CLIENT_TABLE;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HbasePluginMetadataProviderTest {
    @Mock
    private TraceMetadataSetupContext context;

    @Test
    public void setup() {
        HbasePluginMetadataProvider provider = new HbasePluginMetadataProvider();
        provider.setup(context);
        Mockito.verify(context).addServiceType(HBASE_CLIENT);
        Mockito.verify(context).addServiceType(HBASE_CLIENT_ADMIN);
        Mockito.verify(context).addServiceType(HBASE_CLIENT_TABLE);
        Mockito.verify(context).addServiceType(HBASE_ASYNC_CLIENT);
        Mockito.verify(context).addAnnotationKey(HBASE_CLIENT_PARAMS);
    }
}

