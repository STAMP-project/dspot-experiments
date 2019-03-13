package com.apollographql.apollo;


import AllFilmsQuery.Film;
import AllPlanetsQuery.Data;
import AllPlanetsQuery.Planet;
import ApolloCall.StatusEvent;
import ApolloCall.StatusEvent.COMPLETED;
import ApolloCall.StatusEvent.FETCH_CACHE;
import ApolloCall.StatusEvent.FETCH_NETWORK;
import ApolloCall.StatusEvent.SCHEDULED;
import ApolloResponseFetchers.CACHE_AND_NETWORK;
import ApolloResponseFetchers.CACHE_ONLY;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.integration.httpcache.AllFilmsQuery;
import com.apollographql.apollo.integration.httpcache.AllPlanetsQuery;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery;
import com.apollographql.apollo.integration.normalizer.HeroNameQuery;
import com.apollographql.apollo.internal.json.JsonWriter;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.OperationJsonWriter;
import com.apollographql.apollo.response.ScalarTypeAdapters;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings("SimpleDateFormatConstant")
public class IntegrationTest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private ApolloClient apolloClient;

    private CustomTypeAdapter<Date> dateCustomTypeAdapter;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @SuppressWarnings({ "ConstantConditions", "CheckReturnValue" })
    @Test
    public void allPlanetQuery() throws Exception {
        server.enqueue(mockResponse("HttpCacheTestAllPlanets.json"));
        IntegrationTest.assertResponse(apolloClient.query(new AllPlanetsQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response) throws Exception {
                assertThat(response.data().allPlanets().planets().size()).isEqualTo(60);
                List<String> planets = FluentIterable.from(response.data().allPlanets().planets()).transform(new Function<AllPlanetsQuery.Planet, String>() {
                    @Override
                    public String apply(AllPlanetsQuery.Planet planet) {
                        return planet.fragments().planetFragment().name();
                    }
                }).toList();
                assertThat(planets).isEqualTo(Arrays.asList(("Tatooine, Alderaan, Yavin IV, Hoth, Dagobah, Bespin, Endor, Naboo, " + (((("Coruscant, Kamino, Geonosis, Utapau, Mustafar, Kashyyyk, Polis Massa, Mygeeto, Felucia, Cato Neimoidia, " + "Saleucami, Stewjon, Eriadu, Corellia, Rodia, Nal Hutta, Dantooine, Bestine IV, Ord Mantell, unknown, ") + "Trandosha, Socorro, Mon Cala, Chandrila, Sullust, Toydaria, Malastare, Dathomir, Ryloth, Aleen Minor, ") + "Vulpter, Troiken, Tund, Haruun Kal, Cerea, Glee Anselm, Iridonia, Tholoth, Iktotch, Quermia, Dorin, ") + "Champala, Mirial, Serenno, Concord Dawn, Zolan, Ojom, Skako, Muunilinst, Shili, Kalee, Umbara")).split("\\s*,\\s*")));
                AllPlanetsQuery.Planet firstPlanet = response.data().allPlanets().planets().get(0);
                assertThat(firstPlanet.fragments().planetFragment().climates()).isEqualTo(Collections.singletonList("arid"));
                assertThat(firstPlanet.fragments().planetFragment().surfaceWater()).isWithin(1.0);
                assertThat(firstPlanet.filmConnection().totalCount()).isEqualTo(5);
                assertThat(firstPlanet.filmConnection().films().size()).isEqualTo(5);
                assertThat(firstPlanet.filmConnection().films().get(0).fragments().filmFragment().title()).isEqualTo("A New Hope");
                assertThat(firstPlanet.filmConnection().films().get(0).fragments().filmFragment().producers()).isEqualTo(Arrays.asList("Gary Kurtz", "Rick McCallum"));
                return true;
            }
        });
        assertThat(server.takeRequest().getBody().readString(Charsets.UTF_8)).isEqualTo(("{\"operationName\":\"AllPlanets\",\"variables\":{}," + (((((((((((((((((((((((((((((("\"extensions\":{\"persistedQuery\":{\"version\":1," + "\"sha256Hash\":\"c4d45ee8b7464957320775df69251c9bba2f1eea82ef9f64146e71eb6e074930\"}},") + "\"query\":\"query AllPlanets {  ") + "allPlanets(first: 300) {") + "    __typename") + "    planets {") + "      __typename") + "      ...PlanetFragment") + "      filmConnection {") + "        __typename") + "        totalCount") + "        films {") + "          __typename") + "          title") + "          ...FilmFragment") + "        }") + "      }") + "    }") + "  }") + "}") + "fragment PlanetFragment on Planet {") + "  __typename") + "  name") + "  climates") + "  surfaceWater") + "}") + "fragment FilmFragment on Film {") + "  __typename") + "  title") + "  producers") + "}\"}")));
    }

    @Test
    public void error_response() throws Exception {
        server.enqueue(mockResponse("ResponseError.json"));
        IntegrationTest.assertResponse(apolloClient.query(new AllPlanetsQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isTrue();
                // noinspection ConstantConditions
                assertThat(response.errors()).containsExactly(new Error("Cannot query field \"names\" on type \"Species\".", Collections.singletonList(new Error.Location(3, 5)), Collections.<String, Object>emptyMap()));
                return true;
            }
        });
    }

    @Test
    public void error_response_with_nulls_and_custom_attributes() throws Exception {
        server.enqueue(mockResponse("ResponseErrorWithNullsAndCustomAttributes.json"));
        IntegrationTest.assertResponse(apolloClient.query(new AllPlanetsQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isTrue();
                assertThat(response.errors()).hasSize(1);
                assertThat(response.errors().get(0).message()).isNull();
                assertThat(response.errors().get(0).customAttributes()).hasSize(2);
                assertThat(response.errors().get(0).customAttributes().get("code")).isEqualTo("userNotFound");
                assertThat(response.errors().get(0).customAttributes().get("path")).isEqualTo("loginWithPassword");
                assertThat(response.errors().get(0).locations()).hasSize(0);
                return true;
            }
        });
    }

    @Test
    public void errorResponse_custom_attributes() throws Exception {
        server.enqueue(mockResponse("ResponseErrorWithCustomAttributes.json"));
        IntegrationTest.assertResponse(apolloClient.query(new AllPlanetsQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isTrue();
                assertThat(response.errors().get(0).customAttributes()).hasSize(4);
                assertThat(response.errors().get(0).customAttributes().get("code")).isEqualTo(new BigDecimal(500));
                assertThat(response.errors().get(0).customAttributes().get("status")).isEqualTo("Internal Error");
                assertThat(response.errors().get(0).customAttributes().get("fatal")).isEqualTo(true);
                assertThat(response.errors().get(0).customAttributes().get("path")).isEqualTo(Arrays.asList("query"));
                return true;
            }
        });
    }

    @Test
    public void errorResponse_with_data() throws Exception {
        server.enqueue(mockResponse("ResponseErrorWithData.json"));
        IntegrationTest.assertResponse(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(JEDI))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                assertThat(response.data()).isNotNull();
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.errors()).containsExactly(new Error("Cannot query field \"names\" on type \"Species\".", Collections.singletonList(new Error.Location(3, 5)), Collections.<String, Object>emptyMap()));
                return true;
            }
        });
    }

    @Test
    public void allFilmsWithDate() throws Exception {
        server.enqueue(mockResponse("HttpCacheTestAllFilms.json"));
        IntegrationTest.assertResponse(apolloClient.query(new AllFilmsQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllFilmsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllFilmsQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data().allFilms().films()).hasSize(6);
                List<String> dates = FluentIterable.from(response.data().allFilms().films()).transform(new Function<AllFilmsQuery.Film, String>() {
                    @Override
                    public String apply(AllFilmsQuery.Film film) {
                        Date releaseDate = film.releaseDate();
                        return dateCustomTypeAdapter.encode(releaseDate).value.toString();
                    }
                }).copyInto(new ArrayList<String>());
                assertThat(dates).isEqualTo(Arrays.asList("1977-05-25", "1980-05-17", "1983-05-25", "1999-05-19", "2002-05-16", "2005-05-19"));
                return true;
            }
        });
    }

    @Test
    public void dataNull() throws Exception {
        server.enqueue(mockResponse("ResponseDataNull.json"));
        IntegrationTest.assertResponse(apolloClient.query(new HeroNameQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameQuery.Data> response) throws Exception {
                assertThat(response.data()).isNull();
                assertThat(response.hasErrors()).isFalse();
                return true;
            }
        });
    }

    @Test
    public void fieldMissing() throws Exception {
        server.enqueue(mockResponse("ResponseDataMissing.json"));
        com.apollographql.apollo.rx2.Rx2Apollo.from(apolloClient.query(new HeroNameQuery())).test().assertError(ApolloException.class);
    }

    @Test
    public void statusEvents() throws Exception {
        server.enqueue(mockResponse("HeroNameResponse.json"));
        List<ApolloCall.StatusEvent> statusEvents = enqueueCall(apolloClient.query(new HeroNameQuery()));
        assertThat(statusEvents).isEqualTo(Arrays.asList(SCHEDULED, FETCH_NETWORK, COMPLETED));
        statusEvents = enqueueCall(apolloClient.query(new HeroNameQuery()).responseFetcher(CACHE_ONLY));
        assertThat(statusEvents).isEqualTo(Arrays.asList(SCHEDULED, FETCH_CACHE, COMPLETED));
        server.enqueue(mockResponse("HeroNameResponse.json"));
        statusEvents = enqueueCall(apolloClient.query(new HeroNameQuery()).responseFetcher(CACHE_AND_NETWORK));
        assertThat(statusEvents).isEqualTo(Arrays.asList(SCHEDULED, FETCH_CACHE, FETCH_NETWORK, COMPLETED));
    }

    @Test
    public void operationResponseParser() throws Exception {
        String json = Utils.readFileToString(getClass(), "/HeroNameResponse.json");
        HeroNameQuery query = new HeroNameQuery();
        com.apollographql.apollo.api.Response<HeroNameQuery.Data> response = new com.apollographql.apollo.response.OperationResponseParser(query, query.responseFieldMapper(), new ScalarTypeAdapters(Collections.EMPTY_MAP)).parse(new Buffer().writeUtf8(json));
        assertThat(response.data().hero().name()).isEqualTo("R2-D2");
    }

    @Test
    public void operationJsonWriter() throws Exception {
        String json = Utils.readFileToString(getClass(), "/OperationJsonWriter.json");
        AllPlanetsQuery query = new AllPlanetsQuery();
        com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response = new com.apollographql.apollo.response.OperationResponseParser(query, query.responseFieldMapper(), new ScalarTypeAdapters(Collections.EMPTY_MAP)).parse(new Buffer().writeUtf8(json));
        Buffer buffer = new Buffer();
        OperationJsonWriter writer = new OperationJsonWriter(response.data(), new ScalarTypeAdapters(Collections.EMPTY_MAP));
        JsonWriter jsonWriter = JsonWriter.of(buffer);
        jsonWriter.setIndent("  ");
        writer.write(jsonWriter);
        assertThat(buffer.readUtf8()).isEqualTo(json);
    }
}

