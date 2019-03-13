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
package org.apache.shardingsphere.core.parsing.antlr.rule.jaxb.segment;


import org.apache.shardingsphere.core.parsing.antlr.rule.jaxb.entity.extractor.ExtractorRuleDefinitionEntity;
import org.apache.shardingsphere.core.parsing.antlr.rule.jaxb.loader.extractor.ExtractorRuleDefinitionEntityLoader;
import org.junit.Assert;
import org.junit.Test;


public final class ExtractorRuleDefinitionEntityLoaderTest {
    @Test
    public void assertLoadForCommon() {
        ExtractorRuleDefinitionEntity actual = new ExtractorRuleDefinitionEntityLoader().load("META-INF/parsing-rule-definition/common/extractor-rule-definition.xml");
        Assert.assertFalse(actual.getRules().isEmpty());
    }

    @Test
    public void assertLoadForMySQL() {
        ExtractorRuleDefinitionEntity actual = new ExtractorRuleDefinitionEntityLoader().load("META-INF/parsing-rule-definition/sharding/mysql/extractor-rule-definition.xml");
        Assert.assertFalse(actual.getRules().isEmpty());
    }

    @Test
    public void assertLoadForPostgreSQL() {
        ExtractorRuleDefinitionEntity actual = new ExtractorRuleDefinitionEntityLoader().load("META-INF/parsing-rule-definition/sharding/postgresql/extractor-rule-definition.xml");
        Assert.assertFalse(actual.getRules().isEmpty());
    }

    @Test
    public void assertLoadForOracle() {
        ExtractorRuleDefinitionEntity actual = new ExtractorRuleDefinitionEntityLoader().load("META-INF/parsing-rule-definition/sharding/oracle/extractor-rule-definition.xml");
        Assert.assertFalse(actual.getRules().isEmpty());
    }

    @Test
    public void assertLoadForSQLServer() {
        ExtractorRuleDefinitionEntity actual = new ExtractorRuleDefinitionEntityLoader().load("META-INF/parsing-rule-definition/sharding/sqlserver/extractor-rule-definition.xml");
        Assert.assertFalse(actual.getRules().isEmpty());
    }
}

