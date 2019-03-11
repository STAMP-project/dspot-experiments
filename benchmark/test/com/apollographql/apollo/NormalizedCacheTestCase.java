package com.apollographql.apollo;


import Episode.EMPIRE;
import Episode.JEDI;
import Episode.NEWHOPE;
import EpisodeHeroNameQuery.Data;
import HeroParentTypeDependentFieldQuery.AsDroid;
import HeroTypeDependentAliasedFieldQuery.AsHuman;
import Operation.EMPTY_VARIABLES;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.cache.normalized.NormalizedCache;
import com.apollographql.apollo.cache.normalized.Record;
import com.apollographql.apollo.integration.httpcache.AllPlanetsQuery;
import com.apollographql.apollo.integration.normalizer.CharacterDetailsQuery;
import com.apollographql.apollo.integration.normalizer.CharacterNameByIdQuery;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsDirectivesQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDForParentOnlyQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDsQuery;
import com.apollographql.apollo.integration.normalizer.HeroAppearsInQuery;
import com.apollographql.apollo.integration.normalizer.HeroParentTypeDependentFieldQuery;
import com.apollographql.apollo.integration.normalizer.HeroTypeDependentAliasedFieldQuery;
import com.apollographql.apollo.integration.normalizer.SameHeroTwiceQuery;
import com.apollographql.apollo.integration.normalizer.StarshipByIdQuery;
import com.apollographql.apollo.integration.normalizer.fragment.HeroWithFriendsFragment;
import com.apollographql.apollo.integration.normalizer.fragment.HumanWithIdFragment;
import java.util.Arrays;
import java.util.Map;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;


