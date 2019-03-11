package com.baidu.disconf.client.test;


import DisconfMgrBean.SCAN_SPLIT_TOKEN;
import com.baidu.disconf.client.DisconfMgr;
import com.baidu.disconf.client.core.DisconfCoreFactory;
import com.baidu.disconf.client.core.DisconfCoreMgr;
import com.baidu.disconf.client.fetcher.FetcherFactory;
import com.baidu.disconf.client.fetcher.FetcherMgr;
import com.baidu.disconf.client.store.DisconfStoreProcessorFactory;
import com.baidu.disconf.client.store.inner.DisconfCenterHostFilesStore;
import com.baidu.disconf.client.support.registry.Registry;
import com.baidu.disconf.client.support.utils.StringUtil;
import com.baidu.disconf.client.test.common.BaseSpringMockTestCase;
import com.baidu.disconf.client.test.model.ConfA;
import com.baidu.disconf.client.test.model.ServiceA;
import com.baidu.disconf.client.test.model.StaticConf;
import com.baidu.disconf.client.test.scan.inner.ScanPackTestCase;
import com.baidu.disconf.client.watch.WatchMgr;
import java.util.HashSet;
import java.util.Set;
import mockit.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * ??Demo??, ??????????WireMOck, Watch????Jmockit
 *
 * @author liaoqiqi
 * @version 2014-6-10
 */
public class DisconfMgrTestCase extends BaseSpringMockTestCase implements ApplicationContextAware {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DisconfMgrTestCase.class);

    // application context
    private ApplicationContext applicationContext;

    @Autowired
    private ConfA confA;

    @Autowired
    private ServiceA serviceA;

    @Test
    public void demo() {
        // 
        // mock up factory method
        // 
        new mockit.MockUp<DisconfCoreFactory>() {
            @Mock
            public DisconfCoreMgr getDisconfCoreMgr(Registry registry) throws Exception {
                FetcherMgr fetcherMgr = FetcherFactory.getFetcherMgr();
                // Watch ??
                final WatchMgr watchMgr = getMockInstance();
                watchMgr.init("", "", true);
                // registry
                DisconfCoreMgr disconfCoreMgr = new com.baidu.disconf.client.core.impl.DisconfCoreMgrImpl(watchMgr, fetcherMgr, registry);
                return disconfCoreMgr;
            }
        };
        // 
        // ????
        // 
        try {
            DisconfMgrTestCase.LOGGER.info("================ BEFORE DISCONF ==============================");
            DisconfMgrTestCase.LOGGER.info("before disconf values:");
            DisconfMgrTestCase.LOGGER.info(String.valueOf(("varA: " + (confA.getVarA()))));
            DisconfMgrTestCase.LOGGER.info(String.valueOf(("varA2: " + (confA.getVarA2()))));
            DisconfMgrTestCase.LOGGER.info(String.valueOf(("varAA: " + (serviceA.getVarAA()))));
            DisconfMgrTestCase.LOGGER.info("================ BEFORE DISCONF ==============================");
            // 
            // start it
            // 
            Set<String> fileSet = new HashSet<String>();
            fileSet.add("atomserverl.properties");
            fileSet.add("atomserverm_slave.properties");
            DisconfCenterHostFilesStore.getInstance().addJustHostFileSet(fileSet);
            DisconfMgr.getInstance().setApplicationContext(applicationContext);
            DisconfMgr.getInstance().start(StringUtil.parseStringToStringList(ScanPackTestCase.SCAN_PACK_NAME, SCAN_SPLIT_TOKEN));
            // 
            DisconfMgrTestCase.LOGGER.info(DisconfStoreProcessorFactory.getDisconfStoreFileProcessor().confToString());
            // 
            DisconfMgrTestCase.LOGGER.info(DisconfStoreProcessorFactory.getDisconfStoreItemProcessor().confToString());
            DisconfMgrTestCase.LOGGER.info("================ AFTER DISCONF ==============================");
            DisconfMgrTestCase.LOGGER.info(String.valueOf(("varA: " + (confA.getVarA()))));
            Assert.assertEquals(new Long(1000), confA.getVarA());
            DisconfMgrTestCase.LOGGER.info(String.valueOf(("varA2: " + (confA.getVarA2()))));
            Assert.assertEquals(new Long(2000), confA.getVarA2());
            DisconfMgrTestCase.LOGGER.info(String.valueOf(("varAA: " + (serviceA.getVarAA()))));
            Assert.assertEquals(new Integer(1000).intValue(), serviceA.getVarAA());
            DisconfMgrTestCase.LOGGER.info(String.valueOf(("static var: " + (StaticConf.getStaticvar()))));
            Assert.assertEquals(new Integer(50).intValue(), StaticConf.getStaticvar());
            testDynamicGetter();
            DisconfMgrTestCase.LOGGER.info("================ AFTER DISCONF ==============================");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}

