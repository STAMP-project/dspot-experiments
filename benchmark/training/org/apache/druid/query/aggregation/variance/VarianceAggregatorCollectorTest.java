/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.aggregation.variance;


import VarianceBufferAggregator.ObjectVarianceAggregator;
import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.segment.TestFloatColumnSelector;
import org.apache.druid.segment.TestObjectColumnSelector;
import org.junit.Assert;
import org.junit.Test;


public class VarianceAggregatorCollectorTest {
    private static final float[] market_upfront = new float[]{ 800.0F, 800.0F, 826.0602F, 1564.6177F, 1006.4021F, 869.64374F, 809.04175F, 1458.4027F, 852.4375F, 879.9881F, 950.1468F, 712.7746F, 846.2675F, 682.8855F, 1109.875F, 594.3817F, 870.1159F, 677.511F, 1410.2781F, 1219.4321F, 979.306F, 1224.5016F, 1215.5898F, 716.6092F, 1301.0233F, 786.3633F, 989.9315F, 1609.0967F, 1023.2952F, 1367.6381F, 1627.598F, 810.8894F, 1685.5001F, 545.9906F, 1870.061F, 555.476F, 1643.3408F, 943.4972F, 1667.4978F, 913.5611F, 1218.5619F, 1273.7074F, 888.70526F, 1113.1141F, 864.5689F, 1308.582F, 785.07886F, 1363.6149F, 787.1253F, 826.0392F, 1107.2438F, 872.6257F, 1188.3693F, 911.9568F, 794.0988F, 1299.0933F, 1212.9283F, 901.3273F, 723.5143F, 1061.9734F, 602.97955F, 879.4061F, 724.2625F, 862.93134F, 1133.1351F, 948.65796F, 807.6017F, 914.525F, 1553.3485F, 1208.4567F, 679.6193F, 645.1777F, 1120.0887F, 1649.5333F, 1433.3988F, 1598.1793F, 1192.5631F, 1022.85455F, 1228.5024F, 1298.4158F, 1345.9644F, 1291.898F, 1306.4957F, 1287.7667F, 1631.5844F, 578.79596F, 1017.5732F, 782.0135F, 829.91626F, 1862.7379F, 873.3065F, 1427.0167F, 1430.2573F, 1101.9182F, 1166.1411F, 1004.94086F, 740.1837F, 865.7779F, 901.30756F, 691.9589F, 1674.3317F, 975.57794F, 1360.6948F, 755.89935F, 771.34845F, 869.30835F, 1095.6376F, 906.3738F, 988.8938F, 835.76263F, 776.70294F, 875.6834F, 1070.8363F, 835.46124F, 715.5161F, 755.64655F, 771.1005F, 764.50806F, 736.40924F, 884.8373F, 918.72284F, 893.98505F, 832.8749F, 850.995F, 767.9733F, 848.3399F, 878.6838F, 906.1019F, 1403.8302F, 936.4296F, 846.2884F, 856.4901F, 1032.2576F, 954.7542F, 1031.99F, 907.02155F, 1110.789F, 843.95215F, 1362.6506F, 884.8015F, 1684.2688F, 873.65204F, 855.7177F, 996.56415F, 1061.6786F, 962.2358F, 1019.8985F, 1056.4193F, 1198.7231F, 1108.1361F, 1289.0095F, 1069.4318F, 1001.13403F, 1030.4995F, 1734.2749F, 1063.2012F, 1447.3412F, 1234.2476F, 1144.3424F, 1049.7385F, 811.9913F, 768.4231F, 1151.0692F, 877.0794F, 1146.4231F, 902.6157F, 1355.8434F, 897.39343F, 1260.1431F, 762.8625F, 935.168F, 782.10785F, 996.2054F, 767.69214F, 1031.7415F, 775.9656F, 1374.9684F, 853.163F, 1456.6118F, 811.92523F, 989.0328F, 744.7446F, 1166.4012F, 753.105F, 962.7312F, 780.272F };

