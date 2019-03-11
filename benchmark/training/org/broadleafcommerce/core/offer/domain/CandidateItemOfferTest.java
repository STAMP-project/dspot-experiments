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
package org.broadleafcommerce.core.offer.domain;


import OfferDiscountType.AMOUNT_OFF;
import OfferDiscountType.FIX_PRICE;
import junit.framework.TestCase;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrder;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;


/**
 *
 *
 * @author jfischer
 */
public class CandidateItemOfferTest extends TestCase {
    private PromotableCandidateItemOffer promotableCandidate;

    private Offer offer;

    private PromotableCandidateItemOffer candidateOffer;

    private PromotableOrderItem promotableOrderItem;

    private PromotableOrder promotableOrder;

    private PromotableOrderItemPriceDetail priceDetail;

    public void testCalculateSavingsForOrderItem() throws Exception {
        Money savings = promotableCandidate.calculateSavingsForOrderItem(promotableOrderItem, 1);
        TestCase.assertTrue(savings.equals(new Money(2.0)));
        offer.setDiscountType(AMOUNT_OFF);
        savings = promotableCandidate.calculateSavingsForOrderItem(promotableOrderItem, 1);
        TestCase.assertTrue(savings.equals(new Money(10.0)));
        offer.setDiscountType(FIX_PRICE);
        savings = promotableCandidate.calculateSavingsForOrderItem(promotableOrderItem, 1);
        TestCase.assertTrue(savings.equals(new Money((19.99 - 10.0))));
    }

    public void testCalculateMaximumNumberOfUses() throws Exception {
        int maxOfferUses = promotableCandidate.calculateMaximumNumberOfUses();
        TestCase.assertTrue((maxOfferUses == 2));
        offer.setMaxUsesPerOrder(1);
        maxOfferUses = promotableCandidate.calculateMaximumNumberOfUses();
        TestCase.assertTrue((maxOfferUses == 1));
    }

    public void testCalculateMaxUsesForItemCriteria() throws Exception {
        int maxItemCriteriaUses = 9999;
        for (OfferTargetCriteriaXref targetXref : offer.getTargetItemCriteriaXref()) {
            int temp = promotableCandidate.calculateMaxUsesForItemCriteria(targetXref.getOfferItemCriteria(), offer);
            maxItemCriteriaUses = Math.min(maxItemCriteriaUses, temp);
        }
        TestCase.assertTrue((maxItemCriteriaUses == 2));
    }
}

