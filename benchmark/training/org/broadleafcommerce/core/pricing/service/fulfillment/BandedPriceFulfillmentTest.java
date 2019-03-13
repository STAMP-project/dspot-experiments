/**
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.pricing.service.fulfillment;


import Money.ZERO;
import java.math.BigDecimal;
import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedPriceFulfillmentOption;
import org.broadleafcommerce.core.order.fulfillment.domain.BandedWeightFulfillmentOption;
import org.broadleafcommerce.core.order.service.type.FulfillmentBandResultAmountType;


/**
 *
 *
 * @author Phillip Verheyden
 */
public class BandedPriceFulfillmentTest extends TestCase {
    public void testPriceBandRate() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[]{ "10", "20", "30" }, new String[]{ "10", "20", "30" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE });
        TestCase.assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("20.00"), 2, option)));
        TestCase.assertEquals(ZERO, calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3, option)));
        TestCase.assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3, option)));
        TestCase.assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        TestCase.assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
    }

    public void testMinimumAmountsWithZero() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[]{ "0", "20", "30" }, new String[]{ "10", "20", "30" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE });
        TestCase.assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("20.00"), 2, option)));
        TestCase.assertEquals(new Money("10.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3, option)));
        TestCase.assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3, option)));
        TestCase.assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        TestCase.assertEquals(new Money("30.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
    }

    public void testPriceBandPercentage() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[]{ "10", "30", "20" }, new String[]{ ".10", ".20", ".30" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.PERCENTAGE, FulfillmentBandResultAmountType.PERCENTAGE, FulfillmentBandResultAmountType.PERCENTAGE });
        TestCase.assertEquals(new Money("1.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("15.00"), 3, option)));
        TestCase.assertEquals(new Money("6.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("30.00"), 3, option)));
        TestCase.assertEquals(new Money("7.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        TestCase.assertEquals(new Money("20.00"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
    }

    public void testPriceBandRatesWithPercentages() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[]{ "150", "30", "20", "150", "10", "9", "80" }, new String[]{ "50", "20", ".30", "20", ".10", "5", ".5" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.PERCENTAGE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.PERCENTAGE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.PERCENTAGE });
        TestCase.assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("35.00"), 5, option)));
        TestCase.assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("9999.00"), 9, option)));
        TestCase.assertEquals(new Money("7.50"), calculationResponse(option, createCandidateOrder(new BigDecimal("25.00"), 5, option)));
        TestCase.assertEquals(new Money("1.80"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, option)));
        TestCase.assertEquals(new Money("50"), calculationResponse(option, createCandidateOrder(new BigDecimal("100.00"), 5, option)));
        TestCase.assertEquals(new Money("5"), calculationResponse(option, createCandidateOrder(new BigDecimal("9.00"), 3, option)));
        TestCase.assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("66"), 6, option)));
        TestCase.assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("150"), 5, option)));
    }

    /**
     * If the retail price sum falls within 2 bands but with the same retail minimum, the lowest price should be selected
     */
    public void testLowestPriceSelection() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[]{ "10", "10", "10" }, new String[]{ "30", "20", "10" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE });
        TestCase.assertEquals(calculationResponse(option, createCandidateOrder(new BigDecimal("10.00"), 2, option)), new Money("10.00"));
    }

    public void testFlatRatesExclusive() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[]{ "100" }, new String[]{ "30" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.RATE });
        TestCase.assertEquals(new Money("45"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 3, new String[]{ "10", "15", "20" }, null, option)));
        TestCase.assertEquals(new Money("5"), calculationResponse(option, createCandidateOrder(new BigDecimal("80.00"), 1, new String[]{ "5" }, null, option)));
        TestCase.assertEquals(new Money("10"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 2, new String[]{ "8", "2" }, null, option)));
    }

    public void testFlatRatesWithBands() throws Exception {
        BandedPriceFulfillmentOption option = createPriceBands(new String[]{ "30", "20", "10" }, new String[]{ "30", "20", "10" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE });
        TestCase.assertEquals(new Money("35"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, new String[]{ "10", "15" }, null, option)));
        TestCase.assertEquals(new Money("125"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, new String[]{ "5", "100", "20" }, null, option)));
        TestCase.assertEquals(new Money("41"), calculationResponse(option, createCandidateOrder(new BigDecimal("60.00"), 6, new String[]{ "8", "2", "1" }, null, option)));
    }

    public void testWeightBandsWithQuantities() throws Exception {
        BandedWeightFulfillmentOption option = createWeightBands(new String[]{ "50", "100", "65" }, new String[]{ "30", "20", "10" }, new FulfillmentBandResultAmountType[]{ FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE, FulfillmentBandResultAmountType.RATE });
        // 60lbs
        TestCase.assertEquals(new Money("30"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 3, null, new int[]{ 2, 3, 5 }, option)));
        // 66lbs
        TestCase.assertEquals(new Money("10"), calculationResponse(option, createCandidateOrder(new BigDecimal("18.00"), 6, null, new int[]{ 4, 1, 2, 5, 5, 5 }, option)));
        // 120lbs
        TestCase.assertEquals(new Money("20"), calculationResponse(option, createCandidateOrder(new BigDecimal("60.00"), 3, null, new int[]{ 2, 3, 2 }, option)));
    }
}

