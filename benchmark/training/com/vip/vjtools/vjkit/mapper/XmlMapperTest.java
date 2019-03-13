package com.vip.vjtools.vjkit.mapper;


import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;


/**
 * ????JAXB2.0?Java??-XML???Dom4j???.
 *
 * ???xml??:
 *
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <user id="1">
 * 	<name>calvin</name>
 * 	<interests>
 * 		<interest>movie</interest>
 * 		<interest>sports</interest>
 * 	</interests>
 * </user>
 * </pre>
 */
public class XmlMapperTest {
    @Test
    public void objectToXml() {
        XmlMapperTest.User user = new XmlMapperTest.User();
        user.setId(1L);
        user.setName("calvin");
        user.getInterests().add("movie");
        user.getInterests().add("sports");
        String xml = XmlMapper.toXml(user, "UTF-8");
        System.out.println(("Jaxb Object to Xml result:\n" + xml));
        XmlMapperTest.assertXmlByDom4j(xml);
    }

    @Test
    public void xmlToObject() {
        String xml = XmlMapperTest.generateXmlByDom4j();
        XmlMapperTest.User user = XmlMapper.fromXml(xml, XmlMapperTest.User.class);
        System.out.println(("Jaxb Xml to Object result:\n" + user));
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getInterests()).containsOnly("movie", "sports");
    }

    /**
     * ???List?????????XML??
     */
    @Test
    public void toXmlWithListAsRoot() {
        XmlMapperTest.User user1 = new XmlMapperTest.User();
        user1.setId(1L);
        user1.setName("calvin");
        XmlMapperTest.User user2 = new XmlMapperTest.User();
        user2.setId(2L);
        user2.setName("kate");
        List<XmlMapperTest.User> userList = Lists.newArrayList(user1, user2);
        String xml = XmlMapper.toXml(userList, "userList", XmlMapperTest.User.class, "UTF-8");
        System.out.println(("Jaxb Object List to Xml result:\n" + xml));
    }

    // ????????
    @XmlRootElement
    @XmlType(propOrder = { "name", "interests" })
    private static class User {
        private Long id;

        private String name;

        private String password;

        private List<String> interests = Lists.newArrayList();

        // ?????xml??????
        @XmlAttribute
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // ??????xml
        @XmlTransient
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        // ???List<String>???, xml?<interests><interest>movie</interest></interests>
        @XmlElementWrapper(name = "interests")
        @XmlElement(name = "interest")
        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}

