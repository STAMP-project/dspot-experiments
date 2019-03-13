package redis.clients.jedis.tests.commands;


import GeoUnit.FT;
import GeoUnit.KM;
import GeoUnit.MI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.util.SafeEncoder;


public class GeoCommandsTest extends JedisCommandTestBase {
    final byte[] bfoo = new byte[]{ 1, 2, 3, 4 };

    final byte[] bA = new byte[]{ 10 };

    final byte[] bB = new byte[]{ 11 };

    final byte[] bC = new byte[]{ 12 };

    final byte[] bD = new byte[]{ 13 };

    final byte[] bNotexist = new byte[]{ 15 };

    @Test
    public void geoadd() {
        long size = jedis.geoadd("foo", 1, 2, "a");
        Assert.assertEquals(1, size);
        size = jedis.geoadd("foo", 2, 3, "a");
        Assert.assertEquals(0, size);
        Map<String, GeoCoordinate> coordinateMap = new HashMap<String, GeoCoordinate>();
        coordinateMap.put("a", new GeoCoordinate(3, 4));
        coordinateMap.put("b", new GeoCoordinate(2, 3));
        coordinateMap.put("c", new GeoCoordinate(3.314, 2.3241));
        size = jedis.geoadd("foo", coordinateMap);
        Assert.assertEquals(2, size);
        // binary
        size = jedis.geoadd(bfoo, 1, 2, bA);
        Assert.assertEquals(1, size);
        size = jedis.geoadd(bfoo, 2, 3, bA);
        Assert.assertEquals(0, size);
        Map<byte[], GeoCoordinate> bcoordinateMap = new HashMap<byte[], GeoCoordinate>();
        bcoordinateMap.put(bA, new GeoCoordinate(3, 4));
        bcoordinateMap.put(bB, new GeoCoordinate(2, 3));
        bcoordinateMap.put(bC, new GeoCoordinate(3.314, 2.3241));
        size = jedis.geoadd(bfoo, bcoordinateMap);
        Assert.assertEquals(2, size);
    }

    @Test
    public void geodist() {
        prepareGeoData();
        Double dist = jedis.geodist("foo", "a", "b");
        Assert.assertEquals(157149, dist.intValue());
        dist = jedis.geodist("foo", "a", "b", KM);
        Assert.assertEquals(157, dist.intValue());
        dist = jedis.geodist("foo", "a", "b", MI);
        Assert.assertEquals(97, dist.intValue());
        dist = jedis.geodist("foo", "a", "b", FT);
        Assert.assertEquals(515583, dist.intValue());
        // binary
        dist = jedis.geodist(bfoo, bA, bB);
        Assert.assertEquals(157149, dist.intValue());
        dist = jedis.geodist(bfoo, bA, bB, KM);
        Assert.assertEquals(157, dist.intValue());
        dist = jedis.geodist(bfoo, bA, bB, MI);
        Assert.assertEquals(97, dist.intValue());
        dist = jedis.geodist(bfoo, bA, bB, FT);
        Assert.assertEquals(515583, dist.intValue());
    }

    @Test
    public void geohash() {
        prepareGeoData();
        List<String> hashes = jedis.geohash("foo", "a", "b", "notexist");
        Assert.assertEquals(3, hashes.size());
        Assert.assertEquals("s0dnu20t9j0", hashes.get(0));
        Assert.assertEquals("s093jd0k720", hashes.get(1));
        Assert.assertNull(hashes.get(2));
        // binary
        List<byte[]> bhashes = jedis.geohash(bfoo, bA, bB, bNotexist);
        Assert.assertEquals(3, bhashes.size());
        Assert.assertArrayEquals(SafeEncoder.encode("s0dnu20t9j0"), bhashes.get(0));
        Assert.assertArrayEquals(SafeEncoder.encode("s093jd0k720"), bhashes.get(1));
        Assert.assertNull(bhashes.get(2));
    }

