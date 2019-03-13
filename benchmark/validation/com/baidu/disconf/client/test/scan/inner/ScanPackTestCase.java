package com.baidu.disconf.client.test.scan.inner;


import DisconfMgrBean.SCAN_SPLIT_TOKEN;
import com.baidu.disconf.client.scan.inner.statically.model.ScanStaticModel;
import com.baidu.disconf.client.scan.inner.statically.strategy.ScanStaticStrategy;
import com.baidu.disconf.client.scan.inner.statically.strategy.impl.ReflectionScanStatic;
import com.baidu.disconf.client.support.utils.ScanPrinterUtils;
import com.baidu.disconf.client.support.utils.StringUtil;
import com.baidu.disconf.client.test.common.BaseSpringTestCase;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ????
 *
 * @author liaoqiqi
 * @version 2014-6-16
 */
public class ScanPackTestCase extends BaseSpringTestCase {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ScanPackTestCase.class);

    public static final String SCAN_PACK_NAME = "com.baidu.disconf.client.test";

    public static final List<String> SCAN_PACK_NAME_LIST = StringUtil.parseStringToStringList(ScanPackTestCase.SCAN_PACK_NAME, SCAN_SPLIT_TOKEN);

    @Test
    public void scan() {
        try {
            ScanStaticStrategy scanStaticStrategy = new ReflectionScanStatic();
            ScanStaticModel scanModel = scanStaticStrategy.scan(ScanPackTestCase.SCAN_PACK_NAME_LIST);
            // PRINT SCAN STORE
            ScanPrinterUtils.printStoreMap(scanModel.getReflections());
            // disconf file item
            ScanPackTestCase.LOGGER.info("=============DISCONF FILE ITEM===================");
            Set<Method> methods = scanModel.getDisconfFileItemMethodSet();
            ScanPrinterUtils.printFileItemMethod(methods);
            Assert.assertEquals(6, methods.size());
            Assert.assertEquals(4, scanModel.getDisconfFileClassSet().size());
            // disconf file item
            ScanPackTestCase.LOGGER.info("=============DISCONF FILE===================");
            Map<Class<?>, Set<Method>> fileMap = scanModel.getDisconfFileItemMap();
            Assert.assertEquals(4, fileMap.size());
            // disconf item
            ScanPackTestCase.LOGGER.info("=============DISCONF ITEM===================");
            methods = scanModel.getDisconfItemMethodSet();
            ScanPrinterUtils.printFileItemMethod(methods);
            Assert.assertEquals(1, methods.size());
            // Active backup
            ScanPackTestCase.LOGGER.info("=============DISCONF ACTIVE BACKUP===================");
            Set<Class<?>> classSet = scanModel.getDisconfActiveBackupServiceClassSet();
            ScanPrinterUtils.printActiveBackup(classSet);
            Assert.assertEquals(0, classSet.size());
            // Update service
            ScanPackTestCase.LOGGER.info("=============DISCONF Update service===================");
            classSet = scanModel.getDisconfUpdateService();
            ScanPrinterUtils.printUpdateFile(classSet);
            Assert.assertEquals(2, classSet.size());
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}

