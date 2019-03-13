package com.apollographql.apollo;


import CacheHeaders.NONE;
import Episode.EMPIRE;
import Episode.NEWHOPE;
import EpisodeHeroNameQuery.Data;
import EpisodeHeroNameQuery.Hero;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.cache.normalized.Record;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroNameQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDsQuery;
import com.apollographql.apollo.internal.cache.normalized.WriteableStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import junit.framework.Assert;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Rule;
import org.junit.Test;


public class ApolloWatcherTest {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void testQueryWatcherUpdated_SameQuery_DifferentResults() throws Exception {
        final List<String> heroNameList = new ArrayList<>();
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).watcher();
        watcher.enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        // Another newer call gets updated information
        Utils.enqueueAndAssertResponse(server, "EpisodeHeroNameResponseNameChange.json", apolloClient.query(query).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        watcher.cancel();
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        assertThat(heroNameList.get(1)).isEqualTo("Artoo");
        assertThat(heroNameList.size()).isEqualTo(2);
    }

    @Test
    public void testQueryWatcherUpdated_Store_write() throws ApolloException, IOException, InterruptedException, TimeoutException {
        final List<String> heroNameList = new ArrayList<>();
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).watcher();
        watcher.enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        // Someone writes to the store directly
        Set<String> changedKeys = apolloClient.apolloStore().writeTransaction(new com.apollographql.apollo.internal.cache.normalized.Transaction<WriteableStore, Set<String>>() {
            @Nullable
            @Override
            public Set<String> execute(WriteableStore cache) {
                Record record = Record.builder("2001").addField("name", "Artoo").build();
                return cache.merge(Collections.singletonList(record), NONE);
            }
        });
        apolloClient.apolloStore().publish(changedKeys);
        assertThat(heroNameList.get(1)).isEqualTo("Artoo");
        watcher.cancel();
    }

    @Test
    public void testQueryWatcherNotUpdated_SameQuery_SameResults() throws Exception {
        final List<String> heroNameList = new ArrayList<>();
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).watcher();
        watcher.enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        apolloClient.query(query).responseFetcher(NETWORK_ONLY).enqueue(null);
        watcher.cancel();
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        assertThat(heroNameList.size()).isEqualTo(1);
    }

    @Test
    public void testQueryWatcherUpdated_DifferentQuery_DifferentResults() throws Exception {
        final List<String> heroNameList = new ArrayList<>();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).watcher();
        watcher.enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        HeroAndFriendsNamesWithIDsQuery friendsQuery = HeroAndFriendsNamesWithIDsQuery.builder().episode(NEWHOPE).build();
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsNameChange.json", apolloClient.query(friendsQuery).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        watcher.cancel();
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        assertThat(heroNameList.get(1)).isEqualTo("Artoo");
    }

    @Test
    public void testQueryWatcherNotUpdated_DifferentQueries() throws Exception {
        final List<String> heroNameList = new ArrayList<>();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).watcher();
        watcher.enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        HeroAndFriendsNamesWithIDsQuery friendsQuery = HeroAndFriendsNamesWithIDsQuery.builder().episode(NEWHOPE).build();
        server.enqueue(Utils.mockResponse("HeroAndFriendsNameWithIdsResponse.json"));
        apolloClient.query(friendsQuery).responseFetcher(NETWORK_ONLY).enqueue(null);
        watcher.cancel();
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        assertThat(heroNameList.size()).isEqualTo(1);
    }

    @Test
    public void testRefetchCacheControl() throws Exception {
        final List<String> heroNameList = new ArrayList<>();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).watcher();
        // Force network instead of CACHE_FIRST default
        watcher.refetchResponseFetcher(NETWORK_ONLY).enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getCause().getMessage());
            }
        });
        // A different call gets updated information.
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseNameChange.json"));
        // To verify that the updated response comes from server use a different name change
        // -- this is for the refetch
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseNameChangeTwo.json"));
        apolloClient.query(query).responseFetcher(NETWORK_ONLY).enqueue(null);
        watcher.cancel();
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        assertThat(heroNameList.get(1)).isEqualTo("ArTwo");
        assertThat(heroNameList.size()).isEqualTo(2);
    }

    @Test
    public void testQueryWatcherUpdated_SameQuery_DifferentResults_cacheOnly() throws Exception {
        final List<String> heroNameList = new ArrayList<>();
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        apolloClient.query(query).enqueue(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).responseFetcher(CACHE_ONLY).watcher();
        watcher.enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        // Another newer call gets updated information
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseNameChange.json"));
        apolloClient.query(query).responseFetcher(NETWORK_ONLY).enqueue(null);
        watcher.cancel();
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        assertThat(heroNameList.get(1)).isEqualTo("Artoo");
        assertThat(heroNameList.size()).isEqualTo(2);
    }

    @Test
    public void testQueryWatcherNotCalled_WhenCanceled() throws Exception {
        final List<String> heroNameList = new ArrayList<>();
        EpisodeHeroNameQuery query = EpisodeHeroNameQuery.builder().episode(EMPIRE).build();
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        ApolloQueryWatcher<EpisodeHeroNameQuery.Data> watcher = apolloClient.query(query).watcher();
        watcher.enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<EpisodeHeroNameQuery.Data> response) {
                heroNameList.add(response.data().hero().name());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
                Assert.fail(e.getMessage());
            }
        });
        watcher.cancel();
        Utils.enqueueAndAssertResponse(server, "EpisodeHeroNameResponseNameChange.json", apolloClient.query(query).responseFetcher(NETWORK_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroNameQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        assertThat(heroNameList.get(0)).isEqualTo("R2-D2");
        assertThat(heroNameList.size()).isEqualTo(1);
    }

    @Test
    public void emptyCacheQueryWatcherCacheOnly() throws Exception {
        final List<EpisodeHeroNameQuery.Hero> watchedHeroes = new ArrayList<>();
        EpisodeHeroNameQuery query = new EpisodeHeroNameQuery(Input.fromNullable(EMPIRE));
        apolloClient.query(query).responseFetcher(CACHE_ONLY).watcher().enqueueAndWatch(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(Response<EpisodeHeroNameQuery.Data> response) {
                if ((response.data()) != null) {
                    watchedHeroes.add(response.data().hero());
                }
            }

            @Override
            public void onFailure(ApolloException e) {
                fail(e.getMessage());
            }
        });
        server.enqueue(Utils.mockResponse("EpisodeHeroNameResponseWithId.json"));
        apolloClient.query(query).enqueue(new ApolloCall.Callback<EpisodeHeroNameQuery.Data>() {
            @Override
            public void onResponse(Response<EpisodeHeroNameQuery.Data> response) {
                assertThat(response.data()).isNotNull();
                assertThat(response.data().hero()).isNotNull();
            }

            @Override
            public void onFailure(ApolloException e) {
                fail(e.getMessage());
            }
        });
        assertThat(watchedHeroes).hasSize(1);
        assertThat(watchedHeroes.get(0).name()).isEqualTo("R2-D2");
    }
}

