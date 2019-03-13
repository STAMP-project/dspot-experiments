package com.ctrip.framework.apollo.portal.service;


import FavoriteService.POSITION_DEFAULT;
import Sql.ExecutionPhase;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.portal.AbstractIntegrationTest;
import com.ctrip.framework.apollo.portal.entity.po.Favorite;
import com.ctrip.framework.apollo.portal.repository.FavoriteRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;


public class FavoriteServiceTest extends AbstractIntegrationTest {
    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    private String testUser = "apollo";

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddNormalFavorite() {
        String testApp = "testApp";
        Favorite favorite = instanceOfFavorite(testUser, testApp);
        favoriteService.addFavorite(favorite);
        List<Favorite> createdFavorites = favoriteService.search(testUser, testApp, PageRequest.of(0, 10));
        Assert.assertEquals(1, createdFavorites.size());
        Assert.assertEquals(POSITION_DEFAULT, createdFavorites.get(0).getPosition());
        Assert.assertEquals(testUser, createdFavorites.get(0).getUserId());
        Assert.assertEquals(testApp, createdFavorites.get(0).getAppId());
    }

    @Test(expected = BadRequestException.class)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddFavoriteErrorUser() {
        String testApp = "testApp";
        Favorite favorite = instanceOfFavorite("errorUser", testApp);
        favoriteService.addFavorite(favorite);
    }

    @Test
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testSearchByUserId() {
        List<Favorite> favorites = favoriteService.search(testUser, null, PageRequest.of(0, 10));
        Assert.assertEquals(4, favorites.size());
    }

    @Test
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testSearchByAppId() {
        List<Favorite> favorites = favoriteService.search(null, "test0621-04", PageRequest.of(0, 10));
        Assert.assertEquals(3, favorites.size());
    }

    @Test
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testSearchByAppIdAndUserId() {
        List<Favorite> favorites = favoriteService.search(testUser, "test0621-04", PageRequest.of(0, 10));
        Assert.assertEquals(1, favorites.size());
    }

    @Test(expected = BadRequestException.class)
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testSearchWithErrorParams() {
        favoriteService.search(null, null, PageRequest.of(0, 10));
    }

    @Test
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteFavorite() {
        long legalFavoriteId = 21L;
        favoriteService.deleteFavorite(legalFavoriteId);
        Assert.assertNull(favoriteRepository.findById(legalFavoriteId).orElse(null));
    }

    @Test(expected = BadRequestException.class)
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteFavoriteFail() {
        long anotherPersonFavoriteId = 23L;
        favoriteService.deleteFavorite(anotherPersonFavoriteId);
        Assert.assertNull(favoriteRepository.findById(anotherPersonFavoriteId).orElse(null));
    }

    @Test(expected = BadRequestException.class)
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testAdjustFavoriteError() {
        long anotherPersonFavoriteId = 23;
        favoriteService.adjustFavoriteToFirst(anotherPersonFavoriteId);
    }

    @Test
    @Sql(scripts = "/sql/favorites/favorites.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testAdjustFavorite() {
        long toAdjustFavoriteId = 20;
        favoriteService.adjustFavoriteToFirst(toAdjustFavoriteId);
        List<Favorite> favorites = favoriteService.search(testUser, null, PageRequest.of(0, 10));
        Favorite firstFavorite = favorites.get(0);
        Favorite secondFavorite = favorites.get(1);
        Assert.assertEquals(toAdjustFavoriteId, firstFavorite.getId());
        Assert.assertEquals(((firstFavorite.getPosition()) + 1), secondFavorite.getPosition());
    }
}