public class NormalizedCacheTestCase {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void episodeHeroName() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "HeroNameResponse.json", apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(name()).isEqualTo("R2-D2");
                return true;
            }
        });
    }

    @Test
    public void heroAndFriendsNameResponse() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "HeroAndFriendsNameResponse.json", apolloClient.query(new HeroAndFriendsNamesQuery(Input.fromNullable(JEDI))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(name()).isEqualTo("Han Solo");
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
    }

    @Test
    public void heroAndFriendsNamesWithIDs() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1002");
                assertThat(name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().friends().get(2).id()).isEqualTo("1003");
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
    }

    @Test
    public void heroAndFriendsNameWithIdsForParentOnly() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "HeroAndFriendsNameWithIdsParentOnlyResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDForParentOnlyQuery(Input.fromNullable(NEWHOPE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDForParentOnlyQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDForParentOnlyQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(name()).isEqualTo("Han Solo");
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
    }

    @Test
    public void heroAppearsInResponse() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "HeroAppearsInResponse.json", apolloClient.query(new HeroAppearsInQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAppearsInQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAppearsInQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data().hero().appearsIn()).hasSize(3);
                assertThat(name()).isEqualTo("NEWHOPE");
                assertThat(name()).isEqualTo("EMPIRE");
                assertThat(name()).isEqualTo("JEDI");
                return true;
            }
        });
    }

    @SuppressWarnings("CheckReturnValue")
    @Test
    public void heroParentTypeDependentField() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "HeroParentTypeDependentFieldDroidResponse.json", apolloClient.query(new HeroParentTypeDependentFieldQuery(Input.fromNullable(NEWHOPE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroParentTypeDependentFieldQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroParentTypeDependentFieldQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(name()).isEqualTo("R2-D2");
                HeroParentTypeDependentFieldQuery.AsDroid hero = ((HeroParentTypeDependentFieldQuery.AsDroid) (response.data().hero()));
                assertThat(hero.friends()).hasSize(3);
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(height()).isWithin(1.72);
                return true;
            }
        });
    }

    @Test
    public void heroTypeDependentAliasedField() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "HeroTypeDependentAliasedFieldResponse.json", apolloClient.query(new HeroTypeDependentAliasedFieldQuery(Input.fromNullable(NEWHOPE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroTypeDependentAliasedFieldQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroTypeDependentAliasedFieldQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data().hero()).isInstanceOf(HeroTypeDependentAliasedFieldQuery.AsDroid.class);
                assertThat(property()).isEqualTo("Astromech");
                return true;
            }
        });
        server.enqueue(Utils.mockResponse("HeroTypeDependentAliasedFieldResponseHuman.json"));
        Utils.cacheAndAssertCachedResponse(server, "HeroTypeDependentAliasedFieldResponse.json", apolloClient.query(new HeroTypeDependentAliasedFieldQuery(Input.fromNullable(NEWHOPE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroTypeDependentAliasedFieldQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroTypeDependentAliasedFieldQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data().hero()).isInstanceOf(AsHuman.class);
                assertThat(property()).isEqualTo("Tatooine");
                return true;
            }
        });
    }

    @Test
    public void sameHeroTwice() throws Exception {
        Utils.cacheAndAssertCachedResponse(server, "SameHeroTwiceResponse.json", apolloClient.query(new SameHeroTwiceQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<SameHeroTwiceQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<SameHeroTwiceQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().r2().appearsIn()).hasSize(3);
                assertThat(name()).isEqualTo("NEWHOPE");
                assertThat(name()).isEqualTo("EMPIRE");
                assertThat(name()).isEqualTo("JEDI");
                return true;
            }
        });
    }

    @Test
    public void masterDetailSuccess() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.data().character()).isNotNull();
                assertThat(name()).isEqualTo("Han Solo");
                return true;
            }
        });
    }

    @Test
    public void masterDetailFailIncomplete() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterDetailsQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterDetailsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterDetailsQuery.Data> response) throws Exception {
                assertThat(response.data()).isNull();
                return true;
            }
        });
    }

    @Test
    public void independentQueriesGoToNetworkWhenCacheMiss() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroNameResponse.json", apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data()).isNotNull();
                return true;
            }
        });
        Utils.enqueueAndAssertResponse(server, "AllPlanetsNullableField.json", apolloClient.query(new AllPlanetsQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response) throws Exception {
                assertThat(response.hasErrors()).isFalse();
                assertThat(response.data().allPlanets()).isNotNull();
                return true;
            }
        });
    }

    @Test
    public void cacheOnlyMissReturnsNullData() throws Exception {
        Utils.assertResponse(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return (response.data()) == null;
            }
        });
    }

    @Test
    public void cacheResponseWithNullableFields() throws Exception {
        Utils.enqueueAndAssertResponse(server, "AllPlanetsNullableField.json", apolloClient.query(new AllPlanetsQuery()).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response) throws Exception {
                assertThat(response).isNotNull();
                assertThat(response.hasErrors()).isFalse();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new AllPlanetsQuery()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<AllPlanetsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<AllPlanetsQuery.Data> response) throws Exception {
                assertThat(response).isNotNull();
                assertThat(response.hasErrors()).isFalse();
                return true;
            }
        });
    }

    @Test
    public void readOperationFromStore() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1002");
                assertThat(name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().friends().get(2).id()).isEqualTo("1003");
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
    }

    @Test
    public void readFragmentFromStore() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsWithFragmentResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        HeroWithFriendsFragment heroWithFriendsFragment = apolloClient.apolloStore().read(new HeroWithFriendsFragment.Mapper(), CacheKey.from("2001"), EMPTY_VARIABLES).execute();
        assertThat(heroWithFriendsFragment.id()).isEqualTo("2001");
        assertThat(heroWithFriendsFragment.name()).isEqualTo("R2-D2");
        assertThat(heroWithFriendsFragment.friends()).hasSize(3);
        assertThat(heroWithFriendsFragment.friends().get(0).fragments().humanWithIdFragment().id()).isEqualTo("1000");
        assertThat(name()).isEqualTo("Luke Skywalker");
        assertThat(heroWithFriendsFragment.friends().get(1).fragments().humanWithIdFragment().id()).isEqualTo("1002");
        assertThat(name()).isEqualTo("Han Solo");
        assertThat(heroWithFriendsFragment.friends().get(2).fragments().humanWithIdFragment().id()).isEqualTo("1003");
        assertThat(name()).isEqualTo("Leia Organa");
        HumanWithIdFragment fragment = apolloClient.apolloStore().read(new HumanWithIdFragment.Mapper(), CacheKey.from("1000"), EMPTY_VARIABLES).execute();
        assertThat(fragment.id()).isEqualTo("1000");
        assertThat(fragment.name()).isEqualTo("Luke Skywalker");
        fragment = apolloClient.apolloStore().read(new HumanWithIdFragment.Mapper(), CacheKey.from("1002"), EMPTY_VARIABLES).execute();
        assertThat(fragment.id()).isEqualTo("1002");
        assertThat(fragment.name()).isEqualTo("Han Solo");
        fragment = apolloClient.apolloStore().read(new HumanWithIdFragment.Mapper(), CacheKey.from("1003"), EMPTY_VARIABLES).execute();
        assertThat(fragment.id()).isEqualTo("1003");
        assertThat(fragment.name()).isEqualTo("Leia Organa");
    }

    @Test
    public void fromCacheFlag() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroNameResponse.json", apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return !(response.fromCache());
            }
        });
        Utils.enqueueAndAssertResponse(server, "HeroNameResponse.json", apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return !(response.fromCache());
            }
        });
        Utils.assertResponse(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return response.fromCache();
            }
        });
        Utils.assertResponse(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(CACHE_FIRST), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return response.fromCache();
            }
        });
        Utils.assertResponse(apolloClient.query(new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE))).responseFetcher(NETWORK_FIRST), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return response.fromCache();
            }
        });
    }

    @Test
    public void removeFromStore() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Han Solo");
                return true;
            }
        });
        // test remove root query object
        assertThat(apolloClient.apolloStore().remove(CacheKey.from("2001")).execute()).isTrue();
        Utils.assertResponse(apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Han Solo");
                return true;
            }
        });
        // test remove object from the list
        assertThat(apolloClient.apolloStore().remove(CacheKey.from("1002")).execute()).isTrue();
        Utils.assertResponse(apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1003")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNotNull();
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
    }

    @Test
    public void removeMultipleFromStore() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1000")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Luke Skywalker");
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Han Solo");
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1003")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        assertThat(apolloClient.apolloStore().remove(Arrays.asList(CacheKey.from("1002"), CacheKey.from("1000"))).execute()).isEqualTo(2);
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1000")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1003")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
    }

    @Test
    public void skipIncludeDirective() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameResponse.json", apolloClient.query(HeroAndFriendsDirectivesQuery.builder().episode(JEDI).includeName(true).skipFriends(false).build()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        Utils.assertResponse(apolloClient.query(HeroAndFriendsDirectivesQuery.builder().episode(JEDI).includeName(true).skipFriends(false).build()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data> response) throws Exception {
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(name()).isEqualTo("Han Solo");
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(HeroAndFriendsDirectivesQuery.builder().episode(JEDI).includeName(false).skipFriends(false).build()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data> response) throws Exception {
                assertThat(name()).isNull();
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(name()).isEqualTo("Luke Skywalker");
                assertThat(name()).isEqualTo("Han Solo");
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(HeroAndFriendsDirectivesQuery.builder().episode(JEDI).includeName(true).skipFriends(true).build()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data> response) throws Exception {
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).isNull();
                return true;
            }
        });
    }

    @Test
    public void skipIncludeDirectiveUnsatisfiedCache() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroNameResponse.json", apolloClient.query(HeroAndFriendsDirectivesQuery.builder().episode(JEDI).includeName(true).skipFriends(true).build()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        Utils.assertResponse(apolloClient.query(HeroAndFriendsDirectivesQuery.builder().episode(JEDI).includeName(true).skipFriends(true).build()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data> response) throws Exception {
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(HeroAndFriendsDirectivesQuery.builder().episode(JEDI).includeName(true).skipFriends(false).build()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsDirectivesQuery.Data> response) throws Exception {
                assertThat(response.data()).isNull();
                return true;
            }
        });
    }

    @Test
    public void listOfList() throws Exception {
        Utils.enqueueAndAssertResponse(server, "StarshipByIdResponse.json", apolloClient.query(new StarshipByIdQuery("Starship1")), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<StarshipByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<StarshipByIdQuery.Data> response) throws Exception {
                assertThat(response.data().starship().__typename()).isEqualTo("Starship");
                assertThat(name()).isEqualTo("SuperRocket");
                assertThat(response.data().starship().coordinates()).hasSize(3);
                assertThat(response.data().starship().coordinates()).containsExactly(Arrays.asList(100.0, 200.0), Arrays.asList(300.0, 400.0), Arrays.asList(500.0, 600.0));
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new StarshipByIdQuery("Starship1")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<StarshipByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<StarshipByIdQuery.Data> response) throws Exception {
                assertThat(response.data().starship().__typename()).isEqualTo("Starship");
                assertThat(name()).isEqualTo("SuperRocket");
                assertThat(response.data().starship().coordinates()).hasSize(3);
                assertThat(response.data().starship().coordinates()).containsExactly(Arrays.asList(100.0, 200.0), Arrays.asList(300.0, 400.0), Arrays.asList(500.0, 600.0));
                return true;
            }
        });
    }

    @Test
    public void dump() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        Map<Class, Map<String, Record>> dump = apolloClient.apolloStore().normalizedCache().dump();
        assertThat(NormalizedCache.prettifyDump(dump)).isEqualTo(("OptimisticNormalizedCache {}\n" + ((((((((((((((((((((((((((((((((("LruNormalizedCache {\n" + "  \"1002\" : {\n") + "    \"__typename\" : Human\n") + "    \"id\" : 1002\n") + "    \"name\" : Han Solo\n") + "  }\n") + "\n") + "  \"QUERY_ROOT\" : {\n") + "    \"hero({\"episode\":\"NEWHOPE\"})\" : CacheRecordRef(2001)\n") + "  }\n") + "\n") + "  \"1003\" : {\n") + "    \"__typename\" : Human\n") + "    \"id\" : 1003\n") + "    \"name\" : Leia Organa\n") + "  }\n") + "\n") + "  \"1000\" : {\n") + "    \"__typename\" : Human\n") + "    \"id\" : 1000\n") + "    \"name\" : Luke Skywalker\n") + "  }\n") + "\n") + "  \"2001\" : {\n") + "    \"__typename\" : Droid\n") + "    \"id\" : 2001\n") + "    \"name\" : R2-D2\n") + "    \"friends\" : [\n") + "      CacheRecordRef(1000)\n") + "      CacheRecordRef(1002)\n") + "      CacheRecordRef(1003)\n") + "    ]\n") + "  }\n") + "}\n")));
    }

    @Test
    public void cascadeRemove() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1000")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Luke Skywalker");
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Han Solo");
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1003")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        // test remove root query object
        assertThat(apolloClient.apolloStore().remove(CacheKey.from("2001"), true).execute()).isTrue();
        Utils.assertResponse(apolloClient.query(new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(NEWHOPE))).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1000")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1002")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        Utils.assertResponse(apolloClient.query(new CharacterNameByIdQuery("1003")).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<CharacterNameByIdQuery.Data> response) throws Exception {
                assertThat(response.fromCache()).isTrue();
                assertThat(response.data()).isNull();
                return true;
            }
        });
        assertThat(NormalizedCache.prettifyDump(apolloClient.apolloStore().normalizedCache().dump())).isEqualTo(("" + ((((("OptimisticNormalizedCache {}\n" + "LruNormalizedCache {\n") + "  \"QUERY_ROOT\" : {\n") + "    \"hero({\"episode\":\"NEWHOPE\"})\" : CacheRecordRef(2001)\n") + "  }\n") + "}\n")));
    }
}

