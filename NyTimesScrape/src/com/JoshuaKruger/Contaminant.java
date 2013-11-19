package com.JoshuaKruger;

/**
 * Created with IntelliJ IDEA.
 * User: joshkruger
 * Date: 11/13/13
 * Time: 7:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Contaminant {

    private String Name;
    private String Average;
    private String Max;
    private String HealthLimit;
    private String legalLimit;


    //SETTERS
    public void setName(String name){
        Name = name;
    }
    public void setAverage(String a){
        Average = a;
    }
    public void setMax(String m){
        Max = m;
    }
    public void setHealthLimit(String hl){
        HealthLimit = hl;
    }
    public void setLegalLimit(String ll){
        legalLimit = ll;
    }

    //GETTERS
    public String getName(){
        return Name;
    }
    public String getAverage(){
        return Average;
    }
    public String getHealthLimit(){
        return HealthLimit;
    }
    public String getMax(){
        return Max;
    }
    public String getLegalLimit(){
        return legalLimit;
    }


}
