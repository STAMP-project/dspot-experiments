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


import Tab.NAME;
import Tab.PATH;
import java.util.ArrayList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TabTest {
    @Test
    public void shouldErrorOutWhenTabNameIsNotUnique() {
        Tab tab = new Tab("foo", "bar");
        ArrayList<Tab> visitedTabs = new ArrayList<>();
        Tab existingTab = new Tab("foo", "bar");
        visitedTabs.add(existingTab);
        tab.validateTabNameUniqueness(visitedTabs);
        Assert.assertThat(tab.errors().on(NAME), Matchers.is("Tab name 'foo' is not unique."));
        Assert.assertThat(visitedTabs.get(0).errors().on(NAME), Matchers.is("Tab name 'foo' is not unique."));
        Assert.assertThat(visitedTabs.size(), Matchers.is(1));
    }

    @Test
    public void shouldErrorOutWhenTabNameDoesNotConformToTheRequiredPattern() {
        Tab tab = new Tab("bar*&$", "some_path");
        tab.validateTabNamePathCorrectness();
        Assert.assertThat(tab.errors().on(NAME), Matchers.is("Tab name 'bar*&$' is invalid. This must be alphanumeric and can contain underscores and periods."));
    }

    @Test
    public void shouldErrorOutWhenTabPAthDoesNotConformToTheRequiredPattern() {
        Tab tab = new Tab("foo", "some path");
        tab.validateTabNamePathCorrectness();
        Assert.assertThat(tab.errors().on(PATH), Matchers.is("Tab path 'some path' is invalid. This must be a valid file path."));
    }

    @Test
    public void shouldNotErrorOutWhenNamesAreOfDifferentCase() {
        Tab tab = new Tab("foO", "bar");
        ArrayList<Tab> visitedTabs = new ArrayList<>();
        Tab existingTab = new Tab("foo", "bar");
        visitedTabs.add(existingTab);
        tab.validateTabNameUniqueness(visitedTabs);
        Assert.assertThat(visitedTabs.size(), Matchers.is(2));
    }

    @Test
    public void shouldAddToListWhenNoErrorIsEncountered() {
        Tab tab = new Tab("foo1", "bar");
        ArrayList<Tab> visitedTabs = new ArrayList<>();
        Tab existingTab = new Tab("foo0", "bar");
        visitedTabs.add(existingTab);
        tab.validateTabNameUniqueness(visitedTabs);
        Assert.assertThat(visitedTabs.size(), Matchers.is(2));
    }

    @Test
    public void shouldErrorOutWhenTabNameLengthExceeds15Characters() {
        Tab tab = new Tab("fooasdklfjasklfjsdklafjklsdajfklsdajfklsdajklfjsdaklf", "bar");
        tab.validateTabNameSize();
        Assert.assertThat(tab.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(tab.errors().on(NAME), Matchers.is("Tab name should not exceed 15 characters"));
    }

    @Test
    public void shouldErrorOutWhenTabNameHasASpaceInIt() {
        Tab tab = new Tab("foo bar", "bite/me");
        tab.validate(null);
        Assert.assertThat(tab.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(tab.errors().on(NAME), Matchers.is("Tab name 'foo bar' is invalid. This must be alphanumeric and can contain underscores and periods."));
    }

    @Test
    public void shouldAddErrorToTheErroneousTabIfNameExceeds15Characters() {
        Tab tab = new Tab("sjadfklsdjaklfsdjaklfjsdklajfklsdajfklsdakf", "path1");
        tab.validate(null);
        Assert.assertThat(tab.errors().on(NAME), Matchers.is("Tab name should not exceed 15 characters"));
    }

    @Test
    public void shouldAddErrorToTabsWithIncorrectTabNameOrPath() {
        Tab tab1 = new Tab("tab&*", "path1");
        tab1.validate(null);
        Assert.assertThat(tab1.errors().on(NAME), Matchers.is("Tab name 'tab&*' is invalid. This must be alphanumeric and can contain underscores and periods."));
        Tab tab2 = new Tab("tab1", "path 2");
        tab2.validate(null);
        Assert.assertThat(tab2.errors().on(PATH), Matchers.is("Tab path 'path 2' is invalid. This must be a valid file path."));
    }
}

