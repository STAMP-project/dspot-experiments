/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.query.internal;


import junitparams.JUnitParamsRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class AbstractCompiledValueTestJUnitTest {
    @Test
    public void whenLeafIsIdentifierAtTheLeafThenHasIdentifierAtLeafMustReturnTrue() {
        CompiledValue compiledValue1 = new CompiledID("testString");
        CompiledValue compiledValue2 = new CompiledID("testString");
        CompiledIndexOperation compiledIndexOperation = new CompiledIndexOperation(compiledValue1, compiledValue2);
        CompiledPath compiledPath = new CompiledPath(compiledIndexOperation, "test");
        Assert.assertTrue(compiledPath.hasIdentifierAtLeafNode());
    }

    @Test
    public void whenLeafIsNotIndentifierThenHasIdentifierAtLeafMustReturnFalse() {
        CompiledValue compiledValue1 = new CompiledBindArgument(1);
        CompiledValue compiledValue2 = new CompiledBindArgument(1);
        CompiledIndexOperation compiledIndexOperation = new CompiledIndexOperation(compiledValue1, compiledValue2);
        CompiledPath compiledPath = new CompiledPath(compiledIndexOperation, "test");
        Assert.assertFalse(compiledPath.hasIdentifierAtLeafNode());
    }
}

