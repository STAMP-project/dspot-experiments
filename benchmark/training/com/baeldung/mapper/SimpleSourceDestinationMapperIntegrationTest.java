package com.baeldung.mapper;


import com.baeldung.dto.SimpleSource;
import com.baeldung.entity.SimpleDestination;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SimpleSourceDestinationMapperIntegrationTest {
    @Autowired
    SimpleSourceDestinationMapper simpleSourceDestinationMapper;

    @Test
    public void givenSimpleSourceToSimpleDestination_whenMaps_thenCorrect() {
        SimpleSource simpleSource = new SimpleSource();
        simpleSource.setName("SourceName");
        simpleSource.setDescription("SourceDescription");
        SimpleDestination destination = simpleSourceDestinationMapper.sourceToDestination(simpleSource);
        Assert.assertEquals(simpleSource.getName(), destination.getName());
        Assert.assertEquals(simpleSource.getDescription(), destination.getDescription());
    }

    @Test
    public void givenSimpleDestinationToSourceDestination_whenMaps_thenCorrect() {
        SimpleDestination destination = new SimpleDestination();
        destination.setName("DestinationName");
        destination.setDescription("DestinationDescription");
        SimpleSource source = simpleSourceDestinationMapper.destinationToSource(destination);
        Assert.assertEquals(destination.getName(), source.getName());
        Assert.assertEquals(destination.getDescription(), source.getDescription());
    }
}

