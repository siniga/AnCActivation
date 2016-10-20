package com.aggreyclifford.active.ancactivation.models;

/**
 * Created by alicephares on 9/28/16.
 */
public class RouteMember {

    String name;
    int id;



    public RouteMember(String name, int id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String toString()
    {
        return name;
    }
}
