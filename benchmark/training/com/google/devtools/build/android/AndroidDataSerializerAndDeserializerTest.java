/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android;


import FullyQualifiedName.Factory;
import ParsedAndroidData.KeyValueConsumer;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.Truth;
import com.google.devtools.build.android.xml.IdXmlResourceValue;
import com.google.devtools.build.android.xml.ResourcesAttribute;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.devtools.build.android.AndroidDataBuilder.ResourceType.LAYOUT;
import static com.google.devtools.build.android.AndroidDataBuilder.ResourceType.VALUE;


/**
 * Tests for the AndroidDataSerializer and AndroidDataDeserializer.
 */
@RunWith(JUnit4.class)
public class AndroidDataSerializerAndDeserializerTest {
    private FileSystem fs;

    private Factory fqnFactory;

    private Path source;

    private Path manifest;

    @Test
    public void serializeAssets() throws Exception {
        Path binaryPath = fs.getPath("out.bin");
        AndroidDataSerializer serializer = AndroidDataSerializer.create();
        UnwrittenMergedAndroidData expected = UnwrittenMergedAndroidData.of(manifest, ParsedAndroidDataBuilder.buildOn(source).assets(ParsedAndroidDataBuilder.file().source("hunting/of/the/boojum")).build(), ParsedAndroidDataBuilder.empty());
        expected.serializeTo(serializer);
        serializer.flushTo(binaryPath);
        AndroidDataDeserializer deserializer = AndroidParsedDataDeserializer.create();
        AndroidDataSerializerAndDeserializerTest.TestMapConsumer<DataAsset> assets = AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofAssets();
        deserializer.read(binaryPath, KeyValueConsumers.of(null, null, assets));
        Truth.assertThat(assets).isEqualTo(expected.getPrimary().getAssets());
    }

    @Test
    public void serializeCombiningResource() throws Exception {
        Path binaryPath = fs.getPath("out.bin");
        AndroidDataSerializer serializer = AndroidDataSerializer.create();
        UnwrittenMergedAndroidData expected = UnwrittenMergedAndroidData.of(manifest, ParsedAndroidDataBuilder.buildOn(source, fqnFactory).combining(ParsedAndroidDataBuilder.xml("id/snark").source("values/ids.xml").value(IdXmlResourceValue.of())).build(), ParsedAndroidDataBuilder.empty());
        expected.serializeTo(serializer);
        serializer.flushTo(binaryPath);
        AndroidDataDeserializer deserializer = AndroidParsedDataDeserializer.create();
        AndroidDataSerializerAndDeserializerTest.TestMapConsumer<DataResource> resources = AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources();
        deserializer.read(binaryPath, // overwriting
        // combining
        // assets
        KeyValueConsumers.of(null, resources, null));
        Truth.assertThat(resources).isEqualTo(expected.getPrimary().getCombiningResources());
    }

    @Test
    public void serializeOverwritingResource() throws Exception {
        Path binaryPath = fs.getPath("out.bin");
        AndroidDataSerializer serializer = AndroidDataSerializer.create();
        UnwrittenMergedAndroidData expected = UnwrittenMergedAndroidData.of(manifest, ParsedAndroidDataBuilder.buildOn(source, fqnFactory).overwritable(ParsedAndroidDataBuilder.file("layout/banker").source("layout/banker.xml")).build(), ParsedAndroidDataBuilder.empty());
        expected.serializeTo(serializer);
        serializer.flushTo(binaryPath);
        AndroidDataDeserializer deserializer = AndroidParsedDataDeserializer.create();
        AndroidDataSerializerAndDeserializerTest.TestMapConsumer<DataResource> resources = AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources();
        deserializer.read(binaryPath, // overwriting
        // combining
        // assets
        KeyValueConsumers.of(resources, null, null));
        Truth.assertThat(resources).isEqualTo(expected.getPrimary().getOverwritingResources());
    }

