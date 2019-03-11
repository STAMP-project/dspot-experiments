package com.alibaba.json.bvt.issue_1500;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.io.Serializable;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by SongLing.Dong on 11/7/2017.
 */
public class Issue1565 extends TestCase {
    public void test_testLargeBeanContainsOver256Field() {
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        // SmallBean smallBean = new SmallBean();
        // smallBean.setId("S35669xxxxxxxxxxxxxx");
        // smallBean.setNetValueDate(20171105);
        // 
        // System.out.println(JSON.toJSONString(smallBean, serializeConfig));
        Issue1565.LargeBean expectedBean = new Issue1565.LargeBean();
        expectedBean.setId("S35669");
        expectedBean.setNetValueDate(20171105);
        String expectedStr = "{\"id\":\"S35669\",\"net_value_date\":20171105}";
        String actualStr = JSON.toJSONString(expectedBean, serializeConfig);
        JSONObject actualBean = JSON.parseObject(actualStr);
        Assert.assertEquals(expectedStr, actualStr);
        Assert.assertEquals(expectedBean.getId(), actualBean.getString("id"));
        Assert.assertEquals(expectedBean.getNetValueDate(), actualBean.getInteger("net_value_date"));
    }

    public static class SmallBean implements Serializable {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getNetValueDate() {
            return netValueDate;
        }

        public void setNetValueDate(Integer netValueDate) {
            this.netValueDate = netValueDate;
        }

        private Integer netValueDate;
    }

    public static class LargeBean implements Serializable {
        /**
         * ????
         */
        private Integer pageSize;

        /**
         * ??????????
         */
        private Integer firstResult;

        /**
         * ????
         */
        private Integer fetchSize;

        /**
         * ????
         */
        private String startTime;

        /**
         * ????
         */
        private String endTime;

        /**
         * ???????
         */
        private Boolean isAdministrator;

        /**
         * ???? 0:?? 1:??
         */
        private Byte sortMode;

        /**
         * ?????
         */
        private String sortFieldName;

        /**
         * ?????
         */
        private String sortFieldValue;

        /**
         * ?????(??)
         */
        private Long createTimestamp;

        /**
         * ?????
         */
        private Integer lastPage;

        /**
         * ????
         */
        private Byte queryType;

        /**
         * ???
         */
        private String shard;

        /**
         * ????????yyyyMMdd
         */
        private Integer netValueDate;

        /**
         * ????
         */
        private Float unitNetValue;

        /**
         * ????
         */
        private Float totalNetValue;

        /**
         * ?????????
         */
        private Float nomTotalYield;

        /**
         * ????????
         */
        private Float nhyTotalYield;

        /**
         * ????????
         */
        private Float noyTotalYield;

        /**
         * ???????
         */
        private Float tmTotalYield;

        /**
         * ????????
         */
        private Float tqTotalYield;

        /**
         * ???????
         */
        private Float tyTotalYield;

        /**
         * ???????
         */
        private Float allTotalYield;

        /**
         * ?????????
         */
        private Float nomAnnualizedReturn;

        /**
         * ????????
         */
        private Float nhyAnnualizedReturn;

        /**
         * ????????
         */
        private Float noyAnnualizedReturn;

        /**
         * ???????
         */
        private Float tmAnnualizedReturn;

        /**
         * ????????
         */
        private Float tqAnnualizedReturn;

        /**
         * ???????
         */
        private Float tyAnnualizedReturn;

        /**
         * ???????
         */
        private Float allAnnualizedReturn;

        /**
         * ??????????
         */
        private Float nomMaxProfitMargin;

        /**
         * ?????????
         */
        private Float nhyMaxProfitMargin;

        /**
         * ?????????
         */
        private Float noyMaxProfitMargin;

        /**
         * ????????
         */
        private Float tmMaxProfitMargin;

        /**
         * ?????????
         */
        private Float tqMaxProfitMargin;

        /**
         * ????????
         */
        private Float tyMaxProfitMargin;

        /**
         * ????????
         */
        private Float allMaxProfitMargin;

        /**
         * ??????????
         */
        private Float nomMaxSingleProfit;

        /**
         * ?????????
         */
        private Float nhyMaxSingleProfit;

        /**
         * ?????????
         */
        private Float noyMaxSingleProfit;

        /**
         * ????????
         */
        private Float tmMaxSingleProfit;

        /**
         * ?????????
         */
        private Float tqMaxSingleProfit;

        /**
         * ????????
         */
        private Float tyMaxSingleProfit;

        /**
         * ????????
         */
        private Float allMaxSingleProfit;

        /**
         * ????????????
         */
        private Integer nomMaxConProfitTime;

        /**
         * ???????????
         */
        private Integer nhyMaxConProfitTime;

        /**
         * ???????????
         */
        private Integer noyMaxConProfitTime;

        /**
         * ??????????
         */
        private Integer tmMaxConProfitTime;

        /**
         * ???????????
         */
        private Integer tqMaxConProfitTime;

        /**
         * ??????????
         */
        private Integer tyMaxConProfitTime;

        /**
         * ??????????
         */
        private Integer allMaxConProfitTime;

        /**
         * ??????????????
         */
        private Integer allMaxConProfitTimeDate;

        /**
         * ????????
         */
        private Float nomMaxDrawdown;

        /**
         * ???????
         */
        private Float nhyMaxDrawdown;

        /**
         * ???????
         */
        private Float noyMaxDrawdown;

        /**
         * ??????
         */
        private Float tmMaxDrawdown;

        /**
         * ???????
         */
        private Float tqMaxDrawdown;

        /**
         * ??????
         */
        private Float tyMaxDrawdown;

        /**
         * ??????
         */
        private Float allMaxDrawdown;

        /**
         * ??????????
         */
        private Integer allMaxDrawdownDate;

        /**
         * ??????????
         */
        private Float nomMaxSingleDrawdown;

        /**
         * ?????????
         */
        private Float nhyMaxSingleDrawdown;

        /**
         * ?????????
         */
        private Float noyMaxSingleDrawdown;

        /**
         * ????????
         */
        private Float tmMaxSingleDrawdown;

        /**
         * ?????????
         */
        private Float tqMaxSingleDrawdown;

        /**
         * ????????
         */
        private Float tyMaxSingleDrawdown;

        /**
         * ????????
         */
        private Float allMaxSingleDrawdown;

        /**
         * ????????????
         */
        private Integer allMaxSingleDrawdownDate;

        /**
         * ????????????
         */
        private Integer nomMaxConDrawdownTime;

        /**
         * ???????????
         */
        private Integer nhyMaxConDrawdownTime;

        /**
         * ???????????
         */
        private Integer noyMaxConDrawdownTime;

        /**
         * ??????????
         */
        private Integer tmMaxConDrawdownTime;

        /**
         * ???????????
         */
        private Integer tqMaxConDrawdownTime;

        /**
         * ??????????
         */
        private Integer tyMaxConDrawdownTime;

        /**
         * ??????????
         */
        private Integer allMaxConDrawdownTime;

        /**
         * ??????????
         */
        private Float nomYieldStdDeviation;

        /**
         * ?????????
         */
        private Float nhyYieldStdDeviation;

        /**
         * ?????????
         */
        private Float noyYieldStdDeviation;

        /**
         * ????????
         */
        private Float tmYieldStdDeviation;

        /**
         * ?????????
         */
        private Float tqYieldStdDeviation;

        /**
         * ????????
         */
        private Float tyYieldStdDeviation;

        /**
         * ????????
         */
        private Float allYieldStdDeviation;

        /**
         * ?????????
         */
        private Float nomDownStdDeviation;

        /**
         * ????????
         */
        private Float nhyDownStdDeviation;

        /**
         * ????????
         */
        private Float noyDownStdDeviation;

        /**
         * ???????
         */
        private Float tmDownStdDeviation;

        /**
         * ????????
         */
        private Float tqDownStdDeviation;

        /**
         * ???????
         */
        private Float tyDownStdDeviation;

        /**
         * ???????
         */
        private Float allDownStdDeviation;

        /**
         * ??????
         */
        private Float nomWinRatio;

        /**
         * ?????
         */
        private Float nhyWinRatio;

        /**
         * ?????
         */
        private Float noyWinRatio;

        /**
         * ????
         */
        private Float tmWinRatio;

        /**
         * ?????
         */
        private Float tqWinRatio;

        /**
         * ????
         */
        private Float tyWinRatio;

        /**
         * ????
         */
        private Float allWinRatio;

        /**
         * ????????
         */
        private Float nomBeta;

        /**
         * ???????
         */
        private Float nhyBeta;

        /**
         * ???????
         */
        private Float noyBeta;

        /**
         * ??????
         */
        private Float tmBeta;

        /**
         * ???????
         */
        private Float tqBeta;

