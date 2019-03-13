package io.socket.parser;


import IOParser.Decoder;
import Parser.Encoder;
import java.nio.charset.Charset;
import org.hamcrest.CoreMatchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Parser.BINARY_ACK;
import static Parser.BINARY_EVENT;


@RunWith(JUnit4.class)
public class ByteArrayTest {
    private static Encoder encoder = new IOParser.Encoder();

    @Test
    public void encodeByteArray() {
        Packet<byte[]> packet = new Packet<byte[]>(BINARY_EVENT);
        packet.data = "abc".getBytes(Charset.forName("UTF-8"));
        packet.id = 23;
        packet.nsp = "/cool";
        Helpers.testBin(packet);
    }

    @Test
    public void encodeByteArray2() {
        Packet<byte[]> packet = new Packet<byte[]>(BINARY_EVENT);
        packet.data = new byte[2];
        packet.id = 0;
        packet.nsp = "/";
        Helpers.testBin(packet);
    }

    @Test
    public void encodeByteArrayDeepInJson() throws JSONException {
        JSONObject data = new JSONObject("{a: \"hi\", b: {}, c: {a: \"bye\", b: {}}}");
        data.getJSONObject("b").put("why", new byte[3]);
        data.getJSONObject("c").getJSONObject("b").put("a", new byte[6]);
        Packet<JSONObject> packet = new Packet<JSONObject>(BINARY_EVENT);
        packet.data = data;
        packet.id = 999;
        packet.nsp = "/deep";
        Helpers.testBin(packet);
    }

    @Test
    public void encodeDeepBinaryJSONWithNullValue() throws JSONException {
        JSONObject data = new JSONObject("{a: \"b\", c: 4, e: {g: null}, h: null}");
        data.put("h", new byte[9]);
        Packet<JSONObject> packet = new Packet<JSONObject>(BINARY_EVENT);
        packet.data = data;
        packet.nsp = "/";
        packet.id = 600;
        Helpers.testBin(packet);
    }

    @Test
    public void encodeBinaryAckWithByteArray() throws JSONException {
        JSONArray data = new JSONArray("[a, null, {}]");
        data.put(1, "xxx".getBytes(Charset.forName("UTF-8")));
        Packet<JSONArray> packet = new Packet<JSONArray>(BINARY_ACK);
        packet.data = data;
        packet.id = 127;
        packet.nsp = "/back";
        Helpers.testBin(packet);
    }

    @Test
    public void cleanItselfUpOnClose() {
        JSONArray data = new JSONArray();
        data.put(new byte[2]);
        data.put(new byte[3]);
        Packet<JSONArray> packet = new Packet<JSONArray>(BINARY_EVENT);
        packet.data = data;
        packet.id = 0;
        packet.nsp = "/";
        ByteArrayTest.encoder.encode(packet, new Parser.Encoder.Callback() {
            @Override
            public void call(final Object[] encodedPackets) {
                final IOParser.Decoder decoder = new IOParser.Decoder();
                decoder.onDecoded(new Parser.Decoder.Callback() {
                    @Override
                    public void call(Packet packet) {
                        throw new RuntimeException("received a packet when not all binary data was sent.");
                    }
                });
                decoder.add(((String) (encodedPackets[0])));
                decoder.add(((byte[]) (encodedPackets[1])));
                decoder.destroy();
                Assert.assertThat(decoder.reconstructor.buffers.size(), CoreMatchers.is(0));
            }
        });
    }
}