    @Test
    public void serializeFileWithIds() throws Exception {
        Path binaryPath = fs.getPath("out.bin");
        AndroidDataSerializer serializer = AndroidDataSerializer.create();
        ParsedAndroidData direct = // Also check what happens if a value XML file also contains the same ID.
        // Test what happens if a user accidentally uses the same ID in multiple layouts too.
        AndroidDataBuilder.of(source).addResource("layout/some_layout.xml", LAYOUT, "<TextView android:id=\"@+id/MyTextView\"", "          android:text=\"@string/walrus\"", "          android:layout_width=\"wrap_content\"", "          android:layout_height=\"wrap_content\" />").addResource("layout/another_layout.xml", LAYOUT, "<TextView android:id=\"@+id/MyTextView\"", "          android:text=\"@string/walrus\"", "          android:layout_width=\"wrap_content\"", "          android:layout_height=\"wrap_content\" />").addResource("values/ids.xml", VALUE, "<item name=\"MyTextView\" type=\"id\"/>", "<item name=\"OtherId\" type=\"id\"/>").addResource("values/strings.xml", VALUE, "<string name=\"walrus\">I has a bucket</string>").createManifest("AndroidManifest.xml", "com.carroll.lewis", "").buildParsed();
        UnwrittenMergedAndroidData expected = UnwrittenMergedAndroidData.of(manifest, direct, ParsedAndroidDataBuilder.empty());
        expected.serializeTo(serializer);
        serializer.flushTo(binaryPath);
        AndroidDataDeserializer deserializer = AndroidParsedDataDeserializer.create();
        AndroidDataSerializerAndDeserializerTest.TestMapConsumer<DataResource> overwriting = AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources();
        AndroidDataSerializerAndDeserializerTest.TestMapConsumer<DataResource> combining = AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources();
        deserializer.read(binaryPath, // assets
        KeyValueConsumers.of(overwriting, combining, null));
        Truth.assertThat(overwriting).isEqualTo(expected.getPrimary().getOverwritingResources());
        Truth.assertThat(combining).isEqualTo(expected.getPrimary().getCombiningResources());
    }

    @Test
    public void serialize() throws Exception {
        Path binaryPath = fs.getPath("out.bin");
        AndroidDataSerializer serializer = AndroidDataSerializer.create();
        UnwrittenMergedAndroidData expected = UnwrittenMergedAndroidData.of(manifest, ParsedAndroidDataBuilder.buildOn(source, fqnFactory).overwritable(ParsedAndroidDataBuilder.file("layout/banker").source("layout/banker.xml"), ParsedAndroidDataBuilder.xml("<resources>/foo").source("values/ids.xml").value(ResourcesAttribute.of(fqnFactory.parse("<resources>/foo"), "foo", "fooVal"))).combining(ParsedAndroidDataBuilder.xml("id/snark").source("values/ids.xml").value(IdXmlResourceValue.of())).assets(ParsedAndroidDataBuilder.file().source("hunting/of/the/boojum")).build(), ParsedAndroidDataBuilder.buildOn(source, fqnFactory).overwritable(ParsedAndroidDataBuilder.file("layout/butcher").source("layout/butcher.xml")).combining(ParsedAndroidDataBuilder.xml("id/snark").source("values/ids.xml").value(IdXmlResourceValue.of())).assets(ParsedAndroidDataBuilder.file().source("hunting/of/the/snark")).build());
        expected.serializeTo(serializer);
        serializer.flushTo(binaryPath);
        KeyValueConsumers primary = // overwriting
        // combining
        // assets
        KeyValueConsumers.of(AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources(), AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources(), AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofAssets());
        AndroidDataDeserializer deserializer = AndroidParsedDataDeserializer.create();
        deserializer.read(binaryPath, primary);
        Truth.assertThat(primary.overwritingConsumer).isEqualTo(expected.getPrimary().getOverwritingResources());
        Truth.assertThat(primary.combiningConsumer).isEqualTo(expected.getPrimary().getCombiningResources());
        Truth.assertThat(primary.assetConsumer).isEqualTo(expected.getPrimary().getAssets());
    }

