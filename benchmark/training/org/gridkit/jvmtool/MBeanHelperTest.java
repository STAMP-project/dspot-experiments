package org.gridkit.jvmtool;


import MBeanHelper.FORMAT_TABLE_COLUMN_WIDTH_THRESHOLD;
import java.lang.management.ManagementFactory;
import org.junit.Test;


public class MBeanHelperTest {
    @Test
    public void test_thread_bean_describer() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.describe(threadMXBean()));
    }

    @Test
    public void test_os_bean_describer() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.describe(osMXBean()));
    }

    @Test
    public void test_memory_bean_describer() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.describe(memoryMXBean()));
    }

    @Test
    public void test_runtime_bean_describer() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.describe(runtimeMXBean()));
    }

    @Test
    public void test_management_bean_describer() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.describe(managementMXBean()));
    }

    @Test
    public void test_bean_get_set() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        helper.set(threadMXBean(), "ThreadCpuTimeEnabled", "true");
        System.out.println(("ThreadCpuTimeEnabled: " + (helper.get(threadMXBean(), "ThreadAllocatedMemoryEnabled"))));
        helper.set(threadMXBean(), "ThreadCpuTimeEnabled", "FALSE");
        System.out.println(("ThreadCpuTimeEnabled: " + (helper.get(threadMXBean(), "ThreadAllocatedMemoryEnabled"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_bean_invalid_set() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        helper.set(managementMXBean(), "ThreadCpuTimeEnabled", "true");
    }

    @Test
    public void test_get_sys_props() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.get(runtimeMXBean(), "SystemProperties"));
    }

    @Test
    public void test_get_sys_props_wide() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        helper.setFormatingOption(FORMAT_TABLE_COLUMN_WIDTH_THRESHOLD, 200);
        System.out.println(helper.get(runtimeMXBean(), "SystemProperties"));
    }

    @Test
    public void test_get_mem_heap() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.get(memoryMXBean(), "HeapMemoryUsage"));
    }

    @Test
    public void test_get_thread_dump() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.invoke(threadMXBean(), "dumpAllThreads", "true", "true"));
    }

    @Test
    public void test_find_deadlocked_threads() throws Exception {
        MBeanHelper helper = new MBeanHelper(ManagementFactory.getPlatformMBeanServer());
        System.out.println(helper.invoke(threadMXBean(), "findMonitorDeadlockedThreads"));
    }
}

