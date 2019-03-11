package com.apollographql.apollo;


import CacheHeaders.NONE;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.CacheReference;
import com.apollographql.apollo.cache.normalized.NormalizedCache;
import com.apollographql.apollo.cache.normalized.Record;
import com.apollographql.apollo.integration.httpcache.AllPlanetsQuery;
import com.apollographql.apollo.integration.normalizer.HeroAppearsInQuery;
import com.apollographql.apollo.integration.normalizer.HeroNameQuery;
import com.apollographql.apollo.integration.normalizer.SameHeroTwiceQuery;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;


public class ResponseNormalizationTest {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    private NormalizedCache normalizedCache;

    private static final String TEST_FIELD_KEY_JEDI = "hero({\"episode\":\"JEDI\"})";

    public static final String TEST_FIELD_KEY_EMPIRE = "hero({\"episode\":\"EMPIRE\"})";

    private final String QUERY_ROOT_KEY = "QUERY_ROOT";

    @Test
    public void testHeroName() throws Exception {
        assertHasNoErrors("HeroNameResponse.json", new HeroNameQuery());
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference reference = ((CacheReference) (record.field("hero")));
        assertThat(reference).isEqualTo(new CacheReference("hero"));
        final Record heroRecord = normalizedCache.loadRecord(reference.key(), NONE);
        assertThat(heroRecord.field("name")).isEqualTo("R2-D2");
    }

    @Test
    public void testMergeNull() throws Exception {
        Record record = Record.builder("Key").addField("field1", "value1").build();
        normalizedCache.merge(Collections.singletonList(record), NONE);
        Record newRecord = record.toBuilder().addField("field2", null).build();
        normalizedCache.merge(Collections.singletonList(newRecord), NONE);
        final Record finalRecord = normalizedCache.loadRecord(record.key(), NONE);
        assertThat(finalRecord.hasField("field2")).isTrue();
        normalizedCache.remove(CacheKey.from(record.key()));
    }

    @Test
    public void testHeroNameWithVariable() throws Exception {
        assertHasNoErrors("EpisodeHeroNameResponse.json", new com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery(Input.fromNullable(JEDI)));
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference reference = ((CacheReference) (record.field(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI)));
        assertThat(reference).isEqualTo(new CacheReference(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI));
        final Record heroRecord = normalizedCache.loadRecord(reference.key(), NONE);
        assertThat(heroRecord.field("name")).isEqualTo("R2-D2");
    }

