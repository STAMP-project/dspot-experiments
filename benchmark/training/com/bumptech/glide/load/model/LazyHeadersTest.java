package com.bumptech.glide.load.model;


import LazyHeaders.Builder;
import android.support.annotation.Nullable;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class LazyHeadersTest {
    private static final String DEFAULT_USER_AGENT = "default_user_agent";

    private static final String DEFAULT_USER_AGENT_PROPERTY = "http.agent";

    private String initialUserAgent;

    // Tests for #2331.
    @Test
    public void getSanitizedUserAgent_withInvalidAgent_returnsAgentWithInvalidCharactersRemoved() {
        String invalidUserAgent = "Dalvik/2.1.0 (Linux; U; Android 5.0; P98 4G???(A8H8) Build/LRX21M)";
        String validUserAgent = "Dalvik/2.1.0 (Linux; U; Android 5.0; P98 4G???(A8H8) Build/LRX21M)";
        System.setProperty(LazyHeadersTest.DEFAULT_USER_AGENT_PROPERTY, invalidUserAgent);
        assertThat(Builder.getSanitizedUserAgent()).isEqualTo(validUserAgent);
    }

    @Test
    public void getSanitizedUserAgent_withValidAgent_returnsUnmodifiedAgent() {
        String validUserAgent = "Dalvik/2.1.0 (Linux; U; Android 5.0; P98 4G(A8H8) Build/LRX21M)";
        System.setProperty(LazyHeadersTest.DEFAULT_USER_AGENT_PROPERTY, validUserAgent);
        assertThat(Builder.getSanitizedUserAgent()).isEqualTo(validUserAgent);
    }

    @Test
    public void getSanitizedUserAgent_withMissingAgent_returnsNull() {
        System.clearProperty(LazyHeadersTest.DEFAULT_USER_AGENT_PROPERTY);
        assertThat(Builder.getSanitizedUserAgent()).isNull();
    }

    @Test
    public void getSanitizedUserAgent_withEmptyStringAgent_returnsEmptyString() {
        String userAgent = "";
        System.setProperty(LazyHeadersTest.DEFAULT_USER_AGENT_PROPERTY, userAgent);
        assertThat(Builder.getSanitizedUserAgent()).isEqualTo(userAgent);
    }

    @Test
    public void getSanitizedUserAgent_withWhitespace_returnsWhitespaceString() {
        String userAgent = "  \t";
        System.setProperty(LazyHeadersTest.DEFAULT_USER_AGENT_PROPERTY, userAgent);
        assertThat(Builder.getSanitizedUserAgent()).isEqualTo(userAgent);
    }

    @Test
    public void testIncludesEagerHeaders() {
        Map<String, String> headers = new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", "value").build().getHeaders();
        assertThat(headers).containsEntry("key", "value");
    }

    @Test
    public void testIncludesLazyHeaders() {
        LazyHeaderFactory factory = Mockito.mock(LazyHeaderFactory.class);
        Mockito.when(factory.buildHeader()).thenReturn("value");
        Map<String, String> headers = new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", factory).build().getHeaders();
        assertThat(headers).containsEntry("key", "value");
    }

    @Test
    public void testMultipleEagerValuesAreSeparatedByCommas() {
        Map<String, String> headers = new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", "first").addHeader("key", "second").build().getHeaders();
        assertThat(headers).containsEntry("key", "first,second");
    }

    @Test
    public void testMultipleLazyValuesAreSeparatedByCommas() {
        LazyHeaderFactory first = Mockito.mock(LazyHeaderFactory.class);
        Mockito.when(first.buildHeader()).thenReturn("first");
        LazyHeaderFactory second = Mockito.mock(LazyHeaderFactory.class);
        Mockito.when(second.buildHeader()).thenReturn("second");
        Map<String, String> headers = new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", first).addHeader("key", second).build().getHeaders();
        assertThat(headers).containsEntry("key", "first,second");
    }

    @Test
    public void testMixedEagerAndLazyValuesAreIncluded() {
        LazyHeaderFactory factory = Mockito.mock(LazyHeaderFactory.class);
        Mockito.when(factory.buildHeader()).thenReturn("first");
        Map<String, String> headers = new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", factory).addHeader("key", "second").build().getHeaders();
        assertThat(headers).containsEntry("key", "first,second");
        headers = new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", "second").addHeader("key", factory).build().getHeaders();
        assertThat(headers).containsEntry("key", "second,first");
    }

    @Test
    public void testCanAddMultipleKeys() {
        LazyHeaderFactory factory = Mockito.mock(LazyHeaderFactory.class);
        Mockito.when(factory.buildHeader()).thenReturn("lazy");
        Map<String, String> headers = new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("first", factory).addHeader("second", "eager").build().getHeaders();
        assertThat(headers).containsEntry("first", "lazy");
        assertThat(headers).containsEntry("second", "eager");
    }

    @Test
    public void testUpdatingBuilderAfterBuildingDoesNotModifyOriginalHeaders() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.addHeader("key", "firstValue");
        builder.addHeader("otherKey", "otherValue");
        LazyHeaders first = builder.build();
        LazyHeaderFactory factory = Mockito.mock(LazyHeaderFactory.class);
        Mockito.when(factory.buildHeader()).thenReturn("otherValue");
        builder.addHeader("key", "secondValue");
        builder.setHeader("otherKey", factory);
        LazyHeaders second = builder.build();
        assertThat(first.getHeaders()).isNotEqualTo(second.getHeaders());
        assertThat(first.getHeaders()).containsEntry("key", "firstValue");
        assertThat(first.getHeaders()).containsEntry("otherKey", "otherValue");
        assertThat(second.getHeaders()).containsEntry("key", "firstValue,secondValue");
        assertThat(second.getHeaders()).containsEntry("otherKey", "otherValue");
    }

    @Test
    public void testSetHeaderReplacesExistingHeaders() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.addHeader("key", "first").addHeader("key", "second").setHeader("key", "third");
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("key", "third");
    }

    @Test
    public void testSetHeaderWithNullStringRemovesExistingHeader() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.addHeader("key", "first").addHeader("key", "second").setHeader("key", ((String) (null)));
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).doesNotContainKey("key");
    }

    @Test
    public void testSetHeaderWithNullLazyHeaderFactoryRemovesExistingHeader() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.addHeader("key", "first").addHeader("key", "second").setHeader("key", ((LazyHeaderFactory) (null)));
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).doesNotContainKey("key");
    }

    @Test
    public void testAddingEncodingHeaderReplacesDefaultThenAppends() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.addHeader("Accept-Encoding", "false");
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("Accept-Encoding", "false");
        builder.addHeader("Accept-Encoding", "true");
        headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("Accept-Encoding", "false,true");
    }

    @Test
    public void testRemovingAndAddingEncodingHeaderReplacesDefaultThenAppends() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.setHeader("Accept-Encoding", ((String) (null)));
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).doesNotContainKey("Accept-Encoding");
        builder.addHeader("Accept-Encoding", "false");
        headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("Accept-Encoding", "false");
        builder.addHeader("Accept-Encoding", "true");
        headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("Accept-Encoding", "false,true");
    }

    @Test
    public void testAddingUserAgentHeaderReplacesDefaultThenAppends() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.addHeader("User-Agent", "false");
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("User-Agent", "false");
        builder.addHeader("User-Agent", "true");
        headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("User-Agent", "false,true");
    }

    @Test
    public void testRemovingAndAddingUserAgentHeaderReplacesDefaultThenAppends() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.setHeader("User-Agent", ((String) (null)));
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).doesNotContainKey("User-Agent");
        builder.addHeader("User-Agent", "false");
        headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("User-Agent", "false");
        builder.addHeader("User-Agent", "true");
        headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("User-Agent", "false,true");
    }

    @Test
    public void testKeyNotIncludedWithFactoryThatReturnsNullValue() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.setHeader("test", new LazyHeaderFactory() {
            @Nullable
            @Override
            public String buildHeader() {
                return null;
            }
        });
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).doesNotContainKey("test");
    }

    @Test
    public void testKeyNotIncludedWithFactoryThatReturnsEmptyValue() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.setHeader("test", new LazyHeaderFactory() {
            @Nullable
            @Override
            public String buildHeader() {
                return "";
            }
        });
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).doesNotContainKey("test");
    }

    @Test
    public void testKeyIncludedWithOneFactoryThatReturnsNullAndOneFactoryThatDoesNotReturnNull() {
        com.bumptech.glide.load.model.LazyHeaders.Builder builder = new com.bumptech.glide.load.model.LazyHeaders.Builder();
        builder.addHeader("test", new LazyHeaderFactory() {
            @Nullable
            @Override
            public String buildHeader() {
                return null;
            }
        });
        builder.addHeader("test", new LazyHeaderFactory() {
            @Nullable
            @Override
            public String buildHeader() {
                return "value";
            }
        });
        LazyHeaders headers = builder.build();
        assertThat(headers.getHeaders()).containsEntry("test", "value");
    }

    @Test
    public void testEquals() {
        LazyHeaderFactory firstLazyFactory = Mockito.mock(LazyHeaderFactory.class);
        LazyHeaderFactory secondLazyFactory = Mockito.mock(LazyHeaderFactory.class);
        new com.google.common.testing.EqualsTester().addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().build()).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", "value").build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", "value").build()).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", "value").addHeader("key", "value").build()).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", firstLazyFactory).build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", firstLazyFactory).build()).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", firstLazyFactory).addHeader("key", firstLazyFactory).build()).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("firstKey", "value").addHeader("secondKey", firstLazyFactory).build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("secondKey", firstLazyFactory).addHeader("firstKey", "value").build()).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", "secondValue")).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("secondKey", "value")).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("key", secondLazyFactory)).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("secondKey", firstLazyFactory)).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("firstKey", "firstValue").addHeader("secondKey", "secondValue").build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("firstKey", "firstValue").addHeader("secondKey", "secondValue").build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("secondKey", "secondValue").addHeader("firstKey", "firstValue").build()).addEqualityGroup(new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("firstKey", firstLazyFactory).addHeader("secondKey", secondLazyFactory).build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("firstKey", firstLazyFactory).addHeader("secondKey", secondLazyFactory).build(), new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("secondKey", secondLazyFactory).addHeader("firstKey", firstLazyFactory).build()).testEquals();
    }
}

