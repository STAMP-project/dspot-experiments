package com.alibaba.json.bvt.parser.bug;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_yihaodian extends TestCase {
    public void test_for_long_list() throws Exception {
        String str = "{\"backOperatorId\":14281,\"batchNum\":0,\"canPurchase\":1,\"categoryId\":955063}";
        Bug_for_yihaodian.Te ob = JSON.parseObject(str, Bug_for_yihaodian.Te.class);
    }

    public static class Te {
        /**
         * ??ID
         */
        private Long id;

        /**
         * ??????ID
         */
        private String deletedProductId;

        /**
         * ????
         */
        private String productCode;

        /**
         * ???
         */
        private String productCname;

        /**
         * ?????????
         */
        private String productBrandName;

        /**
         * ?????
         */
        private String productEname;

        /**
         * ??????
         */
        private Integer productSaleType;

        /**
         * ????Id
         */
        private Long brandId;

        /**
         * ????
         */
        private String brandName;

        /**
         * ???
         */
        private Double productListPrice;

        /**
         * ??Id
         */
        private Long categoryId;

        /**
         * ???Id
         */
        private Long oldCategoryId;

        /**
         * ????? *
         */
        private Long oldExtendCategoryId;

        /**
         * ??ID,???1
         */
        private Long mfid;

        /**
         * productCanBeChange
         */
        private Integer productCanBeChange;

        /**
         * productChangeDay
         */
        private Integer productChangeDay;

        /**
         * productCanBeReturn
         */
        private Integer productCanBeReturn;

        /**
         * productReturnDay
         */
        private Integer productReturnDay;

        /**
         * ????
         */
        private Integer productCanBeRepair;

        /**
         * ????
         */
        private Integer productCanBeRepairDay;

        /**
         * ????
         */
        private Integer productNeedInstallation;

        /**
         * ???
         */
        private String ean13;

        /**
         * sku
         */
        private String sku;

        /**
         * ?
         */
        private Double length;

        /**
         * ?
         */
        private Double width;

        /**
         * ?
         */
        private Double height;

        /**
         * ??
         */
        private Double weight;

        /**
         * keepTemperature
         */
        private String keepTemperature;

        /**
         * keepHumidity
         */
        private String keepHumidity;

        /**
         * ????
         */
        private String productSname;

        /**
         * keepSpecCondition
         */
        private String keepSpecCondition;

        /**
         * productQualityAssuranceDay
         */
        private Integer productQualityAssuranceDay;

        /**
         * ?????
         */
        private Integer isDeleted;

        /**
         * ??
         */
        private String unit;

        /**
         * ??
         */
        private Double inPrice;

        /**
         * volume
         */
        private Double volume;

        /**
         * countryOfOrgn
         */
        private Long countryOfOrgn;

        /**
         * ??ID
         */
        private Long defaultPictureId;

        /**
         * ??URL
         */
        private String defaultPictureUrl;

        /**
         * color
         */
        private String color;

        /**
         * currencyId
         */
        private Long currencyId;

        /**
         * ??
         */
        private Double grossWeight;

        /**
         * format
         */
        private String format;

        /**
         * ??? 0 ?? 1?
         */
        private String isFragile;

        /**
         * ??0 ?? 1?
         */
        private String putOnDirection;

        /**
         * ???0 ?? 1?
         */
        private String isValuables;

        /**
         * ??0 ?? 1?
         */
        private String isLiquid;

        /**
         * ?????0 ?? 1?
         */
        private String isCrossContamination;

        /**
         * 16???????,?#FF00AA
         */
        private String colorNumber;

        /**
         * ??
         */
        private String productSize;

        /**
         * ??????
         */
        private String replaceProductSize;

        /**
         * ????
         */
        private String saleSkill;

        /**
         * ?????????????
         */
        private String dispositionInstruct;

        /**
         * ??
         */
        private String placeOfOrigin;

        /**
         * ??????
         */
        private String productSeoTitle;

        /**
         * ?????????
         */
        private String productSeoKeyword;

        /**
         * ????????
         */
        private String productSeoDescription;

        /**
         * ????????
         */
        private String accessoryDescription;

        /**
         * ????????
         */
        private Integer needInvoice;

        /**
         * ????
         */
        private String clearCause;

        /**
         * ??????ID
         */
        private Long defaultBarcodeId;

        /**
         * ???
         */
        private String adWord;

        /**
         * ???3c???0:?3C,1:3C???
         */
        private Integer isCcc;

        /**
         * N??
         */
        private Integer shoppingCount;

        /**
         * ?????
         */
        private Integer productIsGift;

        /**
         * ??????? 0???? 1???
         */
        private Integer canReturnAndChange;

        /**
         * ?????? 0???? 1???
         */
        private Integer needExamine;

        /**
         * 1:?????;2:?????;3:?????;4:????;5:??????;6:??????
         */
        private Integer verifyFlg;

        /**
         * ???
         */
        private Long verifyBy;

        /**
         * ????
         */
        /**
         * ?????
         */
        private Long registerBy;

        /**
         * ??????
         */
        /**
         * ?????????
         */
        private String registerPhone;

        /**
         * ????
         */
        private String verifyRemark;

        /**
         * ???
         */
        private Integer batchNum;

        /**
         * ????????0: ??? 1:?? (??/??/??)
         */
        private Integer localLimit;

        /**
         * ?????
         */
        private Integer stdPackQty;

        /**
         * ?????ID
         */
        private Long fromalProductId;

        /**
         * ??????
         */
        private Integer isMustInvoice;

        /**
         * ??????
         */
        private Integer verifyFailureType;

        /**
         * ???? 0????? 1?????? 2?????? 3????? 4???? 5: ???? 6:????
         */
        private Integer productType;

        /**
         * ??????
         */
        private Integer canPurchase;

        /**
         * ?????sku
         */
        private String stdPackageSku;

        /**
         * ??????????? 0:??? 1???
         */
        private Integer userExpireControl;

        /**
         * ????ID
         */
        private Long batchRuleId;

        /**
         * ???????
         */
        private String nameSubtitle;

        private String specialType;

        /**
         * ????????
         */
        private Double batchPrice;

        /**
         * ???????? 0???? 1???
         */
        private Integer needBatchControl;

        /**
         * ????
         */
        private Double salesTax;

        /**
         * ??????
         */
        private String outerId;

        /**
         * ??ID
         */
        private Long merchantId;

        /**
         * ????
         */
        private String merchantName;

        /**
         * ???????????????
         */
        private Long masterCategoryId;

        private Integer concernLevel;

        /**
         * ????
         */
        private String concernReason;

        /**
         * ????
         */
        private Integer canSale;

        /**
         * ????
         */
        private Integer canShow;

        /**
         * ??????
         */
        private Long prodcutTaxRate;

        /**
         * ????VIP0:???1:??
         */
        private Integer canVipDiscount;

        /**
         * ????
         */
        private String categoryName;

        /**
         * ????
         */
        private Double salePrice;

        /**
         * ??
         */
        private Long stockNum;

        /**
         * ??????
         */
        private String merchantCategoryName;

        /**
         * ????
         */
        private String productDescription;

        /**
         * ????? 0???? 1???
         */
        private Integer isTransfer;

        /**
         * ??????0???????1??????2??????
         */
        private Integer isSubmit;

        /**
         * ??????
         */
        private Integer verifyFailueType;

        /**
         * ????
         */
        private String productSpell;

        /**
         * ??????
         */
        private String productNamePrefix;

        /**
         * ??????
         */
        private String failueReason;

        /**
         * orgPicUrl
         */
        private String orgPicUrl;

        /**
         * ??????
         */
        private String subCategoryName;

        /**
         * ????ID
         */
        private Long subCategoryId;

        /**
         * 7??????
         */
        private Integer dailySale;

        /**
         * ???????
         */
        private Integer picCount;

        /**
         * ??????
         */
        private Integer underCarriageReason;

        /**
         * ??????-????
         */
        private String underCarriageReasonStr;

        /**
         * ????
         */
        private String errorMessage;

        /**
         * ??????
         */
        private Integer alertStockCount;

        private String deliveryInfo;

        /**
         * ????
         */
        private String picUrl;

        /**
         * ?????0??? 1??
         */
        private Integer canFenqi;

        private String season;

        /**
         * ???????
         */
        private Integer isDupAudit;

        private Integer viewFromTag;

        /**
         * ????
         */
        private Double productNonMemberPrice;

        /**
         * ????
         */
        /**
         * ??????
         */
        private Integer isUpdate;

        /**
         * merchantRpcVo
         */
        /**
         * ?????????
         */
        /**
         * ???????
         */
        private List<String> productSizeSet;

        /**
         * ?????
         */
        private Boolean isMainProduct;

        /**
         * ??????????ID?URL
         */
        private String productPicIdAndURL;

        private Integer isTemp;

        /**
         * ?????????
         */
        private Double priceRate;

        private Integer picSpecialType;

        private Integer exemptStatus;

        private String violationReasonIds;

        private String violationReasons;

        private Long remainTime;

        private Integer submitOrder;

        private Integer productSource;

        private Integer isKa;

        /**
         * KA??????
         */
        private Integer kaMCreateTime;

        /**
         * ?????
         */
        private Integer deliveryDay;

        /**
         * ????
         */
        private Integer isEdit;

        /**
         * ???
         */
        private Long backOperatorId;

        /**
         * ???pm_info_id
         */
        private Long formalPmInfoId;

        /**
         * ???????
         */
        private String categoryStr;

        /**
         * ??id?????
         */
        private String categoryIdStr;

        /**
         * ?????????
         */
        private String extendCategoryStr;

        /**
         * ????id?????
         */
        private String extendCategoryIdStr;

        /**
         * ????ID
         */
        private List<Long> masterCategoryIdList;

        private Long defaultWarehouseId;

        public Long getBackOperatorId() {
            return backOperatorId;
        }

        public void setBackOperatorId(Long backOperatorId) {
            this.backOperatorId = backOperatorId;
        }

        public Integer getIsDupAudit() {
            return isDupAudit;
        }

        public void setIsDupAudit(Integer isDupAudit) {
            this.isDupAudit = isDupAudit;
        }

        public Long getId() {
            return id;
        }

        public String getUnderCarriageReasonStr() {
            return underCarriageReasonStr;
        }

        public void setUnderCarriageReasonStr(String underCarriageReasonStr) {
            this.underCarriageReasonStr = underCarriageReasonStr;
        }

        /**
         * ??ID
         *
         * @param id
         * 		??ID
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * ????
         *
         * @return productCode
         */
        public String getProductCode() {
            return productCode;
        }

        /**
         * ????
         *
         * @param productCode
         * 		????
         */
        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        /**
         * ???
         *
         * @return productCname
         */
        public String getProductCname() {
            return productCname;
        }

        /**
         * ???
         *
         * @param productCname
         * 		???
         */
        public void setProductCname(String productCname) {
            this.productCname = productCname;
        }

        /**
         * ?????
         *
         * @return productEname
         */
        public String getProductEname() {
            return productEname;
        }

        /**
         * ?????
         *
         * @param productEname
         * 		?????
         */
        public void setProductEname(String productEname) {
            this.productEname = productEname;
        }

        /**
         * ??????
         *
         * @param productSaleType
         * 		??????
         */
        public void setProductSaleType(Integer productSaleType) {
            this.productSaleType = productSaleType;
        }

        /**
         * ????Id
         *
         * @return brandId
         */
        public Long getBrandId() {
            return brandId;
        }

        /**
         * ????Id
         *
         * @param brandId
         * 		????Id
         */
        public void setBrandId(Long brandId) {
            this.brandId = brandId;
        }

        /**
         * ????
         *
         * @return brandName
         */
        public String getBrandName() {
            return brandName;
        }

        /**
         * ????
         *
         * @param brandName
         * 		????
         */
        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        /**
         * ??????
         *
         * @return createTime
         */
        /**
         * ??????
         *
         * @param createTime
         * 		??????
         */
        /**
         * ???
         *
         * @return productListPrice
         */
        public Double getProductListPrice() {
            return productListPrice;
        }

        /**
         * ???
         *
         * @param productListPrice
         * 		???
         */
        public void setProductListPrice(Double productListPrice) {
            this.productListPrice = productListPrice;
        }

        /**
         * ??Id
         *
         * @return categoryId
         */
        public Long getCategoryId() {
            return categoryId;
        }

        /**
         * ??Id
         *
         * @param categoryId
         * 		??Id
         */
        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        /**
         * ??ID???1
         *
         * @return mfid
         */
        public Long getMfid() {
            return mfid;
        }

        /**
         * ??ID???1
         *
         * @param mfid
         * 		??ID???1
         */
        public void setMfid(Long mfid) {
            this.mfid = mfid;
        }

        /**
         * productCanBeChange
         *
         * @return productCanBeChange
         */
        public Integer getProductCanBeChange() {
            return productCanBeChange;
        }

        /**
         * productCanBeChange
         *
         * @param productCanBeChange
         * 		productCanBeChange
         */
        public void setProductCanBeChange(Integer productCanBeChange) {
            this.productCanBeChange = productCanBeChange;
        }

        /**
         * productChangeDay
         *
         * @return productChangeDay
         */
        public Integer getProductChangeDay() {
            return productChangeDay;
        }

        /**
         * productChangeDay
         *
         * @param productChangeDay
         * 		productChangeDay
         */
        public void setProductChangeDay(Integer productChangeDay) {
            this.productChangeDay = productChangeDay;
        }

        /**
         * productCanBeReturn
         *
         * @return productCanBeReturn
         */
        public Integer getProductCanBeReturn() {
            return productCanBeReturn;
        }

        /**
         * productCanBeReturn
         *
         * @param productCanBeReturn
         * 		productCanBeReturn
         */
        public void setProductCanBeReturn(Integer productCanBeReturn) {
            this.productCanBeReturn = productCanBeReturn;
        }

        /**
         * productReturnDay
         *
         * @return productReturnDay
         */
        public Integer getProductReturnDay() {
            return productReturnDay;
        }

        /**
         * productReturnDay
         *
         * @param productReturnDay
         * 		productReturnDay
         */
        public void setProductReturnDay(Integer productReturnDay) {
            this.productReturnDay = productReturnDay;
        }

        /**
         * ????
         *
         * @return productCanBeRepair
         */
        public Integer getProductCanBeRepair() {
            return productCanBeRepair;
        }

        /**
         * ????
         *
         * @param productCanBeRepair
         * 		????
         */
        public void setProductCanBeRepair(Integer productCanBeRepair) {
            this.productCanBeRepair = productCanBeRepair;
        }

        /**
         * ????
         *
         * @return productCanBeRepairDay
         */
        public Integer getProductCanBeRepairDay() {
            return productCanBeRepairDay;
        }

        /**
         * ????
         *
         * @param productCanBeRepairDay
         * 		????
         */
        public void setProductCanBeRepairDay(Integer productCanBeRepairDay) {
            this.productCanBeRepairDay = productCanBeRepairDay;
        }

        /**
         * ????
         *
         * @return productNeedInstallation
         */
        public Integer getProductNeedInstallation() {
            return productNeedInstallation;
        }

        /**
         * ????
         *
         * @param productNeedInstallation
         * 		????
         */
        public void setProductNeedInstallation(Integer productNeedInstallation) {
            this.productNeedInstallation = productNeedInstallation;
        }

        /**
         * ???
         *
         * @return ean13
         */
        public String getEan13() {
            return ean13;
        }

        /**
         * ???
         *
         * @param ean13
         * 		???
         */
        public void setEan13(String ean13) {
            this.ean13 = ean13;
        }

        /**
         * sku
         *
         * @return sku
         */
        public String getSku() {
            return sku;
        }

        /**
         * sku
         *
         * @param sku
         * 		sku
         */
        public void setSku(String sku) {
            this.sku = sku;
        }

        /**
         * ?
         *
         * @return length
         */
        public Double getLength() {
            return length;
        }

        /**
         * ?
         *
         * @param length
         * 		?
         */
        public void setLength(Double length) {
            this.length = length;
        }

        /**
         * ?
         *
         * @return width
         */
        public Double getWidth() {
            return width;
        }

        /**
         * ?
         *
         * @param width
         * 		?
         */
        public void setWidth(Double width) {
            this.width = width;
        }

        /**
         * ?
         *
         * @return height
         */
        public Double getHeight() {
            return height;
        }

        /**
         * ?
         *
         * @param height
         * 		?
         */
        public void setHeight(Double height) {
            this.height = height;
        }

        /**
         * ??
         *
         * @return weight
         */
        public Double getWeight() {
            return weight;
        }

        /**
         * ??
         *
         * @param weight
         * 		??
         */
        public void setWeight(Double weight) {
            this.weight = weight;
        }

        /**
         * keepTemperature
         *
         * @return keepTemperature
         */
        public String getKeepTemperature() {
            return keepTemperature;
        }

        /**
         * keepTemperature
         *
         * @param keepTemperature
         * 		keepTemperature
         */
        public void setKeepTemperature(String keepTemperature) {
            this.keepTemperature = keepTemperature;
        }

        /**
         * keepHumidity
         *
         * @return keepHumidity
         */
        public String getKeepHumidity() {
            return keepHumidity;
        }

        /**
         * keepHumidity
         *
         * @param keepHumidity
         * 		keepHumidity
         */
        public void setKeepHumidity(String keepHumidity) {
            this.keepHumidity = keepHumidity;
        }

        /**
         * keepSpecCondition
         *
         * @return keepSpecCondition
         */
        public String getKeepSpecCondition() {
            return keepSpecCondition;
        }

        /**
         * keepSpecCondition
         *
         * @param keepSpecCondition
         * 		keepSpecCondition
         */
        public void setKeepSpecCondition(String keepSpecCondition) {
            this.keepSpecCondition = keepSpecCondition;
        }

        /**
         * productQualityAssuranceDay
         *
         * @return productQualityAssuranceDay
         */
        public Integer getProductQualityAssuranceDay() {
            return productQualityAssuranceDay;
        }

        /**
         * productQualityAssuranceDay
         *
         * @param productQualityAssuranceDay
         * 		productQualityAssuranceDay
         */
        public void setProductQualityAssuranceDay(Integer productQualityAssuranceDay) {
            this.productQualityAssuranceDay = productQualityAssuranceDay;
        }

        /**
         * ?????
         *
         * @return isDeleted
         */
        public Integer getIsDeleted() {
            return isDeleted;
        }

        /**
         * ?????
         *
         * @param isDeleted
         * 		?????
         */
        public void setIsDeleted(Integer isDeleted) {
            this.isDeleted = isDeleted;
        }

        /**
         * ??
         *
         * @return unit
         */
        public String getUnit() {
            return unit;
        }

        /**
         * ??
         *
         * @param unit
         * 		??
         */
        public void setUnit(String unit) {
            this.unit = unit;
        }

        /**
         * ??
         *
         * @return inPrice
         */
        public Double getInPrice() {
            return inPrice;
        }

        /**
         * ??
         *
         * @param inPrice
         * 		??
         */
        public void setInPrice(Double inPrice) {
            this.inPrice = inPrice;
        }

        /**
         * volume
         *
         * @return volume
         */
        public Double getVolume() {
            return volume;
        }

        /**
         * volume
         *
         * @param volume
         * 		volume
         */
        public void setVolume(Double volume) {
            this.volume = volume;
        }

        /**
         * countryOfOrgn
         *
         * @return countryOfOrgn
         */
        public Long getCountryOfOrgn() {
            return countryOfOrgn;
        }

        /**
         * countryOfOrgn
         *
         * @param countryOfOrgn
         * 		countryOfOrgn
         */
        public void setCountryOfOrgn(Long countryOfOrgn) {
            this.countryOfOrgn = countryOfOrgn;
        }

        /**
         * ??ID
         *
         * @return defaultPictureId
         */
        public Long getDefaultPictureId() {
            return defaultPictureId;
        }

        /**
         * ??ID
         *
         * @param defaultPictureId
         * 		??ID
         */
        public void setDefaultPictureId(Long defaultPictureId) {
            this.defaultPictureId = defaultPictureId;
        }

        /**
         * ??URL
         *
         * @return defaultPictureUrl
         */
        public String getDefaultPictureUrl() {
            return defaultPictureUrl;
        }

        /**
         * ??URL
         *
         * @param defaultPictureUrl
         * 		??URL
         */
        public void setDefaultPictureUrl(String defaultPictureUrl) {
            this.defaultPictureUrl = defaultPictureUrl;
        }

        /**
         * color
         *
         * @return color
         */
        public String getColor() {
            return color;
        }

        /**
         * color
         *
         * @param color
         * 		color
         */
        public void setColor(String color) {
            this.color = color;
        }

        /**
         * currencyId
         *
         * @return currencyId
         */
        public Long getCurrencyId() {
            return currencyId;
        }

        /**
         * currencyId
         *
         * @param currencyId
         * 		currencyId
         */
        public void setCurrencyId(Long currencyId) {
            this.currencyId = currencyId;
        }

        /**
         * ??
         *
         * @return grossWeight
         */
        public Double getGrossWeight() {
            return grossWeight;
        }

        /**
         * ??
         *
         * @param grossWeight
         * 		??
         */
        public void setGrossWeight(Double grossWeight) {
            this.grossWeight = grossWeight;
        }

        /**
         * format
         *
         * @return format
         */
        public String getFormat() {
            return format;
        }

        /**
         * format
         *
         * @param format
         * 		format
         */
        public void setFormat(String format) {
            this.format = format;
        }

        /**
         * ???0??1?
         *
         * @return isFragile
         */
        public String getIsFragile() {
            return isFragile;
        }

        /**
         * ???0??1?
         *
         * @param isFragile
         * 		???0??1?
         */
        public void setIsFragile(String isFragile) {
            this.isFragile = isFragile;
        }

        /**
         * ??0??1?
         *
         * @return putOnDirection
         */
        public String getPutOnDirection() {
            return putOnDirection;
        }

        /**
         * ??0??1?
         *
         * @param putOnDirection
         * 		??0??1?
         */
        public void setPutOnDirection(String putOnDirection) {
            this.putOnDirection = putOnDirection;
        }

        /**
         * ???0??1?
         *
         * @return isValuables
         */
        public String getIsValuables() {
            return isValuables;
        }

        /**
         * ???0??1?
         *
         * @param isValuables
         * 		???0??1?
         */
        public void setIsValuables(String isValuables) {
            this.isValuables = isValuables;
        }

        /**
         * ??0??1?
         *
         * @return isLiquid
         */
        public String getIsLiquid() {
            return isLiquid;
        }

        /**
         * ??0??1?
         *
         * @param isLiquid
         * 		??0??1?
         */
        public void setIsLiquid(String isLiquid) {
            this.isLiquid = isLiquid;
        }

        /**
         * ?????0??1?
         *
         * @return isCrossContamination
         */
        public String getIsCrossContamination() {
            return isCrossContamination;
        }

        /**
         * ?????0??1?
         *
         * @param isCrossContamination
         * 		?????0??1?
         */
        public void setIsCrossContamination(String isCrossContamination) {
            this.isCrossContamination = isCrossContamination;
        }

        /**
         * 16????????#FF00AA
         *
         * @return colorNumber
         */
        public String getColorNumber() {
            return colorNumber;
        }

        /**
         * 16????????#FF00AA
         *
         * @param colorNumber
         * 		16????????#FF00AA
         */
        public void setColorNumber(String colorNumber) {
            this.colorNumber = colorNumber;
        }

        /**
         * ??
         *
         * @return productSize
         */
        public String getProductSize() {
            return productSize;
        }

        /**
         * ??
         *
         * @param productSize
         * 		??
         */
        public void setProductSize(String productSize) {
            this.productSize = productSize;
        }

        /**
         * ????
         *
         * @return saleSkill
         */
        public String getSaleSkill() {
            return saleSkill;
        }

        /**
         * ????
         *
         * @param saleSkill
         * 		????
         */
        public void setSaleSkill(String saleSkill) {
            this.saleSkill = saleSkill;
        }

        /**
         * ?????????????
         *
         * @return dispositionInstruct
         */
        public String getDispositionInstruct() {
            return dispositionInstruct;
        }

        /**
         * ?????????????
         *
         * @param dispositionInstruct
         * 		?????????????
         */
        public void setDispositionInstruct(String dispositionInstruct) {
            this.dispositionInstruct = dispositionInstruct;
        }

        /**
         * ??
         *
         * @return placeOfOrigin
         */
        public String getPlaceOfOrigin() {
            return placeOfOrigin;
        }

        /**
         * ??
         *
         * @param placeOfOrigin
         * 		??
         */
        public void setPlaceOfOrigin(String placeOfOrigin) {
            this.placeOfOrigin = placeOfOrigin;
        }

        /**
         * ??????
         *
         * @return productSeoTitle
         */
        public String getProductSeoTitle() {
            return productSeoTitle;
        }

        /**
         * ??????
         *
         * @param productSeoTitle
         * 		??????
         */
        public void setProductSeoTitle(String productSeoTitle) {
            this.productSeoTitle = productSeoTitle;
        }

        /**
         * ?????????
         *
         * @return productSeoKeyword
         */
        public String getProductSeoKeyword() {
            return productSeoKeyword;
        }

        /**
         * ?????????
         *
         * @param productSeoKeyword
         * 		?????????
         */
        public void setProductSeoKeyword(String productSeoKeyword) {
            this.productSeoKeyword = productSeoKeyword;
        }

        /**
         * ????????
         *
         * @return productSeoDescription
         */
        public String getProductSeoDescription() {
            return productSeoDescription;
        }

        /**
         * ????????
         *
         * @param productSeoDescription
         * 		????????
         */
        public void setProductSeoDescription(String productSeoDescription) {
            this.productSeoDescription = productSeoDescription;
        }

        /**
         * ????????
         *
         * @return accessoryDescription
         */
        public String getAccessoryDescription() {
            return accessoryDescription;
        }

        /**
         * ????????
         *
         * @param accessoryDescription
         * 		????????
         */
        public void setAccessoryDescription(String accessoryDescription) {
            this.accessoryDescription = accessoryDescription;
        }

        /**
         * ????????
         *
         * @return needInvoice
         */
        public Integer getNeedInvoice() {
            return needInvoice;
        }

        /**
         * ????????
         *
         * @param needInvoice
         * 		????????
         */
        public void setNeedInvoice(Integer needInvoice) {
            this.needInvoice = needInvoice;
        }

        /**
         * ????
         *
         * @return clearCause
         */
        public String getClearCause() {
            return clearCause;
        }

        /**
         * ????
         *
         * @param clearCause
         * 		????
         */
        public void setClearCause(String clearCause) {
            this.clearCause = clearCause;
        }

        /**
         * ??????ID
         *
         * @return defaultBarcodeId
         */
        public Long getDefaultBarcodeId() {
            return defaultBarcodeId;
        }

        /**
         * ??????ID
         *
         * @param defaultBarcodeId
         * 		??????ID
         */
        public void setDefaultBarcodeId(Long defaultBarcodeId) {
            this.defaultBarcodeId = defaultBarcodeId;
        }

        /**
         * ???
         *
         * @return adWord
         */
        public String getAdWord() {
            return adWord;
        }

        /**
         * ???
         *
         * @param adWord
         * 		???
         */
        public void setAdWord(String adWord) {
            this.adWord = adWord;
        }

        /**
         * ???3c???0:?3C1:3C???
         *
         * @return isCcc
         */
        public Integer getIsCcc() {
            return isCcc;
        }

        /**
         * ???3c???0:?3C1:3C???
         *
         * @param isCcc
         * 		???3c???0:?3C1:3C???
         */
        public void setIsCcc(Integer isCcc) {
            this.isCcc = isCcc;
        }

        /**
         * N??
         *
         * @return shoppingCount
         */
        public Integer getShoppingCount() {
            return shoppingCount;
        }

        /**
         * N??
         *
         * @param shoppingCount
         * 		N??
         */
        public void setShoppingCount(Integer shoppingCount) {
            this.shoppingCount = shoppingCount;
        }

        /**
         * ?????
         *
         * @return productIsGift
         */
        public Integer getProductIsGift() {
            return productIsGift;
        }

        /**
         * ?????
         *
         * @param productIsGift
         * 		?????
         */
        public void setProductIsGift(Integer productIsGift) {
            this.productIsGift = productIsGift;
        }

        /**
         * ???????0????1???
         *
         * @return canReturnAndChange
         */
        public Integer getCanReturnAndChange() {
            return canReturnAndChange;
        }

        /**
         * ???????0????1???
         *
         * @param canReturnAndChange
         * 		???????0????1???
         */
        public void setCanReturnAndChange(Integer canReturnAndChange) {
            this.canReturnAndChange = canReturnAndChange;
        }

        /**
         * ??????0????1???
         *
         * @return needExamine
         */
        public Integer getNeedExamine() {
            return needExamine;
        }

        /**
         * ??????0????1???
         *
         * @param needExamine
         * 		??????0????1???
         */
        public void setNeedExamine(Integer needExamine) {
            this.needExamine = needExamine;
        }

        /**
         * 1:?????;2:?????;3:?????;4:????;5:??????;6:??????
         *
         * @return verifyFlg
         */
        public Integer getVerifyFlg() {
            return verifyFlg;
        }

        /**
         * 1:?????;2:?????;3:?????;4:????;5:??????;6:??????
         *
         * @param verifyFlg
         * 		1:?????;2:?????;3:?????;4:????;5:??????;6:??????
         */
        public void setVerifyFlg(Integer verifyFlg) {
            this.verifyFlg = verifyFlg;
        }

        /**
         * ???
         *
         * @return verifyBy
         */
        public Long getVerifyBy() {
            return verifyBy;
        }

        /**
         * ???
         *
         * @param verifyBy
         * 		???
         */
        public void setVerifyBy(Long verifyBy) {
            this.verifyBy = verifyBy;
        }

        /**
         * ????
         *
         * @return verifyAt
         */
        /**
         * ????
         *
         * @param verifyAt
         * 		????
         */
        /**
         * ?????
         *
         * @return registerBy
         */
        public Long getRegisterBy() {
            return registerBy;
        }

        /**
         * ?????
         *
         * @param registerBy
         * 		?????
         */
        public void setRegisterBy(Long registerBy) {
            this.registerBy = registerBy;
        }

        /**
         * ??????
         *
         * @return registerAt
         */
        /**
         * ??????
         *
         * @param registerAt
         * 		??????
         */
        /**
         * ?????????
         *
         * @return registerPhone
         */
        public String getRegisterPhone() {
            return registerPhone;
        }

        /**
         * ?????????
         *
         * @param registerPhone
         * 		?????????
         */
        public void setRegisterPhone(String registerPhone) {
            this.registerPhone = registerPhone;
        }

        /**
         * ????
         *
         * @return verifyRemark
         */
        public String getVerifyRemark() {
            return verifyRemark;
        }

        /**
         * ????
         *
         * @param verifyRemark
         * 		????
         */
        public void setVerifyRemark(String verifyRemark) {
            this.verifyRemark = verifyRemark;
        }

        /**
         * ???
         *
         * @return batchNum
         */
        public Integer getBatchNum() {
            return batchNum;
        }

        /**
         * ???
         *
         * @param batchNum
         * 		???
         */
        public void setBatchNum(Integer batchNum) {
            this.batchNum = batchNum;
        }

        /**
         * ????????0:???1:??(??????)
         *
         * @return localLimit
         */
        public Integer getLocalLimit() {
            return localLimit;
        }

        /**
         * ????????0:???1:??(??????)
         *
         * @param localLimit
         * 		????????0:???1:??(??????)
         */
        public void setLocalLimit(Integer localLimit) {
            this.localLimit = localLimit;
        }

        /**
         * ?????
         *
         * @return stdPackQty
         */
        public Integer getStdPackQty() {
            return stdPackQty;
        }

        /**
         * ?????
         *
         * @param stdPackQty
         * 		?????
         */
        public void setStdPackQty(Integer stdPackQty) {
            this.stdPackQty = stdPackQty;
        }

        /**
         * ?????ID
         *
         * @return fromalProductId
         */
        public Long getFromalProductId() {
            return fromalProductId;
        }

        /**
         * ?????ID
         *
         * @param fromalProductId
         * 		?????ID
         */
        public void setFromalProductId(Long fromalProductId) {
            this.fromalProductId = fromalProductId;
        }

        /**
         * ??????
         *
         * @return isMustInvoice
         */
        public Integer getIsMustInvoice() {
            return isMustInvoice;
        }

        /**
         * ??????
         *
         * @param isMustInvoice
         * 		??????
         */
        public void setIsMustInvoice(Integer isMustInvoice) {
            this.isMustInvoice = isMustInvoice;
        }

        /**
         * ??????
         *
         * @return verifyFailureType
         */
        public Integer getVerifyFailureType() {
            return verifyFailureType;
        }

        /**
         * ??????
         *
         * @param verifyFailureType
         * 		??????
         */
        public void setVerifyFailureType(Integer verifyFailureType) {
            this.verifyFailureType = verifyFailureType;
        }

        /**
         * ????0?????1??????2??????3?????4????5:????6:????
         *
         * @return productType
         */
        public Integer getProductType() {
            return productType;
        }

        /**
         * ????0?????1??????2??????3?????4????5:????6:????
         *
         * @param productType
         * 		????0?????1??????2??????3?????4????5:????6:????
         */
        public void setProductType(Integer productType) {
            this.productType = productType;
        }

        /**
         * ??????
         *
         * @return canPurchase
         */
        public Integer getCanPurchase() {
            return canPurchase;
        }

        /**
         * ??????
         *
         * @param canPurchase
         * 		??????
         */
        public void setCanPurchase(Integer canPurchase) {
            this.canPurchase = canPurchase;
        }

        /**
         * ?????sku
         *
         * @return stdPackageSku
         */
        public String getStdPackageSku() {
            return stdPackageSku;
        }

        /**
         * ?????sku
         *
         * @param stdPackageSku
         * 		?????sku
         */
        public void setStdPackageSku(String stdPackageSku) {
            this.stdPackageSku = stdPackageSku;
        }

        /**
         * ???????????0:???1???
         *
         * @return userExpireControl
         */
        public Integer getUserExpireControl() {
            return userExpireControl;
        }

        /**
         * ???????????0:???1???
         *
         * @param userExpireControl
         * 		???????????0:???1???
         */
        public void setUserExpireControl(Integer userExpireControl) {
            this.userExpireControl = userExpireControl;
        }

        /**
         * ????ID
         *
         * @return batchRuleId
         */
        public Long getBatchRuleId() {
            return batchRuleId;
        }

        /**
         * ????ID
         *
         * @param batchRuleId
         * 		????ID
         */
        public void setBatchRuleId(Long batchRuleId) {
            this.batchRuleId = batchRuleId;
        }

        /**
         * ???????
         *
         * @return nameSubtitle
         */
        public String getNameSubtitle() {
            return nameSubtitle;
        }

        /**
         * ???????
         *
         * @param nameSubtitle
         * 		???????
         */
        public void setNameSubtitle(String nameSubtitle) {
            this.nameSubtitle = nameSubtitle;
        }

        /**
         * ???????1????11????12???14-18:????50?????
         *
         * @return specialType
         */
        public String getSpecialType() {
            return specialType;
        }

        /**
         * ???????1????11????12???14-18:????50?????
         *
         * @param specialType
         * 		???????1????11????12???14-18:????50?????
         */
        public void setSpecialType(String specialType) {
            this.specialType = specialType;
        }

        /**
         * ????????
         *
         * @return batchPrice
         */
        public Double getBatchPrice() {
            return batchPrice;
        }

        /**
         * ????????
         *
         * @param batchPrice
         * 		????????
         */
        public void setBatchPrice(Double batchPrice) {
            this.batchPrice = batchPrice;
        }

        /**
         * ????????0????1???
         *
         * @return needBatchControl
         */
        public Integer getNeedBatchControl() {
            return needBatchControl;
        }

        /**
         * ????????0????1???
         *
         * @param needBatchControl
         * 		????????0????1???
         */
        public void setNeedBatchControl(Integer needBatchControl) {
            this.needBatchControl = needBatchControl;
        }

        /**
         * ????
         *
         * @return salesTax
         */
        public Double getSalesTax() {
            return salesTax;
        }

        /**
         * ????
         *
         * @param salesTax
         * 		????
         */
        public void setSalesTax(Double salesTax) {
            this.salesTax = salesTax;
        }

        /**
         * ??????
         *
         * @return outerId
         */
        public String getOuterId() {
            return outerId;
        }

        /**
         * ??????
         *
         * @param outerId
         * 		??????
         */
        public void setOuterId(String outerId) {
            this.outerId = outerId;
        }

        /**
         * ??ID
         *
         * @return merchantId
         */
        public Long getMerchantId() {
            return merchantId;
        }

        /**
         * ??ID
         *
         * @param merchantId
         * 		??ID
         */
        public void setMerchantId(Long merchantId) {
            this.merchantId = merchantId;
        }

        /**
         * ????
         *
         * @return merchantName
         */
        public String getMerchantName() {
            return merchantName;
        }

        /**
         * ????
         *
         * @param merchantName
         * 		????
         */
        public void setMerchantName(String merchantName) {
            this.merchantName = merchantName;
        }

        /**
         * ???????????????
         *
         * @return masterCategoryId
         */
        public Long getMasterCategoryId() {
            return masterCategoryId;
        }

        /**
         * ???????????????
         *
         * @param masterCategoryId
         * 		???????????????
         */
        public void setMasterCategoryId(Long masterCategoryId) {
            this.masterCategoryId = masterCategoryId;
        }

        /**
         * ??????
         *
         * @return concernLevel
         */
        public Integer getConcernLevel() {
            return concernLevel;
        }

        /**
         * ??????
         *
         * @param concernLevel
         * 		??????
         */
        public void setConcernLevel(Integer concernLevel) {
            this.concernLevel = concernLevel;
        }

        /**
         * ????
         *
         * @return concernReason
         */
        public String getConcernReason() {
            return concernReason;
        }

        /**
         * ????
         *
         * @param concernReason
         * 		????
         */
        public void setConcernReason(String concernReason) {
            this.concernReason = concernReason;
        }

        /**
         * ????
         *
         * @return canSale
         */
        public Integer getCanSale() {
            return canSale;
        }

        /**
         * ????
         *
         * @param canSale
         * 		????
         */
        public void setCanSale(Integer canSale) {
            this.canSale = canSale;
        }

        /**
         * ????
         *
         * @return canShow
         */
        public Integer getCanShow() {
            return canShow;
        }

        /**
         * ????
         *
         * @param canShow
         * 		????
         */
        public void setCanShow(Integer canShow) {
            this.canShow = canShow;
        }

        /**
         * ??????
         *
         * @return prodcutTaxRate
         */
        public Long getProdcutTaxRate() {
            return prodcutTaxRate;
        }

        /**
         * ??????
         *
         * @param prodcutTaxRate
         * 		??????
         */
        public void setProdcutTaxRate(Long prodcutTaxRate) {
            this.prodcutTaxRate = prodcutTaxRate;
        }

        /**
         * ????VIP0:???1:??
         *
         * @return canVipDiscount
         */
        public Integer getCanVipDiscount() {
            return canVipDiscount;
        }

        /**
         * ????VIP0:???1:??
         *
         * @param canVipDiscount
         * 		????VIP0:???1:??
         */
        public void setCanVipDiscount(Integer canVipDiscount) {
            this.canVipDiscount = canVipDiscount;
        }

        /**
         * ????
         *
         * @return categoryName
         */
        public String getCategoryName() {
            return categoryName;
        }

        /**
         * ????
         *
         * @param categoryName
         * 		????
         */
        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        /**
         * ????
         *
         * @return salePrice
         */
        public Double getSalePrice() {
            return salePrice;
        }

        /**
         * ????
         *
         * @param salePrice
         * 		????
         */
        public void setSalePrice(Double salePrice) {
            this.salePrice = salePrice;
        }

        /**
         * ??
         *
         * @return stockNum
         */
        public Long getStockNum() {
            return stockNum;
        }

        /**
         * ??
         *
         * @param stockNum
         * 		??
         */
        public void setStockNum(Long stockNum) {
            this.stockNum = stockNum;
        }

        /**
         * ??????
         *
         * @return merchantCategoryName
         */
        public String getMerchantCategoryName() {
            return merchantCategoryName;
        }

        /**
         * ??????
         *
         * @param merchantCategoryName
         * 		??????
         */
        public void setMerchantCategoryName(String merchantCategoryName) {
            this.merchantCategoryName = merchantCategoryName;
        }

        /**
         * ????
         *
         * @return productDescription
         */
        public String getProductDescription() {
            return productDescription;
        }

        /**
         * ????
         *
         * @param productDescription
         * 		????
         */
        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }

        /**
         * ?????0????1???
         *
         * @return isTransfer
         */
        public Integer getIsTransfer() {
            return isTransfer;
        }

        /**
         * ?????0????1???
         *
         * @param isTransfer
         * 		?????0????1???
         */
        public void setIsTransfer(Integer isTransfer) {
            this.isTransfer = isTransfer;
        }

        /**
         * ??????0???????1??????2??????
         *
         * @return isSubmit
         */
        public Integer getIsSubmit() {
            return isSubmit;
        }

        /**
         * ??????0???????1??????2??????
         *
         * @param isSubmit
         * 		??????0???????1??????2??????
         */
        public void setIsSubmit(Integer isSubmit) {
            this.isSubmit = isSubmit;
        }

        /**
         * ??????
         *
         * @return verifyFailueType
         */
        public Integer getVerifyFailueType() {
            return verifyFailueType;
        }

        /**
         * ??????
         *
         * @param verifyFailueType
         * 		??????
         */
        public void setVerifyFailueType(Integer verifyFailueType) {
            this.verifyFailueType = verifyFailueType;
        }

        /**
         * ????
         *
         * @return productSpell
         */
        public String getProductSpell() {
            return productSpell;
        }

        /**
         * ????
         *
         * @param productSpell
         * 		????
         */
        public void setProductSpell(String productSpell) {
            this.productSpell = productSpell;
        }

        /**
         * ??????
         *
         * @return productNamePrefix
         */
        public String getProductNamePrefix() {
            return productNamePrefix;
        }

        /**
         * ??????
         *
         * @param productNamePrefix
         * 		??????
         */
        public void setProductNamePrefix(String productNamePrefix) {
            this.productNamePrefix = productNamePrefix;
        }

        /**
         * ??????
         *
         * @return failueReason
         */
        public String getFailueReason() {
            return failueReason;
        }

        /**
         * ??????
         *
         * @param failueReason
         * 		??????
         */
        public void setFailueReason(String failueReason) {
            this.failueReason = failueReason;
        }

        /**
         * orgPicUrl
         *
         * @return orgPicUrl
         */
        public String getOrgPicUrl() {
            return orgPicUrl;
        }

        /**
         * orgPicUrl
         *
         * @param orgPicUrl
         * 		orgPicUrl
         */
        public void setOrgPicUrl(String orgPicUrl) {
            this.orgPicUrl = orgPicUrl;
        }

        /**
         * ??????
         *
         * @return subCategoryName
         */
        public String getSubCategoryName() {
            return subCategoryName;
        }

        /**
         * ??????
         *
         * @param subCategoryName
         * 		??????
         */
        public void setSubCategoryName(String subCategoryName) {
            this.subCategoryName = subCategoryName;
        }

        /**
         * ????ID
         *
         * @return subCategoryId
         */
        public Long getSubCategoryId() {
            return subCategoryId;
        }

        /**
         * ????ID
         *
         * @param subCategoryId
         * 		????ID
         */
        public void setSubCategoryId(Long subCategoryId) {
            this.subCategoryId = subCategoryId;
        }

        /**
         * 7??????
         *
         * @return dailySale
         */
        public Integer getDailySale() {
            return dailySale;
        }

        /**
         * 7??????
         *
         * @param dailySale
         * 		7??????
         */
        public void setDailySale(Integer dailySale) {
            this.dailySale = dailySale;
        }

        /**
         * ???????
         *
         * @return picCount
         */
        public Integer getPicCount() {
            return picCount;
        }

        /**
         * ???????
         *
         * @param picCount
         * 		???????
         */
        public void setPicCount(Integer picCount) {
            this.picCount = picCount;
        }

        /**
         * ??????
         *
         * @return underCarriageReason
         */
        public Integer getUnderCarriageReason() {
            return underCarriageReason;
        }

        /**
         * ??????
         *
         * @param underCarriageReason
         * 		??????
         */
        public void setUnderCarriageReason(Integer underCarriageReason) {
            this.underCarriageReason = underCarriageReason;
        }

        /**
         * ????
         *
         * @return errorMessage
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * ????
         *
         * @param errorMessage
         * 		????
         */
        /**
         * public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; } ??????
         *
         * @return alertStockCount
         */
        public Integer getAlertStockCount() {
            return alertStockCount;
        }

        /**
         * ??????
         *
         * @param alertStockCount
         * 		??????
         */
        public void setAlertStockCount(Integer alertStockCount) {
            this.alertStockCount = alertStockCount;
        }

        /**
         * ????
         *
         * @return submitTime
         */
        /**
         * public Date getSubmitTime() { return submitTime; } ????
         *
         * @param submitTime
         * 		????
         */
        /**
         * public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; } holdPmPriceRpcVo
         *
         * @return holdPmPriceRpcVo
         */
        /**
         * holdPmPriceRpcVo
         *
         * @param holdPmPrice
         * 		holdPmPriceRpcVo
         */
        /**
         * pmPriceRpcVo
         *
         * @return pmPriceRpcVo
         */
        /**
         * public PmPriceRpcVo getPmPrice() { return pmPrice; } pmPriceRpcVo
         *
         * @param pmPrice
         * 		pmPriceRpcVo public void setPmPrice(PmPriceRpcVo pmPrice) { this.pmPrice = pmPrice; }
         */
        public Long getFormalPmInfoId() {
            return formalPmInfoId;
        }

        public void setFormalPmInfoId(Long formalPmInfoId) {
            this.formalPmInfoId = formalPmInfoId;
        }

        /**
         * ????????????
         *
         * @return deliveryInfo
         */
        public String getDeliveryInfo() {
            return deliveryInfo;
        }

        /**
         * ????????????
         *
         * @param deliveryInfo
         * 		????????????
         */
        public void setDeliveryInfo(String deliveryInfo) {
            this.deliveryInfo = deliveryInfo;
        }

        /**
         * ????
         *
         * @return picUrl
         */
        public String getPicUrl() {
            return picUrl;
        }

        /**
         * ????
         *
         * @param picUrl
         * 		????
         */
        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        /**
         * ??????????0:??????1???????2???????
         *
         * @return viewFromTag
         */
        public Integer getViewFromTag() {
            return viewFromTag;
        }

        /**
         * ??????????0:??????1???????2???????
         *
         * @param viewFromTag
         * 		??????????0:??????1???????2???????
         */
        public void setViewFromTag(Integer viewFromTag) {
            this.viewFromTag = viewFromTag;
        }

        public Double getProductNonMemberPrice() {
            return productNonMemberPrice;
        }

        /**
         * ????
         *
         * @param productNonMemberPrice
         * 		????
         */
        public void setProductNonMemberPrice(Double productNonMemberPrice) {
            this.productNonMemberPrice = productNonMemberPrice;
        }

        public Integer getIsUpdate() {
            return isUpdate;
        }

        /**
         * ??????
         *
         * @param isUpdate
         * 		??????
         */
        public void setIsUpdate(Integer isUpdate) {
            this.isUpdate = isUpdate;
        }

        public List<String> getProductSizeSet() {
            return productSizeSet;
        }

        public void setProductSizeSet(List<String> productSizeSet) {
            this.productSizeSet = productSizeSet;
        }

        public Boolean getIsMainProduct() {
            return isMainProduct;
        }

        /**
         * ?????
         *
         * @param isMainProduct
         * 		?????
         */
        public void setIsMainProduct(Boolean isMainProduct) {
            this.isMainProduct = isMainProduct;
        }

        /**
         * ??????????ID?URL
         *
         * @return productPicIdAndURL
         */
        public String getProductPicIdAndURL() {
            return productPicIdAndURL;
        }

        /**
         * ??????????ID?URL
         *
         * @param productPicIdAndURL
         * 		??????????ID?URL
         */
        public void setProductPicIdAndURL(String productPicIdAndURL) {
            this.productPicIdAndURL = productPicIdAndURL;
        }

        public Integer getIsTemp() {
            return isTemp;
        }

        /**
         * isTemp
         *
         * @param isTemp
         * 		isTemp
         */
        public void setIsTemp(Integer isTemp) {
            this.isTemp = isTemp;
        }

        public Double getPriceRate() {
            return priceRate;
        }

        public void setPriceRate(Double priceRate) {
            this.priceRate = priceRate;
        }

        public Integer getPicSpecialType() {
            return picSpecialType;
        }

        public void setPicSpecialType(Integer picSpecialType) {
            this.picSpecialType = picSpecialType;
        }

        public Integer getExemptStatus() {
            return exemptStatus;
        }

        public void setExemptStatus(Integer exemptStatus) {
            this.exemptStatus = exemptStatus;
        }

        public String getViolationReasonIds() {
            return violationReasonIds;
        }

        /**
         * ????????:???????
         *
         * @param violationReasonIds
         * 		????????:???????
         */
        public void setViolationReasonIds(String violationReasonIds) {
            this.violationReasonIds = violationReasonIds;
        }

        /**
         * ????????:????????????????
         *
         * @return violationReasons
         */
        public String getViolationReasons() {
            return violationReasons;
        }

        public void setViolationReasons(String violationReasons) {
            this.violationReasons = violationReasons;
        }

        /**
         * ???????????????
         *
         * @return remainTime
         */
        public Long getRemainTime() {
            return remainTime;
        }

        /**
         * ???????????????
         *
         * @param remainTime
         * 		???????????????
         */
        public void setRemainTime(Long remainTime) {
            this.remainTime = remainTime;
        }

        public Integer getSubmitOrder() {
            return submitOrder;
        }

        public void setSubmitOrder(Integer submitOrder) {
            this.submitOrder = submitOrder;
        }

        public Integer getProductSource() {
            return productSource;
        }

        public void setProductSource(Integer productSource) {
            this.productSource = productSource;
        }

        public String getProductSname() {
            return productSname;
        }

        public void setProductSname(String productSname) {
            this.productSname = productSname;
        }

        public Integer getCanFenqi() {
            return canFenqi;
        }

        public void setCanFenqi(Integer canFenqi) {
            this.canFenqi = canFenqi;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public Integer getIsKa() {
            return isKa;
        }

        public void setIsKa(Integer isKa) {
            this.isKa = isKa;
        }

        public Integer getKaMCreateTime() {
            return kaMCreateTime;
        }

        public void setKaMCreateTime(Integer kaMCreateTime) {
            this.kaMCreateTime = kaMCreateTime;
        }

        public Integer getDeliveryDay() {
            return deliveryDay;
        }

        public void setDeliveryDay(Integer deliveryDay) {
            this.deliveryDay = deliveryDay;
        }

        public Integer getIsEdit() {
            return isEdit;
        }

        public void setIsEdit(Integer isEdit) {
            this.isEdit = isEdit;
        }

        public String getProductBrandName() {
            return productBrandName;
        }

        public void setProductBrandName(String productBrandName) {
            this.productBrandName = productBrandName;
        }

        /**
         * ???????
         *
         * @return categoryStr
         */
        public String getCategoryStr() {
            return categoryStr;
        }

        /**
         * ???????
         *
         * @param categoryStr
         * 		???????
         */
        public void setCategoryStr(String categoryStr) {
            this.categoryStr = categoryStr;
        }

        /**
         * ?????????
         *
         * @return extendCategoryStr
         */
        public String getExtendCategoryStr() {
            return extendCategoryStr;
        }

        /**
         * ?????????
         *
         * @param extendCategoryStr
         * 		?????????
         */
        public void setExtendCategoryStr(String extendCategoryStr) {
            this.extendCategoryStr = extendCategoryStr;
        }

        public String getCategoryIdStr() {
            return categoryIdStr;
        }

        public void setCategoryIdStr(String categoryIdStr) {
            this.categoryIdStr = categoryIdStr;
        }

        public String getExtendCategoryIdStr() {
            return extendCategoryIdStr;
        }

        public Long getDefaultWarehouseId() {
            return defaultWarehouseId;
        }

        public void setDefaultWarehouseId(Long defaultWarehouseId) {
            this.defaultWarehouseId = defaultWarehouseId;
        }

        public Long getOldCategoryId() {
            return oldCategoryId;
        }

        public void setOldCategoryId(Long oldCategoryId) {
            this.oldCategoryId = oldCategoryId;
        }

        public Long getOldExtendCategoryId() {
            return oldExtendCategoryId;
        }

        public void setOldExtendCategoryId(Long oldExtendCategoryId) {
            this.oldExtendCategoryId = oldExtendCategoryId;
        }

        public String getDeletedProductId() {
            return deletedProductId;
        }

        public void setDeletedProductId(String deletedProductId) {
            this.deletedProductId = deletedProductId;
        }

        public String getReplaceProductSize() {
            return replaceProductSize;
        }

        public void setReplaceProductSize(String replaceProductSize) {
            this.replaceProductSize = replaceProductSize;
        }

        public List<Long> getMasterCategoryIdList() {
            return masterCategoryIdList;
        }

        public void setMasterCategoryIdList(List<Long> masterCategoryIdList) {
            // this.masterCategoryIdList = masterCategoryIdList;
        }
    }
}

