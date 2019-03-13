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
package org.springframework.boot.context.properties.bind;


import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.support.TestPropertySourceUtils;

import static org.springframework.boot.context.properties.bind.BinderTests.ExampleEnum.BAR_BAZ;
import static org.springframework.boot.context.properties.bind.BinderTests.ExampleEnum.FOO_BAR;


/**
 * Tests for {@link CollectionBinder}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class CollectionBinderTests {
    private static final Bindable<List<Integer>> INTEGER_LIST = Bindable.listOf(Integer.class);

    private static final Bindable<List<String>> STRING_LIST = Bindable.listOf(String.class);

    private static final Bindable<Set<String>> STRING_SET = Bindable.setOf(String.class);

    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private Binder binder;

    @Test
    public void bindToCollectionShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0]", "1");
        source.put("foo[1]", "2");
        source.put("foo[2]", "3");
        this.sources.add(source);
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    public void bindToSetShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0]", "a");
        source.put("foo[1]", "b");
        source.put("foo[2]", "c");
        this.sources.add(source);
        Set<String> result = this.binder.bind("foo", CollectionBinderTests.STRING_SET).get();
        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    public void bindToCollectionWhenNestedShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0][0]", "1");
        source.put("foo[0][1]", "2");
        source.put("foo[1][0]", "3");
        source.put("foo[1][1]", "4");
        this.sources.add(source);
        Bindable<List<List<Integer>>> target = Bindable.of(ResolvableType.forClassWithGenerics(List.class, CollectionBinderTests.INTEGER_LIST.getType()));
        List<List<Integer>> result = this.binder.bind("foo", target).get();
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).containsExactly(1, 2);
        assertThat(result.get(1)).containsExactly(3, 4);
    }

    @Test
    public void bindToCollectionWhenNotInOrderShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[1]", "2");
        source.put("foo[0]", "1");
        source.put("foo[2]", "3");
        this.sources.add(source);
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    public void bindToCollectionWhenNonSequentialShouldThrowException() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0]", "2");
        source.put("foo[1]", "1");
        source.put("foo[3]", "3");
        this.sources.add(source);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", INTEGER_LIST)).satisfies(( ex) -> {
            Set<ConfigurationProperty> unbound = ((UnboundConfigurationPropertiesException) (ex.getCause())).getUnboundProperties();
            assertThat(unbound).hasSize(1);
            ConfigurationProperty property = unbound.iterator().next();
            assertThat(property.getName().toString()).isEqualTo("foo[3]");
            assertThat(property.getValue()).isEqualTo("3");
        });
    }

    @Test
    public void bindToNonScalarCollectionWhenNonSequentialShouldThrowException() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0].value", "1");
        source.put("foo[1].value", "2");
        source.put("foo[4].value", "4");
        this.sources.add(source);
        Bindable<List<BinderTests.JavaBean>> target = Bindable.listOf(BinderTests.JavaBean.class);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", target)).satisfies(( ex) -> {
            Set<ConfigurationProperty> unbound = ((UnboundConfigurationPropertiesException) (ex.getCause())).getUnboundProperties();
            assertThat(unbound).hasSize(1);
            ConfigurationProperty property = unbound.iterator().next();
            assertThat(property.getName().toString()).isEqualTo("foo[4].value");
            assertThat(property.getValue()).isEqualTo("4");
        });
    }

    @Test
    public void bindToCollectionWhenNonIterableShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[1]", "2");
        source.put("foo[0]", "1");
        source.put("foo[2]", "3");
        this.sources.add(source.nonIterable());
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    public void bindToCollectionWhenMultipleSourceShouldOnlyUseFirst() {
        MockConfigurationPropertySource source1 = new MockConfigurationPropertySource();
        source1.put("bar", "baz");
        this.sources.add(source1);
        MockConfigurationPropertySource source2 = new MockConfigurationPropertySource();
        source2.put("foo[0]", "1");
        source2.put("foo[1]", "2");
        this.sources.add(source2);
        MockConfigurationPropertySource source3 = new MockConfigurationPropertySource();
        source3.put("foo[0]", "7");
        source3.put("foo[1]", "8");
        source3.put("foo[2]", "9");
        this.sources.add(source3);
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2);
    }

    @Test
    public void bindToCollectionWhenHasExistingCollectionShouldReplaceAllContents() {
        this.sources.add(new MockConfigurationPropertySource("foo[0]", "1"));
        List<Integer> existing = new LinkedList<>();
        existing.add(1000);
        existing.add(1001);
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST.withExistingValue(existing)).get();
        assertThat(result).isExactlyInstanceOf(LinkedList.class);
        assertThat(result).containsExactly(1);
    }

    @Test
    public void bindToCollectionWhenHasExistingCollectionButNoValueShouldReturnUnbound() {
        this.sources.add(new MockConfigurationPropertySource("faf[0]", "1"));
        List<Integer> existing = new LinkedList<>();
        existing.add(1000);
        BindResult<List<Integer>> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST.withExistingValue(existing));
        assertThat(result.isBound()).isFalse();
    }

    @Test
    public void bindToCollectionShouldRespectCollectionType() {
        this.sources.add(new MockConfigurationPropertySource("foo[0]", "1"));
        ResolvableType type = ResolvableType.forClassWithGenerics(LinkedList.class, Integer.class);
        Object defaultList = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        Object customList = this.binder.bind("foo", Bindable.of(type)).get();
        assertThat(customList).isExactlyInstanceOf(LinkedList.class).isNotInstanceOf(defaultList.getClass());
    }

    @Test
    public void bindToCollectionWhenNoValueShouldReturnUnbound() {
        this.sources.add(new MockConfigurationPropertySource("faf.bar", "1"));
        BindResult<List<Integer>> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST);
        assertThat(result.isBound()).isFalse();
    }

    @Test
    public void bindToCollectionWhenCommaListShouldReturnPopulatedCollection() {
        this.sources.add(new MockConfigurationPropertySource("foo", "1,2,3"));
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    public void bindToCollectionWhenCommaListWithPlaceholdersShouldReturnPopulatedCollection() {
        StandardEnvironment environment = new StandardEnvironment();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment, "bar=1,2,3");
        this.binder = new Binder(this.sources, new PropertySourcesPlaceholdersResolver(environment));
        this.sources.add(new MockConfigurationPropertySource("foo", "${bar}"));
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    public void bindToCollectionWhenCommaListAndIndexedShouldOnlyUseFirst() {
        MockConfigurationPropertySource source1 = new MockConfigurationPropertySource();
        source1.put("foo", "1,2");
        this.sources.add(source1);
        MockConfigurationPropertySource source2 = new MockConfigurationPropertySource();
        source2.put("foo[0]", "2");
        source2.put("foo[1]", "3");
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2);
    }

    @Test
    public void bindToCollectionWhenIndexedAndCommaListShouldOnlyUseFirst() {
        MockConfigurationPropertySource source1 = new MockConfigurationPropertySource();
        source1.put("foo[0]", "1");
        source1.put("foo[1]", "2");
        this.sources.add(source1);
        MockConfigurationPropertySource source2 = new MockConfigurationPropertySource();
        source2.put("foo", "2,3");
        List<Integer> result = this.binder.bind("foo", CollectionBinderTests.INTEGER_LIST).get();
        assertThat(result).containsExactly(1, 2);
    }

    @Test
    public void bindToCollectionWhenItemContainsCommasShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0]", "1,2");
        source.put("foo[1]", "3");
        this.sources.add(source);
        List<String> result = this.binder.bind("foo", CollectionBinderTests.STRING_LIST).get();
        assertThat(result).containsExactly("1,2", "3");
    }

    @Test
    public void bindToCollectionWhenEmptyStringShouldReturnEmptyCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo", "");
        this.sources.add(source);
        List<String> result = this.binder.bind("foo", CollectionBinderTests.STRING_LIST).get();
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    public void bindToNonScalarCollectionShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0].value", "a");
        source.put("foo[1].value", "b");
        source.put("foo[2].value", "c");
        this.sources.add(source);
        Bindable<List<BinderTests.JavaBean>> target = Bindable.listOf(BinderTests.JavaBean.class);
        List<BinderTests.JavaBean> result = this.binder.bind("foo", target).get();
        assertThat(result).hasSize(3);
        List<String> values = result.stream().map(BinderTests.JavaBean::getValue).collect(Collectors.toList());
        assertThat(values).containsExactly("a", "b", "c");
    }

    @Test
    public void bindToImmutableCollectionShouldReturnPopulatedCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.values", "a,b,c");
        this.sources.add(source);
        Set<String> result = this.binder.bind("foo.values", CollectionBinderTests.STRING_SET.withExistingValue(Collections.emptySet())).get();
        assertThat(result).hasSize(3);
    }

    @Test
    public void bindToCollectionShouldAlsoCallSetterIfPresent() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.items", "a,b,c");
        this.sources.add(source);
        CollectionBinderTests.ExampleCollectionBean result = this.binder.bind("foo", CollectionBinderTests.ExampleCollectionBean.class).get();
        assertThat(result.getItems()).hasSize(4);
        assertThat(result.getItems()).containsExactly("a", "b", "c", "d");
    }

    @Test
    public void bindToCollectionWithNoDefaultConstructor() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.items", "a,b,c,c");
        this.sources.add(source);
        CollectionBinderTests.ExampleCustomNoDefaultConstructorBean result = this.binder.bind("foo", CollectionBinderTests.ExampleCustomNoDefaultConstructorBean.class).get();
        assertThat(result.getItems()).hasSize(4);
        assertThat(result.getItems()).containsExactly("a", "b", "c", "c");
    }

    @Test
    public void bindToCollectionWithDefaultConstructor() {
        // gh-12322
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.items", "a,b,c,c");
        this.sources.add(source);
        CollectionBinderTests.ExampleCustomWithDefaultConstructorBean result = this.binder.bind("foo", CollectionBinderTests.ExampleCustomWithDefaultConstructorBean.class).get();
        assertThat(result.getItems()).hasSize(4);
        assertThat(result.getItems()).containsExactly("a", "b", "c", "c");
    }

    @Test
    public void bindToListShouldAllowDuplicateValues() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.items", "a,b,c,c");
        this.sources.add(source);
        CollectionBinderTests.ExampleCollectionBean result = this.binder.bind("foo", CollectionBinderTests.ExampleCollectionBean.class).get();
        assertThat(result.getItems()).hasSize(5);
        assertThat(result.getItems()).containsExactly("a", "b", "c", "c", "d");
    }

    @Test
    public void bindToSetShouldNotAllowDuplicateValues() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.items-set", "a,b,c,c");
        this.sources.add(source);
        CollectionBinderTests.ExampleCollectionBean result = this.binder.bind("foo", CollectionBinderTests.ExampleCollectionBean.class).get();
        assertThat(result.getItemsSet()).hasSize(3);
        assertThat(result.getItemsSet()).containsExactly("a", "b", "c");
    }

    @Test
    public void bindToBeanWithNestedCollectionShouldPopulateCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "one");
        source.put("foo.foos[0].value", "two");
        source.put("foo.foos[1].value", "three");
        this.sources.add(source);
        Bindable<CollectionBinderTests.BeanWithNestedCollection> target = Bindable.of(CollectionBinderTests.BeanWithNestedCollection.class);
        CollectionBinderTests.BeanWithNestedCollection foo = this.binder.bind("foo", target).get();
        assertThat(foo.getValue()).isEqualTo("one");
        assertThat(foo.getFoos().get(0).getValue()).isEqualTo("two");
        assertThat(foo.getFoos().get(1).getValue()).isEqualTo("three");
    }

    @Test
    public void bindToNestedCollectionWhenEmptyStringShouldReturnEmptyCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "one");
        source.put("foo.foos", "");
        this.sources.add(source);
        Bindable<CollectionBinderTests.BeanWithNestedCollection> target = Bindable.of(CollectionBinderTests.BeanWithNestedCollection.class);
        CollectionBinderTests.BeanWithNestedCollection foo = this.binder.bind("foo", target).get();
        assertThat(foo.getValue()).isEqualTo("one");
        assertThat(foo.getFoos()).isEmpty();
    }

    @Test
    public void bindToCollectionShouldUsePropertyEditor() {
        // gh-12166
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo[0]", "java.lang.RuntimeException");
        source.put("foo[1]", "java.lang.IllegalStateException");
        this.sources.add(source);
        assertThat(this.binder.bind("foo", Bindable.listOf(Class.class)).get()).containsExactly(RuntimeException.class, IllegalStateException.class);
    }

    @Test
    public void bindToCollectionWhenStringShouldUsePropertyEditor() {
        // gh-12166
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo", "java.lang.RuntimeException,java.lang.IllegalStateException");
        this.sources.add(source);
        assertThat(this.binder.bind("foo", Bindable.listOf(Class.class)).get()).containsExactly(RuntimeException.class, IllegalStateException.class);
    }

    @Test
    public void bindToBeanWithNestedCollectionAndNonIterableSourceShouldNotFail() {
        // gh-10702
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        this.sources.add(source.nonIterable());
        Bindable<CollectionBinderTests.BeanWithNestedCollection> target = Bindable.of(CollectionBinderTests.BeanWithNestedCollection.class);
        this.binder.bind("foo", target);
    }

    @Test
    public void bindToBeanWithClonedArray() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar[0]", "hello");
        this.sources.add(source);
        Bindable<CollectionBinderTests.ClonedArrayBean> target = Bindable.of(CollectionBinderTests.ClonedArrayBean.class);
        CollectionBinderTests.ClonedArrayBean bean = this.binder.bind("foo", target).get();
        assertThat(bean.getBar()).containsExactly("hello");
    }

    @Test
    public void bindToBeanWithExceptionInGetterForExistingValue() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.values", "a,b,c");
        this.sources.add(source);
        CollectionBinderTests.BeanWithGetterException result = this.binder.bind("foo", Bindable.of(CollectionBinderTests.BeanWithGetterException.class)).get();
        assertThat(result.getValues()).containsExactly("a", "b", "c");
    }

    @Test
    public void bindToBeanWithEnumSetCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.values[0]", "foo-bar,bar-baz");
        this.sources.add(source);
        CollectionBinderTests.BeanWithEnumSetCollection result = this.binder.bind("foo", Bindable.of(CollectionBinderTests.BeanWithEnumSetCollection.class)).get();
        assertThat(result.getValues().get(0)).containsExactly(FOO_BAR, BAR_BAZ);
    }

    public static class ExampleCollectionBean {
        private List<String> items = new ArrayList<>();

        private Set<String> itemsSet = new HashSet<>();

        public List<String> getItems() {
            return this.items;
        }

        public void setItems(List<String> items) {
            this.items.add("d");
        }

        public Set<String> getItemsSet() {
            return this.itemsSet;
        }

        public void setItemsSet(Set<String> itemsSet) {
            this.itemsSet = itemsSet;
        }
    }

    public static class ExampleCustomNoDefaultConstructorBean {
        private CollectionBinderTests.MyCustomNoDefaultConstructorList items = new CollectionBinderTests.MyCustomNoDefaultConstructorList(Collections.singletonList("foo"));

        public CollectionBinderTests.MyCustomNoDefaultConstructorList getItems() {
            return this.items;
        }

        public void setItems(CollectionBinderTests.MyCustomNoDefaultConstructorList items) {
            this.items = items;
        }
    }

    public static class MyCustomNoDefaultConstructorList extends ArrayList<String> {
        public MyCustomNoDefaultConstructorList(List<String> items) {
            addAll(items);
        }
    }

    public static class ExampleCustomWithDefaultConstructorBean {
        private CollectionBinderTests.MyCustomWithDefaultConstructorList items = new CollectionBinderTests.MyCustomWithDefaultConstructorList();

        public CollectionBinderTests.MyCustomWithDefaultConstructorList getItems() {
            return this.items;
        }

        public void setItems(CollectionBinderTests.MyCustomWithDefaultConstructorList items) {
            this.items.clear();
            this.items.addAll(items);
        }
    }

    public static class MyCustomWithDefaultConstructorList extends ArrayList<String> {}

    public static class BeanWithNestedCollection {
        private String value;

        private List<CollectionBinderTests.BeanWithNestedCollection> foos;

        public List<CollectionBinderTests.BeanWithNestedCollection> getFoos() {
            return this.foos;
        }

        public void setFoos(List<CollectionBinderTests.BeanWithNestedCollection> foos) {
            this.foos = foos;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ClonedArrayBean {
        private String[] bar;

        public String[] getBar() {
            return this.bar.clone();
        }

        public void setBar(String[] bar) {
            this.bar = bar;
        }
    }

    public static class BeanWithGetterException {
        private List<String> values;

        public void setValues(List<String> values) {
            this.values = values;
        }

        public List<String> getValues() {
            return Collections.unmodifiableList(this.values);
        }
    }

    public static class BeanWithEnumSetCollection {
        private List<EnumSet<BinderTests.ExampleEnum>> values;

        public void setValues(List<EnumSet<BinderTests.ExampleEnum>> values) {
            this.values = values;
        }

        public List<EnumSet<BinderTests.ExampleEnum>> getValues() {
            return this.values;
        }
    }
}

