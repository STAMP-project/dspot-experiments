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
package org.apache.hadoop.mapred;


import StringUtils.COMMA;
import StringUtils.ESCAPE_CHAR;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestInputPath {
    @Test
    public void testInputPath() throws Exception {
        JobConf jobConf = new JobConf();
        Path workingDir = jobConf.getWorkingDirectory();
        Path path = new Path(workingDir, (("xx{y" + (StringUtils.COMMA_STR)) + "z}"));
        FileInputFormat.setInputPaths(jobConf, path);
        Path[] paths = FileInputFormat.getInputPaths(jobConf);
        Assert.assertEquals(1, paths.length);
        Assert.assertEquals(path.toString(), paths[0].toString());
        StringBuilder pathStr = new StringBuilder();
        pathStr.append(ESCAPE_CHAR);
        pathStr.append(ESCAPE_CHAR);
        pathStr.append(COMMA);
        pathStr.append(COMMA);
        pathStr.append('a');
        path = new Path(workingDir, pathStr.toString());
        FileInputFormat.setInputPaths(jobConf, path);
        paths = FileInputFormat.getInputPaths(jobConf);
        Assert.assertEquals(1, paths.length);
        Assert.assertEquals(path.toString(), paths[0].toString());
        pathStr.setLength(0);
        pathStr.append(ESCAPE_CHAR);
        pathStr.append("xx");
        pathStr.append(ESCAPE_CHAR);
        path = new Path(workingDir, pathStr.toString());
        Path path1 = new Path(workingDir, (("yy" + (StringUtils.COMMA_STR)) + "zz"));
        FileInputFormat.setInputPaths(jobConf, path);
        FileInputFormat.addInputPath(jobConf, path1);
        paths = FileInputFormat.getInputPaths(jobConf);
        Assert.assertEquals(2, paths.length);
        Assert.assertEquals(path.toString(), paths[0].toString());
        Assert.assertEquals(path1.toString(), paths[1].toString());
        FileInputFormat.setInputPaths(jobConf, path, path1);
        paths = FileInputFormat.getInputPaths(jobConf);
        Assert.assertEquals(2, paths.length);
        Assert.assertEquals(path.toString(), paths[0].toString());
        Assert.assertEquals(path1.toString(), paths[1].toString());
        Path[] input = new Path[]{ path, path1 };
        FileInputFormat.setInputPaths(jobConf, input);
        paths = FileInputFormat.getInputPaths(jobConf);
        Assert.assertEquals(2, paths.length);
        Assert.assertEquals(path.toString(), paths[0].toString());
        Assert.assertEquals(path1.toString(), paths[1].toString());
        pathStr.setLength(0);
        String str1 = "{a{b,c},de}";
        String str2 = "xyz";
        String str3 = "x{y,z}";
        pathStr.append(str1);
        pathStr.append(COMMA);
        pathStr.append(str2);
        pathStr.append(COMMA);
        pathStr.append(str3);
        FileInputFormat.setInputPaths(jobConf, pathStr.toString());
        paths = FileInputFormat.getInputPaths(jobConf);
        Assert.assertEquals(3, paths.length);
        Assert.assertEquals(new Path(workingDir, str1).toString(), paths[0].toString());
        Assert.assertEquals(new Path(workingDir, str2).toString(), paths[1].toString());
        Assert.assertEquals(new Path(workingDir, str3).toString(), paths[2].toString());
        pathStr.setLength(0);
        String str4 = "abc";
        String str5 = "pq{r,s}";
        pathStr.append(str4);
        pathStr.append(COMMA);
        pathStr.append(str5);
        FileInputFormat.addInputPaths(jobConf, pathStr.toString());
        paths = FileInputFormat.getInputPaths(jobConf);
        Assert.assertEquals(5, paths.length);
        Assert.assertEquals(new Path(workingDir, str1).toString(), paths[0].toString());
        Assert.assertEquals(new Path(workingDir, str2).toString(), paths[1].toString());
        Assert.assertEquals(new Path(workingDir, str3).toString(), paths[2].toString());
        Assert.assertEquals(new Path(workingDir, str4).toString(), paths[3].toString());
        Assert.assertEquals(new Path(workingDir, str5).toString(), paths[4].toString());
    }
}

