package com.simplifyqa.codeeditor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplifyqa.abstraction.driver.IQAWebDriver;
import com.simplifyqa.andoidabstract.driver.IQAAndroidDriver;
import com.simplifyqa.pluginbase.argument.IArgument;
import com.simplifyqa.pluginbase.codeeditor.annotations.AutoInjectAndroidDriver;
import com.simplifyqa.pluginbase.codeeditor.annotations.AutoInjectWebDriver;
import com.simplifyqa.pluginbase.codeeditor.annotations.SyncAction;
import com.simplifyqa.pluginbase.common.enums.TechnologyType;
import com.simplifyqa.pluginbase.plugin.annotations.ObjectTemplate;
import java.util.logging.Logger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
 
/**
 * Hello there!!, please keep the following things in mind while creating custom class
 * Your class should have a public default constructor.
 * @SyncAction methods should be public and return a boolean value not void or anything else
 * @AutoInjectWebDriver/ @AutoInjectAndroidDriver /
 * @AutoInjectIOSDriver/ @AutoInjectApiDriver indicates the driver you want to use.
 * uniqueId field in @SyncAction annotation should be unique throughout the project
 */
public class SampleClass
{
    @AutoInjectWebDriver
    private IQAWebDriver driver;
    @AutoInjectAndroidDriver
    private IQAAndroidDriver adriver;
    private static final Logger log = Logger.getLogger(SampleClass.class.getName());
    public SampleClass() {
    }


