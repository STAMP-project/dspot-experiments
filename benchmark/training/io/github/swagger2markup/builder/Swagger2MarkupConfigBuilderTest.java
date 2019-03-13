/**
 * Copyright 2017 Robert Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.swagger2markup.builder;


import GroupBy.AS_IS;
import GroupBy.TAGS;
import Language.EN;
import Language.RU;
import MarkupLanguage.MARKDOWN;
import OrderBy.CUSTOM;
import OrderBy.NATURAL;
import Swagger2MarkupConfigBuilder.OPERATION_METHOD_NATURAL_ORDERING;
import Swagger2MarkupConfigBuilder.OPERATION_PATH_NATURAL_ORDERING;
import Swagger2MarkupConfigBuilder.PARAMETER_IN_NATURAL_ORDERING;
import Swagger2MarkupConfigBuilder.PARAMETER_NAME_NATURAL_ORDERING;
import Swagger2MarkupProperties.MARKUP_LANGUAGE;
import com.google.common.collect.Ordering;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;


public class Swagger2MarkupConfigBuilderTest {
    @Test
    public void testConfigOfDefaults() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put(MARKUP_LANGUAGE, MARKDOWN.toString());
        configMap.put("swagger2markup.extensions.uniqueId1.customProperty1", "123");
        configMap.put("swagger2markup.extensions.uniqueId1.customProperty2", "123");
        configMap.put("swagger2markup.extensions.uniqueId2.customPropertyList1", "123,456");
        configMap.put("swagger2markup.uniqueId1.customProperty1", "123");
        configMap.put("swagger2markup.uniqueId1.customProperty2", "123");
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder(configMap).build();
        assertThat(config.getAnchorPrefix()).isNull();
        assertThat(config.getDefinitionOrderBy()).isEqualTo(NATURAL);
        assertThat(config.getDefinitionOrdering()).isEqualTo(Ordering.natural());
        assertThat(config.getDefinitionsDocument()).isEqualTo("definitions");
        assertThat(config.isGeneratedExamplesEnabled()).isFalse();
        assertThat(config.isInlineSchemaEnabled()).isEqualTo(true);
        assertThat(config.getInterDocumentCrossReferencesPrefix()).isNull();
        assertThat(config.getMarkupLanguage()).isEqualTo(MARKDOWN);
        assertThat(config.getOperationOrderBy()).isEqualTo(NATURAL);
        assertThat(config.getOperationOrdering()).isNotNull();
        assertThat(config.getOutputLanguage()).isEqualTo(EN);
        assertThat(config.getOverviewDocument()).isEqualTo("overview");
        assertThat(config.getParameterOrderBy()).isEqualTo(NATURAL);
        assertThat(config.getParameterOrdering()).isEqualTo(PARAMETER_IN_NATURAL_ORDERING.compound(PARAMETER_NAME_NATURAL_ORDERING));
        assertThat(config.getPathsDocument()).isEqualTo("paths");
        assertThat(config.getPathsGroupedBy()).isEqualTo(AS_IS);
        assertThat(config.getPropertyOrderBy()).isEqualTo(NATURAL);
        assertThat(config.getPropertyOrdering()).isEqualTo(Ordering.natural());
        assertThat(config.getResponseOrderBy()).isEqualTo(NATURAL);
        assertThat(config.getResponseOrdering()).isEqualTo(Ordering.natural());
        assertThat(config.getSecurityDocument()).isEqualTo("security");
        assertThat(config.getSeparatedDefinitionsFolder()).isEqualTo("definitions");
        assertThat(config.getSeparatedOperationsFolder()).isEqualTo("operations");
        assertThat(config.getTagOrderBy()).isEqualTo(NATURAL);
        assertThat(config.getTagOrdering()).isEqualTo(Ordering.natural());
        assertThat(config.isFlatBodyEnabled()).isFalse();
        assertThat(config.isPathSecuritySectionEnabled()).isTrue();
        assertThat(config.isInterDocumentCrossReferencesEnabled()).isFalse();
        assertThat(config.isSeparatedDefinitionsEnabled()).isFalse();
        assertThat(config.isSeparatedOperationsEnabled()).isFalse();
        assertThat(config.getExtensionsProperties().getKeys()).hasSize(3).containsOnly("uniqueId1.customProperty1", "uniqueId1.customProperty2", "uniqueId2.customPropertyList1");
        assertThat(config.getExtensionsProperties().getString("uniqueId1.customProperty1").get()).isEqualTo("123");
        assertThat(config.getExtensionsProperties().getPathList("uniqueId2.customPropertyList1")).hasSize(1).containsOnly(Paths.get("123,456"));
    }

    @Test
    public void testConfigOfProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(Swagger2MarkupConfigBuilderTest.class.getResourceAsStream("/config/config.properties"));
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder(properties).build();
        assertThat(config.getAnchorPrefix()).isEqualTo("anchorPrefix");
        assertThat(config.getDefinitionOrderBy()).isEqualTo(OrderBy.AS_IS);
        assertThat(config.getDefinitionOrdering()).isNull();
        assertThat(config.getDefinitionsDocument()).isEqualTo("definitionsTest");
        assertThat(config.isGeneratedExamplesEnabled()).isTrue();
        assertThat(config.isInlineSchemaEnabled()).isEqualTo(false);
        assertThat(config.getInterDocumentCrossReferencesPrefix()).isEqualTo("xrefPrefix");
        assertThat(config.getMarkupLanguage()).isEqualTo(MARKDOWN);
        assertThat(config.getOperationOrderBy()).isEqualTo(NATURAL);
        assertThat(config.getOperationOrdering()).isEqualTo(OPERATION_PATH_NATURAL_ORDERING.compound(OPERATION_METHOD_NATURAL_ORDERING));
        assertThat(config.getOutputLanguage()).isEqualTo(RU);
        assertThat(config.getOverviewDocument()).isEqualTo("overviewTest");
        assertThat(config.getParameterOrderBy()).isEqualTo(OrderBy.AS_IS);
        assertThat(config.getParameterOrdering()).isNull();
        assertThat(config.getPathsDocument()).isEqualTo("pathsTest");
        assertThat(config.getPathsGroupedBy()).isEqualTo(TAGS);
        assertThat(config.getPropertyOrderBy()).isEqualTo(OrderBy.AS_IS);
        assertThat(config.getPropertyOrdering()).isNull();
        assertThat(config.getResponseOrderBy()).isEqualTo(OrderBy.AS_IS);
        assertThat(config.getResponseOrdering()).isNull();
        assertThat(config.getSecurityDocument()).isEqualTo("securityTest");
        assertThat(config.getSeparatedDefinitionsFolder()).isEqualTo("definitionsTest");
        assertThat(config.getSeparatedOperationsFolder()).isEqualTo("operationsTest");
        assertThat(config.isListDelimiterEnabled()).isEqualTo(true);
        assertThat(config.getListDelimiter()).isEqualTo(Character.valueOf('|'));
        assertThat(config.getTagOrderBy()).isEqualTo(OrderBy.AS_IS);
        assertThat(config.getTagOrdering()).isNull();
        assertThat(config.isFlatBodyEnabled()).isTrue();
        assertThat(config.isPathSecuritySectionEnabled()).isFalse();
        assertThat(config.isInterDocumentCrossReferencesEnabled()).isTrue();
        assertThat(config.isSeparatedDefinitionsEnabled()).isTrue();
        assertThat(config.isSeparatedOperationsEnabled()).isTrue();
        assertThat(config.getExtensionsProperties().getKeys()).hasSize(5).containsOnly("uniqueId1.customProperty1", "uniqueId1.customProperty2", "uniqueId2.customProperty1", "uniqueId2.customProperty2", "uniqueId2.customPropertyList1");
        assertThat(config.getExtensionsProperties().getPathList("uniqueId2.customPropertyList1")).hasSize(2).containsOnly(Paths.get("123"), Paths.get("456"));
    }

    @Test
    public void testConfigBuilder() {
        Swagger2MarkupConfigBuilder builder = new Swagger2MarkupConfigBuilder();
        try {
            builder.withTagOrdering(CUSTOM);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("You must provide a custom comparator if orderBy == OrderBy.CUSTOM");
        }
        builder.withTagOrdering(Ordering.<String>natural());
        assertThat(builder.config.getTagOrderBy()).isEqualTo(CUSTOM);
        assertThat(builder.config.getTagOrdering()).isEqualTo(Ordering.natural());
        try {
            builder.withOperationOrdering(CUSTOM);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("You must provide a custom comparator if orderBy == OrderBy.CUSTOM");
        }
        builder.withOperationOrdering(OPERATION_PATH_NATURAL_ORDERING);
        assertThat(builder.config.getOperationOrderBy()).isEqualTo(CUSTOM);
        assertThat(builder.config.getOperationOrdering()).isEqualTo(OPERATION_PATH_NATURAL_ORDERING);
        try {
            builder.withDefinitionOrdering(CUSTOM);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("You must provide a custom comparator if orderBy == OrderBy.CUSTOM");
        }
        builder.withDefinitionOrdering(Ordering.<String>natural());
        assertThat(builder.config.getDefinitionOrderBy()).isEqualTo(CUSTOM);
        assertThat(builder.config.getDefinitionOrdering()).isEqualTo(Ordering.natural());
        try {
            builder.withParameterOrdering(CUSTOM);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("You must provide a custom comparator if orderBy == OrderBy.CUSTOM");
        }
        builder.withParameterOrdering(PARAMETER_NAME_NATURAL_ORDERING);
        assertThat(builder.config.getParameterOrderBy()).isEqualTo(CUSTOM);
        assertThat(builder.config.getParameterOrdering()).isEqualTo(PARAMETER_NAME_NATURAL_ORDERING);
        try {
            builder.withPropertyOrdering(CUSTOM);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("You must provide a custom comparator if orderBy == OrderBy.CUSTOM");
        }
        builder.withPropertyOrdering(Ordering.<String>natural());
        assertThat(builder.config.getPropertyOrderBy()).isEqualTo(CUSTOM);
        assertThat(builder.config.getPropertyOrdering()).isEqualTo(Ordering.natural());
        try {
            builder.withResponseOrdering(CUSTOM);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("You must provide a custom comparator if orderBy == OrderBy.CUSTOM");
        }
        builder.withResponseOrdering(Ordering.<String>natural());
        assertThat(builder.config.getResponseOrderBy()).isEqualTo(CUSTOM);
        assertThat(builder.config.getResponseOrdering()).isEqualTo(Ordering.natural());
        assertThat(builder.config.isListDelimiterEnabled()).isEqualTo(false);
        builder.withListDelimiter();
        assertThat(builder.config.getListDelimiter()).isEqualTo(Character.valueOf(','));
        assertThat(builder.config.isListDelimiterEnabled()).isEqualTo(true);
    }

    @Test
    public void testConfigBuilderListDelimiter() {
        Swagger2MarkupConfigBuilder builder = new Swagger2MarkupConfigBuilder();
        assertThat(builder.config.isListDelimiterEnabled()).isEqualTo(false);
        builder.withListDelimiter(Character.valueOf('|'));
        assertThat(builder.config.getListDelimiter()).isEqualTo(Character.valueOf('|'));
        assertThat(builder.config.isListDelimiterEnabled()).isEqualTo(true);
    }
}