    @Test
    public void testHeroAppearsInQuery() throws Exception {
        assertHasNoErrors("HeroAppearsInResponse.json", new HeroAppearsInQuery());
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field("hero")));
        assertThat(heroReference).isEqualTo(new CacheReference("hero"));
        final Record hero = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(hero.field("appearsIn")).isEqualTo(Arrays.asList("NEWHOPE", "EMPIRE", "JEDI"));
    }

    @Test
    public void testHeroAndFriendsNamesQueryWithoutIDs() throws Exception {
        assertHasNoErrors("HeroAndFriendsNameResponse.json", new com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesQuery(Input.fromNullable(JEDI)));
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI)));
        assertThat(heroReference).isEqualTo(new CacheReference(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI));
        final Record heroRecord = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(heroRecord.field("name")).isEqualTo("R2-D2");
        assertThat(heroRecord.field("friends")).isEqualTo(Arrays.asList(new CacheReference(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.0")), new CacheReference(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.1")), new CacheReference(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.2"))));
        final Record luke = normalizedCache.loadRecord(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.0"), NONE);
        assertThat(luke.field("name")).isEqualTo("Luke Skywalker");
    }

    @Test
    public void testHeroAndFriendsNamesQueryWithIDs() throws Exception {
        assertHasNoErrors("HeroAndFriendsNameWithIdsResponse.json", new com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(JEDI)));
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI)));
        assertThat(heroReference).isEqualTo(new CacheReference("2001"));
        final Record heroRecord = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(heroRecord.field("name")).isEqualTo("R2-D2");
        assertThat(heroRecord.field("friends")).isEqualTo(Arrays.asList(new CacheReference("1000"), new CacheReference("1002"), new CacheReference("1003")));
        final Record luke = normalizedCache.loadRecord("1000", NONE);
        assertThat(luke.field("name")).isEqualTo("Luke Skywalker");
    }

    @Test
    public void testHeroAndFriendsNamesWithIDForParentOnly() throws Exception {
        assertHasNoErrors("HeroAndFriendsNameWithIdsParentOnlyResponse.json", new com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDForParentOnlyQuery(Input.fromNullable(JEDI)));
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI)));
        assertThat(heroReference).isEqualTo(new CacheReference("2001"));
        final Record heroRecord = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(heroRecord.field("name")).isEqualTo("R2-D2");
        assertThat(heroRecord.field("friends")).isEqualTo(Arrays.asList(new CacheReference("2001.friends.0"), new CacheReference("2001.friends.1"), new CacheReference("2001.friends.2")));
        final Record luke = normalizedCache.loadRecord("2001.friends.0", NONE);
        assertThat(luke.field("name")).isEqualTo("Luke Skywalker");
    }

    @Test
    public void testSameHeroTwiceQuery() throws Exception {
        assertHasNoErrors("SameHeroTwiceResponse.json", new SameHeroTwiceQuery());
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field("hero")));
        final Record hero = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(hero.field("name")).isEqualTo("R2-D2");
        assertThat(hero.field("appearsIn")).isEqualTo(Arrays.asList("NEWHOPE", "EMPIRE", "JEDI"));
    }

    @Test
    public void testHeroTypeDependentAliasedFieldQueryDroid() throws Exception {
        assertHasNoErrors("HeroTypeDependentAliasedFieldResponse.json", new com.apollographql.apollo.integration.normalizer.HeroTypeDependentAliasedFieldQuery(Input.fromNullable(JEDI)));
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI)));
        final Record hero = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(hero.field("primaryFunction")).isEqualTo("Astromech");
        assertThat(hero.field("__typename")).isEqualTo("Droid");
    }

    @Test
    public void testHeroTypeDependentAliasedFieldQueryHuman() throws Exception {
        assertHasNoErrors("HeroTypeDependentAliasedFieldResponseHuman.json", new com.apollographql.apollo.integration.normalizer.HeroTypeDependentAliasedFieldQuery(Input.fromNullable(EMPIRE)));
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field(ResponseNormalizationTest.TEST_FIELD_KEY_EMPIRE)));
        final Record hero = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(hero.field("homePlanet")).isEqualTo("Tatooine");
        assertThat(hero.field("__typename")).isEqualTo("Human");
    }

    @Test
    public void testHeroParentTypeDependentAliasedFieldQueryHuman() throws Exception {
        assertHasNoErrors("HeroTypeDependentAliasedFieldResponseHuman.json", new com.apollographql.apollo.integration.normalizer.HeroTypeDependentAliasedFieldQuery(Input.fromNullable(EMPIRE)));
        Record record = normalizedCache.loadRecord(QUERY_ROOT_KEY, NONE);
        CacheReference heroReference = ((CacheReference) (record.field(ResponseNormalizationTest.TEST_FIELD_KEY_EMPIRE)));
        final Record hero = normalizedCache.loadRecord(heroReference.key(), NONE);
        assertThat(hero.field("homePlanet")).isEqualTo("Tatooine");
        assertThat(hero.field("__typename")).isEqualTo("Human");
    }

    @Test
    public void testHeroParentTypeDependentFieldDroid() throws Exception {
        assertHasNoErrors("HeroParentTypeDependentFieldDroidResponse.json", new com.apollographql.apollo.integration.normalizer.HeroParentTypeDependentFieldQuery(Input.fromNullable(JEDI)));
        Record lukeRecord = normalizedCache.loadRecord(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.0"), NONE);
        assertThat(lukeRecord.field("name")).isEqualTo("Luke Skywalker");
        assertThat(lukeRecord.field("height({\"unit\":\"METER\"})")).isEqualTo(BigDecimal.valueOf(1.72));
        final List<Object> friends = ((List<Object>) (normalizedCache.loadRecord(ResponseNormalizationTest.TEST_FIELD_KEY_JEDI, NONE).field("friends")));
        assertThat(friends.get(0)).isEqualTo(new CacheReference(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.0")));
        assertThat(friends.get(1)).isEqualTo(new CacheReference(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.1")));
        assertThat(friends.get(2)).isEqualTo(new CacheReference(((ResponseNormalizationTest.TEST_FIELD_KEY_JEDI) + ".friends.2")));
    }

    @Test
    public void testHeroParentTypeDependentFieldHuman() throws Exception {
        assertHasNoErrors("HeroParentTypeDependentFieldHumanResponse.json", new com.apollographql.apollo.integration.normalizer.HeroParentTypeDependentFieldQuery(Input.fromNullable(EMPIRE)));
        Record lukeRecord = normalizedCache.loadRecord(((ResponseNormalizationTest.TEST_FIELD_KEY_EMPIRE) + ".friends.0"), NONE);
        assertThat(lukeRecord.field("name")).isEqualTo("Han Solo");
        assertThat(lukeRecord.field("height({\"unit\":\"FOOT\"})")).isEqualTo(BigDecimal.valueOf(5.905512));
    }

    @Test
    public void list_of_objects_with_null_object() throws Exception {
        assertHasNoErrors("AllPlanetsListOfObjectWithNullObject.json", new AllPlanetsQuery());
        String fieldKey = "allPlanets({\"first\":300})";
        Record record = normalizedCache.loadRecord((fieldKey + ".planets.0"), NONE);
        assertThat(record.field("filmConnection")).isNull();
        record = normalizedCache.loadRecord((fieldKey + ".planets.0.filmConnection"), NONE);
        assertThat(record).isNull();
        record = normalizedCache.loadRecord((fieldKey + ".planets.1.filmConnection"), NONE);
        assertThat(record).isNotNull();
    }
}

