/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.configurationprocessor;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.metadata.ConfigurationMetadata;
import org.springframework.boot.configurationprocessor.metadata.ItemDeprecation;
import org.springframework.boot.configurationprocessor.metadata.ItemHint;
import org.springframework.boot.configurationprocessor.metadata.ItemMetadata;
import org.springframework.boot.configurationprocessor.metadata.Metadata;
import org.springframework.boot.configurationsample.simple.DeprecatedSingleProperty;
import org.springframework.boot.configurationsample.simple.SimpleProperties;
import org.springframework.boot.configurationsample.specific.SimpleConflictingProperties;
import org.springframework.util.FileCopyUtils;


/**
 * Metadata generation tests for merging additional metadata.
 *
 * @author Stephane Nicoll
 */
public class MergeMetadataGenerationTests extends AbstractMetadataGenerationTests {
    @Test
    public void mergingOfAdditionalProperty() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty(null, "foo", "java.lang.String", MergeMetadataGenerationTests.AdditionalMetadata.class.getName(), null, null, null, null);
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.comparator"));
        assertThat(metadata).has(Metadata.withProperty("foo", String.class).fromSource(MergeMetadataGenerationTests.AdditionalMetadata.class));
    }

    @Test
    public void mergingOfAdditionalPropertyMatchingGroup() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty(null, "simple", "java.lang.String", null, null, null, null, null);
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withGroup("simple").fromSource(SimpleProperties.class));
        assertThat(metadata).has(Metadata.withProperty("simple", String.class));
    }

    @Test
    public void mergeExistingPropertyDefaultValue() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty("simple", "flag", null, null, null, null, true, null);
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.flag", Boolean.class).fromSource(SimpleProperties.class).withDescription("A simple flag.").withDeprecation(null, null).withDefaultValue(true));
        assertThat(metadata.getItems()).hasSize(4);
    }

    @Test
    public void mergeExistingPropertyWithSeveralCandidates() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty("simple", "flag", Boolean.class.getName(), null, null, null, true, null);
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(SimpleProperties.class, SimpleConflictingProperties.class);
        assertThat(metadata.getItems()).hasSize(6);
        List<ItemMetadata> items = metadata.getItems().stream().filter(( item) -> item.getName().equals("simple.flag")).collect(Collectors.toList());
        assertThat(items).hasSize(2);
        ItemMetadata matchingProperty = items.stream().filter(( item) -> item.getType().equals(.class.getName())).findFirst().orElse(null);
        assertThat(matchingProperty).isNotNull();
        assertThat(matchingProperty.getDefaultValue()).isEqualTo(true);
        assertThat(matchingProperty.getSourceType()).isEqualTo(SimpleProperties.class.getName());
        assertThat(matchingProperty.getDescription()).isEqualTo("A simple flag.");
        ItemMetadata nonMatchingProperty = items.stream().filter(( item) -> item.getType().equals(.class.getName())).findFirst().orElse(null);
        assertThat(nonMatchingProperty).isNotNull();
        assertThat(nonMatchingProperty.getDefaultValue()).isEqualTo("hello");
        assertThat(nonMatchingProperty.getSourceType()).isEqualTo(SimpleConflictingProperties.class.getName());
        assertThat(nonMatchingProperty.getDescription()).isNull();
    }

    @Test
    public void mergeExistingPropertyDescription() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty("simple", "comparator", null, null, null, "A nice comparator.", null, null);
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.comparator", "java.util.Comparator<?>").fromSource(SimpleProperties.class).withDescription("A nice comparator."));
        assertThat(metadata.getItems()).hasSize(4);
    }

    @Test
    public void mergeExistingPropertyDeprecation() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty("simple", "comparator", null, null, null, null, null, new ItemDeprecation("Don't use this.", "simple.complex-comparator", "error"));
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.comparator", "java.util.Comparator<?>").fromSource(SimpleProperties.class).withDeprecation("Don't use this.", "simple.complex-comparator", "error"));
        assertThat(metadata.getItems()).hasSize(4);
    }

    @Test
    public void mergeExistingPropertyDeprecationOverride() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty("singledeprecated", "name", null, null, null, null, null, new ItemDeprecation("Don't use this.", "single.name"));
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(DeprecatedSingleProperty.class);
        assertThat(metadata).has(Metadata.withProperty("singledeprecated.name", String.class.getName()).fromSource(DeprecatedSingleProperty.class).withDeprecation("Don't use this.", "single.name"));
        assertThat(metadata.getItems()).hasSize(3);
    }

    @Test
    public void mergeExistingPropertyDeprecationOverrideLevel() throws Exception {
        ItemMetadata property = ItemMetadata.newProperty("singledeprecated", "name", null, null, null, null, null, new ItemDeprecation(null, null, "error"));
        writeAdditionalMetadata(property);
        ConfigurationMetadata metadata = compile(DeprecatedSingleProperty.class);
        assertThat(metadata).has(Metadata.withProperty("singledeprecated.name", String.class.getName()).fromSource(DeprecatedSingleProperty.class).withDeprecation("renamed", "singledeprecated.new-name", "error"));
        assertThat(metadata.getItems()).hasSize(3);
    }

    @Test
    public void mergeOfInvalidAdditionalMetadata() throws IOException {
        File additionalMetadataFile = createAdditionalMetadataFile();
        FileCopyUtils.copy("Hello World", new FileWriter(additionalMetadataFile));
        assertThatIllegalStateException().isThrownBy(() -> compile(.class)).withMessage("Compilation failed");
    }

    @Test
    public void mergingOfSimpleHint() throws Exception {
        writeAdditionalHints(ItemHint.newHint("simple.the-name", new ItemHint.ValueHint("boot", "Bla bla"), new ItemHint.ValueHint("spring", null)));
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.the-name", String.class).fromSource(SimpleProperties.class).withDescription("The name of this simple properties.").withDefaultValue("boot").withDeprecation(null, null));
        assertThat(metadata).has(Metadata.withHint("simple.the-name").withValue(0, "boot", "Bla bla").withValue(1, "spring", null));
    }

    @Test
    public void mergingOfHintWithNonCanonicalName() throws Exception {
        writeAdditionalHints(ItemHint.newHint("simple.theName", new ItemHint.ValueHint("boot", "Bla bla")));
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.the-name", String.class).fromSource(SimpleProperties.class).withDescription("The name of this simple properties.").withDefaultValue("boot").withDeprecation(null, null));
        assertThat(metadata).has(Metadata.withHint("simple.the-name").withValue(0, "boot", "Bla bla"));
    }

    @Test
    public void mergingOfHintWithProvider() throws Exception {
        writeAdditionalHints(new ItemHint("simple.theName", Collections.emptyList(), Arrays.asList(new ItemHint.ValueProvider("first", Collections.singletonMap("target", "org.foo")), new ItemHint.ValueProvider("second", null))));
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.the-name", String.class).fromSource(SimpleProperties.class).withDescription("The name of this simple properties.").withDefaultValue("boot").withDeprecation(null, null));
        assertThat(metadata).has(Metadata.withHint("simple.the-name").withProvider("first", "target", "org.foo").withProvider("second"));
    }

    @Test
    public void mergingOfAdditionalDeprecation() throws Exception {
        writePropertyDeprecation(ItemMetadata.newProperty("simple", "wrongName", "java.lang.String", null, null, null, null, new ItemDeprecation("Lame name.", "simple.the-name")));
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.wrong-name", String.class).withDeprecation("Lame name.", "simple.the-name"));
    }

    @Test
    public void mergingOfAdditionalMetadata() throws Exception {
        File metaInfFolder = new File(getCompiler().getOutputLocation(), "META-INF");
        metaInfFolder.mkdirs();
        File additionalMetadataFile = new File(metaInfFolder, "additional-spring-configuration-metadata.json");
        additionalMetadataFile.createNewFile();
        JSONObject property = new JSONObject();
        property.put("name", "foo");
        property.put("type", "java.lang.String");
        property.put("sourceType", MergeMetadataGenerationTests.AdditionalMetadata.class.getName());
        JSONArray properties = new JSONArray();
        properties.put(property);
        JSONObject additionalMetadata = new JSONObject();
        additionalMetadata.put("properties", properties);
        FileWriter writer = new FileWriter(additionalMetadataFile);
        writer.append(additionalMetadata.toString(2));
        writer.flush();
        writer.close();
        ConfigurationMetadata metadata = compile(SimpleProperties.class);
        assertThat(metadata).has(Metadata.withProperty("simple.comparator"));
        assertThat(metadata).has(Metadata.withProperty("foo", String.class).fromSource(MergeMetadataGenerationTests.AdditionalMetadata.class));
    }

    private static class AdditionalMetadata {}
}

