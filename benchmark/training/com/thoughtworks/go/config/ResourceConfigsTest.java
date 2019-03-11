/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config;


import ResourceConfig.VALID_REGEX;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ResourceConfigsTest {
    @Test
    public void shouldTrimResourceNames() {
        ResourceConfigs resourceConfigs = new ResourceConfigs();
        resourceConfigs.add(new ResourceConfig("foo"));
        resourceConfigs.add(new ResourceConfig("foo      "));
        Assert.assertThat(resourceConfigs.size(), Matchers.is(1));
        ResourceConfigs newResourceConfigs = new ResourceConfigs();
        newResourceConfigs.add(new ResourceConfig("foo       "));
        newResourceConfigs.add(new ResourceConfig("foo  "));
        Assert.assertThat(newResourceConfigs.size(), Matchers.is(1));
    }

    @Test
    public void shouldCompareBasedOnSimilarResourceList() {
        ResourceConfigs resourceConfigsA = new ResourceConfigs();
        ResourceConfigs resourceConfigsB = new ResourceConfigs();
        resourceConfigsA.add(new ResourceConfig("xyz"));
        resourceConfigsA.add(new ResourceConfig("aaa"));
        resourceConfigsB.add(new ResourceConfig("xyz"));
        resourceConfigsB.add(new ResourceConfig("aaa"));
        Assert.assertThat(resourceConfigsA.compareTo(resourceConfigsB), Matchers.is(0));
    }

    @Test
    public void shouldCompareBasedOnResourceListItHas() {
        ResourceConfigs resourceConfigsA = new ResourceConfigs();
        ResourceConfigs resourceConfigsB = new ResourceConfigs();
        resourceConfigsA.add(new ResourceConfig("xyz"));
        resourceConfigsA.add(new ResourceConfig("aaa"));
        resourceConfigsB.add(new ResourceConfig("xyz"));
        resourceConfigsB.add(new ResourceConfig("bbb"));
        Assert.assertThat(resourceConfigsA.compareTo(resourceConfigsB), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
        Assert.assertThat(resourceConfigsB.compareTo(resourceConfigsA), Matchers.is(Matchers.greaterThan(0)));
    }

    @Test
    public void shouldUnderstandLesserLengthResourcesAsLesser() {
        ResourceConfigs resourceConfigsA = new ResourceConfigs();
        ResourceConfigs resourceConfigsB = new ResourceConfigs();
        resourceConfigsA.add(new ResourceConfig("xyz"));
        resourceConfigsB.add(new ResourceConfig("xyz"));
        resourceConfigsB.add(new ResourceConfig("zzz"));
        Assert.assertThat(resourceConfigsA.compareTo(resourceConfigsB), Matchers.is(org.hamcrest.Matchers.lessThan(0)));
        Assert.assertThat(resourceConfigsB.compareTo(resourceConfigsA), Matchers.is(Matchers.greaterThan(0)));
    }

    @Test
    public void shouldNotBombIfNoResourcesPresent() {
        Assert.assertThat(new ResourceConfigs(new ResourceConfig("xyz")).compareTo(new ResourceConfigs()), Matchers.is(Matchers.greaterThan(0)));
    }

    @Test
    public void shouldIgnoreCaseNamesOfResources() {
        ResourceConfigs resourceConfigs = new ResourceConfigs();
        resourceConfigs.add(new ResourceConfig("Eoo"));
        resourceConfigs.add(new ResourceConfig("eoo"));
        Assert.assertThat(resourceConfigs.size(), Matchers.is(1));
    }

    @Test
    public void shouldGetAllResourcesNames() {
        ResourceConfigs resourceConfigs = new ResourceConfigs();
        resourceConfigs.add(new ResourceConfig("Eoo"));
        resourceConfigs.add(new ResourceConfig("Poo"));
        List<String> names = new ArrayList<>();
        names.add("Eoo");
        names.add("Poo");
        List<String> resourceNames = resourceConfigs.resourceNames();
        Assert.assertThat(resourceNames, Matchers.is(names));
    }

    @Test
    public void shouldNotAllowBlankNames() {
        ResourceConfigs resourceConfigs = new ResourceConfigs();
        resourceConfigs.add(new ResourceConfig(""));
        Assert.assertThat(resourceConfigs.size(), Matchers.is(0));
        resourceConfigs.add(new ResourceConfig("   "));
        Assert.assertThat(resourceConfigs.size(), Matchers.is(0));
    }

    @Test
    public void shouldNotAddDuplicateResources() {
        ResourceConfigs expected = new ResourceConfigs();
        expected.add(new ResourceConfig("jdk1.4"));
        expected.add(new ResourceConfig("jdk1.5"));
        ResourceConfigs actual = new ResourceConfigs();
        actual.add(new ResourceConfig("jdk1.4"));
        actual.add(new ResourceConfig("jdk1.5"));
        actual.add(new ResourceConfig("Jdk1.5"));
        Assert.assertThat(expected, Matchers.is(actual));
    }

    @Test
    public void shouldHaveNiceConvenienceConstructorThatDoesSomeNiftyParsing() {
        ResourceConfigs actual = new ResourceConfigs("mou, fou");
        Assert.assertThat(actual.toString(), Matchers.is("fou | mou"));
    }

    @Test
    public void shouldNotBeAbleToAddResourceWithWhiteSpaceAsName() {
        ResourceConfigs actual = new ResourceConfigs();
        actual.add(new ResourceConfig(" "));
        Assert.assertThat(actual.size(), Matchers.is(0));
    }

    @Test
    public void shouldReturnSortedResourceNamesAsStringRepresention() {
        ResourceConfigs actual = new ResourceConfigs();
        actual.add(new ResourceConfig("jdk1.4"));
        actual.add(new ResourceConfig("linux"));
        actual.add(new ResourceConfig("gentoo"));
        actual.add(new ResourceConfig("jdk1.5"));
        actual.add(new ResourceConfig("Jdk1.5"));
        Assert.assertThat(actual.size(), Matchers.is(4));
        Assert.assertThat(actual.toString(), Matchers.is("gentoo | jdk1.4 | jdk1.5 | linux"));
    }

    @Test
    public void shouldReturnListOfResoucesAsCommaSeperatedList() {
        ResourceConfigs actual = new ResourceConfigs();
        actual.add(new ResourceConfig("  a  "));
        actual.add(new ResourceConfig("   b"));
        actual.add(new ResourceConfig("c"));
        Assert.assertThat(actual.exportToCsv(), Matchers.is("a, b, c, "));
    }

    @Test
    public void shouldClearAndSetPrimitiveAttributes() {
        ResourceConfigs resourceConfigs = new ResourceConfigs();
        String csv = "a, b,   c,d   ";
        resourceConfigs.add(new ResourceConfig("old_resource"));
        Assert.assertThat(resourceConfigs.size(), Matchers.is(1));
        resourceConfigs.importFromCsv(csv);
        Assert.assertThat(resourceConfigs.size(), Matchers.is(4));
        Assert.assertThat(resourceConfigs.exportToCsv(), Matchers.is("a, b, c, d, "));
    }

    @Test
    public void shouldValidateTree() {
        ResourceConfig resourceConfig1 = new ResourceConfig("a#");
        ResourceConfig resourceConfig2 = new ResourceConfig("b");
        ResourceConfigs resourceConfigs = new ResourceConfigs(resourceConfig1, resourceConfig2);
        resourceConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig()));
        Assert.assertThat(resourceConfig1.errors().size(), Matchers.is(1));
        Assert.assertThat(resourceConfig1.errors().firstError(), Matchers.is(String.format("Resource name 'a#' is not valid. Valid names much match '%s'", VALID_REGEX)));
        Assert.assertThat(resourceConfig2.errors().isEmpty(), Matchers.is(true));
    }
}

