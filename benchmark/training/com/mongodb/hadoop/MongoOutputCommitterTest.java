package com.mongodb.hadoop;


import CompatUtils.TaskAttemptContext;
import MongoOutputCommitter.TEMP_DIR_NAME;
import com.mongodb.hadoop.output.MongoOutputCommitter;
import com.mongodb.hadoop.util.CompatUtils;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Assert;
import org.junit.Test;


public class MongoOutputCommitterTest {
    @Test
    public void testGetTaskAttemptPath() {
        // Empty configuration.
        JobConf conf = new JobConf();
        String taskName = "attempt_local138413205_0007_m_000000_0";
        String suffix = String.format("/%s/%s/_out", taskName, TEMP_DIR_NAME);
        CompatUtils.TaskAttemptContext context = CompatUtils.getTaskAttemptContext(conf, taskName);
        conf.clear();
        // /tmp
        Assert.assertEquals(("/tmp" + suffix), MongoOutputCommitter.getTaskAttemptPath(context).toUri().getPath());
        // system-wide tmp dir
        conf.set("hadoop.tmp.dir", "/system-wide");
        Assert.assertEquals(("/system-wide" + suffix), MongoOutputCommitter.getTaskAttemptPath(context).toUri().getPath());
        // old style option
        conf.set("mapred.child.tmp", "/child-tmp");
        Assert.assertEquals(("/child-tmp" + suffix), MongoOutputCommitter.getTaskAttemptPath(context).toUri().getPath());
        // new style option
        conf.set("mapreduce.task.tmp.dir", "/new-child-tmp");
        Assert.assertEquals(("/new-child-tmp" + suffix), MongoOutputCommitter.getTaskAttemptPath(context).toUri().getPath());
    }

    @Test
    public void testCleanupResources() throws IOException {
        // Empty configuration.
        JobConf conf = new JobConf();
        String taskName = "attempt_local138413205_0007_m_000000_0";
        CompatUtils.TaskAttemptContext context = CompatUtils.getTaskAttemptContext(conf, taskName);
        conf.clear();
        conf.set("mapreduce.task.tmp.dir", "/tmp");
        conf.set("fs.file.impl", LocalFileSystem.class.getName());
        Path taskAttemptDir = MongoOutputCommitter.getTaskAttemptPath(context);
        FileSystem fs = FileSystem.getLocal(conf);
        fs.create(taskAttemptDir);
        MongoOutputCommitter committer = new MongoOutputCommitter();
        // Trigger cleanupResources.
        committer.abortTask(context);
        Assert.assertFalse(fs.exists(taskAttemptDir));
        Assert.assertFalse(fs.exists(new Path(("/tmp/" + taskName))));
        Assert.assertTrue(fs.exists(new Path("/tmp")));
    }
}

