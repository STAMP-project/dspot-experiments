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
package org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer;


import java.net.URISyntaxException;
import java.util.Random;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.junit.Assert;
import org.junit.Test;


public class TestLocalResource {
    @Test
    public void testResourceEquality() throws URISyntaxException {
        Random r = new Random();
        long seed = r.nextLong();
        r.setSeed(seed);
        System.out.println(("SEED: " + seed));
        long basetime = (r.nextLong()) >>> 2;
        LocalResource yA = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), basetime, FILE, PUBLIC, null);
        LocalResource yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), basetime, FILE, PUBLIC, null);
        final LocalResourceRequest a = new LocalResourceRequest(yA);
        LocalResourceRequest b = new LocalResourceRequest(yA);
        TestLocalResource.checkEqual(a, b);
        b = new LocalResourceRequest(yB);
        TestLocalResource.checkEqual(a, b);
        // ignore visibility
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), basetime, FILE, PRIVATE, null);
        b = new LocalResourceRequest(yB);
        TestLocalResource.checkEqual(a, b);
        // ignore size
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), 0, basetime, FILE, PRIVATE, null);
        b = new LocalResourceRequest(yB);
        TestLocalResource.checkEqual(a, b);
        // note path
        yB = TestLocalResource.getYarnResource(new Path("hdfs://dingo.org:80/foobar"), 0, basetime, ARCHIVE, PUBLIC, null);
        b = new LocalResourceRequest(yB);
        TestLocalResource.checkNotEqual(a, b);
        // note type
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), 0, basetime, ARCHIVE, PUBLIC, null);
        b = new LocalResourceRequest(yB);
        TestLocalResource.checkNotEqual(a, b);
        // note timestamp
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), 0, (basetime + 1), FILE, PUBLIC, null);
        b = new LocalResourceRequest(yB);
        TestLocalResource.checkNotEqual(a, b);
        // note pattern
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), 0, (basetime + 1), FILE, PUBLIC, "^/foo/.*");
        b = new LocalResourceRequest(yB);
        TestLocalResource.checkNotEqual(a, b);
    }

    @Test
    public void testResourceOrder() throws URISyntaxException {
        Random r = new Random();
        long seed = r.nextLong();
        r.setSeed(seed);
        System.out.println(("SEED: " + seed));
        long basetime = (r.nextLong()) >>> 2;
        LocalResource yA = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), basetime, FILE, PUBLIC, "^/foo/.*");
        final LocalResourceRequest a = new LocalResourceRequest(yA);
        // Path primary
        LocalResource yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobaz"), (-1), basetime, FILE, PUBLIC, "^/foo/.*");
        LocalResourceRequest b = new LocalResourceRequest(yB);
        Assert.assertTrue((0 > (a.compareTo(b))));
        // timestamp secondary
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), (basetime + 1), FILE, PUBLIC, "^/foo/.*");
        b = new LocalResourceRequest(yB);
        Assert.assertTrue((0 > (a.compareTo(b))));
        // type tertiary
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), basetime, ARCHIVE, PUBLIC, "^/foo/.*");
        b = new LocalResourceRequest(yB);
        Assert.assertTrue((0 != (a.compareTo(b))));// don't care about order, just ne

        // path 4th
        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), basetime, ARCHIVE, PUBLIC, "^/food/.*");
        b = new LocalResourceRequest(yB);
        Assert.assertTrue((0 != (a.compareTo(b))));// don't care about order, just ne

        yB = TestLocalResource.getYarnResource(new Path("http://yak.org:80/foobar"), (-1), basetime, ARCHIVE, PUBLIC, null);
        b = new LocalResourceRequest(yB);
        Assert.assertTrue((0 != (a.compareTo(b))));// don't care about order, just ne

    }
}

