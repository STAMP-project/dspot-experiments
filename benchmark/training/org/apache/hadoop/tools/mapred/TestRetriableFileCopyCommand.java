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
package org.apache.hadoop.tools.mapred;


import Mapper.Context;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.tools.CopyListingFileStatus;
import org.apache.hadoop.tools.mapred.CopyMapper.FileAction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestRetriableFileCopyCommand {
    @SuppressWarnings("rawtypes")
    @Test
    public void testFailOnCloseError() throws Exception {
        Mapper.Context context = Mockito.mock(Context.class);
        Mockito.doReturn(new Configuration()).when(context).getConfiguration();
        Exception expectedEx = new IOException("boom");
        OutputStream out = Mockito.mock(OutputStream.class);
        Mockito.doThrow(expectedEx).when(out).close();
        File f = File.createTempFile(this.getClass().getSimpleName(), null);
        f.deleteOnExit();
        CopyListingFileStatus stat = new CopyListingFileStatus(new org.apache.hadoop.fs.FileStatus(1L, false, 1, 1024, 0, new Path(f.toURI())));
        Exception actualEx = null;
        try {
            new RetriableFileCopyCommand("testFailOnCloseError", FileAction.OVERWRITE).copyBytes(stat, 0, out, 512, context);
        } catch (Exception e) {
            actualEx = e;
        }
        Assert.assertNotNull("close didn't fail", actualEx);
        Assert.assertEquals(expectedEx, actualEx);
    }
}

