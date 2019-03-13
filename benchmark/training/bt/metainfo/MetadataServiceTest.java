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
package bt.metainfo;


import bt.bencoding.BtParseException;
import bt.tracker.AnnounceKey;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class MetadataServiceTest {
    private IMetadataService metadataService;

    @Test
    public void testBuildTorrent_SingleFile() throws Exception {
        Torrent torrent = metadataService.fromUrl(MetadataServiceTest.class.getResource("single_file.torrent"));
        assertHasAttributes(torrent, Optional.of(new AnnounceKey("http://jupiter.gx/ann")), "Arch-Uni-i686.iso", 524288L, 1766, 925892608L, Optional.of(Instant.ofEpochMilli(1359439420000L)), Optional.of("sadpotato"));
        Assert.assertNotNull(torrent.getFiles());
        Assert.assertEquals(1, torrent.getFiles().size());
        // TODO: add check for the torrent file
    }

    @Test
    public void testBuildTorrent_SingleFile_InfoDictionaryOnly() throws Exception {
        Torrent torrent = metadataService.fromUrl(MetadataServiceTest.class.getResource("single_file_info_dictionary.bin"));
        assertHasAttributes(torrent, Optional.empty(), "Arch-Uni-i686.iso", 524288L, 1766, 925892608L, Optional.empty(), Optional.empty());
        Assert.assertNotNull(torrent.getFiles());
        Assert.assertEquals(1, torrent.getFiles().size());
        // TODO: add check for the torrent file
    }

    @Test
    public void testBuildTorrent_MultiFile() throws Exception {
        Torrent torrent = metadataService.fromUrl(MetadataServiceTest.class.getResource("multi_file.torrent"));
        AnnounceKey announceKey = new AnnounceKey(Arrays.asList(Collections.singletonList("http://jupiter.gx/ann"), Collections.singletonList("http://jupiter.local/announce")));
        assertHasAttributes(torrent, Optional.of(announceKey), "BEWARE_BACH", 4194304L, 1329, 5573061611L, Optional.of(Instant.ofEpochMilli(1290553306000L)), Optional.of("sadpotato"));
        Assert.assertNotNull(torrent.getFiles());
        Assert.assertEquals(6, torrent.getFiles().size());
        // TODO: add checks for all torrent files
    }

    @Test
    public void testBuildTorrent_ParseExceptionContents() {
        String metainfo = "d8:announce15:http://t.co/ann####";
        byte[] bytes = metainfo.getBytes(Charset.forName("ASCII"));
        BtParseException exception = null;
        try {
            metadataService.fromByteArray(bytes);
        } catch (BtParseException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
        Assert.assertArrayEquals(Arrays.copyOfRange(bytes, 0, 30), exception.getScannedContents());
    }
}

