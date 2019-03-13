/**
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.discovery;


import DashboardConfig.CONFIG_HIDE_APP_NO_MACHINE_MILLIS;
import DashboardConfig.CONFIG_REMOVE_APP_NO_MACHINE_MILLIS;
import com.alibaba.csp.sentinel.dashboard.config.DashboardConfig;
import java.util.ConcurrentModificationException;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class AppInfoTest {
    @Test
    public void testConcurrentGetMachines() throws Exception {
        AppInfo appInfo = new AppInfo("testApp");
        appInfo.addMachine(genMachineInfo("hostName1", "10.18.129.91"));
        appInfo.addMachine(genMachineInfo("hostName2", "10.18.129.92"));
        Set<MachineInfo> machines = appInfo.getMachines();
        new Thread(() -> {
            try {
                for (MachineInfo m : machines) {
                    System.out.println(m);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
                Assert.fail();
            }
        }).start();
        Thread.sleep(100);
        try {
            appInfo.addMachine(genMachineInfo("hostName3", "10.18.129.93"));
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Thread.sleep(1000);
    }

    @Test
    public void addRemoveMachineTest() {
        AppInfo appInfo = new AppInfo("default");
        Assert.assertEquals("default", appInfo.getApp());
        Assert.assertEquals(0, appInfo.getMachines().size());
        // add one
        {
            MachineInfo machineInfo = new MachineInfo();
            machineInfo.setApp("default");
            machineInfo.setHostname("bogon");
            machineInfo.setIp("127.0.0.1");
            machineInfo.setPort(3389);
            machineInfo.setLastHeartbeat(System.currentTimeMillis());
            machineInfo.setHeartbeatVersion(1);
            machineInfo.setVersion("0.4.1");
            appInfo.addMachine(machineInfo);
        }
        Assert.assertEquals(1, appInfo.getMachines().size());
        // add duplicated one
        {
            MachineInfo machineInfo = new MachineInfo();
            machineInfo.setApp("default");
            machineInfo.setHostname("bogon");
            machineInfo.setIp("127.0.0.1");
            machineInfo.setPort(3389);
            machineInfo.setLastHeartbeat(System.currentTimeMillis());
            machineInfo.setHeartbeatVersion(1);
            machineInfo.setVersion("0.4.2");
            appInfo.addMachine(machineInfo);
        }
        Assert.assertEquals(1, appInfo.getMachines().size());
        // add different one
        {
            MachineInfo machineInfo = new MachineInfo();
            machineInfo.setApp("default");
            machineInfo.setHostname("bogon");
            machineInfo.setIp("127.0.0.1");
            machineInfo.setPort(3390);
            machineInfo.setLastHeartbeat(System.currentTimeMillis());
            machineInfo.setHeartbeatVersion(1);
            machineInfo.setVersion("0.4.3");
            appInfo.addMachine(machineInfo);
        }
        Assert.assertEquals(2, appInfo.getMachines().size());
        appInfo.removeMachine("127.0.0.1", 3389);
        Assert.assertEquals(1, appInfo.getMachines().size());
        appInfo.removeMachine("127.0.0.1", 3390);
        Assert.assertEquals(0, appInfo.getMachines().size());
    }

    @Test
    public void testHealthyAndDead() {
        System.setProperty(CONFIG_HIDE_APP_NO_MACHINE_MILLIS, "60000");
        System.setProperty(CONFIG_REMOVE_APP_NO_MACHINE_MILLIS, "600000");
        DashboardConfig.clearCache();
        String appName = "default";
        AppInfo appInfo = new AppInfo();
        appInfo.setApp(appName);
        {
            MachineInfo machineInfo = MachineInfo.of(appName, "127.0.0.1", 8801);
            machineInfo.setHeartbeatVersion(1);
            machineInfo.setLastHeartbeat(System.currentTimeMillis());
            appInfo.addMachine(machineInfo);
        }
        Assert.assertTrue(appInfo.isShown());
        Assert.assertFalse(appInfo.isDead());
        {
            MachineInfo machineInfo = MachineInfo.of(appName, "127.0.0.1", 8801);
            machineInfo.setHeartbeatVersion(1);
            machineInfo.setLastHeartbeat(((System.currentTimeMillis()) - 70000));
            appInfo.addMachine(machineInfo);
        }
        Assert.assertFalse(appInfo.isShown());
        Assert.assertFalse(appInfo.isDead());
        {
            MachineInfo machineInfo = MachineInfo.of(appName, "127.0.0.1", 8801);
            machineInfo.setHeartbeatVersion(1);
            machineInfo.setLastHeartbeat(((System.currentTimeMillis()) - 700000));
            appInfo.addMachine(machineInfo);
        }
        Assert.assertFalse(appInfo.isShown());
        Assert.assertTrue(appInfo.isDead());
    }
}

