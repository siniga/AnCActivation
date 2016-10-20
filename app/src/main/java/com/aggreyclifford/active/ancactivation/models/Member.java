package com.aggreyclifford.active.ancactivation.models;

/**
 * Created by alicephares on 9/28/16.
 */
public class Member {

    String name;
    String type;
    String effectiveContact, effectiveReach,sourceBusiness;
    int img;




    public Member(String name,String effectiveContact,String effectiveReach,String sourceBusiness){
        this.name = name;
        this.type = type;
        this.img = img;
        this.effectiveContact = effectiveContact;
        this.effectiveReach = effectiveReach;
        this.sourceBusiness = sourceBusiness;
    }

    public String getName() {
        return name;
    }

    public String getEffectiveContact() {
        return effectiveContact;
    }

    public String getEffectiveReach() {
        return effectiveReach;
    }

    public String getSourceBusiness() {
        return sourceBusiness;
    }

    public String getType() {
        return type;
    }

    public int getImg() {
        return img;
    }
}
