/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.query;


import java.util.HashMap;
import java.util.Map;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.cache.query.SqlFieldsQueryEx;
import org.apache.ignite.internal.util.typedef.F;
import org.junit.Test;


/**
 * Tests for {@link SqlFieldsQueryEx#skipReducerOnUpdate} flag.
 */
public class IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final int NODE_COUNT = 4;

    /**
     *
     */
    private static final String NODE_CLIENT = "client";

    /**
     *
     */
    private static final String CACHE_ACCOUNT = "acc";

    /**
     *
     */
    private static final String CACHE_REPORT = "rep";

    /**
     *
     */
    private static final String CACHE_STOCK = "stock";

    /**
     *
     */
    private static final String CACHE_TRADE = "trade";

    /**
     *
     */
    private static final String CACHE_LIST = "list";

    /**
     *
     */
    private static IgniteEx client;

    /**
     *
     */
    @Test
    public void testUpdate() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 100);
        String text = "UPDATE \"acc\".Account SET depo = depo - ? WHERE depo > 0";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), accounts, new SqlFieldsQueryEx(text, false).setArgs(10));
    }

    /**
     *
     */
    @Test
    public void testUpdateFastKey() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 100);
        String text = "UPDATE \"acc\".Account SET depo = depo - ? WHERE _key = ?";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), accounts, new SqlFieldsQueryEx(text, false).setArgs(10, 1));
    }

    /**
     *
     */
    @Test
    public void testUpdateLimit() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 100);
        String text = "UPDATE \"acc\".Account SET depo = depo - ? WHERE sn >= ? AND sn < ? LIMIT ?";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), accounts, new SqlFieldsQueryEx(text, false).setArgs(10, 0, 10, 10));
    }

    /**
     *
     */
    @Test
    public void testUpdateWhereSubquery() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, (-100));
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = getTrades(100, 2);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "UPDATE \"trade\".Trade t SET qty = ? " + "WHERE accountId IN (SELECT p._key FROM \"acc\".Account p WHERE depo < ?)";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), trades, new SqlFieldsQueryEx(text, false).setArgs(0, 0));
    }

    /**
     *
     */
    @Test
    public void testUpdateSetSubquery() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 1000);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = getTrades(100, 2);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "UPDATE \"trade\".Trade t SET qty = " + "(SELECT a.depo/t.price FROM \"acc\".Account a WHERE t.accountId = a._key)";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), trades, new SqlFieldsQueryEx(text, false));
    }

    /**
     *
     */
    @Test
    public void testUpdateSetTableSubquery() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 1000);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = getTrades(100, 2);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "UPDATE \"trade\".Trade t SET (qty) = " + "(SELECT a.depo/t.price FROM \"acc\".Account a WHERE t.accountId = a._key)";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), trades, new SqlFieldsQueryEx(text, false));
    }

    /**
     *
     */
    @Test
    public void testInsertValues() {
        String text = "INSERT INTO \"acc\".Account (_key, name, sn, depo)" + " VALUES (?, ?, ?, ?), (?, ?, ?, ?)";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), null, new SqlFieldsQueryEx(text, false).setArgs(1, "John Marry", 11111, 100, 2, "Marry John", 11112, 200));
    }

    /**
     *
     */
    @Test
    public void testInsertFromSelect() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 1000);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "INSERT INTO \"trade\".Trade (_key, accountId, stockId, qty, price) " + "SELECT a._key, a._key, ?, a.depo/?, ? FROM \"acc\".Account a";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), null, new SqlFieldsQueryEx(text, false).setArgs(1, 10, 10));
    }

    /**
     *
     */
    @Test
    public void testInsertFromSelectOrderBy() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 1000);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "INSERT INTO \"trade\".Trade (_key, accountId, stockId, qty, price) " + ("SELECT a._key, a._key, ?, a.depo/?, ? FROM \"acc\".Account a " + "ORDER BY a.sn DESC");
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), null, new SqlFieldsQueryEx(text, false).setArgs(1, 10, 10));
    }

    /**
     *
     */
    @Test
    public void testInsertFromSelectUnion() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(20, 1, 1000);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "INSERT INTO \"trade\".Trade (_key, accountId, stockId, qty, price) " + (("SELECT a._key, a._key, 0, a.depo, 1 FROM \"acc\".Account a " + "UNION ") + "SELECT 101 + a2._key, a2._key, 1, a2.depo, 1 FROM \"acc\".Account a2");
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), null, new SqlFieldsQueryEx(text, false));
    }

    /**
     *
     */
    @Test
    public void testInsertFromSelectGroupBy() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 1000);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = getTrades(100, 2);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE).putAll(trades);
        String text = "INSERT INTO \"rep\".Report (_key, accountId, spends, count) " + (("SELECT accountId, accountId, SUM(qty * price), COUNT(*) " + "FROM \"trade\".Trade ") + "GROUP BY accountId");
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Report>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_REPORT), null, new SqlFieldsQueryEx(text, false));
    }

    /**
     *
     */
    @Test
    public void testInsertFromSelectDistinct() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 2, 100);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "INSERT INTO \"list\".String (_key, _val) " + "SELECT DISTINCT sn, name FROM \"acc\".Account ";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, String>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_LIST), null, new SqlFieldsQueryEx(text, false));
    }

    /**
     *
     */
    @Test
    public void testInsertFromSelectJoin() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 100);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Stock> stocks = getStocks(5);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_STOCK).putAll(stocks);
        String text = "INSERT INTO \"trade\".Trade(_key, accountId, stockId, qty, price) " + ("SELECT 5*a._key + s._key, a._key, s._key, ?, a.depo/? " + "FROM \"acc\".Account a JOIN \"stock\".Stock s ON 1=1");
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), null, new SqlFieldsQueryEx(text, false).setArgs(10, 10));
    }

    /**
     *
     */
    @Test
    public void testDelete() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 100);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "DELETE FROM \"acc\".Account WHERE sn > ?";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), accounts, new SqlFieldsQueryEx(text, false).setArgs(10));
    }

    /**
     *
     */
    @Test
    public void testDeleteTop() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 100);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        String text = "DELETE TOP ? FROM \"acc\".Account WHERE sn < ?";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), accounts, new SqlFieldsQueryEx(text, false).setArgs(10, 10));
    }

    /**
     *
     */
    @Test
    public void testDeleteWhereSubquery() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(20, 1, 100);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = getTrades(10, 2);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE).putAll(trades);
        String text = "DELETE FROM \"acc\".Account " + "WHERE _key IN (SELECT t.accountId FROM \"trade\".Trade t)";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), accounts, new SqlFieldsQueryEx(text, false));
    }

    /**
     *
     */
    @Test
    public void testMergeValues() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(1, 1, 100);
        String text = "MERGE INTO \"acc\".Account (_key, name, sn, depo)" + " VALUES (?, ?, ?, ?), (?, ?, ?, ?)";
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT), accounts, new SqlFieldsQueryEx(text, false).setArgs(0, "John Marry", 11111, 100, 1, "Marry John", 11112, 200));
    }

    /**
     *
     */
    @Test
    public void testMergeFromSelectJoin() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 100);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Stock> stocks = getStocks(5);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_STOCK).putAll(stocks);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = new HashMap<>();
        trades.put(5, new IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade(1, 1, 1, 1));
        String text = "MERGE INTO \"trade\".Trade(_key, accountId, stockId, qty, price) " + ("SELECT 5*a._key + s._key, a._key, s._key, ?, a.depo/? " + "FROM \"acc\".Account a JOIN \"stock\".Stock s ON 1=1");
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), trades, new SqlFieldsQueryEx(text, false).setArgs(10, 10));
    }

    /**
     *
     */
    @Test
    public void testMergeFromSelectOrderBy() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 1000);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = new HashMap<>();
        trades.put(5, new IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade(1, 1, 1, 1));
        String text = "MERGE INTO \"trade\".Trade (_key, accountId, stockId, qty, price) " + ("SELECT a._key, a._key, ?, a.depo/?, ? FROM \"acc\".Account a " + "ORDER BY a.sn DESC");
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE), trades, new SqlFieldsQueryEx(text, false).setArgs(1, 10, 10));
    }

    /**
     *
     */
    @Test
    public void testMergeFromSelectGroupBy() {
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account> accounts = getAccounts(100, 1, 1000);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade> trades = getTrades(100, 2);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_ACCOUNT).putAll(accounts);
        IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_TRADE).putAll(trades);
        Map<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Report> reports = new HashMap<>();
        reports.put(5, new IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Report(5, 1, 1));
        String text = "MERGE INTO \"rep\".Report (_key, accountId, spends, count) " + (("SELECT accountId, accountId, SUM(qty * price), COUNT(*) " + "FROM \"trade\".Trade ") + "GROUP BY accountId");
        checkUpdate(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.client.<Integer, IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Report>cache(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.CACHE_REPORT), reports, new SqlFieldsQueryEx(text, false));
    }

    /**
     *
     */
    public static class Account {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        @QuerySqlField
        int sn;

        /**
         *
         */
        @QuerySqlField
        int depo;

        /**
         * Constructor.
         *
         * @param name
         * 		Name.
         * @param sn
         * 		ID.
         * @param depo
         * 		Deposit amount.
         */
        Account(String name, int sn, int depo) {
            this.name = name;
            this.sn = sn;
            this.depo = depo;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (((name) == null ? 0 : name.hashCode()) ^ (sn)) ^ (depo);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (!(obj.getClass().equals(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account.class)))
                return false;

            IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account other = ((IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Account) (obj));
            return ((F.eq(name, other.name)) && ((sn) == (other.sn))) && ((depo) == (other.depo));
        }
    }

    /**
     *
     */
    public static class Stock {
        /**
         *
         */
        @QuerySqlField
        String ticker;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         * Constructor.
         *
         * @param ticker
         * 		Short name.
         * @param name
         * 		Name.
         */
        Stock(String ticker, String name) {
            this.ticker = ticker;
            this.name = name;
        }
    }

    /**
     *
     */
    public static class Trade {
        /**
         *
         */
        @QuerySqlField
        int accountId;

        /**
         *
         */
        @QuerySqlField
        int stockId;

        /**
         *
         */
        @QuerySqlField
        int qty;

        /**
         *
         */
        @QuerySqlField
        int price;

        /**
         * Constructor.
         *
         * @param accountId
         * 		Account id.
         * @param stockId
         * 		Stock id.
         * @param qty
         * 		Quantity.
         * @param price
         * 		Price.
         */
        Trade(int accountId, int stockId, int qty, int price) {
            this.accountId = accountId;
            this.stockId = stockId;
            this.qty = qty;
            this.price = price;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (((accountId) ^ (stockId)) ^ (qty)) ^ (price);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (!(obj.getClass().equals(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade.class)))
                return false;

            IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade other = ((IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Trade) (obj));
            return ((((accountId) == (other.accountId)) && ((stockId) == (other.stockId))) && ((qty) == (other.qty))) && ((price) == (other.price));
        }
    }

    /**
     *
     */
    public static class Report {
        /**
         *
         */
        @QuerySqlField
        int accountId;

        /**
         *
         */
        @QuerySqlField
        int spends;

        /**
         *
         */
        @QuerySqlField
        int count;

        /**
         * Constructor.
         *
         * @param accountId
         * 		Account id.
         * @param spends
         * 		Spends.
         * @param count
         * 		Count.
         */
        Report(int accountId, int spends, int count) {
            this.accountId = accountId;
            this.spends = spends;
            this.count = count;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return ((accountId) ^ (spends)) ^ (count);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (!(obj.getClass().equals(IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Report.class)))
                return false;

            IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Report other = ((IgniteSqlSkipReducerOnUpdateDmlFlagSelfTest.Report) (obj));
            return (((accountId) == (other.accountId)) && ((spends) == (other.spends))) && ((count) == (other.count));
        }
    }
}

