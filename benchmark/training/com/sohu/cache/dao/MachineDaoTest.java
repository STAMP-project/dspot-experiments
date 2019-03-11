package com.sohu.cache.dao;


import com.sohu.cache.entity.MachineInfo;
import com.sohu.test.BaseTest;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ????
 *
 * @author leifu
 * @unknown 2016?3?17?
 * @unknown ??2:15:02
 */
public class MachineDaoTest extends BaseTest {
    @Resource
    private MachineDao machineDao;

    @Test
    public void testSaveMachine() throws Exception {
        MachineInfo machineInfo = new MachineInfo();
        machineInfo.setCpu(16);
        machineInfo.setIp("10.10.53.181");
        machineInfo.setMem(96);
        machineInfo.setModifyTime(new Date());
        machineInfo.setRealIp("");
        machineInfo.setRoom("??");
        machineInfo.setServiceTime(new Date());
        machineInfo.setSshPasswd("cachecloud-open");
        machineInfo.setSshUser("cachecloud-open");
        machineDao.saveMachineInfo(machineInfo);
    }
}

