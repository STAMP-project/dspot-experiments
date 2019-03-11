package org.keycloak.common.util;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class CollectionUtilTest {
    @Test
    public void joinInputNoneOutputEmpty() {
        final ArrayList<String> strings = new ArrayList<String>();
        final String retval = CollectionUtil.join(strings, ",");
        Assert.assertEquals("", retval);
    }

    @Test
    public void joinInput2SeparatorNull() {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add("foo");
        strings.add("bar");
        final String retval = CollectionUtil.join(strings, null);
        Assert.assertEquals("foonullbar", retval);
    }

    @Test
    public void joinInput1SeparatorNotNull() {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add("foo");
        final String retval = CollectionUtil.join(strings, ",");
        Assert.assertEquals("foo", retval);
    }

    @Test
    public void joinInput2SeparatorNotNull() {
        final ArrayList<String> strings = new ArrayList<String>();
        strings.add("foo");
        strings.add("bar");
        final String retval = CollectionUtil.join(strings, ",");
        Assert.assertEquals("foo,bar", retval);
    }
}

