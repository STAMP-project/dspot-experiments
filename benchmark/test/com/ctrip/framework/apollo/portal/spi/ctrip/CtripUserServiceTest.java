package com.ctrip.framework.apollo.portal.spi.ctrip;


import CtripUserService.UserServiceRequest;
import CtripUserService.UserServiceRequestBody;
import CtripUserService.UserServiceResponse;
import HttpMethod.POST;
import com.ctrip.framework.apollo.portal.AbstractUnitTest;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class CtripUserServiceTest extends AbstractUnitTest {
    private CtripUserService ctripUserService;

    private String someUserServiceUrl;

    private String someUserServiceToken;

    private ParameterizedTypeReference<Map<String, List<CtripUserService.UserServiceResponse>>> someResponseType;

    @Mock
    private PortalConfig portalConfig;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testAssembleSearchUserRequest() throws Exception {
        String someKeyword = "someKeyword";
        int someOffset = 0;
        int someLimit = 10;
        CtripUserService.UserServiceRequest request = ctripUserService.assembleSearchUserRequest(someKeyword, someOffset, someLimit);
        Assert.assertEquals(someUserServiceToken, request.getAccess_token());
        CtripUserService.UserServiceRequestBody requestBody = request.getRequest_body();
        Assert.assertEquals("itdb_emloyee", requestBody.getIndexAlias());
        Map<String, Object> queryJson = requestBody.getQueryJson();
        Assert.assertEquals(someOffset, queryJson.get("from"));
        Assert.assertEquals(someLimit, queryJson.get("size"));
        Map<String, Object> query = ((Map<String, Object>) (queryJson.get("query")));
        Map<String, Object> multiMatchMap = ((Map<String, Object>) (query.get("multi_match")));
        Assert.assertEquals(someKeyword, multiMatchMap.get("query"));
    }

    @Test
    public void testAssembleFindUserRequest() throws Exception {
        String someUserId = "someUser";
        String anotherUserId = "anotherUser";
        List<String> userIds = Lists.newArrayList(someUserId, anotherUserId);
        CtripUserService.UserServiceRequest request = ctripUserService.assembleFindUserRequest(userIds);
        Assert.assertEquals(someUserServiceToken, request.getAccess_token());
        CtripUserService.UserServiceRequestBody requestBody = request.getRequest_body();
        Assert.assertEquals("itdb_emloyee", requestBody.getIndexAlias());
        Map<String, Object> queryJson = requestBody.getQueryJson();
        Assert.assertEquals(0, queryJson.get("from"));
        Assert.assertEquals(2, queryJson.get("size"));
        Map<String, Object> query = ((Map<String, Object>) (queryJson.get("query")));
        Map<String, Object> terms = getMapFromMap(getMapFromMap(getMapFromMap(query, "filtered"), "filter"), "terms");
        List<String> userIdTerms = ((List<String>) (terms.get("empaccount")));
        Assert.assertTrue(userIdTerms.containsAll(userIds));
    }

    @Test
    public void testSearchUsers() throws Exception {
        String someUserId = "someUserId";
        String someName = "someName";
        String someEmail = "someEmail";
        String anotherUserId = "anotherUserId";
        String anotherName = "anotherName";
        String anotherEmail = "anotherEmail";
        String someKeyword = "someKeyword";
        int someOffset = 0;
        int someLimit = 10;
        CtripUserService.UserServiceResponse someUserResponse = assembleUserServiceResponse(someUserId, someName, someEmail);
        CtripUserService.UserServiceResponse anotherUserResponse = assembleUserServiceResponse(anotherUserId, anotherName, anotherEmail);
        Map<String, List<CtripUserService.UserServiceResponse>> resultMap = ImmutableMap.of("result", Lists.newArrayList(someUserResponse, anotherUserResponse));
        ResponseEntity<Map<String, List<CtripUserService.UserServiceResponse>>> someResponse = new ResponseEntity(resultMap, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.eq(someUserServiceUrl), ArgumentMatchers.eq(POST), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(someResponseType))).thenReturn(someResponse);
        List<UserInfo> users = ctripUserService.searchUsers(someKeyword, someOffset, someLimit);
        Assert.assertEquals(2, users.size());
        compareUserInfoAndUserServiceResponse(someUserResponse, users.get(0));
        compareUserInfoAndUserServiceResponse(anotherUserResponse, users.get(1));
    }

    @Test(expected = HttpClientErrorException.class)
    public void testSearchUsersWithError() throws Exception {
        Mockito.when(restTemplate.exchange(ArgumentMatchers.eq(someUserServiceUrl), ArgumentMatchers.eq(POST), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(someResponseType))).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        String someKeyword = "someKeyword";
        int someOffset = 0;
        int someLimit = 10;
        ctripUserService.searchUsers(someKeyword, someOffset, someLimit);
    }

    @Test
    public void testFindByUserId() throws Exception {
        String someUserId = "someUserId";
        String someName = "someName";
        String someEmail = "someEmail";
        CtripUserService.UserServiceResponse someUserResponse = assembleUserServiceResponse(someUserId, someName, someEmail);
        Map<String, List<CtripUserService.UserServiceResponse>> resultMap = ImmutableMap.of("result", Lists.newArrayList(someUserResponse));
        ResponseEntity<Map<String, List<CtripUserService.UserServiceResponse>>> someResponse = new ResponseEntity(resultMap, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.eq(someUserServiceUrl), ArgumentMatchers.eq(POST), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(someResponseType))).thenReturn(someResponse);
        UserInfo user = ctripUserService.findByUserId(someUserId);
        compareUserInfoAndUserServiceResponse(someUserResponse, user);
    }

    @Test
    public void testFindByUserIds() throws Exception {
        String someUserId = "someUserId";
        String someName = "someName";
        String someEmail = "someEmail";
        String anotherUserId = "anotherUserId";
        String anotherName = "anotherName";
        String anotherEmail = "anotherEmail";
        String someKeyword = "someKeyword";
        CtripUserService.UserServiceResponse someUserResponse = assembleUserServiceResponse(someUserId, someName, someEmail);
        CtripUserService.UserServiceResponse anotherUserResponse = assembleUserServiceResponse(anotherUserId, anotherName, anotherEmail);
        Map<String, List<CtripUserService.UserServiceResponse>> resultMap = ImmutableMap.of("result", Lists.newArrayList(someUserResponse, anotherUserResponse));
        ResponseEntity<Map<String, List<CtripUserService.UserServiceResponse>>> someResponse = new ResponseEntity(resultMap, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(ArgumentMatchers.eq(someUserServiceUrl), ArgumentMatchers.eq(POST), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.eq(someResponseType))).thenReturn(someResponse);
        List<UserInfo> users = ctripUserService.findByUserIds(Lists.newArrayList(someUserId, anotherUserId));
        Assert.assertEquals(2, users.size());
        compareUserInfoAndUserServiceResponse(someUserResponse, users.get(0));
        compareUserInfoAndUserServiceResponse(anotherUserResponse, users.get(1));
    }
}

