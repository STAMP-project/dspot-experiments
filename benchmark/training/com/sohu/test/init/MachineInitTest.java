package com.sohu.test.init;


import com.sohu.cache.init.MachineInitLoad;
import com.sohu.test.BaseTest;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * User: lingguo
 * Date: 14-6-12
 * Time: ??3:20
 */
public class MachineInitTest extends BaseTest {
    @Resource
    MachineInitLoad machineInitLoad;

    @Test
    public void testInit() {
        machineInitLoad.init();
    }
}

