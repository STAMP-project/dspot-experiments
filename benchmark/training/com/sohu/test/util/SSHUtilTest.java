package com.sohu.test.util;


import com.sohu.cache.entity.MachineStats;
import com.sohu.cache.exception.SSHException;
import com.sohu.cache.ssh.SSHUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ssh????
 *
 * @author leifu
 * @unknown 2016-3-28
 * @unknown ??9:09:55
 */
public class SSHUtilTest extends Assert {
    private Logger logger = LoggerFactory.getLogger(SSHUtilTest.class);

    @Test
    public void testMachineStats() throws SSHException {
        String ip = "127.0.0.1";
        int port = 22;
        String userName = "cachecloud-open";
        String password = "cachecloud-open";
        MachineStats machineStats = SSHUtil.getMachineInfo(ip, port, userName, password);
        logger.info("ip {} machineStats: {}", machineStats);
    }

    @Test
    public void testCpu() {
        String redhat = "Cpu(s):  1.1%us,  0.5%sy,  0.0%ni, 98.4%id,  0.0%wa,  0.0%hi,  0.1%si,  0.0%st";
        String centos7 = "%Cpu(s):  0.11 us,  0.0 sy,  0.0 ni, 99.9 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st";
        String centos6 = "Cpu(s):  4.7%us, 16.0%sy,  0.0%ni, 67.0%id, 12.1%wa,  0.1%hi,  0.1%si,  0.0%st";
        Assert.assertTrue(((SSHUtil.getUsCpu(redhat)) == 1.1));
        Assert.assertTrue(((SSHUtil.getUsCpu(centos7)) == 0.11));
        Assert.assertTrue(((SSHUtil.getUsCpu(centos6)) == 4.7));
    }
}

