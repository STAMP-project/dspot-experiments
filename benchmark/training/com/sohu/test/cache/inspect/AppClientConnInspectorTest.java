package com.sohu.test.cache.inspect;


import InspectParamEnum.INSTANCE_LIST;
import InspectParamEnum.SPLIT_KEY;
import com.sohu.cache.entity.InstanceInfo;
import com.sohu.cache.inspect.InspectParamEnum;
import com.sohu.cache.inspect.impl.AppClientConnInspector;
import com.sohu.cache.web.service.AppService;
import com.sohu.test.BaseTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ??????????
 *
 * @author leifu
 * @unknown 2016?6?16?
 * @unknown ??10:31:51
 */
public class AppClientConnInspectorTest extends BaseTest {
    @Resource
    private AppClientConnInspector appClientConnInspector;

    @Resource
    private AppService appService;

    @Test
    public void testApp() {
        long appId = 10024;
        Map<InspectParamEnum, Object> paramMap = new HashMap<InspectParamEnum, Object>();
        paramMap.put(SPLIT_KEY, appId);
        List<InstanceInfo> instanceInfoList = appService.getAppInstanceInfo(appId);
        paramMap.put(INSTANCE_LIST, instanceInfoList);
        appClientConnInspector.inspect(paramMap);
    }
}

