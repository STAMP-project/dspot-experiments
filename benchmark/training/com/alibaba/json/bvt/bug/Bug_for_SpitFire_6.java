package com.alibaba.json.bvt.bug;


import SerializerFeature.PrettyFormat;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_SpitFire_6 extends TestCase {
    public void test_ref() throws Exception {
        Bug_for_SpitFire_6.GenericRS<Bug_for_SpitFire_6.HotelAvailRS> rs = new Bug_for_SpitFire_6.GenericRS<Bug_for_SpitFire_6.HotelAvailRS>();
        Bug_for_SpitFire_6.HotelAvailRS availRs = new Bug_for_SpitFire_6.HotelAvailRS();
        Bug_for_SpitFire_6.AvailRoomStayDTO stay = new Bug_for_SpitFire_6.AvailRoomStayDTO();
        availRs.getHotelAvailRoomStay().getRoomStays().add(stay);
        availRs.getHotelAvailRoomStay().getRoomStays().add(stay);
        availRs.getHotelAvailRoomStay().getRoomStays().add(stay);
        availRs.getHotelAvailRoomStay().getRoomStays().add(stay);
        rs.setPayload(availRs);
        String text = JSON.toJSONString(rs, WriteClassName, PrettyFormat);
        System.out.println(text);
        JSON.parseObject(text, Bug_for_SpitFire_6.GenericRS.class);
    }

    public static class GenericRS<T> {
        private T payload;

        public T getPayload() {
            return payload;
        }

        public void setPayload(T payload) {
            this.payload = payload;
        }
    }

    public static class HotelAvailRS {
        private Bug_for_SpitFire_6.HotelAvailRoomStayDTO hotelAvailRoomStay = new Bug_for_SpitFire_6.HotelAvailRoomStayDTO();

        public Bug_for_SpitFire_6.HotelAvailRoomStayDTO getHotelAvailRoomStay() {
            return hotelAvailRoomStay;
        }

        public void setHotelAvailRoomStay(Bug_for_SpitFire_6.HotelAvailRoomStayDTO hotelAvailRoomStay) {
            this.hotelAvailRoomStay = hotelAvailRoomStay;
        }
    }

    public static class HotelAvailRoomStayDTO {
        private List<Bug_for_SpitFire_6.AvailRoomStayDTO> roomStays = new ArrayList<Bug_for_SpitFire_6.AvailRoomStayDTO>();

        public List<Bug_for_SpitFire_6.AvailRoomStayDTO> getRoomStays() {
            return roomStays;
        }

        public void setRoomStays(List<Bug_for_SpitFire_6.AvailRoomStayDTO> roomStays) {
            this.roomStays = roomStays;
        }
    }

    public static class AvailRoomStayDTO {}
}

