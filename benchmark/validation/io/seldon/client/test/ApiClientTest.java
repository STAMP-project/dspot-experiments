/**
 * Seldon -- open source prediction engine
 * =======================================
 * Copyright 2011-2015 Seldon Technologies Ltd and Rummble Ltd (http://www.seldon.io/)
 *
 * *********************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * *********************************************************************************************
 */
package io.seldon.client.test;


import io.seldon.client.exception.ApiException;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Created by: marc on 31/08/2011 at 10:49
 */
public class ApiClientTest extends BaseClientTest {
    private static final Logger logger = Logger.getLogger(ApiClientTest.class);

    private static final int USER_LIMIT = 10;

    private static final int ITEM_LIMIT = 10;

    private static final int DIMENSIONS_LIMIT = 10;

    private static final int USER_RECOMMENDATIONS_LIMIT = 10;

    private static final int USER_ACTION_LIMIT = 25;

    @Test
    public void getActions() throws ApiException {
        List<ActionBean> actions = apiClient.getActions();
        for (ActionBean action : actions) {
            ApiClientTest.logger.info(("Action> " + action));
        }
    }

    @Test(expected = ApiException.class)
    public void retrieveFakeUser() throws ApiException {
        String fakeUsername = RandomStringUtils.randomAlphanumeric(32);
        apiClient.getUser(fakeUsername, true);
    }

    @Test
    public void retrieveUsers() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, false);
        logList(users);
    }

    @Test
    public void retrieveUsersSecure() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, false, true);
        logList(users);
    }

    @Test
    public void retrieveUsersFull() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, true);
        logList(users);
    }

    @Test
    public void retrieveUsersById() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, false);
        retrieveUsers(users, false);
    }

    @Test
    public void retrieveUsersFullById() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, false);
        retrieveUsers(users, true);
    }

    @Test
    public void retrieveRecommendationsWithDimensionsForUser() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, false);
        List<DimensionBean> dimensions = apiClient.getDimensions();
        for (UserBean user : users) {
            String userId = user.getId();
            int limit = Math.min(dimensions.size(), ApiClientTest.DIMENSIONS_LIMIT);
            for (DimensionBean dimension : dimensions.subList(0, limit)) {
                int dimensionId = dimension.getDimId();
                @SuppressWarnings({ "NullableProblems" })
                final int recommendations_limit = ApiClientTest.USER_RECOMMENDATIONS_LIMIT;
                List<ItemBean> recommendations = apiClient.getRecommendations(userId, null, dimensionId, recommendations_limit);
                for (ItemBean recommendation : recommendations) {
                    ApiClientTest.logger.debug(((((("Recommendation for user: " + userId) + " => in dimension: ") + dimension) + " => ") + recommendation));
                }
            }
        }
    }

    @Test
    public void retrieveItems() throws ApiException {
        List<ItemBean> items = apiClient.getItems(ApiClientTest.ITEM_LIMIT, false);
        logList(items);
    }

    @Test
    public void retrieveItemsFull() throws ApiException {
        List<ItemBean> items = apiClient.getItems(ApiClientTest.ITEM_LIMIT, true);
        logList(items);
    }

    @Test
    public void retrieveItemsById() throws ApiException {
        List<ItemBean> items = apiClient.getItems(ApiClientTest.ITEM_LIMIT, false);
        for (ItemBean item : items) {
            String itemId = item.getId();
            ItemBean itemBean = apiClient.getItem(itemId, false);
            ApiClientTest.logger.info(("Item: " + itemBean));
        }
    }

    @Test
    public void retrieveItemsFullById() throws ApiException {
        List<ItemBean> items = apiClient.getItems(ApiClientTest.ITEM_LIMIT, false);
        for (ItemBean item : items) {
            String itemId = item.getId();
            ItemBean itemBean = apiClient.getItem(itemId, true);
            ApiClientTest.logger.info(("Item: " + itemBean));
        }
    }

    @Test
    public void retrieveDimensions() throws ApiException {
        getDimensions();
    }

    @Test
    public void retrieveDimensionsById() throws ApiException {
        List<DimensionBean> dimensions = getDimensions();
        for (DimensionBean knownDimension : dimensions) {
            String dimensionId = String.valueOf(knownDimension.getDimId());
            DimensionBean dimensionBean = apiClient.getDimensionById(dimensionId);
            ApiClientTest.logger.info(("Dimension: " + dimensionBean));
        }
    }

    @Test
    public void retrieveActionsForUsers() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, false);
        for (UserBean user : users) {
            String userId = user.getId();
            List<ActionBean> actionsList = apiClient.getUserActions(userId, ApiClientTest.USER_ACTION_LIMIT);
            for (ActionBean actionBean : actionsList) {
                ApiClientTest.logger.info(("Action: " + actionBean));
            }
        }
    }

    @Test
    public void retrieveActions() throws ApiException {
        List<ActionBean> actionsList = apiClient.getActions();
        for (ActionBean actionBean : actionsList) {
            ApiClientTest.logger.info(("Action: " + actionBean));
        }
    }

    @Test
    public void retrieveActionsById() throws ApiException {
        List<ActionBean> actionsList = apiClient.getActions();
        for (ActionBean actionBean : actionsList) {
            String actionId = String.valueOf(actionBean.getActionId());
            ActionBean individualActionBean = apiClient.getActionById(actionId);
            ApiClientTest.logger.info(("Action: " + individualActionBean));
        }
    }

    @Test
    public void retrieveRecommendationsForUser() throws ApiException {
        List<UserBean> users = apiClient.getUsers(ApiClientTest.USER_LIMIT, false);
        for (UserBean user : users) {
            String userId = user.getId();
            List<ItemBean> recommendations = apiClient.getRecommendations(userId);
            for (ItemBean recommendation : recommendations) {
                ApiClientTest.logger.info(((("Recommendation for user: " + userId) + " => ") + recommendation));
            }
        }
    }
}

