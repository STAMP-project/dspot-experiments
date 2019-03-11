/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.parsing.antlr.rule.jaxb.loader;


import DatabaseType.MySQL;
import RuleDefinitionFileConstant.SHARDING_ROOT_PATH;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class RuleDefinitionFileConstantTest {
    @Test
    public void assertGetSQLStatementRuleDefinitionFileName() {
        Assert.assertThat(RuleDefinitionFileConstant.getSQLStatementRuleDefinitionFileName(SHARDING_ROOT_PATH, MySQL), CoreMatchers.is("META-INF/parsing-rule-definition/sharding/mysql/sql-statement-rule-definition.xml"));
    }

    @Test
    public void assertGetExtractorRuleDefinitionFileName() {
        Assert.assertThat(RuleDefinitionFileConstant.getExtractorRuleDefinitionFileName(SHARDING_ROOT_PATH, MySQL), CoreMatchers.is("META-INF/parsing-rule-definition/sharding/mysql/extractor-rule-definition.xml"));
    }

    @Test
    public void assertGetCommonExtractorRuleDefinitionFileName() {
        Assert.assertThat(RuleDefinitionFileConstant.getCommonExtractorRuleDefinitionFileName(), CoreMatchers.is("META-INF/parsing-rule-definition/common/extractor-rule-definition.xml"));
    }

    @Test
    public void assertGetFillerRuleDefinitionFileName() {
        Assert.assertThat(RuleDefinitionFileConstant.getCommonFillerRuleDefinitionFileName(), CoreMatchers.is("META-INF/parsing-rule-definition/common/filler-rule-definition.xml"));
    }
}

