/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.test.test;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.carrotsearch.randomizedtesting.annotations.Repeat;
import java.io.IOException;
import org.elasticsearch.common.SuppressForbidden;
import org.elasticsearch.test.ESIntegTestCase;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * This test ensures that the cluster initializion for suite scope is not influencing
 * the tests random sequence due to initializtion using the same random instance.
 */
@ClusterScope(scope = Scope.SUITE)
public class SuiteScopeClusterIT extends ESIntegTestCase {
    private static int ITER = 0;

    private static long[] SEQUENCE = new long[100];

    private static Long CLUSTER_SEED = null;

    @Test
    @SuppressForbidden(reason = "repeat is a feature here")
    @Repeat(iterations = 10, useConstantSeed = true)
    public void testReproducible() throws IOException {
        if (((SuiteScopeClusterIT.ITER)++) == 0) {
            SuiteScopeClusterIT.CLUSTER_SEED = cluster().seed();
            for (int i = 0; i < (SuiteScopeClusterIT.SEQUENCE.length); i++) {
                SuiteScopeClusterIT.SEQUENCE[i] = randomLong();
            }
        } else {
            assertEquals(SuiteScopeClusterIT.CLUSTER_SEED, Long.valueOf(cluster().seed()));
            for (int i = 0; i < (SuiteScopeClusterIT.SEQUENCE.length); i++) {
                assertThat(SuiteScopeClusterIT.SEQUENCE[i], Matchers.equalTo(randomLong()));
            }
        }
    }
}

