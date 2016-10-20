package com.aggreyclifford.active.ancactivation.endpoint;

/**
 * Created by alicephares on 9/28/16.
 */
public class Endpoint {
    private static String ROOT = "http://v1dashboard.aggreyapps.com/";
    private static String complete_url;

    public static String getRootUrl(){
        return complete_url;
    }

    public static void setUrl(String url){
       complete_url = ROOT + url;
    }

}
