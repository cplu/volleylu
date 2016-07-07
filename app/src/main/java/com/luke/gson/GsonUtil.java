package com.luke.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by cplu on 2014/11/20.
 */
public class GsonUtil {
    private static Gson m_gson;
    public static Gson getGson() {
        if(m_gson != null){
            return m_gson;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        //xxxx-xx-xxTxx:xx:xx.xxx+xxxx
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        gsonBuilder.registerTypeAdapter(DeviceStatus.class, new EventTypeDeserializer());
        m_gson = gsonBuilder.create();
        return m_gson;
    }

//    public static Gson getGson(String time_format) {
//        if(m_gson != null){
//            return m_gson;
//        }
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        //xxxx-xx-xxTxx:xx:xx.xxx+xxxx
//        gsonBuilder.setDateFormat(time_format);
////        gsonBuilder.registerTypeAdapter(DeviceStatus.class, new EventTypeDeserializer());
//        m_gson = gsonBuilder.create();
//        return m_gson;
//    }
}