    private static final float[] market_total_market = new float[]{ 1000.0F, 1000.0F, 1040.9456F, 1689.0128F, 1049.142F, 1073.4766F, 1007.36554F, 1545.7089F, 1016.9652F, 1077.6127F, 1075.0896F, 953.9954F, 1022.7833F, 937.06195F, 1156.7448F, 849.8775F, 1066.208F, 904.34064F, 1240.5255F, 1343.2325F, 1088.9431F, 1349.2544F, 1102.8667F, 939.2441F, 1109.8754F, 997.99457F, 1037.4495F, 1686.4197F, 1074.007F, 1486.2013F, 1300.3022F, 1021.3345F, 1314.6195F, 792.32605F, 1233.4489F, 805.9301F, 1184.9207F, 1127.231F, 1203.4656F, 1100.9048F, 1097.2112F, 1410.793F, 1033.4012F, 1283.166F, 1025.6333F, 1331.861F, 1039.5005F, 1332.4684F, 1011.20544F, 1029.9952F, 1047.2129F, 1057.08F, 1064.9727F, 1082.7277F, 971.0508F, 1320.6383F, 1070.1655F, 1089.6478F, 980.3866F, 1179.6959F, 959.2362F, 1092.417F, 987.0674F, 1103.4583F, 1091.2231F, 1199.6074F, 1044.3843F, 1183.2408F, 1289.0973F, 1360.0325F, 993.59125F, 1021.07117F, 1105.3834F, 1601.8295F, 1200.5272F, 1600.7233F, 1317.4584F, 1304.3262F, 1544.1082F, 1488.7378F, 1224.8271F, 1421.6487F, 1251.9062F, 1414.619F, 1350.1754F, 970.7283F, 1057.4272F, 1073.9673F, 996.4337F, 1743.9218F, 1044.5629F, 1474.5911F, 1159.2788F, 1292.5428F, 1124.2014F, 1243.354F, 1051.809F, 1143.0784F, 1097.4907F, 1010.3703F, 1326.8291F, 1179.8038F, 1281.6012F, 994.73126F, 1081.6504F, 1103.2397F, 1177.8584F, 1152.5477F, 1117.954F, 1084.3325F, 1029.8025F, 1121.3854F, 1244.85F, 1077.2794F, 1098.5432F, 998.65076F, 1088.8076F, 1008.74554F, 998.75397F, 1129.7233F, 1075.243F, 1141.5884F, 1037.3811F, 1099.1973F, 981.5773F, 1092.942F, 1072.2394F, 1154.4156F, 1311.1786F, 1176.6052F, 1107.2202F, 1102.699F, 1285.0901F, 1217.5475F, 1283.957F, 1178.8302F, 1301.7781F, 1119.2472F, 1403.3389F, 1156.6019F, 1429.5802F, 1137.8423F, 1124.9352F, 1256.4998F, 1217.8774F, 1247.8909F, 1185.71F, 1345.7817F, 1250.1667F, 1390.754F, 1224.1162F, 1361.0802F, 1190.9337F, 1310.7971F, 1466.2094F, 1366.4476F, 1314.8397F, 1522.0437F, 1193.5563F, 1321.375F, 1055.7837F, 1021.6387F, 1197.0084F, 1131.532F, 1192.1443F, 1154.2896F, 1272.6771F, 1141.5146F, 1190.8961F, 1009.36316F, 1006.9138F, 1032.5999F, 1137.3857F, 1030.0756F, 1005.25305F, 1030.0947F, 1112.7948F, 1113.3575F, 1153.9747F, 1069.6409F, 1016.13745F, 994.9023F, 1032.1543F, 999.5864F, 994.75275F, 1029.057F };

    @Test
    public void testVariance() {
        Random random = ThreadLocalRandom.current();
        for (float[] values : Arrays.asList(VarianceAggregatorCollectorTest.market_upfront, VarianceAggregatorCollectorTest.market_total_market)) {
            double sum = 0;
            for (float f : values) {
                sum += f;
            }
            final double mean = sum / (values.length);
            double temp = 0;
            for (float f : values) {
                temp += Math.pow((f - mean), 2);
            }
            final double variance_pop = temp / (values.length);
            final double variance_sample = temp / ((values.length) - 1);
            VarianceAggregatorCollector holder = new VarianceAggregatorCollector();
            for (float f : values) {
                holder.add(f);
            }
            Assert.assertEquals(holder.getVariance(true), variance_pop, 0.001);
            Assert.assertEquals(holder.getVariance(false), variance_sample, 0.001);
            for (int mergeOn : new int[]{ 2, 3, 5, 9 }) {
                List<VarianceAggregatorCollector> holders1 = Lists.newArrayListWithCapacity(mergeOn);
                List<Pair<VarianceBufferAggregator, ByteBuffer>> holders2 = Lists.newArrayListWithCapacity(mergeOn);
                VarianceAggregatorCollectorTest.FloatHandOver valueHandOver = new VarianceAggregatorCollectorTest.FloatHandOver();
                for (int i = 0; i < mergeOn; i++) {
                    holders1.add(new VarianceAggregatorCollector());
                    holders2.add(Pair.of(new VarianceBufferAggregator.FloatVarianceAggregator(valueHandOver), ByteBuffer.allocate(VarianceAggregatorCollector.getMaxIntermediateSize())));
                }
                for (float f : values) {
                    valueHandOver.v = f;
                    int index = random.nextInt(mergeOn);
                    holders1.get(index).add(f);
                    holders2.get(index).lhs.aggregate(holders2.get(index).rhs, 0);
                }
                VarianceAggregatorCollector holder1 = holders1.get(0);
                for (int i = 1; i < mergeOn; i++) {
                    holder1 = ((VarianceAggregatorCollector) (VarianceAggregatorCollector.combineValues(holder1, holders1.get(i))));
                }
                VarianceAggregatorCollectorTest.ObjectHandOver collectHandOver = new VarianceAggregatorCollectorTest.ObjectHandOver();
                ByteBuffer buffer = ByteBuffer.allocate(VarianceAggregatorCollector.getMaxIntermediateSize());
                VarianceBufferAggregator.ObjectVarianceAggregator merger = new VarianceBufferAggregator.ObjectVarianceAggregator(collectHandOver);
                for (int i = 0; i < mergeOn; i++) {
                    collectHandOver.v = holders2.get(i).lhs.get(holders2.get(i).rhs, 0);
                    merger.aggregate(buffer, 0);
                }
                VarianceAggregatorCollector holder2 = ((VarianceAggregatorCollector) (merger.get(buffer, 0)));
                Assert.assertEquals(holder2.getVariance(true), variance_pop, 0.01);
                Assert.assertEquals(holder2.getVariance(false), variance_sample, 0.01);
            }
        }
    }

    private static class FloatHandOver extends TestFloatColumnSelector {
        float v;

        @Override
        public float getFloat() {
            return v;
        }

        @Override
        public boolean isNull() {
            return false;
        }
    }

    private static class ObjectHandOver extends TestObjectColumnSelector {
        Object v;

        @Override
        public Class classOfObject() {
            return (v) == null ? Object.class : v.getClass();
        }

        @Override
        public Object getObject() {
            return v;
        }
    }
}

