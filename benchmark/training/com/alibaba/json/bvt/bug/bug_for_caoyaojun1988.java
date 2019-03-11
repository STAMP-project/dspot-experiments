package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class bug_for_caoyaojun1988 extends TestCase {
    public void test_for_bug() throws Exception {
        // ?? BusinessVO
        bug_for_caoyaojun1988.BusinessVO businessVO = new bug_for_caoyaojun1988.BusinessVO();
        businessVO.setName("name");
        // ?? ???List list????????? BusinessVO??
        bug_for_caoyaojun1988.ExpiredDto expiredDto = new bug_for_caoyaojun1988.ExpiredDto();
        expiredDto.setBusinessVO(businessVO);
        expiredDto.setId(10001);
        List<bug_for_caoyaojun1988.ExpiredDto> expiredReports = new ArrayList<bug_for_caoyaojun1988.ExpiredDto>();
        expiredReports.add(expiredDto);
        // ?? ???List list????????? BusinessVO??
        List<bug_for_caoyaojun1988.NormalDto> normalReports = new ArrayList<bug_for_caoyaojun1988.NormalDto>();
        {
            bug_for_caoyaojun1988.NormalDto normalDto = new bug_for_caoyaojun1988.NormalDto();
            normalDto.setBusinessVO(businessVO);
            normalDto.setId(10001);
            normalReports.add(normalDto);
        }
        // ?? ?????????????list
        bug_for_caoyaojun1988.ReportDto reportDto = new bug_for_caoyaojun1988.ReportDto();
        reportDto.setExpiredReports(expiredReports);
        reportDto.setNormalReports(normalReports);
        reportDto.setCompanyId(10004);
        // ????? ???businessVO?null?
        String serializeStr = ((String) (JSON.toJSONString(reportDto)));
        System.out.println(serializeStr);
        bug_for_caoyaojun1988.ReportDto reuslt = ((bug_for_caoyaojun1988.ReportDto) (JSON.parseObject(serializeStr, bug_for_caoyaojun1988.ReportDto.class)));
        System.out.println(reuslt.getNormalReports().get(0).getBusinessVO());
        // ????? ???businessVO?????
        expiredReports.add(expiredDto);
        serializeStr = ((String) (JSON.toJSONString(reportDto)));
        System.out.println(serializeStr);
        reuslt = ((bug_for_caoyaojun1988.ReportDto) (JSON.parseObject(serializeStr, bug_for_caoyaojun1988.ReportDto.class)));
        System.out.print(reuslt.getNormalReports().get(0).getBusinessVO().getName());
    }

    public static class BusinessVO implements Serializable {
        private static final long serialVersionUID = -191856665415285103L;

        private String name;

        public BusinessVO() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class ExpiredDto implements Serializable {
        private static final long serialVersionUID = -2361763020563748437L;

        private Integer id;

        private bug_for_caoyaojun1988.BusinessVO businessVO;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public bug_for_caoyaojun1988.BusinessVO getBusinessVO() {
            return businessVO;
        }

        public void setBusinessVO(bug_for_caoyaojun1988.BusinessVO businessVO) {
            this.businessVO = businessVO;
        }
    }

    public static class NormalDto implements Serializable {
        private static final long serialVersionUID = -2392077150026945111L;

        private Integer id;

        private bug_for_caoyaojun1988.BusinessVO businessVO;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public bug_for_caoyaojun1988.BusinessVO getBusinessVO() {
            return businessVO;
        }

        public void setBusinessVO(bug_for_caoyaojun1988.BusinessVO businessVO) {
            this.businessVO = businessVO;
        }

        public static long getSerialversionuid() {
            return bug_for_caoyaojun1988.NormalDto.serialVersionUID;
        }
    }

    public static class ReportDto implements Serializable {
        private static final long serialVersionUID = 4502937258945851832L;

        private Integer companyId;

        private List<bug_for_caoyaojun1988.NormalDto> normalReports;

        private List<bug_for_caoyaojun1988.ExpiredDto> expiredReports;

        public Integer getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Integer companyId) {
            this.companyId = companyId;
        }

        public List<bug_for_caoyaojun1988.NormalDto> getNormalReports() {
            return normalReports;
        }

        public void setNormalReports(List<bug_for_caoyaojun1988.NormalDto> normalReports) {
            this.normalReports = normalReports;
        }

        public List<bug_for_caoyaojun1988.ExpiredDto> getExpiredReports() {
            return expiredReports;
        }

        public void setExpiredReports(List<bug_for_caoyaojun1988.ExpiredDto> expiredReports) {
            this.expiredReports = expiredReports;
        }
    }
}

