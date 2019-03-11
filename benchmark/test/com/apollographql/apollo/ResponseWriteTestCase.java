package com.apollographql.apollo;


import Episode.EMPIRE;
import Episode.NEWHOPE;
import EpisodeHeroWithDatesQuery.Hero;
import EpisodeHeroWithInlineFragmentQuery.AsDroid;
import EpisodeHeroWithInlineFragmentQuery.AsHuman;
import HeroAndFriendsNamesWithIDsQuery.Friend;
import HeroAndFriendsWithFragmentsQuery.Data;
import StarshipByIdQuery.Starship;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.cache.normalized.CacheKey;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroWithDatesQuery;
import com.apollographql.apollo.integration.normalizer.EpisodeHeroWithInlineFragmentQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsNamesWithIDsQuery;
import com.apollographql.apollo.integration.normalizer.HeroAndFriendsWithFragmentsQuery;
import com.apollographql.apollo.integration.normalizer.HeroNameWithEnumsQuery;
import com.apollographql.apollo.integration.normalizer.StarshipByIdQuery;
import com.apollographql.apollo.integration.normalizer.fragment.HeroWithFriendsFragment;
import com.apollographql.apollo.integration.normalizer.fragment.HumanWithIdFragment;
import com.apollographql.apollo.integration.normalizer.type.Episode;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;


@SuppressWarnings("SimpleDateFormatConstant")
public class ResponseWriteTestCase {
    private ApolloClient apolloClient;

    @Rule
    public final MockWebServer server = new MockWebServer();

    private SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-mm-dd", Locale.US);

