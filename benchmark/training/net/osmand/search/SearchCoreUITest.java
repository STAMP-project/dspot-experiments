package net.osmand.search;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.osmand.OsmAndCollator;
import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.data.Amenity;
import net.osmand.data.Building;
import net.osmand.data.City;
import net.osmand.data.LatLon;
import net.osmand.data.MapObject;
import net.osmand.data.Street;
import net.osmand.osm.AbstractPoiType;
import net.osmand.osm.MapPoiTypes;
import net.osmand.search.SearchUICore.SearchResultCollection;
import net.osmand.search.core.SearchPhrase;
import net.osmand.search.core.SearchResult;
import net.osmand.search.core.SearchSettings;
import net.osmand.util.Algorithms;
import org.junit.Assert;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;


public class SearchCoreUITest {
    private static final String SEARCH_RESOURCES_PATH = "src/test/resources/search/";

    private static Map<String, String> enPhrases = new HashMap<>();

    private static Map<String, String> phrases = new HashMap<>();

    static {
        MapPoiTypes.setDefault(new MapPoiTypes("src/test/resources/poi_types.xml"));
        MapPoiTypes poiTypes = MapPoiTypes.getDefault();
        try {
            SearchCoreUITest.enPhrases = Algorithms.parseStringsXml(new File("src/test/resources/phrases/en/phrases.xml"));
            // phrases = Algorithms.parseStringsXml(new File("src/test/resources/phrases/ru/phrases.xml"));
            SearchCoreUITest.phrases = SearchCoreUITest.enPhrases;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        poiTypes.setPoiTranslator(new MapPoiTypes.PoiTranslator() {
            @Override
            public String getTranslation(AbstractPoiType type) {
                AbstractPoiType baseLangType = type.getBaseLangType();
                if (baseLangType != null) {
                    return (((getTranslation(baseLangType)) + " (") + (type.getLang().toLowerCase())) + ")";
                }
                return getTranslation(type.getIconKeyName());
            }

            @Override
            public String getTranslation(String keyName) {
                String val = SearchCoreUITest.phrases.get(("poi_" + keyName));
                if (val != null) {
                    int ind = val.indexOf(';');
                    if (ind > 0) {
                        return val.substring(0, ind);
                    }
                }
                return val;
            }

            @Override
            public String getSynonyms(AbstractPoiType type) {
                AbstractPoiType baseLangType = type.getBaseLangType();
                if (baseLangType != null) {
                    return getSynonyms(baseLangType);
                }
                return getSynonyms(type.getIconKeyName());
            }

            @Override
            public String getSynonyms(String keyName) {
                String val = SearchCoreUITest.phrases.get(("poi_" + keyName));
                if (val != null) {
                    int ind = val.indexOf(';');
                    if (ind > 0) {
                        return val.substring((ind + 1));
                    }
                    return "";
                }
                return null;
            }

            @Override
            public String getEnTranslation(AbstractPoiType type) {
                AbstractPoiType baseLangType = type.getBaseLangType();
                if (baseLangType != null) {
                    return (((getEnTranslation(baseLangType)) + " (") + (type.getLang().toLowerCase())) + ")";
                }
                return getEnTranslation(type.getIconKeyName());
            }

            @Override
            public String getEnTranslation(String keyName) {
                if (SearchCoreUITest.enPhrases.isEmpty()) {
                    return Algorithms.capitalizeFirstLetter(keyName.replace('_', ' '));
                }
                String val = SearchCoreUITest.enPhrases.get(("poi_" + keyName));
                if (val != null) {
                    int ind = val.indexOf(';');
                    if (ind > 0) {
                        return val.substring(0, ind);
                    }
                }
                return val;
            }
        });
    }

    @Test
    public void testDuplicates() throws IOException {
        SearchSettings ss = new SearchSettings(((SearchSettings) (null)));
        ss = ss.setOriginalLocation(new LatLon(0, 0));
        SearchPhrase phrase = new SearchPhrase(ss, OsmAndCollator.primaryCollator());
        SearchResultCollection cll = new SearchUICore.SearchResultCollection(phrase);
        List<SearchResult> rs = new ArrayList<>();
        SearchResult a1 = searchResult(rs, phrase, "a", 100);
        SearchResult b2 = searchResult(rs, phrase, "b", 200);
        SearchResult b1 = searchResult(rs, phrase, "b", 100);
        searchResult(rs, phrase, "a", 100);
        cll.addSearchResults(rs, true, true);
        Assert.assertEquals(3, cll.getCurrentSearchResults().size());
        Assert.assertSame(a1, cll.getCurrentSearchResults().get(0));
        Assert.assertSame(b1, cll.getCurrentSearchResults().get(1));
        Assert.assertSame(b2, cll.getCurrentSearchResults().get(2));
    }

    @Test
    public void testNoResort() throws IOException {
        SearchSettings ss = new SearchSettings(((SearchSettings) (null)));
        ss = ss.setOriginalLocation(new LatLon(0, 0));
        SearchPhrase phrase = new SearchPhrase(ss, OsmAndCollator.primaryCollator());
        SearchResultCollection cll = new SearchUICore.SearchResultCollection(phrase);
        List<SearchResult> rs = new ArrayList<>();
        SearchResult a1 = searchResult(rs, phrase, "a", 100);
        cll.addSearchResults(rs, false, true);
        rs.clear();
        SearchResult b2 = searchResult(rs, phrase, "b", 200);
        cll.addSearchResults(rs, false, true);
        rs.clear();
        SearchResult b1 = searchResult(rs, phrase, "b", 100);
        cll.addSearchResults(rs, false, true);
        rs.clear();
        searchResult(rs, phrase, "a", 100);
        cll.addSearchResults(rs, false, true);
        rs.clear();
        Assert.assertEquals(3, cll.getCurrentSearchResults().size());
        Assert.assertSame(a1, cll.getCurrentSearchResults().get(0));
        Assert.assertSame(b2, cll.getCurrentSearchResults().get(1));
        Assert.assertSame(b1, cll.getCurrentSearchResults().get(2));
    }

    @Test
    public void testNoResortDuplicate() throws IOException {
        SearchSettings ss = new SearchSettings(((SearchSettings) (null)));
        ss = ss.setOriginalLocation(new LatLon(0, 0));
        SearchPhrase phrase = new SearchPhrase(ss, OsmAndCollator.primaryCollator());
        SearchResultCollection cll = new SearchUICore.SearchResultCollection(phrase);
        List<SearchResult> rs = new ArrayList<>();
        SearchResult a1 = searchResult(rs, phrase, "a", 100);
        SearchResult b2 = searchResult(rs, phrase, "b", 200);
        SearchResult b1 = searchResult(rs, phrase, "b", 100);
        cll.addSearchResults(rs, false, true);
        rs.clear();
        searchResult(rs, phrase, "a", 100);
        cll.addSearchResults(rs, false, true);
        rs.clear();
        Assert.assertEquals(3, cll.getCurrentSearchResults().size());
        Assert.assertSame(a1, cll.getCurrentSearchResults().get(0));
        Assert.assertSame(b1, cll.getCurrentSearchResults().get(1));
        Assert.assertSame(b2, cll.getCurrentSearchResults().get(2));
    }

    @Test
    public void testSearchJsons() throws IOException {
        final File[] files = new File(SearchCoreUITest.SEARCH_RESOURCES_PATH).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".json");
            }
        });
        if (files != null) {
            for (File f : files) {
                // testSearchImpl(f);
            }
        }
    }

    private static class BinaryMapIndexReaderTest extends BinaryMapIndexReader {
        List<Amenity> amenities = Collections.emptyList();

        List<City> cities = Collections.emptyList();

        List<City> initCities = Collections.emptyList();

        List<City> matchedCities = Collections.emptyList();

        List<City> streetCities = Collections.emptyList();

        BinaryMapIndexReaderTest() throws IOException {
            super(null, null, false);
        }

        @Override
        public List<Amenity> searchPoiByName(SearchRequest<Amenity> req) throws IOException {
            for (Amenity amenity : amenities) {
                req.publish(amenity);
            }
            return req.getSearchResults();
        }

        @Override
        public List<Amenity> searchPoi(SearchRequest<Amenity> req) throws IOException {
            for (Amenity amenity : amenities) {
                req.publish(amenity);
            }
            return req.getSearchResults();
        }

        @Override
        public List<City> getCities(SearchRequest<City> resultMatcher, int cityType) throws IOException {
            for (City city : initCities) {
                if (resultMatcher != null) {
                    resultMatcher.publish(city);
                }
            }
            return initCities;
        }

        @Override
        public int preloadStreets(City c, SearchRequest<Street> resultMatcher) throws IOException {
            return 0;
        }

        @Override
        public void preloadBuildings(Street s, SearchRequest<Building> resultMatcher) throws IOException {
            // cities must be filled with streets and buildings
        }

        @Override
        public List<MapObject> searchAddressDataByName(SearchRequest<MapObject> req) throws IOException {
            for (City city : streetCities) {
                for (Street street : city.getStreets()) {
                    req.publish(street);
                }
            }
            for (City city : matchedCities) {
                req.publish(city);
            }
            return req.getSearchResults();
        }

        @Override
        public String getRegionName() {
            return "Test region";
        }

        @Override
        public boolean containsPoiData(int left31x, int top31y, int right31x, int bottom31y) {
            return true;
        }

        @Override
        public boolean containsMapData() {
            return true;
        }

        @Override
        public boolean containsPoiData() {
            return true;
        }

        @Override
        public boolean containsRouteData() {
            return true;
        }

        @Override
        public boolean containsRouteData(int left31x, int top31y, int right31x, int bottom31y, int zoom) {
            return true;
        }

        @Override
        public boolean containsAddressData(int left31x, int top31y, int right31x, int bottom31y) {
            return true;
        }

        @Override
        public boolean containsMapData(int tile31x, int tile31y, int zoom) {
            return true;
        }

        @Override
        public boolean containsMapData(int left31x, int top31y, int right31x, int bottom31y, int zoom) {
            return true;
        }

        @Override
        public boolean containsAddressData() {
            return true;
        }
    }
}

