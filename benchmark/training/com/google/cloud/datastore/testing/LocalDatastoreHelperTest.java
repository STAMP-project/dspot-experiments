/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore.testing;


import com.google.cloud.NoCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.threeten.bp.Duration;


@RunWith(JUnit4.class)
public class LocalDatastoreHelperTest {
    private static final double TOLERANCE = 1.0E-5;

    private static final String PROJECT_ID_PREFIX = "test-project-";

    private static final String NAMESPACE = "namespace";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreate() {
        LocalDatastoreHelper helper = LocalDatastoreHelper.create(0.75);
        Assert.assertTrue(((Math.abs((0.75 - (helper.getConsistency())))) < (LocalDatastoreHelperTest.TOLERANCE)));
        Assert.assertTrue(helper.getProjectId().startsWith(LocalDatastoreHelperTest.PROJECT_ID_PREFIX));
        helper = LocalDatastoreHelper.create();
        Assert.assertTrue(((Math.abs((0.9 - (helper.getConsistency())))) < (LocalDatastoreHelperTest.TOLERANCE)));
        Assert.assertTrue(helper.getProjectId().startsWith(LocalDatastoreHelperTest.PROJECT_ID_PREFIX));
    }

    @Test
    public void testOptions() {
        LocalDatastoreHelper helper = LocalDatastoreHelper.create();
        DatastoreOptions options = helper.getOptions();
        Assert.assertTrue(options.getProjectId().startsWith(LocalDatastoreHelperTest.PROJECT_ID_PREFIX));
        Assert.assertTrue(options.getHost().startsWith("localhost:"));
        Assert.assertSame(NoCredentials.getInstance(), options.getCredentials());
        options = helper.getOptions(LocalDatastoreHelperTest.NAMESPACE);
        Assert.assertTrue(options.getProjectId().startsWith(LocalDatastoreHelperTest.PROJECT_ID_PREFIX));
        Assert.assertTrue(options.getHost().startsWith("localhost:"));
        Assert.assertSame(NoCredentials.getInstance(), options.getCredentials());
        Assert.assertEquals(LocalDatastoreHelperTest.NAMESPACE, options.getNamespace());
    }

    @Test
    public void testStartStopReset() throws IOException, InterruptedException, TimeoutException {
        LocalDatastoreHelper helper = LocalDatastoreHelper.create();
        helper.start();
        Datastore datastore = helper.getOptions().getService();
        Key key = datastore.newKeyFactory().setKind("kind").newKey("name");
        datastore.put(Entity.newBuilder(key).build());
        Assert.assertNotNull(datastore.get(key));
        helper.reset();
        Assert.assertNull(datastore.get(key));
        helper.stop(Duration.ofMinutes(1));
        thrown.expect(DatastoreException.class);
        datastore.get(key);
    }
}