    @Test
    public void geopos() {
        prepareGeoData();
        List<GeoCoordinate> coordinates = jedis.geopos("foo", "a", "b", "notexist");
        Assert.assertEquals(3, coordinates.size());
        Assert.assertTrue(equalsWithinEpsilon(3.0, coordinates.get(0).getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(4.0, coordinates.get(0).getLatitude()));
        Assert.assertTrue(equalsWithinEpsilon(2.0, coordinates.get(1).getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(3.0, coordinates.get(1).getLatitude()));
        Assert.assertNull(coordinates.get(2));
        List<GeoCoordinate> bcoordinates = jedis.geopos(bfoo, bA, bB, bNotexist);
        Assert.assertEquals(3, bcoordinates.size());
        Assert.assertTrue(equalsWithinEpsilon(3.0, bcoordinates.get(0).getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(4.0, bcoordinates.get(0).getLatitude()));
        Assert.assertTrue(equalsWithinEpsilon(2.0, bcoordinates.get(1).getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(3.0, bcoordinates.get(1).getLatitude()));
        Assert.assertNull(bcoordinates.get(2));
    }

    @Test
    public void georadius() {
        // prepare datas
        Map<String, GeoCoordinate> coordinateMap = new HashMap<String, GeoCoordinate>();
        coordinateMap.put("Palermo", new GeoCoordinate(13.361389, 38.115556));
        coordinateMap.put("Catania", new GeoCoordinate(15.087269, 37.502669));
        jedis.geoadd("Sicily", coordinateMap);
        List<GeoRadiusResponse> members = jedis.georadius("Sicily", 15, 37, 200, KM);
        Assert.assertEquals(2, members.size());
        // sort
        members = jedis.georadius("Sicily", 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertEquals("Catania", members.get(0).getMemberByString());
        Assert.assertEquals("Palermo", members.get(1).getMemberByString());
        // sort, count 1
        members = jedis.georadius("Sicily", 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1));
        Assert.assertEquals(1, members.size());
        // sort, count 1, withdist, withcoord
        members = jedis.georadius("Sicily", 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse response = members.get(0);
        Assert.assertTrue(equalsWithinEpsilon(56.4413, response.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(15.087269, response.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.502669, response.getCoordinate().getLatitude()));
    }

    @Test
    public void georadiusReadonly() {
        // prepare datas
        Map<String, GeoCoordinate> coordinateMap = new HashMap<String, GeoCoordinate>();
        coordinateMap.put("Palermo", new GeoCoordinate(13.361389, 38.115556));
        coordinateMap.put("Catania", new GeoCoordinate(15.087269, 37.502669));
        jedis.geoadd("Sicily", coordinateMap);
        List<GeoRadiusResponse> members = jedis.georadiusReadonly("Sicily", 15, 37, 200, KM);
        Assert.assertEquals(2, members.size());
        // sort
        members = jedis.georadiusReadonly("Sicily", 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertEquals("Catania", members.get(0).getMemberByString());
        Assert.assertEquals("Palermo", members.get(1).getMemberByString());
        // sort, count 1
        members = jedis.georadiusReadonly("Sicily", 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1));
        Assert.assertEquals(1, members.size());
        // sort, count 1, withdist, withcoord
        members = jedis.georadiusReadonly("Sicily", 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse response = members.get(0);
        Assert.assertTrue(equalsWithinEpsilon(56.4413, response.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(15.087269, response.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.502669, response.getCoordinate().getLatitude()));
    }

    @Test
    public void georadiusBinary() {
        // prepare datas
        Map<byte[], GeoCoordinate> bcoordinateMap = new HashMap<byte[], GeoCoordinate>();
        bcoordinateMap.put(bA, new GeoCoordinate(13.361389, 38.115556));
        bcoordinateMap.put(bB, new GeoCoordinate(15.087269, 37.502669));
        jedis.geoadd(bfoo, bcoordinateMap);
        List<GeoRadiusResponse> members = jedis.georadius(bfoo, 15, 37, 200, KM);
        Assert.assertEquals(2, members.size());
        // sort
        members = jedis.georadius(bfoo, 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertArrayEquals(bB, members.get(0).getMember());
        Assert.assertArrayEquals(bA, members.get(1).getMember());
        // sort, count 1
        members = jedis.georadius(bfoo, 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1));
        Assert.assertEquals(1, members.size());
        // sort, count 1, withdist, withcoord
        members = jedis.georadius(bfoo, 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse response = members.get(0);
        Assert.assertTrue(equalsWithinEpsilon(56.4413, response.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(15.087269, response.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.502669, response.getCoordinate().getLatitude()));
    }

    @Test
    public void georadiusReadonlyBinary() {
        // prepare datas
        Map<byte[], GeoCoordinate> bcoordinateMap = new HashMap<byte[], GeoCoordinate>();
        bcoordinateMap.put(bA, new GeoCoordinate(13.361389, 38.115556));
        bcoordinateMap.put(bB, new GeoCoordinate(15.087269, 37.502669));
        jedis.geoadd(bfoo, bcoordinateMap);
        List<GeoRadiusResponse> members = jedis.georadiusReadonly(bfoo, 15, 37, 200, KM);
        Assert.assertEquals(2, members.size());
        // sort
        members = jedis.georadiusReadonly(bfoo, 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertArrayEquals(bB, members.get(0).getMember());
        Assert.assertArrayEquals(bA, members.get(1).getMember());
        // sort, count 1
        members = jedis.georadiusReadonly(bfoo, 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1));
        Assert.assertEquals(1, members.size());
        // sort, count 1, withdist, withcoord
        members = jedis.georadiusReadonly(bfoo, 15, 37, 200, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse response = members.get(0);
        Assert.assertTrue(equalsWithinEpsilon(56.4413, response.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(15.087269, response.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.502669, response.getCoordinate().getLatitude()));
    }

    @Test
    public void georadiusByMember() {
        jedis.geoadd("Sicily", 13.583333, 37.316667, "Agrigento");
        jedis.geoadd("Sicily", 13.361389, 38.115556, "Palermo");
        jedis.geoadd("Sicily", 15.087269, 37.502669, "Catania");
        List<GeoRadiusResponse> members = jedis.georadiusByMember("Sicily", "Agrigento", 100, KM);
        Assert.assertEquals(2, members.size());
        members = jedis.georadiusByMember("Sicily", "Agrigento", 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertEquals("Agrigento", members.get(0).getMemberByString());
        Assert.assertEquals("Palermo", members.get(1).getMemberByString());
        members = jedis.georadiusByMember("Sicily", "Agrigento", 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse member = members.get(0);
        Assert.assertEquals("Agrigento", member.getMemberByString());
        Assert.assertTrue(equalsWithinEpsilon(0, member.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(13.583333, member.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.316667, member.getCoordinate().getLatitude()));
    }

    @Test
    public void georadiusByMemberReadonly() {
        jedis.geoadd("Sicily", 13.583333, 37.316667, "Agrigento");
        jedis.geoadd("Sicily", 13.361389, 38.115556, "Palermo");
        jedis.geoadd("Sicily", 15.087269, 37.502669, "Catania");
        List<GeoRadiusResponse> members = jedis.georadiusByMemberReadonly("Sicily", "Agrigento", 100, KM);
        Assert.assertEquals(2, members.size());
        members = jedis.georadiusByMemberReadonly("Sicily", "Agrigento", 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertEquals("Agrigento", members.get(0).getMemberByString());
        Assert.assertEquals("Palermo", members.get(1).getMemberByString());
        members = jedis.georadiusByMemberReadonly("Sicily", "Agrigento", 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse member = members.get(0);
        Assert.assertEquals("Agrigento", member.getMemberByString());
        Assert.assertTrue(equalsWithinEpsilon(0, member.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(13.583333, member.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.316667, member.getCoordinate().getLatitude()));
    }

    @Test
    public void georadiusByMemberBinary() {
        jedis.geoadd(bfoo, 13.583333, 37.316667, bA);
        jedis.geoadd(bfoo, 13.361389, 38.115556, bB);
        jedis.geoadd(bfoo, 15.087269, 37.502669, bC);
        List<GeoRadiusResponse> members = jedis.georadiusByMember(bfoo, bA, 100, KM);
        Assert.assertEquals(2, members.size());
        members = jedis.georadiusByMember(bfoo, bA, 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertArrayEquals(bA, members.get(0).getMember());
        Assert.assertArrayEquals(bB, members.get(1).getMember());
        members = jedis.georadiusByMember(bfoo, bA, 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse member = members.get(0);
        Assert.assertArrayEquals(bA, member.getMember());
        Assert.assertTrue(equalsWithinEpsilon(0, member.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(13.583333, member.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.316667, member.getCoordinate().getLatitude()));
    }

    @Test
    public void georadiusByMemberReadonlyBinary() {
        jedis.geoadd(bfoo, 13.583333, 37.316667, bA);
        jedis.geoadd(bfoo, 13.361389, 38.115556, bB);
        jedis.geoadd(bfoo, 15.087269, 37.502669, bC);
        List<GeoRadiusResponse> members = jedis.georadiusByMemberReadonly(bfoo, bA, 100, KM);
        Assert.assertEquals(2, members.size());
        members = jedis.georadiusByMemberReadonly(bfoo, bA, 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending());
        Assert.assertEquals(2, members.size());
        Assert.assertArrayEquals(bA, members.get(0).getMember());
        Assert.assertArrayEquals(bB, members.get(1).getMember());
        members = jedis.georadiusByMemberReadonly(bfoo, bA, 100, KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(1).withCoord().withDist());
        Assert.assertEquals(1, members.size());
        GeoRadiusResponse member = members.get(0);
        Assert.assertArrayEquals(bA, member.getMember());
        Assert.assertTrue(equalsWithinEpsilon(0, member.getDistance()));
        Assert.assertTrue(equalsWithinEpsilon(13.583333, member.getCoordinate().getLongitude()));
        Assert.assertTrue(equalsWithinEpsilon(37.316667, member.getCoordinate().getLatitude()));
    }
}

