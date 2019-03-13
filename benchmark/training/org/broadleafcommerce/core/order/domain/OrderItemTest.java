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
package org.broadleafcommerce.core.order.domain;


import OfferItemRestrictionRuleType.NONE;
import OfferItemRestrictionRuleType.QUALIFIER;
import OfferItemRestrictionRuleType.TARGET;
import junit.framework.TestCase;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItemPriceDetail;


/**
 *
 *
 * @author jfischer
 */
public class OrderItemTest extends TestCase {
    private PromotableOrderItemPriceDetail priceDetail1;

    private PromotableCandidateItemOffer candidateOffer;

    private Offer offer;

    public void testGetQuantityAvailableToBeUsedAsQualifier() throws Exception {
        int quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
        // no previous qualifiers, so all quantity is available
        TestCase.assertTrue((quantity == 2));
        PromotionDiscount discount = new PromotionDiscount();
        discount.setPromotion(offer);
        discount.setQuantity(1);
        priceDetail1.getPromotionDiscounts().add(discount);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
        // items that have already received this promotion cannot get it again
        TestCase.assertTrue((quantity == 1));
        Offer testOffer = new OfferImpl();
        testOffer.setOfferItemQualifierRuleType(NONE);
        testOffer.setOfferItemTargetRuleType(NONE);
        discount.setPromotion(testOffer);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
        // this item received a different promotion, but the restriction rule is NONE, so this item cannot be a qualifier for this promotion
        TestCase.assertTrue((quantity == 1));
        testOffer.setOfferItemTargetRuleType(QUALIFIER);
        candidateOffer.getOffer().setOfferItemQualifierRuleType(TARGET);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
        // this item received a different promotion, but the restriction rule is QUALIFIER, so this item can be a qualifier
        // for this promotion
        // dpc disabling this test for now        assertTrue(quantity==2);
        priceDetail1.getPromotionDiscounts().clear();
        PromotionQualifier qualifier = new PromotionQualifier();
        qualifier.setPromotion(offer);
        qualifier.setQuantity(1);
        priceDetail1.getPromotionQualifiers().add(qualifier);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
        // items that have already qualified for this promotion cannot qualify again
        TestCase.assertTrue((quantity == 1));
        qualifier.setPromotion(testOffer);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
        // this item qualified for a different promotion, but the restriction rule is NONE, so this item cannot be a qualifier for this promotion
        TestCase.assertTrue((quantity == 1));
        testOffer.setOfferItemQualifierRuleType(QUALIFIER);
        candidateOffer.getOffer().setOfferItemQualifierRuleType(QUALIFIER);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsQualifier(candidateOffer);
        // this item qualified for a different promotion, but the restriction rule is QUALIFIER,
        // so this item can be a qualifier for this promotion
        // dpc disabling this test for now        assertTrue(quantity==2);
    }

    public void testGetQuantityAvailableToBeUsedAsTarget() throws Exception {
        int quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // no previous qualifiers, so all quantity is available
        TestCase.assertTrue((quantity == 2));
        PromotionDiscount discount = new PromotionDiscount();
        discount.setPromotion(offer);
        discount.setQuantity(1);
        priceDetail1.getPromotionDiscounts().add(discount);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // items that have already received this promotion cannot get it again
        TestCase.assertTrue((quantity == 1));
        Offer tempOffer = new OfferImpl();
        tempOffer.setCombinableWithOtherOffers(true);
        tempOffer.setOfferItemQualifierRuleType(NONE);
        tempOffer.setOfferItemTargetRuleType(NONE);
        discount.setPromotion(tempOffer);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // this item received a different promotion, but the restriction rule is NONE, so this item cannot be a qualifier
        // for this promotion
        TestCase.assertTrue((quantity == 1));
        tempOffer.setOfferItemTargetRuleType(TARGET);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // this item received a different promotion, but the restriction rule is TARGET,
        // so this item can be a target of this promotion but since the "candidateOffer"
        // is set to NONE, the quantity can only be 1
        TestCase.assertTrue((quantity == 1));
        // Now set the candidateOffer to be "TARGET" and we can use the quantity
        // for both promotions.
        candidateOffer.getOffer().setOfferItemTargetRuleType(TARGET);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // this item received a different promotion, but the restriction rule is TARGET,
        // so this item can be a target of this promotion but since the "candidateOffer"
        // is set to NONE, the quantity can only be 1
        TestCase.assertTrue((quantity == 2));
        priceDetail1.getPromotionDiscounts().clear();
        // rest candidate offer
        candidateOffer.getOffer().setOfferItemTargetRuleType(NONE);
        PromotionQualifier qualifier = new PromotionQualifier();
        qualifier.setPromotion(offer);
        qualifier.setQuantity(1);
        priceDetail1.getPromotionQualifiers().add(qualifier);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // items that have already qualified for this promotion cannot qualify again
        TestCase.assertTrue((quantity == 1));
        qualifier.setPromotion(tempOffer);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // this item qualified for a different promotion, but the restriction rule is NONE,
        // so this item cannot be a qualifier for this promotion
        TestCase.assertTrue((quantity == 1));
        tempOffer.setOfferItemQualifierRuleType(TARGET);
        candidateOffer.getOffer().setOfferItemTargetRuleType(QUALIFIER);
        quantity = priceDetail1.getQuantityAvailableToBeUsedAsTarget(candidateOffer);
        // this item qualified for a different promotion, but the restriction rule is QUALIFIER,
        // so this item can be a qualifier for this promotion
        // dpc disabling this test for now        assertTrue(quantity==2);
    }
}

