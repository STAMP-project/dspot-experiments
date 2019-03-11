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
package org.apache.hadoop.hdfs.server.namenode;


import TreeWalk.TreeIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Validate randomly generated hierarchies, including fork() support in
 * base class.
 */
public class TestRandomTreeWalk {
    @Rule
    public TestName name = new TestName();

    private Random r = new Random();

    @Test
    public void testRandomTreeWalkRepeat() throws Exception {
        Set<TreePath> ns = new HashSet<>();
        final long seed = r.nextLong();
        RandomTreeWalk t1 = new RandomTreeWalk(seed, 10, 0.1F);
        int i = 0;
        for (TreePath p : t1) {
            p.accept((i++));
            Assert.assertTrue(ns.add(p));
        }
        RandomTreeWalk t2 = new RandomTreeWalk(seed, 10, 0.1F);
        int j = 0;
        for (TreePath p : t2) {
            p.accept((j++));
            Assert.assertTrue(ns.remove(p));
        }
        Assert.assertTrue(ns.isEmpty());
    }

    @Test
    public void testRandomTreeWalkFork() throws Exception {
        Set<FileStatus> ns = new HashSet<>();
        final long seed = r.nextLong();
        RandomTreeWalk t1 = new RandomTreeWalk(seed, 10, 0.15F);
        int i = 0;
        for (TreePath p : t1) {
            p.accept((i++));
            Assert.assertTrue(ns.add(p.getFileStatus()));
        }
        RandomTreeWalk t2 = new RandomTreeWalk(seed, 10, 0.15F);
        int j = 0;
        ArrayList<TreeWalk.TreeIterator> iters = new ArrayList<>();
        iters.add(t2.iterator());
        while (!(iters.isEmpty())) {
            for (TreeWalk.TreeIterator sub = iters.remove(((iters.size()) - 1)); sub.hasNext();) {
                TreePath p = sub.next();
                if (0 == ((r.nextInt()) % 4)) {
                    iters.add(sub.fork());
                    Collections.shuffle(iters, r);
                }
                p.accept((j++));
                Assert.assertTrue(ns.remove(p.getFileStatus()));
            }
        } 
        Assert.assertTrue(ns.isEmpty());
    }

    @Test
    public void testRandomRootWalk() throws Exception {
        Set<FileStatus> ns = new HashSet<>();
        final long seed = r.nextLong();
        Path root = new Path("foo://bar:4344/dingos");
        String sroot = root.toString();
        int nroot = sroot.length();
        RandomTreeWalk t1 = new RandomTreeWalk(root, seed, 10, 0.1F);
        int i = 0;
        for (TreePath p : t1) {
            p.accept((i++));
            FileStatus stat = p.getFileStatus();
            Assert.assertTrue(ns.add(stat));
            Assert.assertEquals(sroot, stat.getPath().toString().substring(0, nroot));
        }
        RandomTreeWalk t2 = new RandomTreeWalk(root, seed, 10, 0.1F);
        int j = 0;
        for (TreePath p : t2) {
            p.accept((j++));
            FileStatus stat = p.getFileStatus();
            Assert.assertTrue(ns.remove(stat));
            Assert.assertEquals(sroot, stat.getPath().toString().substring(0, nroot));
        }
        Assert.assertTrue(ns.isEmpty());
    }
}

