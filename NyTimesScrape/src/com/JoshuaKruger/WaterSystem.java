package com.JoshuaKruger;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: joshkruger
 * Date: 11/13/13
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class WaterSystem {

    private ArrayList<Contaminant> Chemicals = new ArrayList();
    private String WaterSystemName;
    private String ParentCounty;

    public void addContaminant(String name, String Average, String Max, String Hlimit, String Llimit){
        Contaminant newContam = new Contaminant();

        newContam.setName(name);
        newContam.setAverage(Average);
        newContam.setMax(Max);
        newContam.setHealthLimit(Hlimit);
        newContam.setLegalLimit(Llimit);

        Chemicals.add(newContam);

    }
    public void addContaminant(Contaminant c){
        Chemicals.add(c);
    }
    public void setName(String name){
        WaterSystemName = name;
    }
    public void setParent(String name){
        ParentCounty = name;
    }
    public ArrayList<Contaminant> getChemicals(){
        return Chemicals;
    }
    public String getWaterSystemName(){
        return WaterSystemName;
    }
    public String getParentCounty(){
        return ParentCounty;
    }

}
