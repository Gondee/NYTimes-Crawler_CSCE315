package com.JoshuaKruger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: joshkruger
 * Date: 11/13/13
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class DownloadMe {

    public ArrayList<WaterSystem> WaterSystems = new ArrayList();
    private ArrayList<String> Links = new ArrayList();
    private ArrayList<String> Names = new ArrayList();
    private String BaseURL = "http://projects.nytimes.com";



    public void CompileLinksAndNames(int numberOFcounty){



        int CountyCounter =0;

        try{
        BufferedReader br = new BufferedReader(new FileReader("NYTIMES.html"));
        String line;
        while ((line = br.readLine()) != null) {

            if(line == null)
                break;

            String L = line;
            boolean linkfound = false;
            boolean namefound = false;
            String newLink ="";
            String newName ="";
            //System.out.println(line);

            for(int i =0; i < L.length();i++){

                if(L.charAt(i) == '\"')
                   linkfound = !linkfound;

                if(linkfound && L.charAt(i) != '\"')
                    newLink += L.charAt(i);

                if(L.charAt(i) == '>')
                    namefound = !namefound;

                if(namefound && L.charAt(i) != '>')
                    newName += L.charAt(i);

            }
            //System.out.println("NewName:" + SpaceToUnderscore(newName)+"");
            //System.out.println("NewLink:" + newLink);
            CountyCounter++;
            Names.add(newName);
            Links.add(newLink);
            if(numberOFcounty ==0)
                GetWaterSystemsPages(newLink, newName);
            else if(numberOFcounty >= CountyCounter){
                GetWaterSystemsPages(newLink, newName);
            }

        }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        System.out.println("END OF WEB CRAWL");
        System.out.println("Begning XML Write...");
        toXML();
        System.out.println("END OF XML WRITE");



    }//endof function

    private String SpaceToUnderscore(String s){

        String newFileName = "";
        for(int i =0; i< s.length();i++){

            if(s.charAt(i) ==' ')
                newFileName+="_";
            else
                newFileName +=s.charAt(i);
        }
         return newFileName;
    }


    private void GetWaterSystemsPages(String URLL, String filename){

        //WaterSystem newSystem = new WaterSystem();



         ArrayList<String> wslinks = new ArrayList();
         ArrayList<String> wsnames = new ArrayList();
         ArrayList<String> WaterSytemHTML = new ArrayList();

            URL url;
            InputStream is = null;
            BufferedReader br;
            String line;

        //----------------Gathering HTML
            try {
                url = new URL(BaseURL+URLL);
                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));

                while ((line = br.readLine()) != null) {
                    WaterSytemHTML.add(line);
                }
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException ioe) {
                    // nothing to see here
                }
            }
        //----------------Grabbing links to data

        //Test for direct page or water systems
        boolean CC = false;

        for(int i=0; i<WaterSytemHTML.size();i++){
            String t = WaterSytemHTML.get(i);
            if(t.contains("<title>Water Systems in")){
                //System.out.println(filename +"- Contains Countys");
                CC = true;
            }

        }

        if(CC){

            ArrayList<String> LinksSection = new ArrayList();
            //Inspect for links
           boolean Start =false;

            for(int i=0; i<WaterSytemHTML.size();i++){
                String t = WaterSytemHTML.get(i);

                if(t.contains("<ul class=\"nytint-county-list\">"))
                    Start = true;

                if(t.contains("</ul>"))
                    Start = false;

                if(Start && t.contains("<li><strong><a href=\"")){

                    boolean linkfound = false;
                    boolean namefound = false;
                    String newLink ="";
                    String newName ="";
                    int bracketindex =0;
                    int s = t.indexOf("<li><strong><a href=");   //Moving past inital part
                    int slength = "<li><strong><a href=".length();

                    for(int x =s+slength; x < t.length();x++){

                        if(t.charAt(x) == '\"')
                            linkfound = !linkfound;

                        if(linkfound && t.charAt(x) != '\"')
                            newLink += t.charAt(x);

                        if(t.charAt(x) == '>'){
                            namefound = !namefound;
                            bracketindex = x;
                        }
                        if(t.charAt(x) == '<'){
                            namefound = !namefound;
                            break; //Found the name and quitting
                        }

                        if(namefound && t.charAt(x) != '>')
                            newName += t.charAt(x);

                    }



                    //System.out.println("NewName:" + newName);
                    //System.out.println("NewLink:" + newLink);
                    wslinks.add(newLink);
                    wsnames.add(newName);


                }

            }  //End of link searching loop


            //Create Water Systems

            for(int i =0; i < wslinks.size();i++){
                WaterSystem newSys = new WaterSystem();
                newSys.setName(wsnames.get(i));
                newSys.setParent(filename);
                ParseContaminantsFromPage(wslinks.get(i), newSys);
                WaterSystems.add(newSys);
                //Add System to list of systems




            }



        }
        else{
            //Parse data now
            WaterSystem newSys = new WaterSystem();
            newSys.setName(filename + " Water System");
            newSys.setParent(filename);
            ParseContaminantsFromPage(URLL, newSys);
            WaterSystems.add(newSys);

        }


    }//Get Water System end

    private void ParseContaminantsFromPage(String urll, WaterSystem sys){  //Populates the contaminents
        ArrayList<String> file = new ArrayList();

        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        boolean error = false;
        //----------------Gathering HTML
        try {
            url = new URL(BaseURL+urll);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                file.add(line);
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                System.out.println("URL does not exist");
                error = true;
            }
        }


     //Search for
        //"contaminant below legal limits, but above health guidelines"
        //"contaminants found within health guidelines and legal limits"
        //"contaminant above legal limits"



        //Chemicals "contaminant below legal limits, but above health guidelines"
        //<td class="nytint-name"> to get name of chemical


        ArrayList<String> ContNames = new ArrayList();
        ArrayList<String> ContInfo = new ArrayList();

        if(!error){
            boolean BelowLegalLimit = false;
            boolean contFound = false;
            int infonumber= 0;



            //SEARCHIN FOR contaminant below legal limits, but above health guidelines
            //System.out.println("contaminant below legal limits, but above health guidelines");

            for(int i=0; i<file.size();i++){
                String L = file.get(i);

                if(L.contains("contaminant below legal limits, but above health guidelines"))
                    BelowLegalLimit = true; //Condition search for
                if(L.contains("contaminants found within health guidelines and legal limits"))
                    BelowLegalLimit = false;
                if(L.contains("contaminant above legal limits"))
                    BelowLegalLimit = false;

                if(BelowLegalLimit){
                    String CONTAMINANT_NAME = "";




                    if(contFound == true){

                        if(infonumber > 3){
                            contFound = false;
                            continue;
                        }

                        String info = "";
                        boolean tagfound = false;
                        //System.out.println(L);
                        for(int x =0; x<L.length();x++){
                            if(L.charAt(x) == '>')
                                tagfound = true;
                            if(L.charAt(x) == '<')
                                tagfound = false;

                            if(tagfound && L.charAt(x) != '>')
                                info += L.charAt(x);


                        }
                        infonumber++;

                        //System.out.println(sys.getWaterSystemName() + " -Info - " + info);
                        ContInfo.add(info);

                    }//Getting Contaminant Information




                    if(L.contains("<td class=\"nytint-name\">")){  //Finding name of contaminant
                        infonumber = 0;
                        contFound = true;
                        int xy = L.indexOf("<td class=\"nytint-name\"><span title=\"");
                        String LengthStart = "<td class=\"nytint-name\"><span title=\"";
                        int xyz = xy + LengthStart.length();
                        boolean namefound = false;

                        for(int y =xyz; y < L.length(); y++){
                            if(L.charAt(y) == '>')
                                namefound = true;
                            if(L.charAt(y) == '<'){
                                namefound = false;
                                break;
                            }
                            if(namefound && L.charAt(y) != '>')
                                CONTAMINANT_NAME += L.charAt(y);

                        } //Searching for name
                        //System.out.println(sys.getWaterSystemName() + " -Contaminant - " + CONTAMINANT_NAME);
                        ContNames.add(CONTAMINANT_NAME);
                    }//found name tag




                }//below legal limit found

            } //main loop



            //SEARCHING FOR "contaminants found within health guidelines and legal limits"

            BelowLegalLimit = false;
            contFound = false;
            infonumber= 0;

            //System.out.println("contaminants found within health guidelines and legal limits");

            for(int i=0; i<file.size();i++){
                String L = file.get(i);

                if(L.contains("contaminants found within health guidelines and legal limits"))
                    BelowLegalLimit = true; //Condition search for
                if(L.contains("contaminant below legal limits, but above health guidelines"))
                    BelowLegalLimit = false;
                if(L.contains("contaminant above legal limits"))
                    BelowLegalLimit = false;

                if(BelowLegalLimit){
                    String CONTAMINANT_NAME = "";




                    if(contFound == true){

                        if(infonumber > 3){
                            contFound = false;
                            continue;
                        }

                        String info = "";
                        boolean tagfound = false;
                        //System.out.println(L);
                        for(int x =0; x<L.length();x++){
                            if(L.charAt(x) == '>')
                                tagfound = true;
                            if(L.charAt(x) == '<')
                                tagfound = false;

                            if(tagfound && L.charAt(x) != '>')
                                info += L.charAt(x);


                        }
                        infonumber++;

                        //System.out.println(sys.getWaterSystemName() + " -Info - " + info);
                        ContInfo.add(info);

                    }//Getting Contaminant Information




                    if(L.contains("<td class=\"nytint-name\">")){  //Finding name of contaminant
                        infonumber =0;
                        contFound = true;
                        int xy = L.indexOf("<td class=\"nytint-name\"><span title=\"");
                        String LengthStart = "<td class=\"nytint-name\"><span title=\"";
                        int xyz = xy + LengthStart.length();
                        boolean namefound = false;

                        for(int y =xyz; y < L.length(); y++){
                            if(L.charAt(y) == '>')
                                namefound = true;
                            if(L.charAt(y) == '<'){
                                namefound = false;
                                break;
                            }
                            if(namefound && L.charAt(y) != '>')
                                CONTAMINANT_NAME += L.charAt(y);

                        } //Searching for name
                        //System.out.println(sys.getWaterSystemName() + " -Contaminant - " + CONTAMINANT_NAME);
                        ContNames.add(CONTAMINANT_NAME);
                    }//found name tag




                }//within health guidelines and limits

            } //main loop






            //SEARCHING FOR "contaminant above legal limits"

            BelowLegalLimit = false;
            contFound = false;
            infonumber= 0;

            //System.out.println("contaminant above legal limits");

            for(int i=0; i<file.size();i++){
                String L = file.get(i);

                if(L.contains("contaminant above legal limits"))
                    BelowLegalLimit = true; //Condition search for
                if(L.contains("contaminant below legal limits, but above health guidelines"))
                    BelowLegalLimit = false;
                if(L.contains("contaminants found within health guidelines and legal limits"))
                    BelowLegalLimit = false;

                if(BelowLegalLimit){
                    String CONTAMINANT_NAME = "";




                    if(contFound == true){

                        if(infonumber > 3){
                            contFound = false;
                            continue;
                        }

                        String info = "";
                        boolean tagfound = false;
                        //System.out.println(L);
                        for(int x =0; x<L.length();x++){
                            if(L.charAt(x) == '>')
                                tagfound = true;
                            if(L.charAt(x) == '<')
                                tagfound = false;

                            if(tagfound && L.charAt(x) != '>')
                                info += L.charAt(x);


                        }
                        infonumber++;

                        //System.out.println(sys.getWaterSystemName() + " -Info - " + info);
                        ContInfo.add(info);

                    }//Getting Contaminant Information




                    if(L.contains("<td class=\"nytint-name\">")){  //Finding name of contaminant
                        infonumber =0;
                        contFound = true;
                        int xy = L.indexOf("<td class=\"nytint-name\"><span title=\"");
                        String LengthStart = "<td class=\"nytint-name\"><span title=\"";
                        int xyz = xy + LengthStart.length();
                        boolean namefound = false;

                        for(int y =xyz; y < L.length(); y++){
                            if(L.charAt(y) == '>')
                                namefound = true;
                            if(L.charAt(y) == '<'){
                                namefound = false;
                                break;
                            }
                            if(namefound && L.charAt(y) != '>')
                                CONTAMINANT_NAME += L.charAt(y);

                        } //Searching for name
                        //System.out.println(sys.getWaterSystemName() + " -Contaminant - " + CONTAMINANT_NAME);
                        ContNames.add(CONTAMINANT_NAME);
                    }//found name tag




                }//above legal limit found

            } //main loop








        } // IF make sure no error in file open

        //System.out.println(ContNames.size() + " - " + ContInfo.size());

        int contSet =0;
        for(int i=0;i<ContNames.size();i++){

            Contaminant newCont = new Contaminant();
            newCont.setName(ContNames.get(i));
            //System.out.println("\n" + ContNames.get(i));
            //System.out.println(ContInfo.get(contSet));
            newCont.setAverage(ContInfo.get(contSet++));
            //System.out.println(ContInfo.get(contSet));
            newCont.setMax(ContInfo.get(contSet++));
            //System.out.println(ContInfo.get(contSet));
            newCont.setHealthLimit(ContInfo.get(contSet++));
            //System.out.println(ContInfo.get(contSet));
            newCont.setLegalLimit(ContInfo.get(contSet++));
            sys.addContaminant(newCont);

        }






    }

    private boolean toXML(){


            try {



                File file = new File("NYTimes_TexasWater.xml");

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("<water_pollution>\n");
                bw.write("\t"+"<locations>\n");
                bw.write("\t"+"\t"+"<state>\n");
                bw.write("\t"+"\t"+"<state_name>TX</state_name>\n");
                //loop
                for(int i =0; i < Names.size();i++){

                ArrayList<WaterSystem> CS = new ArrayList(); //County water systems

                for(int x =0; x < WaterSystems.size();x++){
                   if(WaterSystems.get(x).getParentCounty().equals(Names.get(i))){
                    CS.add(WaterSystems.get(x));
                   }

                } //sorting counties

                bw.write("\t"+"\t"+"\t"+"<county>\n");
                bw.write("\t"+"\t"+"\t"+"<county_name>"+Names.get(i)+"</county_name>\n");

                    for(int x =0; x<CS.size();x++){

                        bw.write("\t"+"\t"+"\t"+"\t"+"<water_system>\n");
                        bw.write("\t"+"\t"+"\t"+"\t"+"<system_name>"+CS.get(x).getWaterSystemName()+"</system_name>\n");

                        for(int y=0; y<CS.get(x).getChemicals().size(); y++){
                            Contaminant c = CS.get(x).getChemicals().get(y);

                        bw.write("\t"+"\t"+"\t"+"\t"+"\t"+"<contaminant>\n");
                        bw.write("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"<contaminant_name>"+c.getName()+"</contaminant_name>\n");
                        bw.write("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"<average>"+c.getAverage()+"</average>\n");
                        bw.write("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"<max>"+c.getMax()+"</max>\n");
                        bw.write("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"<health_limit>"+c.getHealthLimit()+"</health_limit>\n");
                        bw.write("\t"+"\t"+"\t"+"\t"+"\t"+"\t"+"<legal_limit>"+c.getLegalLimit()+"</legal_limit>\n");
                        bw.write("\t"+"\t"+"\t"+"\t"+"\t"+"</contaminant>\n");
                        }//looping chemicals in system

                        bw.write("\t"+"\t"+"\t"+"\t"+"</water_system>\n");
                    }//looping systems in county

                bw.write("\t"+"\t"+"\t"+"</county>\n");
                } //county loop



                bw.write("\t"+"\t"+"</state>\n");
                bw.write("\t"+"</locations>\n");
                bw.write("</water_pollution>");




                bw.close();
                System.out.println("XML complete");

            } catch (IOException e) {
                e.printStackTrace();
            }





        return true;
    }

}
