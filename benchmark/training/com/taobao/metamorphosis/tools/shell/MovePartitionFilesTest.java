/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
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
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.tools.shell;


import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 * @since 2011-8-25 ????2:20:38
 */
public class MovePartitionFilesTest {
    MovePartitionFiles movePartitionFiles;

    File testMetaDataDir;

    @Test
    public void testMove_normal_forward() throws Exception {
        // [4,5,6]-->[0,1,2]
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        final File partition4DataFile = new File(partition4Dir, "0000.meta");
        partition4DataFile.createNewFile();
        this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 4 -end 6 -offset -4").split(" "));
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-0")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-1")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-2")).exists());
        Assert.assertTrue(new File((((((this.testMetaDataDir) + (File.separator)) + "topicxx-0") + (File.separator)) + "0000.meta")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6")).exists());
    }

    @Test
    public void testMove_normal_backward() throws Exception {
        // [0,1,2]-->[1,2,3]
        this.testMove_normal_forward();
        this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 0 -end 2 -offset 1").split(" "));
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-1")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-2")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-3")).exists());
        Assert.assertTrue(new File((((((this.testMetaDataDir) + (File.separator)) + "topicxx-1") + (File.separator)) + "0000.meta")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-0")).exists());
    }

    @Test
    public void testMove_newFileExists() throws Exception {
        // [4,5,6]-->[0,1,2]. 1???????
        FileUtils.forceMkdir(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-1")));
        try {
            this.testMove_normal_forward();
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        // ???
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-1")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6")).exists());
        Assert.assertTrue(new File((((((this.testMetaDataDir) + (File.separator)) + "topicxx-4") + (File.separator)) + "0000.meta")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-0")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-2")).exists());
    }

    @Test
    public void testMove_oldFileNotExists() throws Exception {
        // [4,5,6]-->[0,1,2] . 5??????
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition6Dir);
        final File partition4DataFile = new File(partition4Dir, "0000.meta");
        partition4DataFile.createNewFile();
        try {
            this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 4 -end 6 -offset -4").split(" "));
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        // ???
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5")).exists());
        Assert.assertTrue(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6")).exists());
        Assert.assertTrue(new File((((((this.testMetaDataDir) + (File.separator)) + "topicxx-4") + (File.separator)) + "0000.meta")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-0")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-1")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-2")).exists());
    }

    @Test
    public void testMove_illOffset() throws Exception {
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 4 -end 6 -offset -5").split(" "));
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testMove_blankTopic() throws Exception {
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic  -start 4 -end 6 -offset -4").split(" "));
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testMove_startLessThenEnd() throws Exception {
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 4 -end 3 -offset -5").split(" "));
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testMove_0Offset() throws Exception {
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 4 -end 5 -offset 0").split(" "));
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testMove_startLessThen0() throws Exception {
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.movePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start -1 -end 5 -offset 2").split(" "));
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testDelete_dataDirBlank() throws Exception {
        final File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        final File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        final File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.movePartitionFiles.doMain("-dataDir  -topic topicxx -start -1 -end 5 -offset 2".split(" "));
            Assert.fail();
        } catch (final RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }
}

