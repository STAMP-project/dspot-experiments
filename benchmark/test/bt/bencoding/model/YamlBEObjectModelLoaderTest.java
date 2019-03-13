/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.bencoding.model;


import org.junit.Test;


public class YamlBEObjectModelLoaderTest {
    BEObjectModel model;

    @Test
    public void testValidateTorrent() {
        Object torrentObject = readTorrent("single_file_correct.torrent");
        YamlBEObjectModelLoaderTest.assertValidationSuccess(model.validate(torrentObject));
    }

    @Test
    public void testValidateTorrent_MissingProperty() {
        Object torrentObject = readTorrent("single_file_missing_property.torrent");
        YamlBEObjectModelLoaderTest.assertValidationFailure(model.validate(torrentObject), "properties are required: [piece length, pieces]");
    }

    @Test
    public void testValidateTorrent_InvalidType() {
        Object torrentObject = readTorrent("single_file_invalid_type.torrent");
        YamlBEObjectModelLoaderTest.assertValidationFailure(model.validate(torrentObject), "Wrong type -- expected java.math.BigInteger");
    }

    @Test
    public void testValidateTorrent_MutuallyExclusiveProperties() {
        Object torrentObject = readTorrent("single_file_exclusive_properties.torrent");
        YamlBEObjectModelLoaderTest.assertValidationFailure(model.validate(torrentObject), "properties are mutually exclusive: [[length], [files]]");
    }
}

