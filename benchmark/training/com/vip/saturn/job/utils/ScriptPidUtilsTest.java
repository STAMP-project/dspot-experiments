package com.vip.saturn.job.utils;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class ScriptPidUtilsTest {
    private static final String executorName = "executor_test_01";

    private static final String jobName = "jobName_test_01";

    private String executorFilePath = ((ScriptPidUtils.EXECUTINGPATH) + (ScriptPidUtils.FILESEPARATOR)) + (ScriptPidUtilsTest.executorName);

    private String jobFilePath = ((executorFilePath) + (ScriptPidUtils.FILESEPARATOR)) + (ScriptPidUtilsTest.jobName);

    @Test
    public void assertGetSaturnExecutingHome() {
        File file = ScriptPidUtils.getSaturnExecutingHome();
        assertThat(file).exists();
        assertThat(file).canRead();
        assertThat(file).canWrite();
        assertThat(file).isDirectory();
    }

    @Test
    public void assertWritePidToFile() {
        ScriptPidUtils.writePidToFile(ScriptPidUtilsTest.executorName, ScriptPidUtilsTest.jobName, 3, 103);
        long pid = ScriptPidUtils.getFirstPidFromFile(ScriptPidUtilsTest.executorName, ScriptPidUtilsTest.jobName, ("" + 3));
        assertThat(pid).isEqualTo(103);
    }

    @Test
    public void assertgetPidFromFile() {
        long pid = ScriptPidUtils.getFirstPidFromFile(ScriptPidUtilsTest.executorName, ScriptPidUtilsTest.jobName, ("" + 1));
        assertThat(pid).isEqualTo(101);
    }

    @Test
    public void assertgetPidFromFile2() {
        String[] files = ScriptPidUtils.getItemsPaths(ScriptPidUtilsTest.executorName, ScriptPidUtilsTest.jobName);
        assertThat(files.length).isEqualTo(2);
        Arrays.asList(files).contains((((jobFilePath) + (ScriptPidUtils.FILESEPARATOR)) + "1"));
        Arrays.asList(files).contains((((jobFilePath) + (ScriptPidUtils.FILESEPARATOR)) + "2"));
    }

    @Test
    public void assertremovePidFile() {
        long pid = ScriptPidUtils.getFirstPidFromFile(ScriptPidUtilsTest.executorName, ScriptPidUtilsTest.jobName, ("" + 1));
        assertThat(pid).isEqualTo(101);
        ScriptPidUtils.removeAllPidFile(ScriptPidUtilsTest.executorName, ScriptPidUtilsTest.jobName, 1);
        long pid2 = ScriptPidUtils.getFirstPidFromFile(ScriptPidUtilsTest.executorName, ScriptPidUtilsTest.jobName, ("" + 1));
        assertThat(pid2).isEqualTo((-1));
    }

    @Test
    public void testFilterEnvInCmdStr() {
        Map<String, String> env = new HashMap<>();
        env.put("fool", "duff");
        env.put("ass", "david");
        env.put("LS_COLORS", "r=01;31:*.ace=01;31:*.zoo=01;31:*.cpio=01;31:*.7z=01;31:*.rz=01;31:*.jpg=01;35:*.jpeg=01;35:*.gif=01;35:*.bmp=01");
        String cmd = "In front of me is ${fool}, beside me is $ass. $fool likes ${ass}. this is ${nobody} i don't know.";
        String expected = "In front of me is duff, beside me is david. duff likes david. this is ${nobody} i don't know.";
        String result = ScriptPidUtils.filterEnvInCmdStr(env, cmd);
        assertThat(result).isEqualTo(expected);
    }
}

