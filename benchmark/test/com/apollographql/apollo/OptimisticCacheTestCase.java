package com.apollographql.apollo;


import ApolloResponseFetchers.CACHE_FIRST;
import ApolloResponseFetchers.NETWORK_FIRST;
import Episode.EMPIRE;
import Episode.JEDI;
import Episode.NEWHOPE;
import HeroAndFriendsNamesQuery.Data;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.exception.ApolloException;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDsQuery;
import com.apollographql.apollo.integration.normalizer.HeroNameQuery;
import com.apollographql.apollo.integration.normalizer.HeroNameWithEnumsQuery;
import com.apollographql.apollo.integration.normalizer.HeroNameWithIdQuery;
import com.apollographql.apollo.integration.normalizer.ReviewsByEpisodeQuery;
import com.apollographql.apollo.integration.normalizer.UpdateReviewMutation;
import com.apollographql.apollo.integration.normalizer.type.ColorInput;
import com.apollographql.apollo.integration.normalizer.type.Episode;
import com.apollographql.apollo.integration.normalizer.type.ReviewInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;


public class OptimisticCacheTestCase {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void simple() throws Exception {
        HeroAndFriendsNamesQuery query = new HeroAndFriendsNamesQuery(Input.fromNullable(JEDI));
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        UUID mutationId = UUID.randomUUID();
        HeroAndFriendsNamesQuery.Data data = new HeroAndFriendsNamesQuery.Data(new HeroAndFriendsNamesQuery.Hero("Droid", "R222-D222", Arrays.asList(new HeroAndFriendsNamesQuery.Friend("Human", "SuperMan"), new HeroAndFriendsNamesQuery.Friend("Human", "Batman"))));
        apolloClient.apolloStore().writeOptimisticUpdatesAndPublish(query, data, mutationId).execute();
        Utils.assertResponse(apolloClient.query(query).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data> response) throws Exception {
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().friends()).hasSize(2);
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("SuperMan");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Batman");
                return true;
            }
        });
        apolloClient.apolloStore().rollbackOptimisticUpdates(mutationId).execute();
        Utils.assertResponse(apolloClient.query(query).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesQuery.Data> response) throws Exception {
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("Luke Skywalker");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().friends().get(2).name()).isEqualTo("Leia Organa");
                return true;
            }
        });
    }

    @Test
    public void two_optimistic_two_rollback() throws Exception {
        HeroAndFriendsNamesWithIDsQuery query1 = new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(JEDI));
        UUID mutationId1 = UUID.randomUUID();
        HeroNameWithIdQuery query2 = new HeroNameWithIdQuery();
        UUID mutationId2 = UUID.randomUUID();
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(query1), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        HeroAndFriendsNamesWithIDsQuery.Data data1 = new HeroAndFriendsNamesWithIDsQuery.Data(new HeroAndFriendsNamesWithIDsQuery.Hero("Droid", "2001", "R222-D222", Arrays.asList(new HeroAndFriendsNamesWithIDsQuery.Friend("Human", "1000", "SuperMan"), new HeroAndFriendsNamesWithIDsQuery.Friend("Human", "1003", "Batman"))));
        apolloClient.apolloStore().writeOptimisticUpdatesAndPublish(query1, data1, mutationId1).execute();
        // check if query1 see optimistic updates
        Utils.assertResponse(apolloClient.query(query1).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().friends()).hasSize(2);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("SuperMan");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1003");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Batman");
                return true;
            }
        });
        Utils.enqueueAndAssertResponse(server, "HeroNameWithIdResponse.json", apolloClient.query(query2), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        HeroNameWithIdQuery.Data data2 = new HeroNameWithIdQuery.Data(new HeroNameWithIdQuery.Hero("Human", "1000", "Beast"));
        apolloClient.apolloStore().writeOptimisticUpdatesAndPublish(query2, data2, mutationId2).execute();
        // check if query1 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query1).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().friends()).hasSize(2);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("Beast");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1003");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Batman");
                return true;
            }
        });
        // check if query2 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query2).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("1000");
                assertThat(response.data().hero().name()).isEqualTo("Beast");
                return true;
            }
        });
        // rollback query1 optimistic updates
        apolloClient.apolloStore().rollbackOptimisticUpdates(mutationId1).execute();
        // check if query1 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query1).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("Beast");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1002");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().friends().get(2).id()).isEqualTo("1003");
                assertThat(response.data().hero().friends().get(2).name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        // check if query2 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query2).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("1000");
                assertThat(response.data().hero().name()).isEqualTo("Beast");
                return true;
            }
        });
        // rollback query2 optimistic updates
        apolloClient.apolloStore().rollbackOptimisticUpdates(mutationId2).execute();
        // check if query2 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query2).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("1000");
                assertThat(response.data().hero().name()).isEqualTo("SuperMan");
                return true;
            }
        });
    }

    @Test
    public void full_persisted_partial_optimistic() throws Exception {
        Utils.enqueueAndAssertResponse(server, "HeroNameWithEnumsResponse.json", apolloClient.query(new HeroNameWithEnumsQuery()), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        UUID mutationId = UUID.randomUUID();
        apolloClient.apolloStore().writeOptimisticUpdates(new HeroNameQuery(), new HeroNameQuery.Data(new HeroNameQuery.Hero("Droid", "R22-D22")), mutationId).execute();
        Utils.assertResponse(apolloClient.query(new HeroNameWithEnumsQuery()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().name()).isEqualTo("R22-D22");
                assertThat(response.data().hero().firstAppearsIn()).isEqualTo(EMPIRE);
                assertThat(response.data().hero().appearsIn()).isEqualTo(Arrays.asList(NEWHOPE, EMPIRE, JEDI));
                return true;
            }
        });
        apolloClient.apolloStore().rollbackOptimisticUpdates(mutationId).execute();
        Utils.assertResponse(apolloClient.query(new HeroNameWithEnumsQuery()).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().firstAppearsIn()).isEqualTo(EMPIRE);
                assertThat(response.data().hero().appearsIn()).isEqualTo(Arrays.asList(NEWHOPE, EMPIRE, JEDI));
                return true;
            }
        });
    }

    @Test
    public void mutation_and_query_watcher() throws Exception {
        server.enqueue(mockResponse("ReviewsEmpireEpisodeResponse.json"));
        final List<ReviewsByEpisodeQuery.Data> watcherData = new ArrayList<>();
        apolloClient.query(new ReviewsByEpisodeQuery(Episode.EMPIRE)).responseFetcher(NETWORK_FIRST).watcher().refetchResponseFetcher(CACHE_FIRST).enqueueAndWatch(new ApolloCall.Callback<ReviewsByEpisodeQuery.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<ReviewsByEpisodeQuery.Data> response) {
                watcherData.add(response.data());
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
            }
        });
        server.enqueue(mockResponse("UpdateReviewResponse.json"));
        UpdateReviewMutation updateReviewMutation = new UpdateReviewMutation("empireReview2", ReviewInput.builder().commentary("Great").stars(5).favoriteColor(ColorInput.builder().build()).build());
        apolloClient.mutate(updateReviewMutation, new UpdateReviewMutation.Data(new UpdateReviewMutation.UpdateReview("Review", "empireReview2", 5, "Great"))).enqueue(new ApolloCall.Callback<UpdateReviewMutation.Data>() {
            @Override
            public void onResponse(@NotNull
            Response<UpdateReviewMutation.Data> response) {
            }

            @Override
            public void onFailure(@NotNull
            ApolloException e) {
            }
        });
        assertThat(watcherData).hasSize(3);
        // before mutation and optimistic updates
        assertThat(watcherData.get(0).reviews()).hasSize(3);
        assertThat(watcherData.get(0).reviews().get(0).id()).isEqualTo("empireReview1");
        assertThat(watcherData.get(0).reviews().get(0).stars()).isEqualTo(1);
        assertThat(watcherData.get(0).reviews().get(0).commentary()).isEqualTo("Boring");
        assertThat(watcherData.get(0).reviews().get(1).id()).isEqualTo("empireReview2");
        assertThat(watcherData.get(0).reviews().get(1).stars()).isEqualTo(2);
        assertThat(watcherData.get(0).reviews().get(1).commentary()).isEqualTo("So-so");
        assertThat(watcherData.get(0).reviews().get(2).id()).isEqualTo("empireReview3");
        assertThat(watcherData.get(0).reviews().get(2).stars()).isEqualTo(5);
        assertThat(watcherData.get(0).reviews().get(2).commentary()).isEqualTo("Amazing");
        // optimistic updates
        assertThat(watcherData.get(1).reviews()).hasSize(3);
        assertThat(watcherData.get(1).reviews().get(0).id()).isEqualTo("empireReview1");
        assertThat(watcherData.get(1).reviews().get(0).stars()).isEqualTo(1);
        assertThat(watcherData.get(1).reviews().get(0).commentary()).isEqualTo("Boring");
        assertThat(watcherData.get(1).reviews().get(1).id()).isEqualTo("empireReview2");
        assertThat(watcherData.get(1).reviews().get(1).stars()).isEqualTo(5);
        assertThat(watcherData.get(1).reviews().get(1).commentary()).isEqualTo("Great");
        assertThat(watcherData.get(1).reviews().get(2).id()).isEqualTo("empireReview3");
        assertThat(watcherData.get(1).reviews().get(2).stars()).isEqualTo(5);
        assertThat(watcherData.get(1).reviews().get(2).commentary()).isEqualTo("Amazing");
        // after mutation with rolled back optimistic updates
        assertThat(watcherData.get(2).reviews()).hasSize(3);
        assertThat(watcherData.get(2).reviews().get(0).id()).isEqualTo("empireReview1");
        assertThat(watcherData.get(2).reviews().get(0).stars()).isEqualTo(1);
        assertThat(watcherData.get(2).reviews().get(0).commentary()).isEqualTo("Boring");
        assertThat(watcherData.get(2).reviews().get(1).id()).isEqualTo("empireReview2");
        assertThat(watcherData.get(2).reviews().get(1).stars()).isEqualTo(4);
        assertThat(watcherData.get(2).reviews().get(1).commentary()).isEqualTo("Not Bad");
        assertThat(watcherData.get(2).reviews().get(2).id()).isEqualTo("empireReview3");
        assertThat(watcherData.get(2).reviews().get(2).stars()).isEqualTo(5);
        assertThat(watcherData.get(2).reviews().get(2).commentary()).isEqualTo("Amazing");
    }

    @Test
    public void two_optimistic_reverse_rollback_order() throws Exception {
        HeroAndFriendsNamesWithIDsQuery query1 = new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(JEDI));
        UUID mutationId1 = UUID.randomUUID();
        HeroNameWithIdQuery query2 = new HeroNameWithIdQuery();
        UUID mutationId2 = UUID.randomUUID();
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(query1), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        Utils.enqueueAndAssertResponse(server, "HeroNameWithIdResponse.json", apolloClient.query(query2), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                return !(response.hasErrors());
            }
        });
        HeroAndFriendsNamesWithIDsQuery.Data data1 = new HeroAndFriendsNamesWithIDsQuery.Data(new HeroAndFriendsNamesWithIDsQuery.Hero("Droid", "2001", "R222-D222", Arrays.asList(new HeroAndFriendsNamesWithIDsQuery.Friend("Human", "1000", "Robocop"), new HeroAndFriendsNamesWithIDsQuery.Friend("Human", "1003", "Batman"))));
        apolloClient.apolloStore().writeOptimisticUpdatesAndPublish(query1, data1, mutationId1).execute();
        HeroNameWithIdQuery.Data data2 = new HeroNameWithIdQuery.Data(new HeroNameWithIdQuery.Hero("Human", "1000", "Spiderman"));
        apolloClient.apolloStore().writeOptimisticUpdatesAndPublish(query2, data2, mutationId2).execute();
        // check if query1 see optimistic updates
        Utils.assertResponse(apolloClient.query(query1).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().friends()).hasSize(2);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("Spiderman");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1003");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Batman");
                return true;
            }
        });
        // check if query2 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query2).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("1000");
                assertThat(response.data().hero().name()).isEqualTo("Spiderman");
                return true;
            }
        });
        // rollback query2 optimistic updates
        apolloClient.apolloStore().rollbackOptimisticUpdates(mutationId2).execute();
        // check if query1 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query1).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().friends()).hasSize(2);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("Robocop");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1003");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Batman");
                return true;
            }
        });
        // check if query2 see the latest optimistic updates
        Utils.assertResponse(apolloClient.query(query2).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("1000");
                assertThat(response.data().hero().name()).isEqualTo("Robocop");
                return true;
            }
        });
        // rollback query1 optimistic updates
        apolloClient.apolloStore().rollbackOptimisticUpdates(mutationId1).execute();
        // check if query1 see the latest non-optimistic updates
        Utils.assertResponse(apolloClient.query(query1).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("SuperMan");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1002");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().friends().get(2).id()).isEqualTo("1003");
                assertThat(response.data().hero().friends().get(2).name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        // check if query2 see the latest non-optimistic updates
        Utils.assertResponse(apolloClient.query(query2).responseFetcher(CACHE_ONLY), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithIdQuery.Data> response) throws Exception {
                assertThat(response.data().hero().id()).isEqualTo("1000");
                assertThat(response.data().hero().name()).isEqualTo("SuperMan");
                return true;
            }
        });
    }
}

