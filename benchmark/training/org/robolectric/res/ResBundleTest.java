package org.robolectric.res;


import ResBundle.ResMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.robolectric.res.android.ResTable_config;

import static ResType.CHAR_SEQUENCE;


@RunWith(JUnit4.class)
public class ResBundleTest {
    private ResMap resMap = new ResBundle.ResMap();

    private ResName resName;

    @Test
    public void closestMatchIsPicked() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("v16");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("v17");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("v18"));
        assertThat(v).isEqualTo(val2);
    }

    @Test
    public void firstValIsPickedWhenNoMatch() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("en");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("fr");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("en-v18"));
        assertThat(v).isEqualTo(val1);
    }

    @Test
    public void bestValIsPickedForSdkVersion() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("v16");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("v17");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("v26"));
        assertThat(v).isEqualTo(val2);
    }

    @Test
    public void eliminatedValuesAreNotPickedForVersion() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("land-v16");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("v17");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("land-v18"));
        assertThat(v).isEqualTo(val1);
    }

    @Test
    public void greaterVersionsAreNotPicked() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("v11");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("v19");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("v18"));
        assertThat(v).isEqualTo(val1);
    }

    @Test
    public void greaterVersionsAreNotPickedReordered() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("v19");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("v11");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("v18"));
        assertThat(v).isEqualTo(val2);
    }

    @Test
    public void greaterVersionsAreNotPickedMoreQualifiers() {
        // List the contradicting qualifier first, in case the algorithm has a tendency
        // to pick the first qualifier when none of the qualifiers are a "perfect" match.
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("anydpi-v21");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("xhdpi-v9");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("v18"));
        assertThat(v).isEqualTo(val2);
    }

    @Test
    public void onlyMatchingVersionsQualifiersWillBePicked() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("v16");
        resMap.put(resName, val1);
        TypedResource<String> val2 = ResBundleTest.createStringTypedResource("sw600dp-v17");
        resMap.put(resName, val2);
        TypedResource v = resMap.pick(resName, ResBundleTest.from("v18"));
        assertThat(v).isEqualTo(val1);
    }

    @Test
    public void illegalResourceQualifierThrowsException() {
        TypedResource<String> val1 = ResBundleTest.createStringTypedResource("en-v12");
        resMap.put(resName, val1);
        try {
            resMap.pick(resName, ResBundleTest.from("nosuchqualifier"));
            Assert.fail("Expected exception to be caught");
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().startsWith("Invalid qualifiers \"nosuchqualifier\"");
        }
    }

    @Test
    public void shouldMatchQualifiersPerAndroidSpec() throws Exception {
        Assert.assertEquals("en-port", asResMap("", "en", "fr-rCA", "en-port", "en-notouch-12key", "port-ldpi", "land-notouch-12key").pick(resName, ResBundleTest.from("en-rGB-port-hdpi-notouch-12key-v25")).asString());
    }

    @Test
    public void shouldMatchQualifiersInSizeRange() throws Exception {
        Assert.assertEquals("sw300dp-port", asResMap("", "sw200dp", "sw350dp-port", "sw300dp-port", "sw300dp").pick(resName, ResBundleTest.from("sw320dp-port-v25")).asString());
    }

    @Test
    public void shouldPreferWidthOverHeight() throws Exception {
        Assert.assertEquals("sw300dp-w200dp", asResMap("", "sw200dp", "sw200dp-w300dp", "sw300dp-w200dp", "w300dp").pick(resName, ResBundleTest.from("sw320dp-w320dp-v25")).asString());
    }

    @Test
    public void shouldNotOverwriteValuesWithMatchingQualifiers() {
        ResBundle bundle = new ResBundle();
        XmlContext xmlContext = Mockito.mock(XmlContext.class);
        Mockito.when(xmlContext.getQualifiers()).thenReturn(Qualifiers.parse("--"));
        Mockito.when(xmlContext.getConfig()).thenReturn(new ResTable_config());
        Mockito.when(xmlContext.getPackageName()).thenReturn("org.robolectric");
        TypedResource firstValue = new TypedResource("first_value", CHAR_SEQUENCE, xmlContext);
        TypedResource secondValue = new TypedResource("second_value", CHAR_SEQUENCE, xmlContext);
        bundle.put(new ResName("org.robolectric", "string", "resource_name"), firstValue);
        bundle.put(new ResName("org.robolectric", "string", "resource_name"), secondValue);
        assertThat(bundle.get(new ResName("org.robolectric", "string", "resource_name"), ResBundleTest.from("")).getData()).isEqualTo("first_value");
    }
}

