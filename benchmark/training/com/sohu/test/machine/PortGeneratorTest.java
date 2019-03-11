package com.sohu.test.machine;


import com.sohu.cache.dao.MachineDao;
import com.sohu.cache.entity.MachineInfo;
import com.sohu.cache.exception.SSHException;
import com.sohu.cache.machine.PortGenerator;
import com.sohu.cache.ssh.SSHUtil;
import com.sohu.test.BaseTest;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;


/**
 * ????????????
 *
 * @author leifu
 * @unknown 2016?4?21?
 * @unknown ??6:14:31
 */
public class PortGeneratorTest extends BaseTest {
    @Resource
    private MachineDao machineDao;

    @Test
    public void testCheckMaxPort() throws SSHException {
        List<MachineInfo> machineList = machineDao.getAllMachines();
        for (MachineInfo machineInfo : machineList) {
            String ip = machineInfo.getIp();
            int sshPort = SSHUtil.getSshPort(ip);
            String m1 = PortGenerator.getMaxPortStrOld(ip, sshPort);
            String m2 = PortGenerator.getMaxPortStr(ip, sshPort);
            boolean isSame = m1.equals(m2);
            if (!isSame) {
                System.out.println(((((ip + ", m1: ") + m1) + ", m2:") + m2));
            }
        }
    }
}

