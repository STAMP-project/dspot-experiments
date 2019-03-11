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
 * @since 2011-8-25 ????3:53:01
 */
public class DeletePartitionFilesTest {
    DeletePartitionFiles deletePartitionFiles;

    File testMetaDataDir;

    @Test
    public void testDelete_normal_NoFiles() throws Exception {
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6")).exists());
        this.deletePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 4 -end 6 -f").split(" "));
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5")).exists());
        Assert.assertFalse(new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6")).exists());
    }

    @Test
    public void testDelete_normal_OneFileNotExists() throws Exception {
        File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        File partition7Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-7"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition6Dir);
        FileUtils.forceMkdir(partition7Dir);
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertFalse(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
        this.deletePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic topicxx -start 4 -end 6  -f").split(" "));
        Assert.assertFalse(partition4Dir.exists());
        Assert.assertFalse(partition5Dir.exists());
        Assert.assertFalse(partition6Dir.exists());
        Assert.assertTrue(partition7Dir.exists());
    }

    @Test
    public void testDelete_dataDirBlank() throws Exception {
        File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.deletePartitionFiles.doMain("-dataDir  -topic topicxx -start 4 -end 6 -f".split(" "));
            Assert.fail();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testDelete_blankTopic() throws Exception {
        File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.deletePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -topic   -f  -start 4 -end 6").split(" "));
            Assert.fail();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testDelete_startLessThenEnd() throws Exception {
        File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.deletePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -f -topic topicxx -start 4 -end 3").split(" "));
            Assert.fail();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }

    @Test
    public void testMove_startLessThen0() throws Exception {
        File partition4Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-4"));
        File partition5Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-5"));
        File partition6Dir = new File((((this.testMetaDataDir) + (File.separator)) + "topicxx-6"));
        FileUtils.forceMkdir(partition4Dir);
        FileUtils.forceMkdir(partition5Dir);
        FileUtils.forceMkdir(partition6Dir);
        try {
            this.deletePartitionFiles.doMain((("-dataDir " + (this.testMetaDataDir.getAbsolutePath())) + " -f -topic topicxx -start -1 -end 5").split(" "));
            Assert.fail();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        Assert.assertTrue(partition4Dir.exists());
        Assert.assertTrue(partition5Dir.exists());
        Assert.assertTrue(partition6Dir.exists());
    }
}

