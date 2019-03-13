package com.sohu.cache.dao;


import com.sohu.test.BaseTest;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ????
 *
 * @author leifu
 * @unknown 2016?3?17?
 * @unknown ??2:15:02
 */
public class InstanceDaoTest extends BaseTest {
    @Resource
    private InstanceDao instanceDao;

    @Test
    public void testGetMachineInstanceCountMap() throws Exception {
        System.out.println("================testGetMachineInstanceCountMap start================");
        List<Map<String, Object>> mapList = instanceDao.getMachineInstanceCountMap();
        for (Map<String, Object> map : mapList) {
            System.out.println(map.get("ip"));
            System.out.println(map.get("count"));
        }
        System.out.println("================testGetMachineInstanceCountMap start================");
    }
}

