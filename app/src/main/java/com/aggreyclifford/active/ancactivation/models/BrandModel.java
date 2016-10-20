package com.aggreyclifford.active.ancactivation.models;

import android.widget.ArrayAdapter;

import com.aggreyclifford.active.ancactivation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alicephares on 10/19/16.
 */
public class BrandModel {

    public static List<BrandSob> getBrands() {
        List<BrandSob> brands = new ArrayList<>();


        brands.add(new BrandSob(0, "Embassy Kings", "PM"));
        brands.add(new BrandSob(0, "Embassy Light", "PM"));

        brands.add(new BrandSob(0, "Camel White", "PM"));
        brands.add(new BrandSob(0, "Camel Black", "PM"));

        brands.add(new BrandSob(0, "Portsman", "MP"));
        brands.add(new BrandSob(0, "SweetMenthol (SM)", "MP"));

        brands.add(new BrandSob(0, "Winston Red", "VL"));
        brands.add(new BrandSob(0, "Winston Methol", "VL"));

        brands.add(new BrandSob(0, "Safari Full Flavor", "BS"));
        brands.add(new BrandSob(0, "Club Menthol", "BS"));

        brands.add(new BrandSob(0, "Club Full Flavor", "VL"));
        brands.add(new BrandSob(0, "LD Menthol", "VL"));

        brands.add(new BrandSob(0, "LD Full Flavor", "BS"));
        brands.add(new BrandSob(0, "Nyota", "BS"));

        //competitors
        brands.add(new BrandSob(0, "Marlboro Light", "CP"));
        brands.add(new BrandSob(0, "Malboro Menthol", "CP"));
        brands.add(new BrandSob(0, "Marlboro Red", "CP"));
        brands.add(new BrandSob(0, "Dunhill", "CP"));
        brands.add(new BrandSob(0, "Benson & Hedges", "CP"));
        brands.add(new BrandSob(0, "Masters", "CP"));
        brands.add(new BrandSob(0, "Roceo", "CP"));

        return brands;

    }
}
