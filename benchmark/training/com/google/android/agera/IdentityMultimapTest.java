package com.google.android.agera;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class IdentityMultimapTest {
    private static final String KEY_1 = "key1";

    private static final String KEY_2 = "key2";

    private static final String VALUE_1 = "value1";

    private static final String VALUE_2 = "value2";

    private IdentityMultimap identityMultimap;

    @Test
    public void shouldReturnThatNotAddedKeyWasNotRemoved() {
        MatcherAssert.assertThat(identityMultimap.removeKey(new Object()), Matchers.is(false));
    }

    @Test
    public void shouldHandleRemovalOfNonAddedKeyValuePairWithoutKeyOrValue() {
        identityMultimap.removeKeyValuePair(new Object(), new Object());
    }

    @Test
    public void shouldReturnThatKeyValuePairWasAdded() {
        MatcherAssert.assertThat(identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1), Matchers.is(true));
    }

    @Test
    public void shouldReturnThatKeyValuePairWasNotAdded() {
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        MatcherAssert.assertThat(identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1), Matchers.is(false));
    }

    @Test
    public void shouldHandleRemovalOfAddedKeyValuePairWithoutKeyOrValue() {
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        identityMultimap.removeKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
    }

    @Test
    public void shouldRemoveSecondValueForKeyOnRemoveKey() {
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_2);
        identityMultimap.removeKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        MatcherAssert.assertThat(identityMultimap.removeKey(IdentityMultimapTest.KEY_1), Matchers.is(true));
        MatcherAssert.assertThat(identityMultimap.removeKey(IdentityMultimapTest.KEY_1), Matchers.is(false));
    }

    @Test
    public void shouldRemoveOnlyKeySpecified() {
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_2, IdentityMultimapTest.VALUE_2);
        identityMultimap.removeKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        MatcherAssert.assertThat(identityMultimap.removeKey(IdentityMultimapTest.KEY_2), Matchers.is(true));
    }

    @Test
    public void shouldRemoveOnlyKeyValueSpecified() {
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_2, IdentityMultimapTest.VALUE_2);
        identityMultimap.removeKey(IdentityMultimapTest.KEY_1);
        MatcherAssert.assertThat(identityMultimap.removeKey(IdentityMultimapTest.KEY_2), Matchers.is(true));
    }

    @Test
    public void shouldHandleAddOfNewKeyValueAfterRemove() {
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_1, IdentityMultimapTest.VALUE_1);
        identityMultimap.removeKey(IdentityMultimapTest.KEY_1);
        identityMultimap.addKeyValuePair(IdentityMultimapTest.KEY_2, IdentityMultimapTest.VALUE_2);
        MatcherAssert.assertThat(identityMultimap.removeKey(IdentityMultimapTest.KEY_2), Matchers.is(true));
    }
}

