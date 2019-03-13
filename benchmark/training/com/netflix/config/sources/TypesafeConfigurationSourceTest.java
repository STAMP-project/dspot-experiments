package com.netflix.config.sources;


import com.netflix.config.DynamicPropertyFactory;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class TypesafeConfigurationSourceTest {
    private static DynamicPropertyFactory props;

    @Test
    public void topLevel() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("top", (-1)).get(), IsEqual.equalTo(3));
    }

    @Test
    public void topLevelDotted() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("nest.dotted", "n/a").get(), IsEqual.equalTo("wxy"));
    }

    @Test
    public void variableSubstitution() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("top-var", "n/a").get(), IsEqual.equalTo("3.14"));
    }

    @Test
    public void simpleNested() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("nest.nested", (-1)).get(), IsEqual.equalTo(7));
    }

    @Test
    public void nestedMap() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("nest.nested-map.inner", "n/a").get(), IsEqual.equalTo("abc"));
    }

    @Test
    public void willNotClobberWhenExpandingArrays() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("an-unexpanded-array.length", (-1)).get(), IsEqual.equalTo(13));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("an-expanded-array.length", (-1)).get(), IsEqual.equalTo(7));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("an-unexpanded-array[0]", "n/a").get(), IsEqual.equalTo("n/a"));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("an-expanded-array[4]", "n/a").get(), IsEqual.equalTo("e"));
    }

    @Test
    public void nestedIntegerArray() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("nest.nested-list.length", (-1)).get(), IsEqual.equalTo(4));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("nest.nested-list[0]", (-1)).get(), IsEqual.equalTo(3));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("nest.nested-list[1]", (-1)).get(), IsEqual.equalTo(5));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("nest.nested-list[2]", (-1)).get(), IsEqual.equalTo(7));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("nest.nested-list[3]", (-1)).get(), IsEqual.equalTo(11));
    }

    @Test
    public void nestedStringArray() {
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getIntProperty("arrays.nesting.nested.length", (-1)).get(), IsEqual.equalTo(3));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("arrays.nesting.nested[0]", "n/a").get(), IsEqual.equalTo("abc"));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("arrays.nesting.nested[1]", "n/a").get(), IsEqual.equalTo("def"));
        Assert.assertThat(TypesafeConfigurationSourceTest.props.getStringProperty("arrays.nesting.nested[2]", "n/a").get(), IsEqual.equalTo("ghi"));
    }
}