        /**
         * ??????
         */
        private Float tyBeta;

        /**
         * ??????
         */
        private Float allBeta;

        /**
         * ?????????
         */
        private Float nomAlpha;

        /**
         * ????????
         */
        private Float nhyAlpha;

        /**
         * ????????
         */
        private Float noyAlpha;

        /**
         * ???????
         */
        private Float tmAlpha;

        /**
         * ????????
         */
        private Float tqAlpha;

        /**
         * ???????
         */
        private Float tyAlpha;

        /**
         * ???????
         */
        private Float allAlpha;

        /**
         * ????????
         */
        private Float nomJansen;

        /**
         * ???????
         */
        private Float nhyJansen;

        /**
         * ???????
         */
        private Float noyJansen;

        /**
         * ??????
         */
        private Float tmJansen;

        /**
         * ???????
         */
        private Float tqJansen;

        /**
         * ??????
         */
        private Float tyJansen;

        /**
         * ??????
         */
        private Float allJansen;

        /**
         * ????????
         */
        private Float nomKumarRatio;

        /**
         * ???????
         */
        private Float nhyKumarRatio;

        /**
         * ???????
         */
        private Float noyKumarRatio;

        /**
         * ??????
         */
        private Float tmKumarRatio;

        /**
         * ???????
         */
        private Float tqKumarRatio;

        /**
         * ??????
         */
        private Float tyKumarRatio;

        /**
         * ??????
         */
        private Float allKumarRatio;

        /**
         * ????????
         */
        private Float nomSharpeRatio;

        /**
         * ???????
         */
        private Float nhySharpeRatio;

        /**
         * ???????
         */
        private Float noySharpeRatio;

        /**
         * ??????
         */
        private Float tmSharpeRatio;

        /**
         * ???????
         */
        private Float tqSharpeRatio;

        /**
         * ??????
         */
        private Float tySharpeRatio;

        /**
         * ??????
         */
        private Float allSharpeRatio;

        /**
         * ?????????
         */
        private Float nomSortinoRatio;

        /**
         * ????????
         */
        private Float nhySortinoRatio;

        /**
         * ????????
         */
        private Float noySortinoRatio;

        /**
         * ???????
         */
        private Float tmSortinoRatio;

        /**
         * ????????
         */
        private Float tqSortinoRatio;

        /**
         * ???????
         */
        private Float tySortinoRatio;

        /**
         * ???????
         */
        private Float allSortinoRatio;

        /**
         * ?????????
         */
        private Float nomHurstIndex;

        /**
         * ????????
         */
        private Float nhyHurstIndex;

        /**
         * ????????
         */
        private Float noyHurstIndex;

        /**
         * ???????
         */
        private Float tmHurstIndex;

        /**
         * ????????
         */
        private Float tqHurstIndex;

        /**
         * ???????
         */
        private Float tyHurstIndex;

        /**
         * ???????
         */
        private Float allHurstIndex;

        /**
         * ????VaR??(95%)
         */
        private Float nomVarIndex;

        /**
         * ???VaR??(95%)
         */
        private Float nhyVarIndex;

        /**
         * ???VaR??(95%)
         */
        private Float noyVarIndex;

        /**
         * ??VaR??(95%)
         */
        private Float tmVarIndex;

        /**
         * ???VaR??(95%)
         */
        private Float tqVarIndex;

        /**
         * ??VaR??(95%)
         */
        private Float tyVarIndex;

        /**
         * ??VaR??(95%)
         */
        private Float allVarIndex;

        /**
         * ????VaR??(99%)
         */
        private Float nomVarIndex99;

        /**
         * ???VaR??(99%)
         */
        private Float nhyVarIndex99;

        /**
         * ???VaR??(99%)
         */
        private Float noyVarIndex99;

        /**
         * ??VaR??(99%)
         */
        private Float tmVarIndex99;

        /**
         * ???VaR??(99%)
         */
        private Float tqVarIndex99;

        /**
         * ??VaR??(99%)
         */
        private Float tyVarIndex99;

        /**
         * ??VaR??(99%)
         */
        private Float allVarIndex99;

        /**
         * ?????????
         */
        private Float nomUpCaptureRate;

        /**
         * ????????
         */
        private Float nhyUpCaptureRate;

        /**
         * ????????
         */
        private Float noyUpCaptureRate;

        /**
         * ???????
         */
        private Float tmUpCaptureRate;

        /**
         * ????????
         */
        private Float tqUpCaptureRate;

        /**
         * ???????
         */
        private Float tyUpCaptureRate;

        /**
         * ???????
         */
        private Float allUpCaptureRate;

        /**
         * ?????????
         */
        private Float nomDownCaptureRate;

        /**
         * ????????
         */
        private Float nhyDownCaptureRate;

        /**
         * ????????
         */
        private Float noyDownCaptureRate;

        /**
         * ???????
         */
        private Float tmDownCaptureRate;

        /**
         * ????????
         */
        private Float tqDownCaptureRate;

        /**
         * ???????
         */
        private Float tyDownCaptureRate;

        /**
         * ???????
         */
        private Float allDownCaptureRate;

        /**
         * ????????
         */
        private Float nomInfoRatio;

        /**
         * ???????
         */
        private Float nhyInfoRatio;

        /**
         * ???????
         */
        private Float noyInfoRatio;

        /**
         * ??????
         */
        private Float tmInfoRatio;

        /**
         * ???????
         */
        private Float tqInfoRatio;

        /**
         * ??????
         */
        private Float tyInfoRatio;

        /**
         * ??????
         */
        private Float allInfoRatio;

        /**
         * ?????????
         */
        private Float nomAlgorithmVolatility;

        /**
         * ????????
         */
        private Float nhyAlgorithmVolatility;

        /**
         * ????????
         */
        private Float noyAlgorithmVolatility;

        /**
         * ???????
         */
        private Float tmAlgorithmVolatility;

        /**
         * ????????
         */
        private Float tqAlgorithmVolatility;

        /**
         * ???????
         */
        private Float tyAlgorithmVolatility;

        /**
         * ???????
         */
        private Float allAlgorithmVolatility;

        /**
         * ????M??
         */
        private Float nomMSquare;

        /**
         * ???M??
         */
        private Float nhyMSquare;

        /**
         * ???M??
         */
        private Float noyMSquare;

        /**
         * ??M??
         */
        private Float tmMSquare;

        /**
         * ???M??
         */
        private Float tqMSquare;

        /**
         * ??M??
         */
        private Float tyMSquare;

        /**
         * ??M??
         */
        private Float allMSquare;

        /**
         * ?????????(TR)
         */
        private Float nomTreynorIndex;

        /**
         * ????????(TR)
         */
        private Float nhyTreynorIndex;

        /**
         * ????????(TR)
         */
        private Float noyTreynorIndex;

        /**
         * ???????(TR)
         */
        private Float tmTreynorIndex;

        /**
         * ????????(TR)
         */
        private Float tqTreynorIndex;

        /**
         * ???????(TR)
         */
        private Float tyTreynorIndex;

        /**
         * ???????(TR)
         */
        private Float allTreynorIndex;

        /**
         * ????ID(???)
         */
        private String id;

        /**
         * ??????
         */
        private String name;

        /**
         * ???????
         */
        private String shortName;

        /**
         * ????
         */
        private String code;

        /**
         * ???
         */
        private String recordNumber;

        /**
         * ???? 0:???? 1:???? 2:????
         */
        private Byte fundType;

        /**
         * ???? 0:????? 1:????? 2:????? 3:????? 4:???????
         */
        private Byte fundBreed;

        /**
         * ???? 0:??? 1:???
         */
        private Byte fundStatus;

        /**
         * ??????????=1:???????????
         */
        private String buyStatus;

        /**
         * ??????????=1:???????????
         */
        private String redeemStatus;

        /**
         * ????????yyyy-MM-dd
         */
        private String recordDate;

        /**
         * ????????yyyy-MM-dd
         */
        private String createDate;

        /**
         * ????????yyyy-MM-dd
         */
        private String stopDate;

        /**
         * ??????
         */
        private String fundFilingStage;

        /**
         * ??????
         */
        private String fundInvestmentType;

        /**
         * ??
         */
        private String currency;

        /**
         * ????
         */
        private String managerType;

        /**
         * ?????
         */
        private String managerName;

        /**
         * ????
         */
        private String investmentTarget;

        /**
         * ????????????
         */
        private String majorInvestAreas;

        /**
         * ??????????
         */
        private String fundLastModifyDate;

        /**
         * ??????????????
         */
        private String specialNote;

        /**
         * ????
         */
        private String registeredAddress;

        /**
         * ????
         */
        private String investmentStrategy;

        /**
         * ?????
         */
        private String investmentSubStrategy;

