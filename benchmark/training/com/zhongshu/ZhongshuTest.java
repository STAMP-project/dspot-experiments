package com.zhongshu;


import java.sql.SQLFeatureNotSupportedException;
import org.elasticsearch.client.Client;
import org.elasticsearch.plugin.nlpcn.executors.ActionRequestRestExecuterFactory;
import org.elasticsearch.plugin.nlpcn.executors.RestExecutor;
import org.junit.Test;
import org.nlpcn.es4sql.exception.SqlParseException;
import org.nlpcn.es4sql.query.ESActionFactory;
import org.nlpcn.es4sql.query.QueryAction;


/**
 *
 *
 * @author zhongshu
 * @since 2018/5/13 ??1:58
zhongshu-comment ????sql????????????????????????????
 */
public class ZhongshuTest {
    String sql = null;

    Client client = null;

    @Test
    public void testSelectStar() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "select a,case when c='1' then 'haha' when c='2' then 'book' else 'hbhb' end as gg from tbl_a group by a,gg";// order by a asc,c desc,d asc limit 8,12";

        // sql = "select * from tbl_a group by a,b, case when c='1' then 'haha' when c='2' then 'book' else 'hbhb' end order by a asc,c desc,d asc limit 8,12";
        // sql = "select * from tbl_a group by a,b";
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testESActionFactoryCreate() throws Exception {
        // sql = "select zs as zs, a mya, b + 1 as myb, floor(c), case when d = 1 then 'hehe' when d <> 2 then 'haha' else 'gg' end as myd from TEST_TBL";
        sql = "select a, floor(num) my_b, case when os = 'iOS' then 'hehe' else 'haha' end as my_os from t_zhongshu_test";
        QueryAction queryAction = ESActionFactory.create(client, sql);
        RestExecutor restExecutor = ActionRequestRestExecuterFactory.createExecutor(null);
        restExecutor.execute(client, null, queryAction, null);
        System.out.println();
    }

