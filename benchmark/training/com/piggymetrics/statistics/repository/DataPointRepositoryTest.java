package com.piggymetrics.statistics.repository;


import StatisticMetric.EXPENSES_AMOUNT;
import StatisticMetric.INCOMES_AMOUNT;
import StatisticMetric.SAVING_AMOUNT;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.domain.timeseries.DataPointId;
import com.piggymetrics.statistics.domain.timeseries.ItemMetric;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataMongoTest
public class DataPointRepositoryTest {
    @Autowired
    private DataPointRepository repository;

    @Test
    public void shouldSaveDataPoint() {
        ItemMetric salary = new ItemMetric("salary", new BigDecimal(20000));
        ItemMetric grocery = new ItemMetric("grocery", new BigDecimal(1000));
        ItemMetric vacation = new ItemMetric("vacation", new BigDecimal(2000));
        DataPointId pointId = new DataPointId("test-account", new Date(0));
        DataPoint point = new DataPoint();
        point.setId(pointId);
        point.setIncomes(Sets.newHashSet(salary));
        point.setExpenses(Sets.newHashSet(grocery, vacation));
        point.setStatistics(ImmutableMap.of(SAVING_AMOUNT, new BigDecimal(400000), INCOMES_AMOUNT, new BigDecimal(20000), EXPENSES_AMOUNT, new BigDecimal(3000)));
        repository.save(point);
        List<DataPoint> points = repository.findByIdAccount(pointId.getAccount());
        Assert.assertEquals(1, points.size());
        Assert.assertEquals(pointId.getDate(), points.get(0).getId().getDate());
        Assert.assertEquals(point.getStatistics().size(), points.get(0).getStatistics().size());
        Assert.assertEquals(point.getIncomes().size(), points.get(0).getIncomes().size());
        Assert.assertEquals(point.getExpenses().size(), points.get(0).getExpenses().size());
    }

    @Test
    public void shouldRewriteDataPointWithinADay() {
        final BigDecimal earlyAmount = new BigDecimal(100);
        final BigDecimal lateAmount = new BigDecimal(200);
        DataPointId pointId = new DataPointId("test-account", new Date(0));
        DataPoint earlier = new DataPoint();
        earlier.setId(pointId);
        earlier.setStatistics(ImmutableMap.of(SAVING_AMOUNT, earlyAmount));
        repository.save(earlier);
        DataPoint later = new DataPoint();
        later.setId(pointId);
        later.setStatistics(ImmutableMap.of(SAVING_AMOUNT, lateAmount));
        repository.save(later);
        List<DataPoint> points = repository.findByIdAccount(pointId.getAccount());
        Assert.assertEquals(1, points.size());
        Assert.assertEquals(lateAmount, points.get(0).getStatistics().get(SAVING_AMOUNT));
    }
}