        /**
         * ????ID??
         */
        private List<String> fundManagerIds;

        /**
         * ????ID
         */
        private String companyId;

        /**
         * ??
         */
        private Long orderNum;

        /**
         * ????
         */
        private String createScale;

        /**
         * ????
         */
        private String latestScale;

        /**
         * ??????
         */
        private String benchmark;

        /**
         * ??????
         */
        private Byte netValueUpdateRate;

        /**
         * ??????ID
         */
        private String fundOuterId;

        /**
         * ??
         */
        private String tags;

        /**
         * ??
         */
        private String remark;

        /**
         * ????
         */
        private String strategyCapacity;

        /**
         * ????
         */
        private Long createTime;

        /**
         * ???ID
         */
        private String creatorId;

        /**
         * ??????
         */
        private Long lastModifyTime;

        /**
         * ?????ID
         */
        private String lastModifierId;

        /**
         * ??????ID
         */
        private String companyOuterId;

        /**
         * ??????
         */
        private String companyName;

        /**
         * ??????ID??
         */
        private List<String> managerOuterIds;

        /**
         * ????ID??
         */
        private List<String> fundIds;

        /**
         * ????ID??
         */
        private List<String> companyIds;

        /**
         * ???????
         */
        private Float startAnnualizedReturn;

        /**
         * ???????
         */
        private Float endAnnualizedReturn;

        /**
         * ????
         */
        private String timeInterval;

        /**
         * ????????
         */
        private List<String> fundManagerNames;

        /**
         * ?????? 0:??? 1:???
         */
        private String fundStatusName;

        /**
         * ??????  0:???? 1:???? 2:????'
         */
        private String fundTypeName;

        /**
         * ?????? 0:? 1:?
         */
        private Byte isConcern;

        /**
         * ????(%)
         */
        private Float configWeight;

        /**
         * ??????? yyyy-MM-dd??
         */
        private String netValueDateString;

        /**
         * ????ID
         */
        private String managerId;

        /**
         * ????ID
         */
        private String tagId;

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getFirstResult() {
            return firstResult;
        }

        public void setFirstResult(Integer firstResult) {
            this.firstResult = firstResult;
        }

        public Integer getFetchSize() {
            return fetchSize;
        }