    @Test
    public void testHints() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "select /*! USE_SCROLL(10,120000) */ * FROM spark_es_table";
        ESActionFactory.create(client, sql);
    }

    /**
     * ?????????????
     *  SqlParser??parseSelect()??????from?????????select.getFrom().addAll(findFrom(query.getFrom()));
     * ???from??????
     *
     * @throws SQLFeatureNotSupportedException
     * 		
     * @throws SqlParseException
     * 		
     */
    @Test
    public void testFromSubQuery() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "select dt,theo_inv from  (select \n" + ((((((("dt\n" + ",os\n") + ",click_num\n") + ",case when (ad_source =\'\u4e2d\u957f\u5c3e\' and os=\'iOS\') and (dt=\'111\' or sspid=998) then charge else \'-\' end as charge\n") + "from\n") + "t_md_xps2_all_inv_consume_report \n") + "where dt>=\'2018-05-01\' and dt<=\'2018-05-10\' \n") + ") tmp  where  1=1 and adslotid='13016'  order by dt asc , os desc");
        // ESActionFactory.create(client, sql);
        System.out.println(sql);
    }

    @Test
    public void testWhereSubQuery() throws SQLFeatureNotSupportedException, SqlParseException {
        // sql = "select goods_id,goods_name from goods\n" +
        // " where goods_id in (select max(goods_id) from goods group by cat_id);";
        sql = "select goods_id,goods_name from goods where goods_id = (select max(goods_id) from goods)";
        ESActionFactory.create(client, sql);
    }

    /**
     * ???????????????????????????
     *
     * @throws SQLFeatureNotSupportedException
     * 		
     * @throws SqlParseException
     * 		
     */
    @Test
    public void testJoin() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "select * from " + ("emp e inner join " + "org o on e.org_id = o.id ");
        // "left join " +
        // "TEST_TBL tbl on e.e_id = tbl.tbl_id";
        ESActionFactory.create(client, sql);
    }

    @Test
    public void testWhereClause() throws SQLFeatureNotSupportedException, SqlParseException {
        // sql = "select a,b,c as my_c from tbl where a = 1";
        // sql = "select a,b,c as my_c from tbl where a = 1 or b = 2 and c = 3";
        sql = "select a,b,c as my_c from tbl where a = 1 and b = 2 and c = 3";
        // sql = "select a,b,c as my_c from tbl where a = 1 or b = 2 and c = 3 and 1 > 1";
        // sql = "select a,b,c as my_c from tbl where a = 1 or b = 2 and (c = 3 or d = 4) and e>1";
        /* zhongshu-comment ??sql??????????????????????where??????????????????????
        OR a = 1 Condition
        OR b = 2 and (c = 3 or d = 4) Where
        AND b = 2 Condition
        AND (c = 3 or d = 4) Where
        OR c = 3 Condition
        OR d = 4 Condition
        OR e > 1 Condition

        ?????
        1????????????Condition??????a=1?e>1??
        2????????Condition????????Where??????b = 2 and (c = 3 or d = 4)
        ??Where????????????Condition???b=2?c=3?d=4
        3?Condition??????And??????
        ??or??????and??????????????3??
        a = 1
        b = 2 and (c = 3 or d = 4)
        e > 1
         */
        // sql = "select a,b,c as my_c from tbl where a = 1 or b = 2 and (c = 3 or d = 4) or e > 1";
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testGroupBy() {
        sql = "select " + (((((((("count(*), " + "gg, ") + "a as my_a, ") + "floor(b) as my_b , ") + "count(c) as my_count, ") + "sum(d), ") + "case when e='1' then 'hehe' when e='2' then 'haha' else 'book' end as my_e ") + "from tbl ") + "group by my_a, my_b");
        System.out.println(sql);
    }

    @Test
    public void testStr() {
        String a = "abc";
        String b = "abc";
        System.out.println((a == b));
        System.out.println((a != b));
    }

    @Test
    public void testOrderByCaseWhen() throws SQLFeatureNotSupportedException, SqlParseException {
        // zhongshu-comment ??????
        // sql = "SELECT dt, os, appid\n" +
        // "FROM t_od_xps2_app_channel_cube_report\n" +
        // "WHERE dt >= '2018-05-29'\n" +
        // "\tAND dt <= '2018-05-29'\n" +
        // "ORDER BY CASE WHEN accounttype = '1' THEN effect_inv ELSE theo_inv END";
        // zhongshu-comment ????????????order by???????select case when?????
        // sql = "select  dt, os,  appid, case when accounttype='0' then '??' when  accounttype='1' then '???'  else accounttype end as accounttype, channel_no, adslotid, adposition_name, dau, case when accounttype='1' then effect_inv else theo_inv end as theo_inv, v_num , av_num , click_num , case when accounttype='1' then charge else '-' end as charge, v_rate, av_rate, ctr1, ctr2, case when accounttype='1' then ecpm1 else '-' end ecpm1, case when accounttype='1' then ecpm2 else '-' end ecpm2, case when accounttype='1' then acp else '-' end acp from t_od_xps2_app_channel_cube_report  where dt>='2018-05-29' and dt<='2018-05-29'   and   1=1  and accounttype not in ('all','??') and os = '??' and appid not in ('all','??') and adslotid in ('??') and channel_no = '??'  order by dt desc,theo_inv desc limit 0, 10";
        // sql = "select dt, os, appid, case when accounttype='0' then '??' when  accounttype='1' then '???'  else accounttype end as accounttype, channel_no, adslotid, adposition_name, dau, case when accounttype='1' then effect_inv else theo_inv end as theo_inv, v_num , av_num , click_num , case when accounttype='1' then charge else '-' end as charge, case when v_rate='' then '0.00' else v_rate end as v_rate, case when av_rate='' then '0.00' else av_rate end as av_rate, case when ctr1='' then '0.00' else ctr1 end as ctr1, case when ctr2='' then '0.00' else ctr2 end as ctr2, case when ecpm1='' then '0.00' else ecpm1 end ecpm1, case when ecpm2='' then '0.00' else ecpm2 end ecpm2, case when acp='' then '0.00' else acp end acp, case when arpu='' then '0.00' else arpu end arpu from t_od_xps2_app_channel_cube_report where dt>='2018-05-27' and dt<='2018-06-03'  and   1=1  and accounttype = '??' and os = '??' and appid = '??' and adslotid in ('??') and channel_no = '??'  order by dt desc,theo_inv desc limit 0, 10";
        // sql = "select dt, os, appid, case when accounttype='0' then '??' when  accounttype='1' then '???'  else accounttype end as accounttype, channel_no, adslotid, adposition_name, dau, case when accounttype='1' then effect_inv else theo_inv end as theo_inv, v_num , av_num , click_num , case when accounttype='1' then charge else '-' end as charge, case when v_rate='' then '0.00' else v_rate end as v_rate, case when av_rate='' then '0.00' else av_rate end as av_rate, case when ctr1='' then '0.00' else ctr1 end as ctr1, case when ctr2='' then '0.00' else ctr2 end as ctr2, case when ecpm1='' then '0.00' else ecpm1 end ecpm1, case when ecpm2='' then '0.00' else ecpm2 end ecpm2, case when acp='' then '0.00' else acp end acp, case when arpu='' then '0.00' else arpu end arpu from t_od_xps2_app_channel_cube_report where dt>='2018-05-29' and dt<='2018-05-29'  and   1=1  and accounttype not in ('all','??') and os not in ('all','??') and appid not in ('all','??') and adslotid in ('??') and channel_no = '??'  order by dt desc,theo_inv desc limit 0, 10";
        // zhongshu-comment ???????????order by??????case when?????
        // sql = "select  dt, os,  appid, case when accounttype='0' then '??' when  accounttype='1' then '???'  else accounttype end as accounttype, channel_no, adslotid, adposition_name, dau, case when accounttype='1' then effect_inv else theo_inv end as theo_inv, v_num , av_num , click_num , case when accounttype='1' then charge else '-' end as charge, v_rate, av_rate, ctr1, ctr2, case when accounttype='1' then ecpm1 else '-' end ecpm1, case when accounttype='1' then ecpm2 else '-' end ecpm2, case when accounttype='1' then acp else '-' end acp from t_od_xps2_app_channel_cube_report  where dt>='2018-05-29' and dt<='2018-05-29'   and   1=1  and accounttype not in ('all','??') and os = '??' and appid not in ('all','??') and adslotid in ('??') and channel_no = '??'  order by dt desc,case when accounttype='1' then effect_inv else theo_inv end desc limit 0, 10";
        // sql = "select dt, os, appid, case when accounttype='0' then '??' when  accounttype='1' then '???'  else accounttype end as accounttype, channel_no, adslotid, adposition_name, dau, case when accounttype='1' then effect_inv else theo_inv end as theo_inv, v_num , av_num , click_num , case when accounttype='1' then charge else '-' end as charge, case when v_rate='' then '0.00' else v_rate end as v_rate, case when av_rate='' then '0.00' else av_rate end as av_rate, case when ctr1='' then '0.00' else ctr1 end as ctr1, case when ctr2='' then '0.00' else ctr2 end as ctr2, case when ecpm1='' then '0.00' else ecpm1 end ecpm1, case when ecpm2='' then '0.00' else ecpm2 end ecpm2, case when acp='' then '0.00' else acp end acp, case when arpu='' then '0.00' else arpu end arpu from t_od_xps2_app_channel_cube_report where dt>='2018-05-27' and dt<='2018-06-03'  and   1=1  and accounttype = '??' and os = '??' and appid = '??' and adslotid in ('??') and channel_no = '??'  order by dt desc,case when accounttype='1' then effect_inv else theo_inv end desc limit 0, 10";
        sql = "select dt, os, appid, case when accounttype='0' then '??' when  accounttype='1' then '???'  else accounttype end as accounttype, channel_no, adslotid, adposition_name, dau, case when accounttype='1' then effect_inv else theo_inv end as theo_inv, v_num , av_num , click_num , case when accounttype='1' then charge else '-' end as charge, case when v_rate='' then '0.00' else v_rate end as v_rate, case when av_rate='' then '0.00' else av_rate end as av_rate, case when ctr1='' then '0.00' else ctr1 end as ctr1, case when ctr2='' then '0.00' else ctr2 end as ctr2, case when ecpm1='' then '0.00' else ecpm1 end ecpm1, case when ecpm2='' then '0.00' else ecpm2 end ecpm2, case when acp='' then '0.00' else acp end acp, case when arpu='' then '0.00' else arpu end arpu from t_od_xps2_app_channel_cube_report where dt>='2018-05-29' and dt<='2018-05-29'  and   1=1  and accounttype not in ('all','??') and os not in ('all','??') and appid not in ('all','??') and adslotid in ('??') and channel_no = '??'  order by dt desc,case when accounttype='1' then effect_inv else theo_inv end desc limit 0, 10";
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testInJudge() throws SQLFeatureNotSupportedException, SqlParseException {
        // sql = "select dt, case when a in ('1', '2', '3') then 'hehe' when b=2 then 'haha' when c not in (7,8,9) then 'book' else 'gg' end as a from tbl";
        sql = "select dt,case when os in ('Android', 'iOS') then 'hello' else 'world' end abc from t_zhongshu_test";
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testRound() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "SELECT round(av_num/v_num,4) as av_rate1 FROM t_md_xps2_all_inv_consume_report WHERE dt = '2018-06-14' limit 100";
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testStringOrderBy() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "SELECT dt\n" + ((((((((((((((((((((((((((((((((((((((((("\t, CASE\n" + "\t\tWHEN data_source = \'0\' THEN \'\u65b0\u54c1\u7b97\'\n") + "\t\tWHEN data_source = \'1\' THEN \'\u6c47\u7b97\'\n") + "\t\tELSE data_source\n") + "\tEND AS data_source, os, platform_id, ssp_id, adslotid\n") + "\t, adposition_name, ad_source, appid, appdelaytrack, bidtype\n") + "\t, stock, v_num, av_num, click_num\n") + "\t, CASE\n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN charge\n") + "\t\tELSE \'-\'\n") + "\tEND AS charge, av_stock, v_rate, av_rate, ctr1\n") + "\t, ctr2\n") + "\t, CASE\n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN ecpm1\n") + "\t\tELSE \'-\'\n") + "\tEND AS ecpm1\n") + "\t, CASE\n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN ecpm2\n") + "\t\tELSE \'-\'\n") + "\tEND AS ecpm2\n") + "\t, CASE\n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN acp\n") + "\t\tELSE \'-\'\n") + "\tEND AS acp\n") + "FROM t_md_xps2_all_inv_consume_report_v2\n") + "WHERE dt >= \'2018-05-20\'\n") + "AND dt <= \'2018-06-19\'\n") + "AND 1 = 1\n") + "AND data_source NOT IN (\'all\', \'\u5168\u90e8\')\n") + "AND ad_source = \'\u5168\u90e8\'\n") + "AND os = \'\u5168\u90e8\'\n") + "AND appdelaytrack = \'\u5168\u90e8\'\n") + "AND platform_id = \'\u5168\u90e8\'\n") + "AND appid = \'\u5168\u90e8\'\n") + "AND ssp_id = \'\u5168\u90e8\'\n") + "AND bidtype = \'\u5168\u90e8\'\n") + "AND adslotid IN (\'\u5168\u90e8\')\n") + "ORDER BY dt DESC, CASE\n") + "\tWHEN data_source = \'0\' THEN \'\u65b0\u54c1\u7b97\'\n") + "\tWHEN data_source = \'1\' THEN \'\u6c47\u7b97\'\n") + "\tELSE data_source\n") + "END ASC");
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testIf() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "select if(name='Joe','hehe','gg') from t_zhongshu_test_hive2es";
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testGroupBySize() throws SQLFeatureNotSupportedException, SqlParseException {
        sql = "SELECT \n" + ((((((((("aid,\n" + "appid,\n") + "accounttype,\n") + "(case when bidtype=\'1\' then \'CPD\' when bidtype=\'2\' then \'CPM\' when bidtype=\'3\' then \'CPC\' when bidtype=\'4\' then \'DCPM\' else \'\u5176\u5b83\' end ) as bidtype,\n") + "sum(count_v) as count_v,\n") + "sum(count_av) as count_av,\n") + "sum(count_click) as count_click,\n") + "sum(sum_charge) as sum_charge \n") + "FROM t_fact_tracking_charge where 1=1 and appid in (\'news\',\'newssdk\',\'wapnews\',\'pcnews\',\'tv\',\'h5tv\',\'pctv\',\'union\',\'wapunion\',\'squirrel\')\n") + "and dt=20180628 group by aid,appid order by count_v desc");
        QueryAction qa = ESActionFactory.create(client, sql);
        qa.explain();
    }

    @Test
    public void testBoge() {
        sql = "select \n" + ((((((((((((((((((((((((((((((((((((((("dt\n" + ",ad_source\n") + ",CASE WHEN platform_id = \'PC\' and os not in (\'\u5168\u90e8\') THEN \'unknown\' ELSE os  END AS os\n") + ",platform_id\n") + ",appid\n") + ",bidtype\n") + ",adslotid\n") + ",adposition_name\n") + ",adpostion_type\n") + ",is_effect\n") + ",theo_inv\n") + ",v_num\n") + ",av_num\n") + ",click_num\n") + ",charge\n") + ",av_stock\n") + ",av_stock_real\n") + ",v_rate\n") + ",av_rate\n") + ",ctr1\n") + ",ctr2\n") + ",ecpm1\n") + ",ecpm2\n") + ",acp\n") + ",case when theo_inv>v_num_high then 0 else theo_inv-v_num_high end as bid_inv\n") + ",v_num_high\n") + ",av_num_high\n") + ",click_num_high\n") + ",av_stock_tail\n") + ",av_stock_tail_real\n") + ",v_num_tail\n") + ",av_num_tail\n") + ",click_num_tail\n") + ",ctr_tail\n") + ",charge_tail\n") + ",ecpm_tail\n") + ",acp_tail\n") + "from\n") + "t_md_xps2_report_consume_video\n") + "where dt>='2018-09-03' and dt<='2018-09-03' and ssp_id not in ('??','??')");
    }

    @Test
    public void testBoge2() {
        sql = "SELECT dt, ad_source\n" + ((((((((((((((((((((((((((((((((("\t, CASE \n" + "\t\tWHEN platform_id = \'PC\'\n") + "\t\tAND os NOT IN (\'\u5168\u90e8\') THEN \'unknown\'\n") + "\t\tELSE os\n") + "\tEND AS os, platform_id, appid, bidtype, adslotid\n") + "\t, adposition_name, adpostion_type, is_effect, theo_inv, v_num\n") + "\t, av_num, click_num, charge, av_stock, av_stock_real\n") + "\t, v_rate, av_rate, ctr1, ctr2, ecpm1\n") + "\t, ecpm2, acp\n") + "\t, CASE \n") + "\t\tWHEN theo_inv <= v_num_high THEN 0\n") + "\t\tELSE theo_inv - v_num_high\n") + "\tEND AS bid_inv, v_num_high, av_num_high, click_num_high, av_stock_tail\n") + "\t, av_stock_tail_real, v_num_tail, av_num_tail, click_num_tail, ctr_tail\n") + "\t, charge_tail, ecpm_tail, acp_tail\n") + "FROM t_md_xps2_report_consume_video\n") + "WHERE (dt >= \'2018-09-04\'\n") + "\tAND dt <= \'2018-09-04\'\n") + "\tAND ssp_id NOT IN (\'\u5168\u90e8\', \'\u5a92\u4f53\')\n") + "\tAND 1 = 1\n") + "\tAND ad_source NOT IN (\'all\', \'\u5168\u90e8\')\n") + "\tAND os = \'\u5168\u90e8\'\n") + "\tAND platform_id IN (\'\u5168\u90e8\')\n") + "\tAND appid IN (\'\u5168\u90e8\')\n") + "\tAND bidtype IN (\'\u5168\u90e8\')\n") + "\tAND adslotid IN (\'\u5168\u90e8\')\n") + "\tAND adpostion_type = \'\u5168\u90e8\'\n") + "\tAND is_effect NOT IN (\'all\', \'\u5168\u90e8\'))\n") + "ORDER BY dt DESC, CASE \n") + "\tWHEN platform_id = \'PC\'\n") + "\tAND os NOT IN (\'\u5168\u90e8\') THEN \'unknown\'\n") + "\tELSE os\n") + "END ASC, platform_id DESC, appid ASC, ssp_id DESC, charge DESC\n") + "LIMIT 0, 10");
    }

    /**
     * ??????sql??
     * ubi????????sql?select * from (SELECT dt, CASE WHEN platform_id = 'PC' and os not in ('??') THEN 'unknown' ELSE os END AS os, platform_id, ssp_id, adslotid, adposition_name, ad_source, appid, appdelaytrack, bidtype, theo_inv, v_num, av_num, click_num, CASE WHEN ad_source = '???' THEN charge ELSE '-' END AS charge, CASE WHEN av_num >= v_num THEN av_num ELSE v_stock END AS av_stock, CASE WHEN appid in ('squirrel') THEN av_stock_real WHEN ssp_id = '??' and platform_id in ('APP', 'WAP') THEN av_stock_real WHEN adslotid in ('15695', '15696', '15650') THEN av_stock_real WHEN ssp_id = '???' THEN av_stock_real WHEN adslotid in ('1000001', '1000002', '1000003', '1000004') THEN av_stock_real ELSE '-' END AS av_stock_real, v_rate, av_rate, ctr1, ctr2, CASE WHEN ad_source = '???' THEN ecpm1 ELSE '-' END AS ecpm1, CASE WHEN ad_source = '???' THEN ecpm2 ELSE '-' END AS ecpm2, CASE WHEN ad_source = '???' THEN acp ELSE '-' END AS acp FROM t_md_xps2_all_inv_consume_report WHERE dt >= '2018-09-04' AND dt <= '2018-09-10' and ssp_id = '??' and ad_source='??' and appdelaytrack='??' and bidtype='??' ) tmp where 1=1 and os in ('Android') and platform_id not in ('all','??') and appid in ('??') and ssp_id = '??' and adslotid in ('??') order by dt desc,os asc,platform_id desc,appid asc,ssp_id desc,theo_inv desc limit 0, 10
     * ??????sql?????es-query-service???????sql
     * ?????
     * ??
     * 1???es-query-service??select??case when???where????
     * 2???es-sql????????sql where????????case when???script??
     */
    @Test
    public void testSongge() {
        // zhongshu-comment ???sql
        sql = "SELECT dt\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((("\t, CASE \n" + "\t\tWHEN platform_id = \'PC\'\n") + "\t\tAND os NOT IN (\'\u5168\u90e8\') THEN \'unknown\'\n") + "\t\tELSE os\n") + "\tEND AS os, platform_id, ssp_id, adslotid, adposition_name\n") + "\t, ad_source, appid, appdelaytrack, bidtype, theo_inv\n") + "\t, v_num, av_num, click_num\n") + "\t, CASE \n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN charge\n") + "\t\tELSE \'-\'\n") + "\tEND AS charge\n") + "\t, CASE \n") + "\t\tWHEN av_num >= v_num THEN av_num\n") + "\t\tELSE v_stock\n") + "\tEND AS av_stock\n") + "\t, CASE \n") + "\t\tWHEN appid IN (\'squirrel\') THEN av_stock_real\n") + "\t\tWHEN ssp_id = \'\u89c6\u9891\'\n") + "\t\tAND platform_id IN (\'APP\', \'WAP\') THEN av_stock_real\n") + "\t\tWHEN adslotid IN (\'15695\', \'15696\', \'15650\') THEN av_stock_real\n") + "\t\tWHEN ssp_id = \'\u8d44\u8baf\u7248\' THEN av_stock_real\n") + "\t\tWHEN adslotid IN (\'1000001\', \'1000002\', \'1000003\', \'1000004\') THEN av_stock_real\n") + "\t\tELSE \'-\'\n") + "\tEND AS av_stock_real, v_rate, av_rate, ctr1, ctr2\n") + "\t, CASE \n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN ecpm1\n") + "\t\tELSE \'-\'\n") + "\tEND AS ecpm1\n") + "\t, CASE \n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN ecpm2\n") + "\t\tELSE \'-\'\n") + "\tEND AS ecpm2\n") + "\t, CASE \n") + "\t\tWHEN ad_source = \'\u4e2d\u957f\u5c3e\' THEN acp\n") + "\t\tELSE \'-\'\n") + "\tEND AS acp\n") + "FROM t_md_xps2_all_inv_consume_report\n") + "WHERE (dt >= \'2018-09-04\'\n") + "\tAND dt <= \'2018-09-10\'\n") + "\tAND ssp_id = \'\u89c6\u9891\'\n") + "\tAND ad_source = \'\u5168\u90e8\'\n") + "\tAND appdelaytrack = \'\u5168\u90e8\'\n") + "\tAND bidtype = \'\u5168\u90e8\'\n") + "\tAND 1 = 1\n") + "\tAND CASE \n") + "\t\tWHEN platform_id = \'PC\'\n") + "\t\tAND os NOT IN (\'\u5168\u90e8\') THEN \'unknown\'\n") + "\t\tELSE os\n") + "\tEND IN (\'Android\')\n") + "\tAND platform_id NOT IN (\'all\', \'\u5168\u90e8\')\n") + "\tAND appid IN (\'\u5168\u90e8\')\n") + "\tAND ssp_id = \'\u89c6\u9891\'\n") + "\tAND adslotid IN (\'\u5168\u90e8\'))\n") + "ORDER BY dt DESC, CASE \n") + "\tWHEN platform_id = \'PC\'\n") + "\tAND os NOT IN (\'\u5168\u90e8\') THEN \'unknown\'\n") + "\tELSE os\n") + "END ASC, platform_id DESC, appid ASC, ssp_id DESC, theo_inv DESC\n") + "LIMIT 0, 10");
        System.out.println();
        /* sql = "SELECT CASE \n" +
        "\t\tWHEN platform_id = 'PC'\n" +
        "\t\tAND os NOT IN ('??') THEN 'unknown'\n" +
        "\t\tELSE os\n" +
        "\tEND AS os\n" +
        "FROM t_md_xps2_all_inv_consume_report\n" +
        "WHERE \n" +
        "\tCASE \n" +
        "\t\tWHEN platform_id = 'PC'\n" +
        "\t\tAND os NOT IN ('??') THEN 'unknown'\n" +
        "\t\tELSE os\n" +
        "\tEND NOT IN ('Android', 'iOS', 'WP')\n" +
        "LIMIT 0, 10";
         */
    }

    @Test
    public void testSongge2() {
        sql = "SELECT SUM(CASE \n" + (((((((((((((((((((((((((((((((((((((((((("\t\tWHEN (ett = \'v\'\n" + "\t\tAND bidtype = \'2\'\n") + "\t\tAND priority_f = \'p_70_75\'\n") + "\t\tAND accounttype = \'1\') THEN sum_charge\n") + "\t\tWHEN (ett = \'av\'\n") + "\t\tAND bidtype = \'4\'\n") + "\t\tAND ac_20 = \'1\'\n") + "\t\tAND priority_f = \'p_70_75\'\n") + "\t\tAND accounttype = \'1\') THEN sum_charge\n") + "\t\tWHEN (ett = \'click\'\n") + "\t\tAND bidtype = \'3\'\n") + "\t\tAND priority_f = \'p_70_75\'\n") + "\t\tAND accounttype = \'1\') THEN sum_charge\n") + "\t\tELSE 0\n") + "\tEND) AS sum_charge, SUM(CASE \n") + "\t\tWHEN ett = \'v\' THEN count_num\n") + "\t\tELSE 0\n") + "\tEND) AS v\n") + "\t, SUM(CASE \n") + "\t\tWHEN ett = \'av\' THEN count_num\n") + "\t\tELSE 0\n") + "\tEND) AS total_av, SUM(CASE \n") + "\t\tWHEN ett = \'av\'\n") + "\t\tAND ac_20 = \'1\' THEN count_num\n") + "\t\tELSE 0\n") + "\tEND) AS count_av\n") + "\t, SUM(CASE \n") + "\t\tWHEN ett = \'click\' THEN count_num\n") + "\t\tELSE 0\n") + "\tEND) AS count_click, SUM(CASE \n") + "\t\tWHEN ett = \'na\' THEN count_num\n") + "\t\tELSE 0\n") + "\tEND) AS na\n") + "\t, SUM(CASE \n") + "\t\tWHEN ett = \'naa\' THEN count_num\n") + "\t\tELSE 0\n") + "\tEND) AS naa, aid, adslotid\n") + "\t, appid, bidtype, priority_f, accounttype\n") + "FROM t_xps2_track_stream_aid_d\n") + "WHERE 1 = 1\n") + "\tAND dt = 20181018\n") + "GROUP BY adslotid, aid, appid, accounttype, priority_f, bidtype\n") + "LIMIT 10000000");
    }

    @Test
    public void test13() {
        sql = "SELECT COUNT(*) FROM elasticsearch-sql_test_index_account/account \n" + (("GROUP BY \n" + "\tgender, \n") + "\tterms(\'alias\'=\'ageAgg\',\'field\'=\'age\',\'size\'=3)");
    }
}

