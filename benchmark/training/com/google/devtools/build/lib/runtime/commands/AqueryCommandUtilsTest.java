/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.runtime.commands;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.query2.engine.QueryEnvironment.QueryFunction;
import com.google.devtools.build.lib.query2.engine.QueryException;
import com.google.devtools.build.lib.query2.engine.QueryExpression;
import com.google.devtools.build.lib.query2.engine.QueryParser;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link AqueryCommandUtils}.
 */
@RunWith(JUnit4.class)
public class AqueryCommandUtilsTest {
    private ImmutableMap<String, QueryFunction> functions;

    @Test
    public void testAqueryCommandGetTopLevelTargets_skyframeState_targetLabelSpecified() throws Exception {
        String query = "//some_target";
        QueryExpression expr = QueryParser.parse(query, functions);
        MoreAsserts.assertThrows(QueryException.class, () -> /* universeScope= */
        /* queryCurrentSkyframeState= */
        AqueryCommandUtils.getTopLevelTargets(ImmutableList.of(), expr, true, query));
    }
}

