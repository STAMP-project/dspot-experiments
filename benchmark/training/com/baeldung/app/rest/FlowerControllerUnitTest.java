package com.baeldung.app.rest;


import com.baeldung.app.api.Flower;
import com.baeldung.domain.service.FlowerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FlowerControllerUnitTest {
    @Mock
    private FlowerService flowerService;

    @InjectMocks
    private FlowerController flowerController;

    @Test
    public void isAFlower_withMockito_OK() {
        Mockito.when(flowerService.analize(ArgumentMatchers.eq("violetta"))).thenReturn("Flower");
        String response = flowerController.isAFlower("violetta");
        Assert.assertEquals("Flower", response);
    }

    @Test
    public void isABigFlower_withMockito_OK() {
        Mockito.when(flowerService.isABigFlower(ArgumentMatchers.eq("violetta"), ArgumentMatchers.anyInt())).thenReturn(true);
        Flower flower = new Flower("violetta", 15);
        Boolean response = flowerController.isABigFlower(flower);
        Assert.assertTrue(response);
    }
}