       @SyncAction(uniqueId = "CCL_GR",groupName = "Generic",objectTemplate = @ObjectTemplate(name = TechnologyType.ANDROID,description = "This action belongs to GENERIC"))
       public Boolean getGuestRequest(IArgument folioNum , IArgument age, IArgument stateRoom,IArgument name,IArgument loyalty){
        String endpointUrl="https://stlatapiuat1.shiptech.carnival.com/css-api/css/dining/getGuest";
        Boolean valid= true;
        String loyaltycolor = "";
        String requestBody = "{\r\n"
        		+ "  \"DeptNum\": 1,\r\n"
        		+ "  \"searchtype\": \"FOLIO\",\r\n"
        		+ "  \"folio\": "+folioNum.getValue().trim()+",\r\n"
        		+ "  \"CabinNumber\": \"\",\r\n"
        		+ "  \"LastName\": \"\",\r\n"
        		+ "  \"BatchNumber\": \"HZ20250104008\",\r\n"
        		+ "  \"Barcode\": \"\"\r\n"
        		+ "}"; 
                
        try {
            //adriver.findElement(FindBy.id("Id")).getAttributeAndStore(ATTRIBUTE.CLASS,folioNum);
            URI uri=new URI(endpointUrl);
            HttpClient client = HttpClient.newHttpClient() ;
            HttpRequest request = HttpRequest.newBuilder(uri).header("Content-Type", "application/json")
            .header("x-deviceid", "1")
            .header("x-accesstoken", "1")
            .header("x-workstationid", "345")
            .header("CrewId", "999200")
            .header("TokenName", "Bearer")
            .header("Token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJDU1MgRXh0ZXJuYWwiLCJuYW1lIjoiQ1NTIEV4dGVybmFsIEFQSSIsInJvbGUiOltdLCJjbGllbnRfaWQiOiJJQk0gV2ViTWV0aG9kIiwiY2xpZW50X3NlY3JldCI6IjE4MEMyMDRDLUEzNkMtNDI4Qy1CNDFBLTlBQjVDOUNDNTNFNCIsImlzcyI6Imh0dHBzOi8vY2Fybml2YWwuYXV0aC5jb20iLCJhdWQiOiJodHRwczovL2Nhcm5pdmFsLmNvbSIsImp0aSI6IkRENUE0M0Y5LTIzREUtNERBOS04NzJFLTQyNjRENDVFRUM4NSIsImlhdCI6MTY3MjU2NjAwNCwibmJmIjoxNjcyNTY2MDA0LCJleHAiOjE3NDkwOTQ5ODN9.-YBeHbTsaklUM1gxEK1_9MHYE1nB5OnOLl4go801uks")
            .POST(BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("the Response of the GetGusteAPi: "+response.body().toString());
            
            String responseBody= response.body().toString();
            log.info("Response Body:" +responseBody);
            
            ObjectMapper objectMapper= new ObjectMapper();

            JsonNode rootNode = objectMapper.readTree(responseBody);

            JsonNode childNode = rootNode.get("cardModels");

            String foliotobeValidated = folioNum.getValue().trim();
            String agetobeValidated = age.getValue().trim();
            String stateroomtobeValidated = stateRoom.getValue().trim();
            String nametobeValidated = name.getValue().trim();
            String loyaltytobeValidated=loyalty.getValue().trim();
            


            if(childNode.size()>0 && childNode.isArray()){

                JsonNode carNode= childNode.get(0);
                String ageNumforValid= String.valueOf(carNode.get("age"));
                ageNumforValid = ageNumforValid.substring(1, ageNumforValid.length() - 1);
              
                if(Integer.parseInt(ageNumforValid)<21){

                 valid= false;
                }

                if(!ageNumforValid.equalsIgnoreCase(agetobeValidated)){
                    valid= false;
                 }
                 log.info("requestBody = "+String.valueOf(carNode.get("cabinNumber")));
                String cabinNumforValid= String.valueOf(carNode.get("cabinNumber"));

                cabinNumforValid = cabinNumforValid.substring(1, cabinNumforValid.length() - 1);
                if(!cabinNumforValid.equalsIgnoreCase(stateroomtobeValidated)){
                   valid= false;
                }
                
                String folioNumforValid= String.valueOf(carNode.get("folio"));
                folioNumforValid = folioNumforValid.substring(1, folioNumforValid.length() - 1);
                
                if (!folioNumforValid.equalsIgnoreCase(foliotobeValidated)){
                    valid= false;
                }

                String firstNameforValid= String.valueOf(carNode.get("firstName"));
                firstNameforValid = firstNameforValid.substring(1, firstNameforValid.length() - 1);               
                String lastNameforValid= String.valueOf(carNode.get("lastName"));
                lastNameforValid = lastNameforValid.substring(1, lastNameforValid.length() - 1);
                String nameforvalid = firstNameforValid+" "+lastNameforValid;

                if (!nameforvalid.equalsIgnoreCase(nametobeValidated)){
                    valid= false;
                }
            
                String loyaltyforValid = String.valueOf(carNode.get("cruiseTierCode"));
                loyaltyforValid = loyaltyforValid.substring(1, loyaltyforValid.length() - 1);
              
                if(loyaltyforValid.contains("00")){
                    loyaltycolor = "Blue";
                }else if(loyaltyforValid.contains("01")){
                    loyaltycolor = "Red";
                }else if(loyaltyforValid.contains("02")){
                    loyaltycolor = "Gold";
                }else if(loyaltyforValid.contains("03")){
                    loyaltycolor = "Platinum";
                }else if(loyaltyforValid.contains("04")){
                    loyaltycolor = "Diamond";
                }

                if (!loyaltycolor.isEmpty()) {
                    if (!loyaltytobeValidated.equalsIgnoreCase(loyaltycolor)) {
                        valid = false;
                    }
                }

            }else 
            {
                valid=false;
            }

            

        }catch(Exception e){
            e.printStackTrace();
            valid=false;
        }
        return valid;
       }

       @SyncAction(uniqueId = "CCL_GR_01",groupName = "Generic",objectTemplate = @ObjectTemplate(name = TechnologyType.ANDROID,description = "This action belongs to GENERIC"))
       public Boolean getpopupvalidation(IArgument guestName , IArgument guestFolio, IArgument gueststateRoom,IArgument guestAge,IArgument orderName , IArgument orderFolio, IArgument orderstateRoom,IArgument orderAge){
        Boolean valid= true;
           
        try {
            
       
            String guestpageName = guestName.getValue().trim();
            String guestpageFolio = guestFolio.getValue().trim();
            String guestpagestateroom = gueststateRoom.getValue().trim();
            String guestpageage = guestAge.getValue().trim();
            String orderpageName = orderName.getValue().trim();
            String orderpageFolio = orderFolio.getValue().trim();
            String[] parts = orderpageFolio.split(" ");
            String finalorderpageFolio = parts[0];
            String orderpagestateroom = orderstateRoom.getValue().trim();
            String orderpageage = orderAge.getValue().trim();

            if (!guestpageName.equalsIgnoreCase(orderpageName)){
                valid= false;
            }
            if (!guestpageFolio.equalsIgnoreCase(finalorderpageFolio)){
                valid= false;
            }if (!guestpagestateroom.equalsIgnoreCase(orderpagestateroom)){
                valid= false;
            }if (!guestpageage.equalsIgnoreCase(orderpageage)){
                valid= false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            valid=false;
        }

    return valid;
        }

@SyncAction(uniqueId = "CCL_GR_02",groupName = "Generic",objectTemplate = @ObjectTemplate(name = TechnologyType.ANDROID,description = "This action belongs to GENERIC"))
       public Boolean comparedataset(IArgument orderCheersvalue , IArgument orderBubblevalue, IArgument orderspadevalue,IArgument orderOffersvalue,IArgument popupCheersvalue , IArgument popupBubblevalue, IArgument popupspadevalue,IArgument popupOffersvalue){
        Boolean valid= true;
           
        try {
            
       
            String cheersvalue = orderCheersvalue.getValue().trim();
            String bublevalue = orderBubblevalue.getValue().trim();
            String spadevalue = orderspadevalue.getValue().trim();
            String offersvalue = orderOffersvalue.getValue().trim();
            String pcheersvalue = popupCheersvalue.getValue().trim();
            String pbublevalue = popupBubblevalue.getValue().trim();
            String pspadevalue = popupspadevalue.getValue().trim();
            String poffersvalue = popupOffersvalue.getValue().trim();

            if (!cheersvalue.equalsIgnoreCase(pcheersvalue)){
                valid= false;
            }
            if (!bublevalue.equalsIgnoreCase(pbublevalue)){
                valid= false;
            }if (!spadevalue.equalsIgnoreCase(pspadevalue)){
                valid= false;
            }if (!offersvalue.equalsIgnoreCase(poffersvalue)){
                valid= false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            valid=false;
        }

    return valid;
        }

        @SyncAction(uniqueId = "CCL_SyncApp",groupName = "Generic",objectTemplate = @ObjectTemplate(name = TechnologyType.ANDROID,description = "This action belongs to Download the DB File"))
        public Boolean downloadDBFile()
    {
        Boolean valid=true;

       
        String fileURL = "https://stlatapiuat1.shiptech.carnival.com/css-api/css/dining/DownloadDBFile/SESPOS_SQLITE";
        String resourcesDir = "C:/Users/SridharanDhamodaran/AppData/Local/Programs/SimplifyQA/dist/agent/workspaces/2/SailorMate Bar Hybrid/src/main/resources/"; 
        String fileName = "SESPOS_SQLITE.db";

        File file = new File(resourcesDir + fileName);

        // Check if file exists and delete if it does
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Existing file deleted successfully.");
            } else {
                System.err.println("Failed to delete the existing file.");
                return false;  // Stop further execution if file deletion fails
            }
        }

 
        try {
            downloadFile(fileURL, resourcesDir, fileName,valid);
            System.out.println("File downloaded successfully!");
        } catch (IOException e) {
            valid=false;
            e.printStackTrace();
        }

        return valid;
    }

    public static void downloadFile(String fileURL, String saveDir, String fileName, Boolean valid) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty("x-deviceid", "1");
        httpConn.setRequestProperty("x-accesstoken", "1");
        httpConn.setRequestProperty("x-workstationid", "345");
        httpConn.setRequestProperty("CrewId", "999200"); 
        int responseCode = httpConn.getResponseCode();       
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
 
         
            FileOutputStream outputStream = new FileOutputStream(saveDir + fileName);
 
            byte[] buffer = new byte[4096];
            int bytesRead;
 
         
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            
            outputStream.close();
            inputStream.close();
 
            System.out.println("File downloaded to: " + saveDir + fileName);
        } else {
            valid=false;
            throw new IOException("Server returned HTTP response code: " + responseCode);
        }
 
       
        httpConn.disconnect();
    }
}

    

    