    @Test
    public void testDeserializeMissing() throws Exception {
        Path binaryPath = fs.getPath("out.bin");
        AndroidDataSerializer serializer = AndroidDataSerializer.create();
        UnwrittenMergedAndroidData expected = UnwrittenMergedAndroidData.of(manifest, ParsedAndroidDataBuilder.buildOn(source, fqnFactory).overwritable(ParsedAndroidDataBuilder.file("layout/banker").source("layout/banker.xml"), ParsedAndroidDataBuilder.xml("<resources>/foo").source("values/ids.xml").value(ResourcesAttribute.of(fqnFactory.parse("<resources>/foo"), "foo", "fooVal"))).combining(ParsedAndroidDataBuilder.xml("id/snark").source("values/ids.xml").value(IdXmlResourceValue.of())).assets(ParsedAndroidDataBuilder.file().source("hunting/of/the/boojum")).build(), ParsedAndroidDataBuilder.buildOn(source, fqnFactory).overwritable(ParsedAndroidDataBuilder.file("layout/butcher").source("layout/butcher.xml")).combining(ParsedAndroidDataBuilder.xml("id/snark").source("values/ids.xml").value(IdXmlResourceValue.of())).assets(ParsedAndroidDataBuilder.file().source("hunting/of/the/snark")).build());
        expected.serializeTo(serializer);
        serializer.flushTo(binaryPath);
        AndroidDataDeserializer deserializer = AndroidParsedDataDeserializer.withFilteredResources(ImmutableList.of("the/boojum", "values/ids.xml", "layout/banker.xml"));
        KeyValueConsumers primary = // overwriting
        // combining
        // assets
        KeyValueConsumers.of(AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources(), AndroidDataSerializerAndDeserializerTest.TestMapConsumer.ofResources(), null);
        deserializer.read(binaryPath, primary);
        Truth.assertThat(primary.overwritingConsumer).isEqualTo(Collections.emptyMap());
        Truth.assertThat(primary.combiningConsumer).isEqualTo(Collections.emptyMap());
    }

    private static class TestMapConsumer<T extends DataValue> implements KeyValueConsumer<DataKey, T> , Map<DataKey, T> {
        Map<DataKey, T> target;

        static AndroidDataSerializerAndDeserializerTest.TestMapConsumer<DataAsset> ofAssets() {
            return new AndroidDataSerializerAndDeserializerTest.TestMapConsumer(new HashMap<DataKey, DataAsset>());
        }

        static AndroidDataSerializerAndDeserializerTest.TestMapConsumer<DataResource> ofResources() {
            return new AndroidDataSerializerAndDeserializerTest.TestMapConsumer(new HashMap<DataKey, DataResource>());
        }

        public TestMapConsumer(Map<DataKey, T> target) {
            this.target = target;
        }

        @Override
        public void accept(DataKey key, T value) {
            target.put(key, value);
        }

        @Override
        public int size() {
            return target.size();
        }

        @Override
        public boolean isEmpty() {
            return target.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return target.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return target.containsValue(value);
        }

        @Override
        public T get(Object key) {
            return target.get(key);
        }

        @Override
        public T put(DataKey key, T value) {
            return target.put(key, value);
        }

        @Override
        public T remove(Object key) {
            return target.remove(key);
        }

        @Override
        public void putAll(Map<? extends DataKey, ? extends T> m) {
            target.putAll(m);
        }

        @Override
        public void clear() {
            target.clear();
        }

        @Override
        public Set<DataKey> keySet() {
            return target.keySet();
        }

        @Override
        public Collection<T> values() {
            return target.values();
        }

        @Override
        public Set<Map.Entry<DataKey, T>> entrySet() {
            return target.entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return target.equals(o);
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return toStringHelper(this).add("target", target).toString();
        }
    }
}

