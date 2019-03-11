package com.alibaba.csp.sentinel.slots.logger;


import LogBase.LOG_DIR;
import com.alibaba.csp.sentinel.log.RecordLog;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.hamcrest.io.FileMatchers;
import org.junit.Test;

import static EagleEyeLogUtil.FILE_NAME;


/**
 *
 *
 * @author Carpenter Lee
 */
public class EagleEyeLogUtilTest {
    @Test
    public void testWriteLog() throws Exception {
        EagleEyeLogUtil.log("resourceName", "BlockException", "app1", "origin", 1);
        final File file = new File(((RecordLog.getLogBaseDir()) + (FILE_NAME)));
        await().timeout(2, TimeUnit.SECONDS).until(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return file;
            }
        }, FileMatchers.anExistingFile());
    }

    @Test
    public void testChangeLogBase() throws Exception {
        String userHome = System.getProperty("user.home");
        String newLogBase = ((userHome + (File.separator)) + "tmpLogDir") + (System.currentTimeMillis());
        System.setProperty(LOG_DIR, newLogBase);
        EagleEyeLogUtil.log("resourceName", "BlockException", "app1", "origin", 1);
        final File file = new File(((RecordLog.getLogBaseDir()) + (FILE_NAME)));
        await().timeout(2, TimeUnit.SECONDS).until(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return file;
            }
        }, FileMatchers.anExistingFile());
    }
}

