/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.idm.engine.test.api.identity;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.idm.api.Token;
import org.flowable.idm.api.TokenQuery;
import org.flowable.idm.engine.impl.persistence.entity.TokenEntity;
import org.flowable.idm.engine.test.PluggableFlowableIdmTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class TokenQueryTest extends PluggableFlowableIdmTestCase {
    @Test
    public void testQueryByNoCriteria() {
        TokenQuery query = idmIdentityService.createTokenQuery();
        verifyQueryResults(query, 3);
    }

    @Test
    public void testQueryById() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenId("1111");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidId() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenId("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().tokenId(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByTokenValue() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenValue("aaaa");
        verifyQueryResults(query, 1);
        Token result = query.singleResult();
        Assertions.assertEquals("aaaa", result.getTokenValue());
    }

    @Test
    public void testQueryByInvalidTokenValue() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenValue("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().tokenValue(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByTokenDateBefore() {
        Calendar queryCal = new GregorianCalendar(2015, 6, 1, 0, 0, 0);
        TokenQuery query = idmIdentityService.createTokenQuery().tokenDateBefore(queryCal.getTime());
        verifyQueryResults(query, 1);
        Token result = query.singleResult();
        Calendar tokenCal = new GregorianCalendar();
        tokenCal.setTime(result.getTokenDate());
        Assertions.assertEquals(2015, tokenCal.get(Calendar.YEAR));
        Assertions.assertEquals(1, tokenCal.get(Calendar.MONTH));
    }

    @Test
    public void testQueryByTokenDateAfter() {
        Calendar queryCal = new GregorianCalendar(2016, 6, 1, 0, 0, 0);
        TokenQuery query = idmIdentityService.createTokenQuery().tokenDateAfter(queryCal.getTime());
        verifyQueryResults(query, 1);
        Token result = query.singleResult();
        Calendar tokenCal = new GregorianCalendar();
        tokenCal.setTime(result.getTokenDate());
        Assertions.assertEquals(2017, tokenCal.get(Calendar.YEAR));
        Assertions.assertEquals(1, tokenCal.get(Calendar.MONTH));
    }

    @Test
    public void testQueryByIpAddress() {
        TokenQuery query = idmIdentityService.createTokenQuery().ipAddress("127.0.0.1");
        verifyQueryResults(query, 1);
        Token result = query.singleResult();
        Assertions.assertEquals("127.0.0.1", result.getIpAddress());
    }

    @Test
    public void testQueryByInvalidIpAddress() {
        TokenQuery query = idmIdentityService.createTokenQuery().ipAddress("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().ipAddress(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByIpAddressLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().ipAddressLike("%0.0.1%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createTokenQuery().ipAddressLike("129.%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidIpAddressLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().ipAddressLike("%invalid%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().ipAddressLike(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByUserAgent() {
        TokenQuery query = idmIdentityService.createTokenQuery().userAgent("chrome");
        verifyQueryResults(query, 1);
        Token result = query.singleResult();
        Assertions.assertEquals("chrome", result.getUserAgent());
    }

    @Test
    public void testQueryByInvalidUserAgent() {
        TokenQuery query = idmIdentityService.createTokenQuery().userAgent("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().userAgent(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByUserAgentLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().userAgentLike("%fire%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createTokenQuery().userAgentLike("ch%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidUserAgentLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().userAgentLike("%invalid%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().userAgentLike(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByUserId() {
        TokenQuery query = idmIdentityService.createTokenQuery().userId("user1");
        verifyQueryResults(query, 1);
        Token result = query.singleResult();
        Assertions.assertEquals("user1", result.getUserId());
    }

    @Test
    public void testQueryByInvalidUserId() {
        TokenQuery query = idmIdentityService.createTokenQuery().userId("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().userId(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByUserIdLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().userIdLike("%user%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createTokenQuery().userIdLike("bla%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidUserIdLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().userIdLike("%invalid%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().userIdLike(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByTokenData() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenData("test");
        verifyQueryResults(query, 1);
        Token result = query.singleResult();
        Assertions.assertEquals("test", result.getTokenData());
    }

    @Test
    public void testQueryByInvalidTokenData() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenData("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().tokenData(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByTokenDataLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenDataLike("%test%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidTokenDataLike() {
        TokenQuery query = idmIdentityService.createTokenQuery().tokenDataLike("%invalid%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createTokenQuery().tokenDataLike(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQuerySorting() {
        // asc
        Assertions.assertEquals(3, idmIdentityService.createTokenQuery().orderByTokenId().asc().count());
        Assertions.assertEquals(3, idmIdentityService.createTokenQuery().orderByTokenDate().asc().count());
        // desc
        Assertions.assertEquals(3, idmIdentityService.createTokenQuery().orderByTokenId().desc().count());
        Assertions.assertEquals(3, idmIdentityService.createTokenQuery().orderByTokenDate().desc().count());
        // Combined with criteria
        TokenQuery query = idmIdentityService.createTokenQuery().userAgentLike("%firefox%").orderByTokenDate().asc();
        List<Token> tokens = query.list();
        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals("firefox", tokens.get(0).getUserAgent());
        Assertions.assertEquals("firefox2", tokens.get(1).getUserAgent());
    }

    @Test
    public void testQueryInvalidSortingUsage() {
        try {
            idmIdentityService.createTokenQuery().orderByTokenId().list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
        try {
            idmIdentityService.createTokenQuery().orderByTokenId().orderByTokenDate().list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testNativeQuery() {
        Assertions.assertEquals("ACT_ID_TOKEN", idmManagementService.getTableName(Token.class));
        Assertions.assertEquals("ACT_ID_TOKEN", idmManagementService.getTableName(TokenEntity.class));
        String tableName = idmManagementService.getTableName(Token.class);
        String baseQuerySql = "SELECT * FROM " + tableName;
        Assertions.assertEquals(3, idmIdentityService.createNativeUserQuery().sql(baseQuerySql).list().size());
        Assertions.assertEquals(1, idmIdentityService.createNativeUserQuery().sql((baseQuerySql + " where ID_ = #{id}")).parameter("id", "1111").list().size());
        // paging
        Assertions.assertEquals(2, idmIdentityService.createNativeUserQuery().sql(baseQuerySql).listPage(0, 2).size());
        Assertions.assertEquals(2, idmIdentityService.createNativeUserQuery().sql(baseQuerySql).listPage(1, 3).size());
        Assertions.assertEquals(1, idmIdentityService.createNativeUserQuery().sql((baseQuerySql + " where ID_ = #{id}")).parameter("id", "1111").listPage(0, 1).size());
    }
}

