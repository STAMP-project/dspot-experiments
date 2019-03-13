package com.querydsl.collections;


import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.group.Group;
import com.querydsl.core.types.Projections;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

import static QGroupBy3Test_AssetThreat.assetThreat;
import static QGroupBy3Test_RiskAnalysis.riskAnalysis;
import static QGroupBy3Test_Threat.threat;


public class GroupBy3Test {
    @QueryEntity
    public static class RiskAnalysis {
        public String id;

        public Set<GroupBy3Test.AssetThreat> assetThreats;
    }

    @QueryEntity
    public static class AssetThreat {
        public String id;

        public Set<GroupBy3Test.Threat> threats;
    }

    @QueryEntity
    public static class Threat {
        public String id;
    }

    @Test
    public void nested_expressions() {
        QGroupBy3Test_RiskAnalysis riskAnalysis = riskAnalysis;
        QGroupBy3Test_AssetThreat assetThreat = assetThreat;
        QGroupBy3Test_Threat threat = threat;
        ResultTransformer<Map<String, GroupBy3Test.RiskAnalysis>> transformer = groupBy(riskAnalysis.id).as(Projections.bean(GroupBy3Test.RiskAnalysis.class, riskAnalysis.id, set(Projections.bean(GroupBy3Test.AssetThreat.class, assetThreat.id, set(Projections.bean(GroupBy3Test.Threat.class, threat.id)).as("threats"))).as("assetThreats")));
        CloseableIterator iter = createMock(CloseableIterator.class);
        FetchableQuery projectable = createMock(FetchableQuery.class);
        expect(projectable.select(Projections.tuple(riskAnalysis.id, riskAnalysis.id, assetThreat.id, Projections.bean(GroupBy3Test.Threat.class, threat.id)))).andReturn(projectable);
        expect(projectable.iterate()).andReturn(iter);
        expect(iter.hasNext()).andReturn(false);
        iter.close();
        replay(iter, projectable);
        transformer.transform(projectable);
        verify(projectable);
    }

    @Test
    public void alias_usage() {
        QGroupBy3Test_RiskAnalysis riskAnalysis = riskAnalysis;
        QGroupBy3Test_AssetThreat assetThreat = assetThreat;
        QGroupBy3Test_Threat threat = threat;
        ResultTransformer<Map<String, Group>> transformer = groupBy(riskAnalysis.id).as(riskAnalysis.id, set(Projections.bean(GroupBy3Test.AssetThreat.class, assetThreat.id, set(Projections.bean(GroupBy3Test.Threat.class, threat.id)).as("threats")).as("assetThreats")));
        CloseableIterator iter = createMock(CloseableIterator.class);
        FetchableQuery projectable = createMock(FetchableQuery.class);
        expect(projectable.select(Projections.tuple(riskAnalysis.id, riskAnalysis.id, assetThreat.id, Projections.bean(GroupBy3Test.Threat.class, threat.id)))).andReturn(projectable);
        expect(projectable.iterate()).andReturn(iter);
        expect(iter.hasNext()).andReturn(false);
        iter.close();
        replay(iter, projectable);
        transformer.transform(projectable);
        verify(projectable);
    }
}

