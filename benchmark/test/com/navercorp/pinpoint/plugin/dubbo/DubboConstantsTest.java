package com.navercorp.pinpoint.plugin.dubbo;


import DubboConstants.DUBBO_ARGS_ANNOTATION_KEY;
import DubboConstants.DUBBO_CONSUMER_SERVICE_TYPE;
import DubboConstants.DUBBO_PROVIDER_SERVICE_NO_STATISTICS_TYPE;
import DubboConstants.DUBBO_PROVIDER_SERVICE_TYPE;
import DubboConstants.DUBBO_RESULT_ANNOTATION_KEY;
import DubboConstants.DUBBO_RPC_ANNOTATION_KEY;
import DubboConstants.META_DO_NOT_TRACE;
import DubboConstants.META_FLAGS;
import DubboConstants.META_PARENT_APPLICATION_NAME;
import DubboConstants.META_PARENT_APPLICATION_TYPE;
import DubboConstants.META_PARENT_SPAN_ID;
import DubboConstants.META_SPAN_ID;
import DubboConstants.META_TRANSACTION_ID;
import DubboConstants.MONITOR_SERVICE_FQCN;
import org.junit.Assert;
import org.junit.Test;


public class DubboConstantsTest {
    @Test
    public void test() {
        Assert.assertEquals(DUBBO_PROVIDER_SERVICE_TYPE.getCode(), 1110);
        Assert.assertEquals(DUBBO_CONSUMER_SERVICE_TYPE.getCode(), 9110);
        Assert.assertEquals(DUBBO_PROVIDER_SERVICE_NO_STATISTICS_TYPE.getCode(), 9111);
        Assert.assertEquals(DUBBO_ARGS_ANNOTATION_KEY.getCode(), 90);
        Assert.assertEquals(DUBBO_RESULT_ANNOTATION_KEY.getCode(), 91);
        Assert.assertEquals(DUBBO_RPC_ANNOTATION_KEY.getCode(), 92);
        Assert.assertEquals(META_DO_NOT_TRACE, "_DUBBO_DO_NOT_TRACE");
        Assert.assertEquals(META_TRANSACTION_ID, "_DUBBO_TRASACTION_ID");
        Assert.assertEquals(META_SPAN_ID, "_DUBBO_SPAN_ID");
        Assert.assertEquals(META_PARENT_SPAN_ID, "_DUBBO_PARENT_SPAN_ID");
        Assert.assertEquals(META_PARENT_APPLICATION_NAME, "_DUBBO_PARENT_APPLICATION_NAME");
        Assert.assertEquals(META_PARENT_APPLICATION_TYPE, "_DUBBO_PARENT_APPLICATION_TYPE");
        Assert.assertEquals(META_FLAGS, "_DUBBO_FLAGS");
        Assert.assertEquals(MONITOR_SERVICE_FQCN, "com.alibaba.dubbo.monitor.MonitorService");
    }
}

