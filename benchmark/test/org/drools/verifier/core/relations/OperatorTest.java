/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core.relations;


import Operator.AFTER;
import Operator.BEFORE;
import Operator.COINCIDES;
import Operator.EQUALS;
import Operator.GREATER_OR_EQUAL;
import Operator.GREATER_THAN;
import Operator.IN;
import Operator.LESS_OR_EQUAL;
import Operator.LESS_THAN;
import Operator.MATCHES;
import Operator.NOT_EQUALS;
import Operator.NOT_IN;
import Operator.SOUNDSLIKE;
import org.junit.Assert;
import org.junit.Test;


public class OperatorTest {
    @Test
    public void testOperators() throws Exception {
        Assert.assertEquals(EQUALS, Operator.resolve("=="));
        Assert.assertEquals(GREATER_THAN, Operator.resolve(">"));
        Assert.assertEquals(LESS_THAN, Operator.resolve("<"));
        Assert.assertEquals(GREATER_OR_EQUAL, Operator.resolve(">="));
        Assert.assertEquals(LESS_OR_EQUAL, Operator.resolve("<="));
        Assert.assertEquals(NOT_EQUALS, Operator.resolve("!="));
        Assert.assertEquals(IN, Operator.resolve("in"));
        Assert.assertEquals(NOT_IN, Operator.resolve("not in"));
        Assert.assertEquals(AFTER, Operator.resolve("after"));
        Assert.assertEquals(BEFORE, Operator.resolve("before"));
        Assert.assertEquals(COINCIDES, Operator.resolve("coincides"));
        Assert.assertEquals(MATCHES, Operator.resolve("matches"));
        Assert.assertEquals(SOUNDSLIKE, Operator.resolve("soundslike"));
    }
}

