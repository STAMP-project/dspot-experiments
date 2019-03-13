package org.mp4parser.muxer.tracks;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;
import org.mp4parser.Container;
import org.mp4parser.muxer.InMemRandomAccessSourceImpl;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.builder.FragmentedMp4Builder;
import org.mp4parser.muxer.builder.Mp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.encryption.CencDecryptingTrackImpl;
import org.mp4parser.muxer.tracks.encryption.CencEncryptedTrack;
import org.mp4parser.tools.ByteBufferByteChannel;


public class CencTracksImplTest {
    @Test
    public void testEncryptDecryptDefaultMp4() throws Exception {
        SecretKey sk = new SecretKeySpec(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "AES");
        Movie m = MovieCreator.build(((CencTracksImplTest.class.getProtectionDomain().getCodeSource().getLocation().getFile()) + "/org/mp4parser/muxer/samples/1365070268951.mp4"));
        List<Track> encTracks = new LinkedList<Track>();
        for (Track track : m.getTracks()) {
            encTracks.add(new org.mp4parser.muxer.tracks.encryption.CencEncryptingTrackImpl(track, UUID.randomUUID(), sk, false));
        }
        m.setTracks(encTracks);
        Mp4Builder mp4Builder = new DefaultMp4Builder();
        Container c = mp4Builder.build(m);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        c.writeContainer(Channels.newChannel(baos));
        // c.writeContainer(new FileOutputStream("output.mp4").getChannel());
        Movie m2 = MovieCreator.build(new ByteBufferByteChannel(baos.toByteArray()), new InMemRandomAccessSourceImpl(baos.toByteArray()), "inmem");
        List<Track> decTracks = new LinkedList<Track>();
        for (Track track : m2.getTracks()) {
            decTracks.add(new CencDecryptingTrackImpl(((CencEncryptedTrack) (track)), sk));
        }
        m2.setTracks(decTracks);
        c = mp4Builder.build(m2);
        // c.writeContainer(new FileOutputStream("output2.mp4").getChannel());
    }

    @Test
    public void testEncryptDecryptFragmentedMp4() throws Exception {
        SecretKey sk = new SecretKeySpec(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "AES");
        Movie m = MovieCreator.build(((CencTracksImplTest.class.getProtectionDomain().getCodeSource().getLocation().getFile()) + "/org/mp4parser/muxer/samples/1365070268951.mp4"));
        List<Track> encTracks = new LinkedList<Track>();
        for (Track track : m.getTracks()) {
            encTracks.add(new org.mp4parser.muxer.tracks.encryption.CencEncryptingTrackImpl(track, UUID.randomUUID(), sk, false));
        }
        m.setTracks(encTracks);
        Mp4Builder mp4Builder = new FragmentedMp4Builder();
        Container c = mp4Builder.build(m);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        c.writeContainer(Channels.newChannel(baos));
        // c.writeContainer(new FileOutputStream("output.mp4").getChannel());
        Movie m2 = MovieCreator.build(new ByteBufferByteChannel(baos.toByteArray()), new InMemRandomAccessSourceImpl(baos.toByteArray()), "inmem");
        List<Track> decTracks = new LinkedList<Track>();
        for (Track track : m2.getTracks()) {
            decTracks.add(new CencDecryptingTrackImpl(((CencEncryptedTrack) (track)), sk));
        }
        m2.setTracks(decTracks);
        c = mp4Builder.build(m2);
        // c.writeContainer(new FileOutputStream("output2.mp4").getChannel());
    }

    @Test
    public void testEncryptDecryptCbc1FragmentedMp4() throws Exception {
        UUID keyId = UUID.randomUUID();
        SecretKey key = new SecretKeySpec(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, "AES");
        Movie m = MovieCreator.build(((CencTracksImplTest.class.getProtectionDomain().getCodeSource().getLocation().getFile()) + "/org/mp4parser/muxer/samples/1365070268951.mp4"));
        List<Track> encTracks = new LinkedList<Track>();
        for (Track track : m.getTracks()) {
            encTracks.add(new org.mp4parser.muxer.tracks.encryption.CencEncryptingTrackImpl(track, new org.mp4parser.tools.RangeStartMap(0, keyId), Collections.singletonMap(keyId, key), "cbc1", true, false));
        }
        m.setTracks(encTracks);
        Mp4Builder mp4Builder = new FragmentedMp4Builder();
        Container c = mp4Builder.build(m);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        c.writeContainer(Channels.newChannel(baos));
        // c.writeContainer(new FileOutputStream("output.mp4").getChannel());
        Movie m2 = MovieCreator.build(new ByteBufferByteChannel(baos.toByteArray()), new InMemRandomAccessSourceImpl(baos.toByteArray()), "inmem");
        List<Track> decTracks = new LinkedList<Track>();
        for (Track track : m2.getTracks()) {
            decTracks.add(new CencDecryptingTrackImpl(((CencEncryptedTrack) (track)), key));
        }
        m2.setTracks(decTracks);
        c = mp4Builder.build(m2);
        c.writeContainer(new FileOutputStream("output2.mp4").getChannel());
    }
}

