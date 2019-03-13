/**
 * Copyright ? 2010-2017 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsonschema2pojo.rules;


import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DynamicPropertiesRuleTest {
    JCodeModel codeModel = new JCodeModel();

    RuleFactory factory;

    DynamicPropertiesRule rule;

    JDefinedClass type;

    JMethod numberGetter;

    JMethod numberSetter;

    JDefinedClass type2;

    @Test
    public void shouldAddNotFoundField() {
        JFieldRef var = rule.getOrAddNotFoundVar(type);
        MatcherAssert.assertThat(var, Matchers.notNullValue());
    }
}