        public void setFetchSize(Integer fetchSize) {
            this.fetchSize = fetchSize;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public Boolean getAdministrator() {
            return isAdministrator;
        }

        public void setAdministrator(Boolean administrator) {
            isAdministrator = administrator;
        }

        public Byte getSortMode() {
            return sortMode;
        }

        public void setSortMode(Byte sortMode) {
            this.sortMode = sortMode;
        }

        public String getSortFieldName() {
            return sortFieldName;
        }

        public void setSortFieldName(String sortFieldName) {
            this.sortFieldName = sortFieldName;
        }

        public String getSortFieldValue() {
            return sortFieldValue;
        }

        public void setSortFieldValue(String sortFieldValue) {
            this.sortFieldValue = sortFieldValue;
        }

        public Long getCreateTimestamp() {
            return createTimestamp;
        }

        public void setCreateTimestamp(Long createTimestamp) {
            this.createTimestamp = createTimestamp;
        }

        public Integer getLastPage() {
            return lastPage;
        }

        public void setLastPage(Integer lastPage) {
            this.lastPage = lastPage;
        }

        public Byte getQueryType() {
            return queryType;
        }

        public void setQueryType(Byte queryType) {
            this.queryType = queryType;
        }

        public String getShard() {
            return shard;
        }

        public void setShard(String shard) {
            this.shard = shard;
        }

        public Integer getNetValueDate() {
            return netValueDate;
        }

        public void setNetValueDate(Integer netValueDate) {
            this.netValueDate = netValueDate;
        }

        public Float getUnitNetValue() {
            return unitNetValue;
        }

        public void setUnitNetValue(Float unitNetValue) {
            this.unitNetValue = unitNetValue;
        }

        public Float getTotalNetValue() {
            return totalNetValue;
        }

        public void setTotalNetValue(Float totalNetValue) {
            this.totalNetValue = totalNetValue;
        }

        public Float getNomTotalYield() {
            return nomTotalYield;
        }

        public void setNomTotalYield(Float nomTotalYield) {
            this.nomTotalYield = nomTotalYield;
        }

        public Float getNhyTotalYield() {
            return nhyTotalYield;
        }

        public void setNhyTotalYield(Float nhyTotalYield) {
            this.nhyTotalYield = nhyTotalYield;
        }

        public Float getNoyTotalYield() {
            return noyTotalYield;
        }

        public void setNoyTotalYield(Float noyTotalYield) {
            this.noyTotalYield = noyTotalYield;
        }

        public Float getTmTotalYield() {
            return tmTotalYield;
        }

        public void setTmTotalYield(Float tmTotalYield) {
            this.tmTotalYield = tmTotalYield;
        }

        public Float getTqTotalYield() {
            return tqTotalYield;
        }

        public void setTqTotalYield(Float tqTotalYield) {
            this.tqTotalYield = tqTotalYield;
        }

        public Float getTyTotalYield() {
            return tyTotalYield;
        }

        public void setTyTotalYield(Float tyTotalYield) {
            this.tyTotalYield = tyTotalYield;
        }

        public Float getAllTotalYield() {
            return allTotalYield;
        }

        public void setAllTotalYield(Float allTotalYield) {
            this.allTotalYield = allTotalYield;
        }

        public Float getNomAnnualizedReturn() {
            return nomAnnualizedReturn;
        }

        public void setNomAnnualizedReturn(Float nomAnnualizedReturn) {
            this.nomAnnualizedReturn = nomAnnualizedReturn;
        }

        public Float getNhyAnnualizedReturn() {
            return nhyAnnualizedReturn;
        }

        public void setNhyAnnualizedReturn(Float nhyAnnualizedReturn) {
            this.nhyAnnualizedReturn = nhyAnnualizedReturn;
        }

        public Float getNoyAnnualizedReturn() {
            return noyAnnualizedReturn;
        }

        public void setNoyAnnualizedReturn(Float noyAnnualizedReturn) {
            this.noyAnnualizedReturn = noyAnnualizedReturn;
        }

        public Float getTmAnnualizedReturn() {
            return tmAnnualizedReturn;
        }

        public void setTmAnnualizedReturn(Float tmAnnualizedReturn) {
            this.tmAnnualizedReturn = tmAnnualizedReturn;
        }

        public Float getTqAnnualizedReturn() {
            return tqAnnualizedReturn;
        }

        public void setTqAnnualizedReturn(Float tqAnnualizedReturn) {
            this.tqAnnualizedReturn = tqAnnualizedReturn;
        }

        public Float getTyAnnualizedReturn() {
            return tyAnnualizedReturn;
        }

        public void setTyAnnualizedReturn(Float tyAnnualizedReturn) {
            this.tyAnnualizedReturn = tyAnnualizedReturn;
        }

        public Float getAllAnnualizedReturn() {
            return allAnnualizedReturn;
        }

        public void setAllAnnualizedReturn(Float allAnnualizedReturn) {
            this.allAnnualizedReturn = allAnnualizedReturn;
        }

        public Float getNomMaxProfitMargin() {
            return nomMaxProfitMargin;
        }

        public void setNomMaxProfitMargin(Float nomMaxProfitMargin) {
            this.nomMaxProfitMargin = nomMaxProfitMargin;
        }

        public Float getNhyMaxProfitMargin() {
            return nhyMaxProfitMargin;
        }

        public void setNhyMaxProfitMargin(Float nhyMaxProfitMargin) {
            this.nhyMaxProfitMargin = nhyMaxProfitMargin;
        }

        public Float getNoyMaxProfitMargin() {
            return noyMaxProfitMargin;
        }

        public void setNoyMaxProfitMargin(Float noyMaxProfitMargin) {
            this.noyMaxProfitMargin = noyMaxProfitMargin;
        }

        public Float getTmMaxProfitMargin() {
            return tmMaxProfitMargin;
        }

        public void setTmMaxProfitMargin(Float tmMaxProfitMargin) {
            this.tmMaxProfitMargin = tmMaxProfitMargin;
        }

        public Float getTqMaxProfitMargin() {
            return tqMaxProfitMargin;
        }

        public void setTqMaxProfitMargin(Float tqMaxProfitMargin) {
            this.tqMaxProfitMargin = tqMaxProfitMargin;
        }

        public Float getTyMaxProfitMargin() {
            return tyMaxProfitMargin;
        }

        public void setTyMaxProfitMargin(Float tyMaxProfitMargin) {
            this.tyMaxProfitMargin = tyMaxProfitMargin;
        }

        public Float getAllMaxProfitMargin() {
            return allMaxProfitMargin;
        }

        public void setAllMaxProfitMargin(Float allMaxProfitMargin) {
            this.allMaxProfitMargin = allMaxProfitMargin;
        }

        public Float getNomMaxSingleProfit() {
            return nomMaxSingleProfit;
        }

        public void setNomMaxSingleProfit(Float nomMaxSingleProfit) {
            this.nomMaxSingleProfit = nomMaxSingleProfit;
        }

        public Float getNhyMaxSingleProfit() {
            return nhyMaxSingleProfit;
        }

        public void setNhyMaxSingleProfit(Float nhyMaxSingleProfit) {
            this.nhyMaxSingleProfit = nhyMaxSingleProfit;
        }

        public Float getNoyMaxSingleProfit() {
            return noyMaxSingleProfit;
        }

        public void setNoyMaxSingleProfit(Float noyMaxSingleProfit) {
            this.noyMaxSingleProfit = noyMaxSingleProfit;
        }

        public Float getTmMaxSingleProfit() {
            return tmMaxSingleProfit;
        }

        public void setTmMaxSingleProfit(Float tmMaxSingleProfit) {
            this.tmMaxSingleProfit = tmMaxSingleProfit;
        }

        public Float getTqMaxSingleProfit() {
            return tqMaxSingleProfit;
        }

        public void setTqMaxSingleProfit(Float tqMaxSingleProfit) {
            this.tqMaxSingleProfit = tqMaxSingleProfit;
        }

        public Float getTyMaxSingleProfit() {
            return tyMaxSingleProfit;
        }

        public void setTyMaxSingleProfit(Float tyMaxSingleProfit) {
            this.tyMaxSingleProfit = tyMaxSingleProfit;
        }

        public Float getAllMaxSingleProfit() {
            return allMaxSingleProfit;
        }

        public void setAllMaxSingleProfit(Float allMaxSingleProfit) {
            this.allMaxSingleProfit = allMaxSingleProfit;
        }

        public Integer getNomMaxConProfitTime() {
            return nomMaxConProfitTime;
        }

        public void setNomMaxConProfitTime(Integer nomMaxConProfitTime) {
            this.nomMaxConProfitTime = nomMaxConProfitTime;
        }

        public Integer getNhyMaxConProfitTime() {
            return nhyMaxConProfitTime;
        }

        public void setNhyMaxConProfitTime(Integer nhyMaxConProfitTime) {
            this.nhyMaxConProfitTime = nhyMaxConProfitTime;
        }

        public Integer getNoyMaxConProfitTime() {
            return noyMaxConProfitTime;
        }

        public void setNoyMaxConProfitTime(Integer noyMaxConProfitTime) {
            this.noyMaxConProfitTime = noyMaxConProfitTime;
        }

        public Integer getTmMaxConProfitTime() {
            return tmMaxConProfitTime;
        }

        public void setTmMaxConProfitTime(Integer tmMaxConProfitTime) {
            this.tmMaxConProfitTime = tmMaxConProfitTime;
        }

        public Integer getTqMaxConProfitTime() {
            return tqMaxConProfitTime;
        }

        public void setTqMaxConProfitTime(Integer tqMaxConProfitTime) {
            this.tqMaxConProfitTime = tqMaxConProfitTime;
        }

        public Integer getTyMaxConProfitTime() {
            return tyMaxConProfitTime;
        }

        public void setTyMaxConProfitTime(Integer tyMaxConProfitTime) {
            this.tyMaxConProfitTime = tyMaxConProfitTime;
        }

        public Integer getAllMaxConProfitTime() {
            return allMaxConProfitTime;
        }

        public void setAllMaxConProfitTime(Integer allMaxConProfitTime) {
            this.allMaxConProfitTime = allMaxConProfitTime;
        }

        public Integer getAllMaxConProfitTimeDate() {
            return allMaxConProfitTimeDate;
        }

        public void setAllMaxConProfitTimeDate(Integer allMaxConProfitTimeDate) {
            this.allMaxConProfitTimeDate = allMaxConProfitTimeDate;
        }

        public Float getNomMaxDrawdown() {
            return nomMaxDrawdown;
        }

        public void setNomMaxDrawdown(Float nomMaxDrawdown) {
            this.nomMaxDrawdown = nomMaxDrawdown;
        }

        public Float getNhyMaxDrawdown() {
            return nhyMaxDrawdown;
        }

        public void setNhyMaxDrawdown(Float nhyMaxDrawdown) {
            this.nhyMaxDrawdown = nhyMaxDrawdown;
        }

        public Float getNoyMaxDrawdown() {
            return noyMaxDrawdown;
        }

        public void setNoyMaxDrawdown(Float noyMaxDrawdown) {
            this.noyMaxDrawdown = noyMaxDrawdown;
        }

        public Float getTmMaxDrawdown() {
            return tmMaxDrawdown;
        }

        public void setTmMaxDrawdown(Float tmMaxDrawdown) {
            this.tmMaxDrawdown = tmMaxDrawdown;
        }

        public Float getTqMaxDrawdown() {
            return tqMaxDrawdown;
        }

        public void setTqMaxDrawdown(Float tqMaxDrawdown) {
            this.tqMaxDrawdown = tqMaxDrawdown;
        }

        public Float getTyMaxDrawdown() {
            return tyMaxDrawdown;
        }

        public void setTyMaxDrawdown(Float tyMaxDrawdown) {
            this.tyMaxDrawdown = tyMaxDrawdown;
        }

        public Float getAllMaxDrawdown() {
            return allMaxDrawdown;
        }

        public void setAllMaxDrawdown(Float allMaxDrawdown) {
            this.allMaxDrawdown = allMaxDrawdown;
        }

        public Integer getAllMaxDrawdownDate() {
            return allMaxDrawdownDate;
        }

        public void setAllMaxDrawdownDate(Integer allMaxDrawdownDate) {
            this.allMaxDrawdownDate = allMaxDrawdownDate;
        }

        public Float getNomMaxSingleDrawdown() {
            return nomMaxSingleDrawdown;
        }

        public void setNomMaxSingleDrawdown(Float nomMaxSingleDrawdown) {
            this.nomMaxSingleDrawdown = nomMaxSingleDrawdown;
        }

        public Float getNhyMaxSingleDrawdown() {
            return nhyMaxSingleDrawdown;
        }

        public void setNhyMaxSingleDrawdown(Float nhyMaxSingleDrawdown) {
            this.nhyMaxSingleDrawdown = nhyMaxSingleDrawdown;
        }

        public Float getNoyMaxSingleDrawdown() {
            return noyMaxSingleDrawdown;
        }

        public void setNoyMaxSingleDrawdown(Float noyMaxSingleDrawdown) {
            this.noyMaxSingleDrawdown = noyMaxSingleDrawdown;
        }

        public Float getTmMaxSingleDrawdown() {
            return tmMaxSingleDrawdown;
        }

        public void setTmMaxSingleDrawdown(Float tmMaxSingleDrawdown) {
            this.tmMaxSingleDrawdown = tmMaxSingleDrawdown;
        }

        public Float getTqMaxSingleDrawdown() {
            return tqMaxSingleDrawdown;
        }

        public void setTqMaxSingleDrawdown(Float tqMaxSingleDrawdown) {
            this.tqMaxSingleDrawdown = tqMaxSingleDrawdown;
        }

        public Float getTyMaxSingleDrawdown() {
            return tyMaxSingleDrawdown;
        }

        public void setTyMaxSingleDrawdown(Float tyMaxSingleDrawdown) {
            this.tyMaxSingleDrawdown = tyMaxSingleDrawdown;
        }

        public Float getAllMaxSingleDrawdown() {
            return allMaxSingleDrawdown;
        }

        public void setAllMaxSingleDrawdown(Float allMaxSingleDrawdown) {
            this.allMaxSingleDrawdown = allMaxSingleDrawdown;
        }

        public Integer getAllMaxSingleDrawdownDate() {
            return allMaxSingleDrawdownDate;
        }

        public void setAllMaxSingleDrawdownDate(Integer allMaxSingleDrawdownDate) {
            this.allMaxSingleDrawdownDate = allMaxSingleDrawdownDate;
        }

        public Integer getNomMaxConDrawdownTime() {
            return nomMaxConDrawdownTime;
        }

        public void setNomMaxConDrawdownTime(Integer nomMaxConDrawdownTime) {
            this.nomMaxConDrawdownTime = nomMaxConDrawdownTime;
        }

        public Integer getNhyMaxConDrawdownTime() {
            return nhyMaxConDrawdownTime;
        }

        public void setNhyMaxConDrawdownTime(Integer nhyMaxConDrawdownTime) {
            this.nhyMaxConDrawdownTime = nhyMaxConDrawdownTime;
        }

        public Integer getNoyMaxConDrawdownTime() {
            return noyMaxConDrawdownTime;
        }

        public void setNoyMaxConDrawdownTime(Integer noyMaxConDrawdownTime) {
            this.noyMaxConDrawdownTime = noyMaxConDrawdownTime;
        }

        public Integer getTmMaxConDrawdownTime() {
            return tmMaxConDrawdownTime;
        }

        public void setTmMaxConDrawdownTime(Integer tmMaxConDrawdownTime) {
            this.tmMaxConDrawdownTime = tmMaxConDrawdownTime;
        }

        public Integer getTqMaxConDrawdownTime() {
            return tqMaxConDrawdownTime;
        }

        public void setTqMaxConDrawdownTime(Integer tqMaxConDrawdownTime) {
            this.tqMaxConDrawdownTime = tqMaxConDrawdownTime;
        }

        public Integer getTyMaxConDrawdownTime() {
            return tyMaxConDrawdownTime;
        }

        public void setTyMaxConDrawdownTime(Integer tyMaxConDrawdownTime) {
            this.tyMaxConDrawdownTime = tyMaxConDrawdownTime;
        }

        public Integer getAllMaxConDrawdownTime() {
            return allMaxConDrawdownTime;
        }

        public void setAllMaxConDrawdownTime(Integer allMaxConDrawdownTime) {
            this.allMaxConDrawdownTime = allMaxConDrawdownTime;
        }

        public Float getNomYieldStdDeviation() {
            return nomYieldStdDeviation;
        }

        public void setNomYieldStdDeviation(Float nomYieldStdDeviation) {
            this.nomYieldStdDeviation = nomYieldStdDeviation;
        }

        public Float getNhyYieldStdDeviation() {
            return nhyYieldStdDeviation;
        }

        public void setNhyYieldStdDeviation(Float nhyYieldStdDeviation) {
            this.nhyYieldStdDeviation = nhyYieldStdDeviation;
        }

        public Float getNoyYieldStdDeviation() {
            return noyYieldStdDeviation;
        }

        public void setNoyYieldStdDeviation(Float noyYieldStdDeviation) {
            this.noyYieldStdDeviation = noyYieldStdDeviation;
        }

        public Float getTmYieldStdDeviation() {
            return tmYieldStdDeviation;
        }

        public void setTmYieldStdDeviation(Float tmYieldStdDeviation) {
            this.tmYieldStdDeviation = tmYieldStdDeviation;
        }

        public Float getTqYieldStdDeviation() {
            return tqYieldStdDeviation;
        }

        public void setTqYieldStdDeviation(Float tqYieldStdDeviation) {
            this.tqYieldStdDeviation = tqYieldStdDeviation;
        }

        public Float getTyYieldStdDeviation() {
            return tyYieldStdDeviation;
        }

        public void setTyYieldStdDeviation(Float tyYieldStdDeviation) {
            this.tyYieldStdDeviation = tyYieldStdDeviation;
        }

        public Float getAllYieldStdDeviation() {
            return allYieldStdDeviation;
        }

        public void setAllYieldStdDeviation(Float allYieldStdDeviation) {
            this.allYieldStdDeviation = allYieldStdDeviation;
        }

        public Float getNomDownStdDeviation() {
            return nomDownStdDeviation;
        }

        public void setNomDownStdDeviation(Float nomDownStdDeviation) {
            this.nomDownStdDeviation = nomDownStdDeviation;
        }

        public Float getNhyDownStdDeviation() {
            return nhyDownStdDeviation;
        }

        public void setNhyDownStdDeviation(Float nhyDownStdDeviation) {
            this.nhyDownStdDeviation = nhyDownStdDeviation;
        }

        public Float getNoyDownStdDeviation() {
            return noyDownStdDeviation;
        }

        public void setNoyDownStdDeviation(Float noyDownStdDeviation) {
            this.noyDownStdDeviation = noyDownStdDeviation;
        }

        public Float getTmDownStdDeviation() {
            return tmDownStdDeviation;
        }

        public void setTmDownStdDeviation(Float tmDownStdDeviation) {
            this.tmDownStdDeviation = tmDownStdDeviation;
        }

        public Float getTqDownStdDeviation() {
            return tqDownStdDeviation;
        }

        public void setTqDownStdDeviation(Float tqDownStdDeviation) {
            this.tqDownStdDeviation = tqDownStdDeviation;
        }

        public Float getTyDownStdDeviation() {
            return tyDownStdDeviation;
        }

        public void setTyDownStdDeviation(Float tyDownStdDeviation) {
            this.tyDownStdDeviation = tyDownStdDeviation;
        }

        public Float getAllDownStdDeviation() {
            return allDownStdDeviation;
        }

        public void setAllDownStdDeviation(Float allDownStdDeviation) {
            this.allDownStdDeviation = allDownStdDeviation;
        }

        public Float getNomWinRatio() {
            return nomWinRatio;
        }

        public void setNomWinRatio(Float nomWinRatio) {
            this.nomWinRatio = nomWinRatio;
        }

        public Float getNhyWinRatio() {
            return nhyWinRatio;
        }

        public void setNhyWinRatio(Float nhyWinRatio) {
            this.nhyWinRatio = nhyWinRatio;
        }

        public Float getNoyWinRatio() {
            return noyWinRatio;
        }

        public void setNoyWinRatio(Float noyWinRatio) {
            this.noyWinRatio = noyWinRatio;
        }

        public Float getTmWinRatio() {
            return tmWinRatio;
        }

        public void setTmWinRatio(Float tmWinRatio) {
            this.tmWinRatio = tmWinRatio;
        }

        public Float getTqWinRatio() {
            return tqWinRatio;
        }

        public void setTqWinRatio(Float tqWinRatio) {
            this.tqWinRatio = tqWinRatio;
        }

        public Float getTyWinRatio() {
            return tyWinRatio;
        }

        public void setTyWinRatio(Float tyWinRatio) {
            this.tyWinRatio = tyWinRatio;
        }

        public Float getAllWinRatio() {
            return allWinRatio;
        }

        public void setAllWinRatio(Float allWinRatio) {
            this.allWinRatio = allWinRatio;
        }

        public Float getNomBeta() {
            return nomBeta;
        }

        public void setNomBeta(Float nomBeta) {
            this.nomBeta = nomBeta;
        }

        public Float getNhyBeta() {
            return nhyBeta;
        }

        public void setNhyBeta(Float nhyBeta) {
            this.nhyBeta = nhyBeta;
        }

        public Float getNoyBeta() {
            return noyBeta;
        }

        public void setNoyBeta(Float noyBeta) {
            this.noyBeta = noyBeta;
        }

        public Float getTmBeta() {
            return tmBeta;
        }

        public void setTmBeta(Float tmBeta) {
            this.tmBeta = tmBeta;
        }

        public Float getTqBeta() {
            return tqBeta;
        }

        public void setTqBeta(Float tqBeta) {
            this.tqBeta = tqBeta;
        }

        public Float getTyBeta() {
            return tyBeta;
        }

        public void setTyBeta(Float tyBeta) {
            this.tyBeta = tyBeta;
        }

        public Float getAllBeta() {
            return allBeta;
        }

        public void setAllBeta(Float allBeta) {
            this.allBeta = allBeta;
        }

        public Float getNomAlpha() {
            return nomAlpha;
        }

        public void setNomAlpha(Float nomAlpha) {
            this.nomAlpha = nomAlpha;
        }

        public Float getNhyAlpha() {
            return nhyAlpha;
        }

        public void setNhyAlpha(Float nhyAlpha) {
            this.nhyAlpha = nhyAlpha;
        }

        public Float getNoyAlpha() {
            return noyAlpha;
        }

        public void setNoyAlpha(Float noyAlpha) {
            this.noyAlpha = noyAlpha;
        }

        public Float getTmAlpha() {
            return tmAlpha;
        }

        public void setTmAlpha(Float tmAlpha) {
            this.tmAlpha = tmAlpha;
        }

        public Float getTqAlpha() {
            return tqAlpha;
        }

        public void setTqAlpha(Float tqAlpha) {
            this.tqAlpha = tqAlpha;
        }

        public Float getTyAlpha() {
            return tyAlpha;
        }

        public void setTyAlpha(Float tyAlpha) {
            this.tyAlpha = tyAlpha;
        }

        public Float getAllAlpha() {
            return allAlpha;
        }

        public void setAllAlpha(Float allAlpha) {
            this.allAlpha = allAlpha;
        }

        public Float getNomJansen() {
            return nomJansen;
        }

        public void setNomJansen(Float nomJansen) {
            this.nomJansen = nomJansen;
        }

        public Float getNhyJansen() {
            return nhyJansen;
        }

        public void setNhyJansen(Float nhyJansen) {
            this.nhyJansen = nhyJansen;
        }

        public Float getNoyJansen() {
            return noyJansen;
        }

        public void setNoyJansen(Float noyJansen) {
            this.noyJansen = noyJansen;
        }

        public Float getTmJansen() {
            return tmJansen;
        }

        public void setTmJansen(Float tmJansen) {
            this.tmJansen = tmJansen;
        }

        public Float getTqJansen() {
            return tqJansen;
        }

        public void setTqJansen(Float tqJansen) {
            this.tqJansen = tqJansen;
        }

        public Float getTyJansen() {
            return tyJansen;
        }

        public void setTyJansen(Float tyJansen) {
            this.tyJansen = tyJansen;
        }

        public Float getAllJansen() {
            return allJansen;
        }

        public void setAllJansen(Float allJansen) {
            this.allJansen = allJansen;
        }

        public Float getNomKumarRatio() {
            return nomKumarRatio;
        }

        public void setNomKumarRatio(Float nomKumarRatio) {
            this.nomKumarRatio = nomKumarRatio;
        }

        public Float getNhyKumarRatio() {
            return nhyKumarRatio;
        }

        public void setNhyKumarRatio(Float nhyKumarRatio) {
            this.nhyKumarRatio = nhyKumarRatio;
        }

        public Float getNoyKumarRatio() {
            return noyKumarRatio;
        }

        public void setNoyKumarRatio(Float noyKumarRatio) {
            this.noyKumarRatio = noyKumarRatio;
        }

        public Float getTmKumarRatio() {
            return tmKumarRatio;
        }

        public void setTmKumarRatio(Float tmKumarRatio) {
            this.tmKumarRatio = tmKumarRatio;
        }

        public Float getTqKumarRatio() {
            return tqKumarRatio;
        }

        public void setTqKumarRatio(Float tqKumarRatio) {
            this.tqKumarRatio = tqKumarRatio;
        }

        public Float getTyKumarRatio() {
            return tyKumarRatio;
        }

        public void setTyKumarRatio(Float tyKumarRatio) {
            this.tyKumarRatio = tyKumarRatio;
        }

        public Float getAllKumarRatio() {
            return allKumarRatio;
        }

        public void setAllKumarRatio(Float allKumarRatio) {
            this.allKumarRatio = allKumarRatio;
        }

        public Float getNomSharpeRatio() {
            return nomSharpeRatio;
        }

        public void setNomSharpeRatio(Float nomSharpeRatio) {
            this.nomSharpeRatio = nomSharpeRatio;
        }

        public Float getNhySharpeRatio() {
            return nhySharpeRatio;
        }

        public void setNhySharpeRatio(Float nhySharpeRatio) {
            this.nhySharpeRatio = nhySharpeRatio;
        }

        public Float getNoySharpeRatio() {
            return noySharpeRatio;
        }

        public void setNoySharpeRatio(Float noySharpeRatio) {
            this.noySharpeRatio = noySharpeRatio;
        }

        public Float getTmSharpeRatio() {
            return tmSharpeRatio;
        }

        public void setTmSharpeRatio(Float tmSharpeRatio) {
            this.tmSharpeRatio = tmSharpeRatio;
        }

        public Float getTqSharpeRatio() {
            return tqSharpeRatio;
        }

        public void setTqSharpeRatio(Float tqSharpeRatio) {
            this.tqSharpeRatio = tqSharpeRatio;
        }

        public Float getTySharpeRatio() {
            return tySharpeRatio;
        }

        public void setTySharpeRatio(Float tySharpeRatio) {
            this.tySharpeRatio = tySharpeRatio;
        }

        public Float getAllSharpeRatio() {
            return allSharpeRatio;
        }

        public void setAllSharpeRatio(Float allSharpeRatio) {
            this.allSharpeRatio = allSharpeRatio;
        }

        public Float getNomSortinoRatio() {
            return nomSortinoRatio;
        }

        public void setNomSortinoRatio(Float nomSortinoRatio) {
            this.nomSortinoRatio = nomSortinoRatio;
        }

        public Float getNhySortinoRatio() {
            return nhySortinoRatio;
        }

        public void setNhySortinoRatio(Float nhySortinoRatio) {
            this.nhySortinoRatio = nhySortinoRatio;
        }

        public Float getNoySortinoRatio() {
            return noySortinoRatio;
        }

        public void setNoySortinoRatio(Float noySortinoRatio) {
            this.noySortinoRatio = noySortinoRatio;
        }

        public Float getTmSortinoRatio() {
            return tmSortinoRatio;
        }

        public void setTmSortinoRatio(Float tmSortinoRatio) {
            this.tmSortinoRatio = tmSortinoRatio;
        }

        public Float getTqSortinoRatio() {
            return tqSortinoRatio;
        }

        public void setTqSortinoRatio(Float tqSortinoRatio) {
            this.tqSortinoRatio = tqSortinoRatio;
        }

        public Float getTySortinoRatio() {
            return tySortinoRatio;
        }

        public void setTySortinoRatio(Float tySortinoRatio) {
            this.tySortinoRatio = tySortinoRatio;
        }

        public Float getAllSortinoRatio() {
            return allSortinoRatio;
        }

        public void setAllSortinoRatio(Float allSortinoRatio) {
            this.allSortinoRatio = allSortinoRatio;
        }

        public Float getNomHurstIndex() {
            return nomHurstIndex;
        }

        public void setNomHurstIndex(Float nomHurstIndex) {
            this.nomHurstIndex = nomHurstIndex;
        }

        public Float getNhyHurstIndex() {
            return nhyHurstIndex;
        }

        public void setNhyHurstIndex(Float nhyHurstIndex) {
            this.nhyHurstIndex = nhyHurstIndex;
        }

        public Float getNoyHurstIndex() {
            return noyHurstIndex;
        }

        public void setNoyHurstIndex(Float noyHurstIndex) {
            this.noyHurstIndex = noyHurstIndex;
        }

        public Float getTmHurstIndex() {
            return tmHurstIndex;
        }

        public void setTmHurstIndex(Float tmHurstIndex) {
            this.tmHurstIndex = tmHurstIndex;
        }

        public Float getTqHurstIndex() {
            return tqHurstIndex;
        }

        public void setTqHurstIndex(Float tqHurstIndex) {
            this.tqHurstIndex = tqHurstIndex;
        }

        public Float getTyHurstIndex() {
            return tyHurstIndex;
        }

        public void setTyHurstIndex(Float tyHurstIndex) {
            this.tyHurstIndex = tyHurstIndex;
        }

        public Float getAllHurstIndex() {
            return allHurstIndex;
        }

        public void setAllHurstIndex(Float allHurstIndex) {
            this.allHurstIndex = allHurstIndex;
        }

        public Float getNomVarIndex() {
            return nomVarIndex;
        }

        public void setNomVarIndex(Float nomVarIndex) {
            this.nomVarIndex = nomVarIndex;
        }

        public Float getNhyVarIndex() {
            return nhyVarIndex;
        }

        public void setNhyVarIndex(Float nhyVarIndex) {
            this.nhyVarIndex = nhyVarIndex;
        }

        public Float getNoyVarIndex() {
            return noyVarIndex;
        }

        public void setNoyVarIndex(Float noyVarIndex) {
            this.noyVarIndex = noyVarIndex;
        }

        public Float getTmVarIndex() {
            return tmVarIndex;
        }

        public void setTmVarIndex(Float tmVarIndex) {
            this.tmVarIndex = tmVarIndex;
        }

        public Float getTqVarIndex() {
            return tqVarIndex;
        }

        public void setTqVarIndex(Float tqVarIndex) {
            this.tqVarIndex = tqVarIndex;
        }

        public Float getTyVarIndex() {
            return tyVarIndex;
        }

        public void setTyVarIndex(Float tyVarIndex) {
            this.tyVarIndex = tyVarIndex;
        }

        public Float getAllVarIndex() {
            return allVarIndex;
        }

        public void setAllVarIndex(Float allVarIndex) {
            this.allVarIndex = allVarIndex;
        }

        public Float getNomVarIndex99() {
            return nomVarIndex99;
        }

        public void setNomVarIndex99(Float nomVarIndex99) {
            this.nomVarIndex99 = nomVarIndex99;
        }

        public Float getNhyVarIndex99() {
            return nhyVarIndex99;
        }

        public void setNhyVarIndex99(Float nhyVarIndex99) {
            this.nhyVarIndex99 = nhyVarIndex99;
        }

        public Float getNoyVarIndex99() {
            return noyVarIndex99;
        }

        public void setNoyVarIndex99(Float noyVarIndex99) {
            this.noyVarIndex99 = noyVarIndex99;
        }

        public Float getTmVarIndex99() {
            return tmVarIndex99;
        }

        public void setTmVarIndex99(Float tmVarIndex99) {
            this.tmVarIndex99 = tmVarIndex99;
        }

        public Float getTqVarIndex99() {
            return tqVarIndex99;
        }

        public void setTqVarIndex99(Float tqVarIndex99) {
            this.tqVarIndex99 = tqVarIndex99;
        }

        public Float getTyVarIndex99() {
            return tyVarIndex99;
        }

        public void setTyVarIndex99(Float tyVarIndex99) {
            this.tyVarIndex99 = tyVarIndex99;
        }

        public Float getAllVarIndex99() {
            return allVarIndex99;
        }

        public void setAllVarIndex99(Float allVarIndex99) {
            this.allVarIndex99 = allVarIndex99;
        }

        public Float getNomUpCaptureRate() {
            return nomUpCaptureRate;
        }

        public void setNomUpCaptureRate(Float nomUpCaptureRate) {
            this.nomUpCaptureRate = nomUpCaptureRate;
        }

        public Float getNhyUpCaptureRate() {
            return nhyUpCaptureRate;
        }

        public void setNhyUpCaptureRate(Float nhyUpCaptureRate) {
            this.nhyUpCaptureRate = nhyUpCaptureRate;
        }

        public Float getNoyUpCaptureRate() {
            return noyUpCaptureRate;
        }

        public void setNoyUpCaptureRate(Float noyUpCaptureRate) {
            this.noyUpCaptureRate = noyUpCaptureRate;
        }

        public Float getTmUpCaptureRate() {
            return tmUpCaptureRate;
        }

        public void setTmUpCaptureRate(Float tmUpCaptureRate) {
            this.tmUpCaptureRate = tmUpCaptureRate;
        }

        public Float getTqUpCaptureRate() {
            return tqUpCaptureRate;
        }

        public void setTqUpCaptureRate(Float tqUpCaptureRate) {
            this.tqUpCaptureRate = tqUpCaptureRate;
        }

        public Float getTyUpCaptureRate() {
            return tyUpCaptureRate;
        }

        public void setTyUpCaptureRate(Float tyUpCaptureRate) {
            this.tyUpCaptureRate = tyUpCaptureRate;
        }

        public Float getAllUpCaptureRate() {
            return allUpCaptureRate;
        }

        public void setAllUpCaptureRate(Float allUpCaptureRate) {
            this.allUpCaptureRate = allUpCaptureRate;
        }

        public Float getNomDownCaptureRate() {
            return nomDownCaptureRate;
        }

        public void setNomDownCaptureRate(Float nomDownCaptureRate) {
            this.nomDownCaptureRate = nomDownCaptureRate;
        }

        public Float getNhyDownCaptureRate() {
            return nhyDownCaptureRate;
        }

        public void setNhyDownCaptureRate(Float nhyDownCaptureRate) {
            this.nhyDownCaptureRate = nhyDownCaptureRate;
        }

        public Float getNoyDownCaptureRate() {
            return noyDownCaptureRate;
        }

        public void setNoyDownCaptureRate(Float noyDownCaptureRate) {
            this.noyDownCaptureRate = noyDownCaptureRate;
        }

        public Float getTmDownCaptureRate() {
            return tmDownCaptureRate;
        }

        public void setTmDownCaptureRate(Float tmDownCaptureRate) {
            this.tmDownCaptureRate = tmDownCaptureRate;
        }

        public Float getTqDownCaptureRate() {
            return tqDownCaptureRate;
        }

        public void setTqDownCaptureRate(Float tqDownCaptureRate) {
            this.tqDownCaptureRate = tqDownCaptureRate;
        }

        public Float getTyDownCaptureRate() {
            return tyDownCaptureRate;
        }

        public void setTyDownCaptureRate(Float tyDownCaptureRate) {
            this.tyDownCaptureRate = tyDownCaptureRate;
        }

        public Float getAllDownCaptureRate() {
            return allDownCaptureRate;
        }

        public void setAllDownCaptureRate(Float allDownCaptureRate) {
            this.allDownCaptureRate = allDownCaptureRate;
        }

        public Float getNomInfoRatio() {
            return nomInfoRatio;
        }

        public void setNomInfoRatio(Float nomInfoRatio) {
            this.nomInfoRatio = nomInfoRatio;
        }

        public Float getNhyInfoRatio() {
            return nhyInfoRatio;
        }

        public void setNhyInfoRatio(Float nhyInfoRatio) {
            this.nhyInfoRatio = nhyInfoRatio;
        }

        public Float getNoyInfoRatio() {
            return noyInfoRatio;
        }

        public void setNoyInfoRatio(Float noyInfoRatio) {
            this.noyInfoRatio = noyInfoRatio;
        }

        public Float getTmInfoRatio() {
            return tmInfoRatio;
        }

        public void setTmInfoRatio(Float tmInfoRatio) {
            this.tmInfoRatio = tmInfoRatio;
        }

        public Float getTqInfoRatio() {
            return tqInfoRatio;
        }

        public void setTqInfoRatio(Float tqInfoRatio) {
            this.tqInfoRatio = tqInfoRatio;
        }

        public Float getTyInfoRatio() {
            return tyInfoRatio;
        }

        public void setTyInfoRatio(Float tyInfoRatio) {
            this.tyInfoRatio = tyInfoRatio;
        }

        public Float getAllInfoRatio() {
            return allInfoRatio;
        }

        public void setAllInfoRatio(Float allInfoRatio) {
            this.allInfoRatio = allInfoRatio;
        }

        public Float getNomAlgorithmVolatility() {
            return nomAlgorithmVolatility;
        }

        public void setNomAlgorithmVolatility(Float nomAlgorithmVolatility) {
            this.nomAlgorithmVolatility = nomAlgorithmVolatility;
        }

        public Float getNhyAlgorithmVolatility() {
            return nhyAlgorithmVolatility;
        }

        public void setNhyAlgorithmVolatility(Float nhyAlgorithmVolatility) {
            this.nhyAlgorithmVolatility = nhyAlgorithmVolatility;
        }

        public Float getNoyAlgorithmVolatility() {
            return noyAlgorithmVolatility;
        }

        public void setNoyAlgorithmVolatility(Float noyAlgorithmVolatility) {
            this.noyAlgorithmVolatility = noyAlgorithmVolatility;
        }

        public Float getTmAlgorithmVolatility() {
            return tmAlgorithmVolatility;
        }

        public void setTmAlgorithmVolatility(Float tmAlgorithmVolatility) {
            this.tmAlgorithmVolatility = tmAlgorithmVolatility;
        }

        public Float getTqAlgorithmVolatility() {
            return tqAlgorithmVolatility;
        }

        public void setTqAlgorithmVolatility(Float tqAlgorithmVolatility) {
            this.tqAlgorithmVolatility = tqAlgorithmVolatility;
        }

        public Float getTyAlgorithmVolatility() {
            return tyAlgorithmVolatility;
        }

        public void setTyAlgorithmVolatility(Float tyAlgorithmVolatility) {
            this.tyAlgorithmVolatility = tyAlgorithmVolatility;
        }

        public Float getAllAlgorithmVolatility() {
            return allAlgorithmVolatility;
        }

        public void setAllAlgorithmVolatility(Float allAlgorithmVolatility) {
            this.allAlgorithmVolatility = allAlgorithmVolatility;
        }

        public Float getNomMSquare() {
            return nomMSquare;
        }

        public void setNomMSquare(Float nomMSquare) {
            this.nomMSquare = nomMSquare;
        }

        public Float getNhyMSquare() {
            return nhyMSquare;
        }

        public void setNhyMSquare(Float nhyMSquare) {
            this.nhyMSquare = nhyMSquare;
        }

        public Float getNoyMSquare() {
            return noyMSquare;
        }

        public void setNoyMSquare(Float noyMSquare) {
            this.noyMSquare = noyMSquare;
        }

        public Float getTmMSquare() {
            return tmMSquare;
        }

        public void setTmMSquare(Float tmMSquare) {
            this.tmMSquare = tmMSquare;
        }

        public Float getTqMSquare() {
            return tqMSquare;
        }

        public void setTqMSquare(Float tqMSquare) {
            this.tqMSquare = tqMSquare;
        }

        public Float getTyMSquare() {
            return tyMSquare;
        }

        public void setTyMSquare(Float tyMSquare) {
            this.tyMSquare = tyMSquare;
        }

        public Float getAllMSquare() {
            return allMSquare;
        }

        public void setAllMSquare(Float allMSquare) {
            this.allMSquare = allMSquare;
        }

        public Float getNomTreynorIndex() {
            return nomTreynorIndex;
        }

        public void setNomTreynorIndex(Float nomTreynorIndex) {
            this.nomTreynorIndex = nomTreynorIndex;
        }

        public Float getNhyTreynorIndex() {
            return nhyTreynorIndex;
        }

        public void setNhyTreynorIndex(Float nhyTreynorIndex) {
            this.nhyTreynorIndex = nhyTreynorIndex;
        }

        public Float getNoyTreynorIndex() {
            return noyTreynorIndex;
        }

        public void setNoyTreynorIndex(Float noyTreynorIndex) {
            this.noyTreynorIndex = noyTreynorIndex;
        }

        public Float getTmTreynorIndex() {
            return tmTreynorIndex;
        }

        public void setTmTreynorIndex(Float tmTreynorIndex) {
            this.tmTreynorIndex = tmTreynorIndex;
        }

        public Float getTqTreynorIndex() {
            return tqTreynorIndex;
        }

        public void setTqTreynorIndex(Float tqTreynorIndex) {
            this.tqTreynorIndex = tqTreynorIndex;
        }

        public Float getTyTreynorIndex() {
            return tyTreynorIndex;
        }

        public void setTyTreynorIndex(Float tyTreynorIndex) {
            this.tyTreynorIndex = tyTreynorIndex;
        }

        public Float getAllTreynorIndex() {
            return allTreynorIndex;
        }

        public void setAllTreynorIndex(Float allTreynorIndex) {
            this.allTreynorIndex = allTreynorIndex;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getRecordNumber() {
            return recordNumber;
        }

        public void setRecordNumber(String recordNumber) {
            this.recordNumber = recordNumber;
        }

        public Byte getFundType() {
            return fundType;
        }

        public void setFundType(Byte fundType) {
            this.fundType = fundType;
        }

        public Byte getFundBreed() {
            return fundBreed;
        }

        public void setFundBreed(Byte fundBreed) {
            this.fundBreed = fundBreed;
        }

        public Byte getFundStatus() {
            return fundStatus;
        }

        public void setFundStatus(Byte fundStatus) {
            this.fundStatus = fundStatus;
        }

        public String getBuyStatus() {
            return buyStatus;
        }

        public void setBuyStatus(String buyStatus) {
            this.buyStatus = buyStatus;
        }

        public String getRedeemStatus() {
            return redeemStatus;
        }

        public void setRedeemStatus(String redeemStatus) {
            this.redeemStatus = redeemStatus;
        }

        public String getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(String recordDate) {
            this.recordDate = recordDate;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getStopDate() {
            return stopDate;
        }

        public void setStopDate(String stopDate) {
            this.stopDate = stopDate;
        }

        public String getFundFilingStage() {
            return fundFilingStage;
        }

        public void setFundFilingStage(String fundFilingStage) {
            this.fundFilingStage = fundFilingStage;
        }

        public String getFundInvestmentType() {
            return fundInvestmentType;
        }

        public void setFundInvestmentType(String fundInvestmentType) {
            this.fundInvestmentType = fundInvestmentType;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getManagerType() {
            return managerType;
        }

        public void setManagerType(String managerType) {
            this.managerType = managerType;
        }

        public String getManagerName() {
            return managerName;
        }

        public void setManagerName(String managerName) {
            this.managerName = managerName;
        }

        public String getInvestmentTarget() {
            return investmentTarget;
        }

        public void setInvestmentTarget(String investmentTarget) {
            this.investmentTarget = investmentTarget;
        }

        public String getMajorInvestAreas() {
            return majorInvestAreas;
        }

        public void setMajorInvestAreas(String majorInvestAreas) {
            this.majorInvestAreas = majorInvestAreas;
        }

        public String getFundLastModifyDate() {
            return fundLastModifyDate;
        }

        public void setFundLastModifyDate(String fundLastModifyDate) {
            this.fundLastModifyDate = fundLastModifyDate;
        }

        public String getSpecialNote() {
            return specialNote;
        }

        public void setSpecialNote(String specialNote) {
            this.specialNote = specialNote;
        }

        public String getRegisteredAddress() {
            return registeredAddress;
        }

        public void setRegisteredAddress(String registeredAddress) {
            this.registeredAddress = registeredAddress;
        }

        public String getInvestmentStrategy() {
            return investmentStrategy;
        }

        public void setInvestmentStrategy(String investmentStrategy) {
            this.investmentStrategy = investmentStrategy;
        }

        public String getInvestmentSubStrategy() {
            return investmentSubStrategy;
        }

        public void setInvestmentSubStrategy(String investmentSubStrategy) {
            this.investmentSubStrategy = investmentSubStrategy;
        }

        public List<String> getFundManagerIds() {
            return fundManagerIds;
        }

        public void setFundManagerIds(List<String> fundManagerIds) {
            this.fundManagerIds = fundManagerIds;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public Long getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(Long orderNum) {
            this.orderNum = orderNum;
        }

        public String getCreateScale() {
            return createScale;
        }

        public void setCreateScale(String createScale) {
            this.createScale = createScale;
        }

        public String getLatestScale() {
            return latestScale;
        }

        public void setLatestScale(String latestScale) {
            this.latestScale = latestScale;
        }

        public String getBenchmark() {
            return benchmark;
        }

        public void setBenchmark(String benchmark) {
            this.benchmark = benchmark;
        }

        public Byte getNetValueUpdateRate() {
            return netValueUpdateRate;
        }

        public void setNetValueUpdateRate(Byte netValueUpdateRate) {
            this.netValueUpdateRate = netValueUpdateRate;
        }

        public String getFundOuterId() {
            return fundOuterId;
        }

        public void setFundOuterId(String fundOuterId) {
            this.fundOuterId = fundOuterId;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getStrategyCapacity() {
            return strategyCapacity;
        }

        public void setStrategyCapacity(String strategyCapacity) {
            this.strategyCapacity = strategyCapacity;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public String getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(String creatorId) {
            this.creatorId = creatorId;
        }

        public Long getLastModifyTime() {
            return lastModifyTime;
        }

        public void setLastModifyTime(Long lastModifyTime) {
            this.lastModifyTime = lastModifyTime;
        }

        public String getLastModifierId() {
            return lastModifierId;
        }

        public void setLastModifierId(String lastModifierId) {
            this.lastModifierId = lastModifierId;
        }

        public String getCompanyOuterId() {
            return companyOuterId;
        }

        public void setCompanyOuterId(String companyOuterId) {
            this.companyOuterId = companyOuterId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public List<String> getManagerOuterIds() {
            return managerOuterIds;
        }

        public void setManagerOuterIds(List<String> managerOuterIds) {
            this.managerOuterIds = managerOuterIds;
        }

        public List<String> getFundIds() {
            return fundIds;
        }

        public void setFundIds(List<String> fundIds) {
            this.fundIds = fundIds;
        }

        public List<String> getCompanyIds() {
            return companyIds;
        }

        public void setCompanyIds(List<String> companyIds) {
            this.companyIds = companyIds;
        }

        public Float getStartAnnualizedReturn() {
            return startAnnualizedReturn;
        }

        public void setStartAnnualizedReturn(Float startAnnualizedReturn) {
            this.startAnnualizedReturn = startAnnualizedReturn;
        }

        public Float getEndAnnualizedReturn() {
            return endAnnualizedReturn;
        }

        public void setEndAnnualizedReturn(Float endAnnualizedReturn) {
            this.endAnnualizedReturn = endAnnualizedReturn;
        }

        public String getTimeInterval() {
            return timeInterval;
        }

        public void setTimeInterval(String timeInterval) {
            this.timeInterval = timeInterval;
        }

        public List<String> getFundManagerNames() {
            return fundManagerNames;
        }

        public void setFundManagerNames(List<String> fundManagerNames) {
            this.fundManagerNames = fundManagerNames;
        }

        public String getFundStatusName() {
            return fundStatusName;
        }

        public void setFundStatusName(String fundStatusName) {
            this.fundStatusName = fundStatusName;
        }

        public String getFundTypeName() {
            return fundTypeName;
        }

        public void setFundTypeName(String fundTypeName) {
            this.fundTypeName = fundTypeName;
        }

        public Byte getIsConcern() {
            return isConcern;
        }

        public void setIsConcern(Byte isConcern) {
            this.isConcern = isConcern;
        }

        public Float getConfigWeight() {
            return configWeight;
        }

        public void setConfigWeight(Float configWeight) {
            this.configWeight = configWeight;
        }

        public String getNetValueDateString() {
            return netValueDateString;
        }

        public void setNetValueDateString(String netValueDateString) {
            this.netValueDateString = netValueDateString;
        }

        public String getManagerId() {
            return managerId;
        }

        public void setManagerId(String managerId) {
            this.managerId = managerId;
        }

        public String getTagId() {
            return tagId;
        }

        public void setTagId(String tagId) {
            this.tagId = tagId;
        }
    }
}

