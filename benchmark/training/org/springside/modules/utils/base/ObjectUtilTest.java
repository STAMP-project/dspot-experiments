package org.springside.modules.utils.base;


import org.junit.Test;
import org.springside.modules.utils.collection.ListUtil;


public class ObjectUtilTest {
    @Test
    public void hashCodeTest() {
        assertThat(((ObjectUtil.hashCode("a", "b")) - (ObjectUtil.hashCode("a", "a")))).isEqualTo(1);
    }

    @Test
    public void toPrettyString() {
        assertThat(ObjectUtil.toPrettyString(null)).isEqualTo("null");
        assertThat(ObjectUtil.toPrettyString(1)).isEqualTo("1");
        assertThat(ObjectUtil.toPrettyString(new int[]{ 1, 2 })).isEqualTo("[1, 2]");
        assertThat(ObjectUtil.toPrettyString(new long[]{ 1, 2 })).isEqualTo("[1, 2]");
        assertThat(ObjectUtil.toPrettyString(new double[]{ 1.1, 2.2 })).isEqualTo("[1.1, 2.2]");
        assertThat(ObjectUtil.toPrettyString(new float[]{ 1.1F, 2.2F })).isEqualTo("[1.1, 2.2]");
        assertThat(ObjectUtil.toPrettyString(new boolean[]{ true, false })).isEqualTo("[true, false]");
        assertThat(ObjectUtil.toPrettyString(new short[]{ 1, 2 })).isEqualTo("[1, 2]");
        assertThat(ObjectUtil.toPrettyString(new byte[]{ 1, 2 })).isEqualTo("[1, 2]");
        assertThat(ObjectUtil.toPrettyString(new Integer[]{ 1, 2 })).isEqualTo("[1, 2]");
        assertThat(ObjectUtil.toPrettyString(ListUtil.newArrayList("1", "2"))).isEqualTo("{1,2}");
        System.out.println(new Integer[]{ 1, 2 }.toString());
    }
}