    @Test
    public void customType() throws Exception {
        EpisodeHeroWithDatesQuery query = new EpisodeHeroWithDatesQuery(Input.fromNullable(JEDI));
        Utils.enqueueAndAssertResponse(server, "EpisodeHeroWithDatesResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroWithDatesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroWithDatesQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().heroName()).isEqualTo("R2-D2");
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().birthDate())).isEqualTo("1984-04-16");
                assertThat(response.data().hero().showUpDates()).hasSize(3);
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().showUpDates().get(0))).isEqualTo("2017-01-16");
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().showUpDates().get(1))).isEqualTo("2017-02-16");
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().showUpDates().get(2))).isEqualTo("2017-03-16");
                return true;
            }
        });
        EpisodeHeroWithDatesQuery.Hero hero = new EpisodeHeroWithDatesQuery.Hero("Droid", "R222-D222", DATE_TIME_FORMAT.parse("1985-04-16"), Collections.<Date>emptyList());
        apolloClient.apolloStore().write(query, new EpisodeHeroWithDatesQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroWithDatesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroWithDatesQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().heroName()).isEqualTo("R222-D222");
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().birthDate())).isEqualTo("1985-04-16");
                assertThat(response.data().hero().showUpDates()).hasSize(0);
                return true;
            }
        });
        hero = new EpisodeHeroWithDatesQuery.Hero(hero.__typename(), "R22-D22", DATE_TIME_FORMAT.parse("1986-04-16"), Arrays.asList(DATE_TIME_FORMAT.parse("2017-04-16"), DATE_TIME_FORMAT.parse("2017-05-16")));
        apolloClient.apolloStore().write(query, new EpisodeHeroWithDatesQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroWithDatesQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroWithDatesQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().heroName()).isEqualTo("R22-D22");
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().birthDate())).isEqualTo("1986-04-16");
                assertThat(response.data().hero().showUpDates()).hasSize(2);
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().showUpDates().get(0))).isEqualTo("2017-04-16");
                assertThat(DATE_TIME_FORMAT.format(response.data().hero().showUpDates().get(1))).isEqualTo("2017-05-16");
                return true;
            }
        });
    }

    @Test
    public void enums() throws Exception {
        HeroNameWithEnumsQuery query = new HeroNameWithEnumsQuery();
        Utils.enqueueAndAssertResponse(server, "HeroNameWithEnumsResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().firstAppearsIn()).isEqualTo(EMPIRE);
                assertThat(response.data().hero().appearsIn()).hasSize(3);
                assertThat(response.data().hero().appearsIn().get(0)).isEqualTo(NEWHOPE);
                assertThat(response.data().hero().appearsIn().get(1)).isEqualTo(EMPIRE);
                assertThat(response.data().hero().appearsIn().get(2)).isEqualTo(Episode.JEDI);
                return true;
            }
        });
        HeroNameWithEnumsQuery.Hero hero = new HeroNameWithEnumsQuery.Hero("Droid", "R222-D222", Episode.JEDI, Collections.<Episode>emptyList());
        apolloClient.apolloStore().write(query, new HeroNameWithEnumsQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().firstAppearsIn()).isEqualTo(Episode.JEDI);
                assertThat(response.data().hero().appearsIn()).hasSize(0);
                return true;
            }
        });
        hero = new HeroNameWithEnumsQuery.Hero(hero.__typename(), "R22-D22", Episode.JEDI, Arrays.asList(EMPIRE));
        apolloClient.apolloStore().write(query, new HeroNameWithEnumsQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroNameWithEnumsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R22-D22");
                assertThat(response.data().hero().firstAppearsIn()).isEqualTo(Episode.JEDI);
                assertThat(response.data().hero().appearsIn()).hasSize(1);
                assertThat(response.data().hero().appearsIn().get(0)).isEqualTo(EMPIRE);
                return true;
            }
        });
    }

    @Test
    public void objects() throws Exception {
        HeroAndFriendsNamesWithIDsQuery query = new HeroAndFriendsNamesWithIDsQuery(Input.fromNullable(JEDI));
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsNameWithIdsResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().friends()).hasSize(3);
                assertThat(response.data().hero().friends().get(0).__typename()).isEqualTo("Human");
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1000");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("Luke Skywalker");
                assertThat(response.data().hero().friends().get(1).__typename()).isEqualTo("Human");
                assertThat(response.data().hero().friends().get(1).id()).isEqualTo("1002");
                assertThat(response.data().hero().friends().get(1).name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().friends().get(2).__typename()).isEqualTo("Human");
                assertThat(response.data().hero().friends().get(2).id()).isEqualTo("1003");
                assertThat(response.data().hero().friends().get(2).name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        HeroAndFriendsNamesWithIDsQuery.Hero hero = new HeroAndFriendsNamesWithIDsQuery.Hero("Droid", "2001", "R222-D222", null);
        apolloClient.apolloStore().write(query, new HeroAndFriendsNamesWithIDsQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().friends()).isNull();
                return true;
            }
        });
        HeroAndFriendsNamesWithIDsQuery.Friend friend = new HeroAndFriendsNamesWithIDsQuery.Friend("Human", "1002", "Han Soloooo");
        hero = new HeroAndFriendsNamesWithIDsQuery.Hero(hero.__typename(), hero.id(), "R222-D222", Collections.singletonList(friend));
        apolloClient.apolloStore().write(query, new HeroAndFriendsNamesWithIDsQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsNamesWithIDsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().id()).isEqualTo("2001");
                assertThat(response.data().hero().friends()).hasSize(1);
                assertThat(response.data().hero().friends().get(0).__typename()).isEqualTo("Human");
                assertThat(response.data().hero().friends().get(0).id()).isEqualTo("1002");
                assertThat(response.data().hero().friends().get(0).name()).isEqualTo("Han Soloooo");
                return true;
            }
        });
    }

    @Test
    public void operation_with_fragments() throws Exception {
        HeroAndFriendsWithFragmentsQuery query = new HeroAndFriendsWithFragmentsQuery(Input.fromNullable(NEWHOPE));
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsWithFragmentResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().id()).isEqualTo("2001");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends()).hasSize(3);
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().id()).isEqualTo("1000");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().name()).isEqualTo("Luke Skywalker");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().id()).isEqualTo("1002");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(2).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(2).fragments().humanWithIdFragment().id()).isEqualTo("1003");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(2).fragments().humanWithIdFragment().name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        HeroAndFriendsWithFragmentsQuery.Hero hero = new HeroAndFriendsWithFragmentsQuery.Hero("Droid", new HeroAndFriendsWithFragmentsQuery.Hero.Fragments(new HeroWithFriendsFragment("Droid", "2001", "R222-D222", Arrays.asList(new HeroWithFriendsFragment.Friend("Human", new HeroWithFriendsFragment.Friend.Fragments(new HumanWithIdFragment("Human", "1006", "SuperMan"))), new HeroWithFriendsFragment.Friend("Human", new HeroWithFriendsFragment.Friend.Fragments(new HumanWithIdFragment("Human", "1004", "Beast")))))));
        apolloClient.apolloStore().write(query, new HeroAndFriendsWithFragmentsQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().id()).isEqualTo("2001");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends()).hasSize(2);
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().id()).isEqualTo("1006");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().name()).isEqualTo("SuperMan");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().id()).isEqualTo("1004");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().name()).isEqualTo("Beast");
                return true;
            }
        });
    }

    @SuppressWarnings("CheckReturnValue")
    @Test
    public void operation_with_inline_fragments() throws Exception {
        EpisodeHeroWithInlineFragmentQuery query = new EpisodeHeroWithInlineFragmentQuery(Input.fromNullable(NEWHOPE));
        Utils.enqueueAndAssertResponse(server, "EpisodeHeroWithInlineFragmentResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroWithInlineFragmentQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroWithInlineFragmentQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().friends()).hasSize(3);
                EpisodeHeroWithInlineFragmentQuery.AsHuman asHuman = ((EpisodeHeroWithInlineFragmentQuery.AsHuman) (response.data().hero().friends().get(0)));
                assertThat(asHuman.__typename()).isEqualTo("Human");
                assertThat(asHuman.id()).isEqualTo("1000");
                assertThat(asHuman.name()).isEqualTo("Luke Skywalker");
                assertThat(asHuman.height()).isWithin(1.5);
                EpisodeHeroWithInlineFragmentQuery.AsDroid asDroid1 = ((EpisodeHeroWithInlineFragmentQuery.AsDroid) (response.data().hero().friends().get(1)));
                assertThat(asDroid1.__typename()).isEqualTo("Droid");
                assertThat(asDroid1.name()).isEqualTo("Android");
                assertThat(asDroid1.primaryFunction()).isEqualTo("Hunt and destroy iOS devices");
                EpisodeHeroWithInlineFragmentQuery.AsDroid asDroid2 = ((EpisodeHeroWithInlineFragmentQuery.AsDroid) (response.data().hero().friends().get(2)));
                assertThat(asDroid2.__typename()).isEqualTo("Droid");
                assertThat(asDroid2.name()).isEqualTo("Battle Droid");
                assertThat(asDroid2.primaryFunction()).isEqualTo("Controlled alternative to human soldiers");
                return true;
            }
        });
        EpisodeHeroWithInlineFragmentQuery.Hero hero = new EpisodeHeroWithInlineFragmentQuery.Hero("Droid", "R22-D22", Arrays.asList(new EpisodeHeroWithInlineFragmentQuery.AsHuman("Human", "1002", "Han Solo", 2.5), new EpisodeHeroWithInlineFragmentQuery.AsDroid("Droid", "RD", "Entertainment")));
        apolloClient.apolloStore().write(query, new EpisodeHeroWithInlineFragmentQuery.Data(hero)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<EpisodeHeroWithInlineFragmentQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<EpisodeHeroWithInlineFragmentQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().name()).isEqualTo("R22-D22");
                assertThat(response.data().hero().friends()).hasSize(2);
                EpisodeHeroWithInlineFragmentQuery.AsHuman asHuman = ((EpisodeHeroWithInlineFragmentQuery.AsHuman) (response.data().hero().friends().get(0)));
                assertThat(asHuman.__typename()).isEqualTo("Human");
                assertThat(asHuman.id()).isEqualTo("1002");
                assertThat(asHuman.name()).isEqualTo("Han Solo");
                assertThat(asHuman.height()).isWithin(2.5);
                EpisodeHeroWithInlineFragmentQuery.AsDroid asDroid = ((EpisodeHeroWithInlineFragmentQuery.AsDroid) (response.data().hero().friends().get(1)));
                assertThat(asDroid.__typename()).isEqualTo("Droid");
                assertThat(asDroid.name()).isEqualTo("RD");
                assertThat(asDroid.primaryFunction()).isEqualTo("Entertainment");
                return true;
            }
        });
    }

    @Test
    public void fragments() throws Exception {
        HeroAndFriendsWithFragmentsQuery query = new HeroAndFriendsWithFragmentsQuery(Input.fromNullable(NEWHOPE));
        Utils.enqueueAndAssertResponse(server, "HeroAndFriendsWithFragmentResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().id()).isEqualTo("2001");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().name()).isEqualTo("R2-D2");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends()).hasSize(3);
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().id()).isEqualTo("1000");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().name()).isEqualTo("Luke Skywalker");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().id()).isEqualTo("1002");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().name()).isEqualTo("Han Solo");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(2).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(2).fragments().humanWithIdFragment().id()).isEqualTo("1003");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(2).fragments().humanWithIdFragment().name()).isEqualTo("Leia Organa");
                return true;
            }
        });
        apolloClient.apolloStore().write(new HeroWithFriendsFragment("Droid", "2001", "R222-D222", Arrays.asList(new HeroWithFriendsFragment.Friend("Human", new HeroWithFriendsFragment.Friend.Fragments(new HumanWithIdFragment("Human", "1000", "SuperMan"))), new HeroWithFriendsFragment.Friend("Human", new HeroWithFriendsFragment.Friend.Fragments(new HumanWithIdFragment("Human", "1002", "Han Solo"))))), CacheKey.from("2001"), query.variables()).execute();
        apolloClient.apolloStore().write(new HumanWithIdFragment("Human", "1002", "Beast"), CacheKey.from("1002"), query.variables()).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<HeroAndFriendsWithFragmentsQuery.Data> response) throws Exception {
                assertThat(response.data().hero().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().__typename()).isEqualTo("Droid");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().id()).isEqualTo("2001");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().name()).isEqualTo("R222-D222");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends()).hasSize(2);
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().id()).isEqualTo("1000");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(0).fragments().humanWithIdFragment().name()).isEqualTo("SuperMan");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().__typename()).isEqualTo("Human");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().id()).isEqualTo("1002");
                assertThat(response.data().hero().fragments().heroWithFriendsFragment().friends().get(1).fragments().humanWithIdFragment().name()).isEqualTo("Beast");
                return true;
            }
        });
    }

    @Test
    public void listOfList() throws Exception {
        StarshipByIdQuery query = new StarshipByIdQuery("Starship1");
        Utils.enqueueAndAssertResponse(server, "StarshipByIdResponse.json", apolloClient.query(query), new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<StarshipByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<StarshipByIdQuery.Data> response) throws Exception {
                assertThat(response.data().starship().__typename()).isEqualTo("Starship");
                assertThat(response.data().starship().name()).isEqualTo("SuperRocket");
                assertThat(response.data().starship().coordinates()).hasSize(3);
                assertThat(response.data().starship().coordinates()).containsExactly(Arrays.asList(100.0, 200.0), Arrays.asList(300.0, 400.0), Arrays.asList(500.0, 600.0));
                return true;
            }
        });
        StarshipByIdQuery.Starship starship = new StarshipByIdQuery.Starship("Starship", "Starship1", "SuperRocket", Arrays.asList(Arrays.asList(900.0, 800.0), Arrays.asList(700.0, 600.0)));
        apolloClient.apolloStore().write(query, new StarshipByIdQuery.Data(starship)).execute();
        assertCachedQueryResponse(query, new io.reactivex.functions.Predicate<com.apollographql.apollo.api.Response<StarshipByIdQuery.Data>>() {
            @Override
            public boolean test(com.apollographql.apollo.api.Response<StarshipByIdQuery.Data> response) throws Exception {
                assertThat(response.data().starship().__typename()).isEqualTo("Starship");
                assertThat(response.data().starship().name()).isEqualTo("SuperRocket");
                assertThat(response.data().starship().coordinates()).hasSize(2);
                assertThat(response.data().starship().coordinates()).containsExactly(Arrays.asList(900.0, 800.0), Arrays.asList(700.0, 600.0));
                return true;
            }
        });
    }
}

