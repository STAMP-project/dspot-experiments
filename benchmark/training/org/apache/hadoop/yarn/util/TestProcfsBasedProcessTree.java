/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.util;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.util.Shell.ExitCodeException;
import org.apache.hadoop.util.Shell.ShellCommandExecutor;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.util.ProcfsBasedProcessTree.ProcessTreeSmapMemInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ProcfsBasedProcessTree.JIFFY_LENGTH_IN_MILLIS;
import static ProcfsBasedProcessTree.PAGE_SIZE;
import static ResourceCalculatorProcessTree.UNAVAILABLE;


/**
 * A JUnit test to test ProcfsBasedProcessTree.
 */
public class TestProcfsBasedProcessTree {
    private static final Logger LOG = LoggerFactory.getLogger(TestProcfsBasedProcessTree.class);

    protected static File TEST_ROOT_DIR = new File("target", ((TestProcfsBasedProcessTree.class.getName()) + "-localDir"));

    private ShellCommandExecutor shexec = null;

    private String pidFile;

    private String lowestDescendant;

    private String lostDescendant;

    private String shellScript;

    private static final int N = 6;// Controls the RogueTask


    private class RogueTaskThread extends Thread {
        public void run() {
            try {
                Vector<String> args = new Vector<String>();
                if (TestProcfsBasedProcessTree.isSetsidAvailable()) {
                    args.add("setsid");
                }
                args.add("bash");
                args.add("-c");
                args.add(((((((" echo $$ > " + (pidFile)) + "; sh ") + (shellScript)) + " ") + (TestProcfsBasedProcessTree.N)) + ";"));
                shexec = new ShellCommandExecutor(args.toArray(new String[0]));
                shexec.execute();
            } catch (ExitCodeException ee) {
                TestProcfsBasedProcessTree.LOG.info((("Shell Command exit with a non-zero exit code. This is" + (" expected as we are killing the subprocesses of the" + " task intentionally. ")) + ee));
            } catch (IOException ioe) {
                TestProcfsBasedProcessTree.LOG.info(("Error executing shell command " + ioe));
            } finally {
                TestProcfsBasedProcessTree.LOG.info(("Exit code: " + (shexec.getExitCode())));
            }
        }
    }

