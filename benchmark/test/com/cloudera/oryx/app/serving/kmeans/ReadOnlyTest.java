/**
 * Copyright (c) 2015, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.app.serving.kmeans;


import Response.Status.FORBIDDEN;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.typesafe.config.Config;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


public final class ReadOnlyTest extends AbstractKMeansServingTest {
    @Test
    public void testNoAdd() {
        try (Response response = target("/add").request().post(Entity.text(AddTest.ADD_DATA))) {
            Assert.assertEquals(FORBIDDEN.getStatusCode(), response.getStatus());
        }
    }

    public static final class ReadOnlyMockManagerInitListener extends AbstractKMeansServingTest.MockManagerInitListener {
        @Override
        protected AbstractKMeansServingTest.MockServingModelManager getModelManager() {
            return new ReadOnlyTest.ReadOnlyMockServingModelManager(ConfigUtils.getDefault());
        }
    }

    static final class ReadOnlyMockServingModelManager extends AbstractKMeansServingTest.MockServingModelManager {
        ReadOnlyMockServingModelManager(Config config) {
            super(config);
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }
    }
}

