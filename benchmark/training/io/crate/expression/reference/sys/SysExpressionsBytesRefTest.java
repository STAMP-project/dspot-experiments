/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.expression.reference.sys;


import io.crate.expression.NestableInput;
import io.crate.expression.reference.NestedObjectExpression;
import io.crate.test.integration.CrateUnitTest;
import java.util.Map;
import org.apache.lucene.util.BytesRef;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SysExpressionsBytesRefTest extends CrateUnitTest {
    static class BytesRefNullSysExpression implements NestableInput<BytesRef> {
        @Override
        public BytesRef value() {
            return null;
        }
    }

    static class NullFieldSysObjectReference extends NestedObjectExpression {
        protected NullFieldSysObjectReference() {
            childImplementations.put("n", new SysExpressionsBytesRefTest.BytesRefNullSysExpression());
        }
    }

    static class NullSysObjectArrayReference extends SysStaticObjectArrayReference {
        protected NullSysObjectArrayReference() {
            childImplementations.add(new SysExpressionsBytesRefTest.NullFieldSysObjectReference());
        }
    }

    @Test
    public void testSysObjectReferenceNull() throws Exception {
        SysExpressionsBytesRefTest.NullFieldSysObjectReference nullRef = new SysExpressionsBytesRefTest.NullFieldSysObjectReference();
        NestableInput n = getChild("n");
        assertThat(n, Matchers.instanceOf(SysExpressionsBytesRefTest.BytesRefNullSysExpression.class));
        Map<String, Object> value = value();
        assertThat(value.size(), Matchers.is(1));
        assertThat(value, Matchers.hasKey("n"));
        assertThat(value.get("n"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testSysObjectArrayReferenceNull() throws Exception {
        SysExpressionsBytesRefTest.NullSysObjectArrayReference nullArrayRef = new SysExpressionsBytesRefTest.NullSysObjectArrayReference();
        Object[] values = nullArrayRef.value();
        assertThat(values.length, Matchers.is(1));
        assertThat(values[0], Matchers.instanceOf(Map.class));
        Map<String, Object> mapValue = ((Map<String, Object>) (values[0]));
        assertThat(mapValue.size(), Matchers.is(1));
        assertThat(mapValue, Matchers.hasKey("n"));
        assertThat(mapValue.get("n"), Matchers.is(Matchers.nullValue()));
    }
}