    @Test(timeout = 30000)
    public void testProcessTree() throws Exception {
        try {
            Assert.assertTrue(ProcfsBasedProcessTree.isAvailable());
        } catch (Exception e) {
            TestProcfsBasedProcessTree.LOG.info(StringUtils.stringifyException(e));
            Assert.assertTrue("ProcfsBaseProcessTree should be available on Linux", false);
            return;
        }
        // create shell script
        Random rm = new Random();
        File tempFile = new File(TestProcfsBasedProcessTree.TEST_ROOT_DIR, ((((getClass().getName()) + "_shellScript_") + (rm.nextInt())) + ".sh"));
        tempFile.deleteOnExit();
        shellScript = ((TestProcfsBasedProcessTree.TEST_ROOT_DIR) + (File.separator)) + (tempFile.getName());
        // create pid file
        tempFile = new File(TestProcfsBasedProcessTree.TEST_ROOT_DIR, ((((getClass().getName()) + "_pidFile_") + (rm.nextInt())) + ".pid"));
        tempFile.deleteOnExit();
        pidFile = ((TestProcfsBasedProcessTree.TEST_ROOT_DIR) + (File.separator)) + (tempFile.getName());
        lowestDescendant = ((TestProcfsBasedProcessTree.TEST_ROOT_DIR) + (File.separator)) + "lowestDescendantPidFile";
        lostDescendant = ((TestProcfsBasedProcessTree.TEST_ROOT_DIR) + (File.separator)) + "lostDescendantPidFile";
        // write to shell-script
        File file = new File(shellScript);
        FileUtils.writeStringToFile(file, ((((((((((((((("# rogue task\n" + (((("sleep 1\n" + "echo hello\n") + "if [ $1 -ne 0 ]\n") + "then\n") + " sh ")) + (shellScript)) + " $(($1-1))\n") + "else\n") + " echo $$ > ") + (lowestDescendant)) + "\n") + "(sleep 300&\n") + "echo $! > ") + (lostDescendant)) + ")\n") + " while true\n do\n") + "  sleep 5\n") + " done\n") + "fi"), StandardCharsets.UTF_8);
        Thread t = new TestProcfsBasedProcessTree.RogueTaskThread();
        t.start();
        String pid = getRogueTaskPID();
        TestProcfsBasedProcessTree.LOG.info(("Root process pid: " + pid));
        ProcfsBasedProcessTree p = createProcessTree(pid);
        p.updateProcessTree();// initialize

        TestProcfsBasedProcessTree.LOG.info(("ProcessTree: " + p));
        File leaf = new File(lowestDescendant);
        // wait till lowest descendant process of Rougue Task starts execution
        while (!(leaf.exists())) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                break;
            }
        } 
        p.updateProcessTree();// reconstruct

        TestProcfsBasedProcessTree.LOG.info(("ProcessTree: " + p));
        // Verify the orphaned pid is In process tree
        String lostpid = TestProcfsBasedProcessTree.getPidFromPidFile(lostDescendant);
        TestProcfsBasedProcessTree.LOG.info(("Orphaned pid: " + lostpid));
        Assert.assertTrue("Child process owned by init escaped process tree.", p.contains(lostpid));
        // Get the process-tree dump
        String processTreeDump = p.getProcessTreeDump();
        // destroy the process and all its subprocesses
        destroyProcessTree(pid);
        boolean isAlive = true;
        for (int tries = 100; tries > 0; tries--) {
            if (TestProcfsBasedProcessTree.isSetsidAvailable()) {
                // whole processtree
                isAlive = TestProcfsBasedProcessTree.isAnyProcessInTreeAlive(p);
            } else {
                // process
                isAlive = TestProcfsBasedProcessTree.isAlive(pid);
            }
            if (!isAlive) {
                break;
            }
            Thread.sleep(100);
        }
        if (isAlive) {
            Assert.fail("ProcessTree shouldn't be alive");
        }
        TestProcfsBasedProcessTree.LOG.info(("Process-tree dump follows: \n" + processTreeDump));
        Assert.assertTrue("Process-tree dump doesn't start with a proper header", processTreeDump.startsWith(("\t|- PID PPID PGRPID SESSID CMD_NAME " + ("USER_MODE_TIME(MILLIS) SYSTEM_TIME(MILLIS) VMEM_USAGE(BYTES) " + "RSSMEM_USAGE(PAGES) FULL_CMD_LINE\n"))));
        for (int i = TestProcfsBasedProcessTree.N; i >= 0; i--) {
            String cmdLineDump = ((("\\|- [0-9]+ [0-9]+ [0-9]+ [0-9]+ \\(sh\\)" + " [0-9]+ [0-9]+ [0-9]+ [0-9]+ sh ") + (shellScript)) + " ") + i;
            Pattern pat = Pattern.compile(cmdLineDump);
            Matcher mat = pat.matcher(processTreeDump);
            Assert.assertTrue((("Process-tree dump doesn't contain the cmdLineDump of " + i) + "th process!"), mat.find());
        }
        // Not able to join thread sometimes when forking with large N.
        try {
            t.join(2000);
            TestProcfsBasedProcessTree.LOG.info("RogueTaskThread successfully joined.");
        } catch (InterruptedException ie) {
            TestProcfsBasedProcessTree.LOG.info("Interrupted while joining RogueTaskThread.");
        }
        // ProcessTree is gone now. Any further calls should be sane.
        p.updateProcessTree();
        Assert.assertFalse("ProcessTree must have been gone", TestProcfsBasedProcessTree.isAlive(pid));
        Assert.assertTrue((("vmem for the gone-process is " + (p.getVirtualMemorySize())) + " . It should be UNAVAILABLE(-1)."), ((p.getVirtualMemorySize()) == (ResourceCalculatorProcessTree.UNAVAILABLE)));
        Assert.assertEquals("[ ]", p.toString());
    }

    public static class ProcessStatInfo {
        // sample stat in a single line : 3910 (gpm) S 1 3910 3910 0 -1 4194624
        // 83 0 0 0 0 0 0 0 16 0 1 0 7852 2408448 88 4294967295 134512640
        // 134590050 3220521392 3220520036 10975138 0 0 4096 134234626
        // 4294967295 0 0 17 1 0 0
        String pid;

        String name;

        String ppid;

        String pgrpId;

        String session;

        String vmem = "0";

        String rssmemPage = "0";

        String utime = "0";

        String stime = "0";

        public ProcessStatInfo(String[] statEntries) {
            pid = statEntries[0];
            name = statEntries[1];
            ppid = statEntries[2];
            pgrpId = statEntries[3];
            session = statEntries[4];
            vmem = statEntries[5];
            if ((statEntries.length) > 6) {
                rssmemPage = statEntries[6];
            }
            if ((statEntries.length) > 7) {
                utime = statEntries[7];
                stime = statEntries[8];
            }
        }

        // construct a line that mimics the procfs stat file.
        // all unused numerical entries are set to 0.
        public String getStatLine() {
            return String.format(("%s (%s) S %s %s %s 0 0 0" + ((" 0 0 0 0 %s %s 0 0 0 0 0 0 0 %s %s 0 0" + " 0 0 0 0 0 0 0 0") + " 0 0 0 0 0")), pid, name, ppid, pgrpId, session, utime, stime, vmem, rssmemPage);
        }
    }

    /**
     * A basic test that creates a few process directories and writes stat files.
     * Verifies that the cpu time and memory is correctly computed.
     *
     * @throws IOException
     * 		if there was a problem setting up the fake procfs directories or
     * 		files.
     */
    @Test(timeout = 30000)
    public void testCpuAndMemoryForProcessTree() throws IOException {
        // test processes
        String[] pids = new String[]{ "100", "200", "300", "400" };
        ControlledClock testClock = new ControlledClock();
        testClock.setTime(0);
        // create the fake procfs root directory.
        File procfsRootDir = new File(TestProcfsBasedProcessTree.TEST_ROOT_DIR, "proc");
        try {
            TestProcfsBasedProcessTree.setupProcfsRootDir(procfsRootDir);
            TestProcfsBasedProcessTree.setupPidDirs(procfsRootDir, pids);
            // create stat objects.
            // assuming processes 100, 200, 300 are in tree and 400 is not.
            TestProcfsBasedProcessTree.ProcessStatInfo[] procInfos = new TestProcfsBasedProcessTree.ProcessStatInfo[4];
            procInfos[0] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "100", "proc1", "1", "100", "100", "100000", "100", "1000", "200" });
            procInfos[1] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "200", "process two", "100", "100", "100", "200000", "200", "2000", "400" });
            procInfos[2] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "300", "proc(3)", "200", "100", "100", "300000", "300", "3000", "600" });
            procInfos[3] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "400", "proc4", "1", "400", "400", "400000", "400", "4000", "800" });
            ProcessTreeSmapMemInfo[] memInfo = new ProcessTreeSmapMemInfo[4];
            memInfo[0] = new ProcessTreeSmapMemInfo("100");
            memInfo[1] = new ProcessTreeSmapMemInfo("200");
            memInfo[2] = new ProcessTreeSmapMemInfo("300");
            memInfo[3] = new ProcessTreeSmapMemInfo("400");
            createMemoryMappingInfo(memInfo);
            TestProcfsBasedProcessTree.writeStatFiles(procfsRootDir, pids, procInfos, memInfo);
            // crank up the process tree class.
            Configuration conf = new Configuration();
            ProcfsBasedProcessTree processTree = createProcessTree("100", procfsRootDir.getAbsolutePath(), testClock);
            processTree.setConf(conf);
            // build the process tree.
            processTree.updateProcessTree();
            // verify virtual memory
            Assert.assertEquals("Virtual memory does not match", 600000L, processTree.getVirtualMemorySize());
            // verify rss memory
            long cumuRssMem = ((PAGE_SIZE) > 0) ? 600L * (PAGE_SIZE) : UNAVAILABLE;
            Assert.assertEquals("rss memory does not match", cumuRssMem, processTree.getRssMemorySize());
            // verify cumulative cpu time
            long cumuCpuTime = ((JIFFY_LENGTH_IN_MILLIS) > 0) ? 7200L * (JIFFY_LENGTH_IN_MILLIS) : 0L;
            Assert.assertEquals("Cumulative cpu time does not match", cumuCpuTime, processTree.getCumulativeCpuTime());
            // verify CPU usage
            Assert.assertEquals("Percent CPU time should be set to -1 initially", (-1.0), processTree.getCpuUsagePercent(), 0.01);
            // Check by enabling smaps
            setSmapsInProceTree(processTree, true);
            // anon (exclude r-xs,r--s)
            Assert.assertEquals("rss memory does not match", ((20 * (ProcfsBasedProcessTree.KB_TO_BYTES)) * 3), processTree.getRssMemorySize());
            // test the cpu time again to see if it cumulates
            procInfos[0] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "100", "proc1", "1", "100", "100", "100000", "100", "2000", "300" });
            procInfos[1] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "200", "process two", "100", "100", "100", "200000", "200", "3000", "500" });
            TestProcfsBasedProcessTree.writeStatFiles(procfsRootDir, pids, procInfos, memInfo);
            long elapsedTimeBetweenUpdatesMsec = 200000;
            testClock.setTime(elapsedTimeBetweenUpdatesMsec);
            // build the process tree.
            processTree.updateProcessTree();
            // verify cumulative cpu time again
            long prevCumuCpuTime = cumuCpuTime;
            cumuCpuTime = ((JIFFY_LENGTH_IN_MILLIS) > 0) ? 9400L * (JIFFY_LENGTH_IN_MILLIS) : 0L;
            Assert.assertEquals("Cumulative cpu time does not match", cumuCpuTime, processTree.getCumulativeCpuTime());
            double expectedCpuUsagePercent = ((JIFFY_LENGTH_IN_MILLIS) > 0) ? ((cumuCpuTime - prevCumuCpuTime) * 100.0) / elapsedTimeBetweenUpdatesMsec : 0;
            // expectedCpuUsagePercent is given by (94000L - 72000) * 100/
            // 200000;
            // which in this case is 11. Lets verify that first
            Assert.assertEquals(11, expectedCpuUsagePercent, 0.001);
            Assert.assertEquals(("Percent CPU time is not correct expected " + expectedCpuUsagePercent), expectedCpuUsagePercent, processTree.getCpuUsagePercent(), 0.01);
        } finally {
            FileUtil.fullyDelete(procfsRootDir);
        }
    }

    /**
     * Tests that cumulative memory is computed only for processes older than a
     * given age.
     *
     * @throws IOException
     * 		if there was a problem setting up the fake procfs directories or
     * 		files.
     */
    @Test(timeout = 30000)
    public void testMemForOlderProcesses() throws IOException {
        testMemForOlderProcesses(false);
        testMemForOlderProcesses(true);
    }

    /**
     * Verifies ProcfsBasedProcessTree.checkPidPgrpidForMatch() in case of
     * 'constructProcessInfo() returning null' by not writing stat file for the
     * mock process
     *
     * @throws IOException
     * 		if there was a problem setting up the fake procfs directories or
     * 		files.
     */
    @Test(timeout = 30000)
    public void testDestroyProcessTree() throws IOException {
        // test process
        String pid = "100";
        // create the fake procfs root directory.
        File procfsRootDir = new File(TestProcfsBasedProcessTree.TEST_ROOT_DIR, "proc");
        try {
            TestProcfsBasedProcessTree.setupProcfsRootDir(procfsRootDir);
            // crank up the process tree class.
            createProcessTree(pid, procfsRootDir.getAbsolutePath(), SystemClock.getInstance());
            // Let us not create stat file for pid 100.
            Assert.assertTrue(ProcfsBasedProcessTree.checkPidPgrpidForMatch(pid, procfsRootDir.getAbsolutePath()));
        } finally {
            FileUtil.fullyDelete(procfsRootDir);
        }
    }

    /**
     * Test the correctness of process-tree dump.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testProcessTreeDump() throws IOException {
        String[] pids = new String[]{ "100", "200", "300", "400", "500", "600" };
        File procfsRootDir = new File(TestProcfsBasedProcessTree.TEST_ROOT_DIR, "proc");
        try {
            TestProcfsBasedProcessTree.setupProcfsRootDir(procfsRootDir);
            TestProcfsBasedProcessTree.setupPidDirs(procfsRootDir, pids);
            int numProcesses = pids.length;
            // Processes 200, 300, 400 and 500 are descendants of 100. 600 is not.
            TestProcfsBasedProcessTree.ProcessStatInfo[] procInfos = new TestProcfsBasedProcessTree.ProcessStatInfo[numProcesses];
            procInfos[0] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "100", "proc1", "1", "100", "100", "100000", "100", "1000", "200" });
            procInfos[1] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "200", "process two", "100", "100", "100", "200000", "200", "2000", "400" });
            procInfos[2] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "300", "proc(3)", "200", "100", "100", "300000", "300", "3000", "600" });
            procInfos[3] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "400", "proc4", "200", "100", "100", "400000", "400", "4000", "800" });
            procInfos[4] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "500", "proc5", "400", "100", "100", "400000", "400", "4000", "800" });
            procInfos[5] = new TestProcfsBasedProcessTree.ProcessStatInfo(new String[]{ "600", "proc6", "1", "1", "1", "400000", "400", "4000", "800" });
            ProcessTreeSmapMemInfo[] memInfos = new ProcessTreeSmapMemInfo[6];
            memInfos[0] = new ProcessTreeSmapMemInfo("100");
            memInfos[1] = new ProcessTreeSmapMemInfo("200");
            memInfos[2] = new ProcessTreeSmapMemInfo("300");
            memInfos[3] = new ProcessTreeSmapMemInfo("400");
            memInfos[4] = new ProcessTreeSmapMemInfo("500");
            memInfos[5] = new ProcessTreeSmapMemInfo("600");
            String[] cmdLines = new String[numProcesses];
            cmdLines[0] = "proc1 arg1 arg2";
            cmdLines[1] = "process two arg3 arg4";
            cmdLines[2] = "proc(3) arg5 arg6";
            cmdLines[3] = "proc4 arg7 arg8";
            cmdLines[4] = "proc5 arg9 arg10";
            cmdLines[5] = "proc6 arg11 arg12";
            createMemoryMappingInfo(memInfos);
            TestProcfsBasedProcessTree.writeStatFiles(procfsRootDir, pids, procInfos, memInfos);
            TestProcfsBasedProcessTree.writeCmdLineFiles(procfsRootDir, pids, cmdLines);
            ProcfsBasedProcessTree processTree = createProcessTree("100", procfsRootDir.getAbsolutePath(), SystemClock.getInstance());
            // build the process tree.
            processTree.updateProcessTree();
            // Get the process-tree dump
            String processTreeDump = processTree.getProcessTreeDump();
            TestProcfsBasedProcessTree.LOG.info(("Process-tree dump follows: \n" + processTreeDump));
            Assert.assertTrue("Process-tree dump doesn't start with a proper header", processTreeDump.startsWith(("\t|- PID PPID PGRPID SESSID CMD_NAME " + ("USER_MODE_TIME(MILLIS) SYSTEM_TIME(MILLIS) VMEM_USAGE(BYTES) " + "RSSMEM_USAGE(PAGES) FULL_CMD_LINE\n"))));
            for (int i = 0; i < 5; i++) {
                TestProcfsBasedProcessTree.ProcessStatInfo p = procInfos[i];
                Assert.assertTrue(("Process-tree dump doesn't contain the cmdLineDump of process " + (p.pid)), processTreeDump.contains(((((((((((((((((((("\t|- " + (p.pid)) + " ") + (p.ppid)) + " ") + (p.pgrpId)) + " ") + (p.session)) + " (") + (p.name)) + ") ") + (p.utime)) + " ") + (p.stime)) + " ") + (p.vmem)) + " ") + (p.rssmemPage)) + " ") + (cmdLines[i]))));
            }
            // 600 should not be in the dump
            TestProcfsBasedProcessTree.ProcessStatInfo p = procInfos[5];
            Assert.assertFalse(("Process-tree dump shouldn't contain the cmdLineDump of process " + (p.pid)), processTreeDump.contains(((((((((((((((((("\t|- " + (p.pid)) + " ") + (p.ppid)) + " ") + (p.pgrpId)) + " ") + (p.session)) + " (") + (p.name)) + ") ") + (p.utime)) + " ") + (p.stime)) + " ") + (p.vmem)) + " ") + (cmdLines[5]))));
        } finally {
            FileUtil.fullyDelete(procfsRootDir);
        }
    }
}

