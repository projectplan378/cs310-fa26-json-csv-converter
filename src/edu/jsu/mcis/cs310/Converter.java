package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.*;
import java.util.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            // Reading CSV
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> rows = reader.readAll();
            
            // JSON arrays
            JsonArray prodNums = new JsonArray();
            JsonArray colHeadings = new JsonArray();
            JsonArray data = new JsonArray();
            
            // Column headings and CSV data rows
            String[] headings = rows.get(0);
            for (String heading : headings) {
                colHeadings.add(heading);
            }
            
            for (int i = 1; i <  rows.size(); i++) {
                String[] row = rows.get(i);
                prodNums.add(row[0]); // prod. number column
                JsonArray episodeData = new JsonArray();
                for (int j = 1; j < row.length; j++) {
                    if (j == 2 || j == 3) {
                        episodeData.add(Integer.parseInt(row[j]));
                    } else {
                        episodeData.add(row[j]);
                    }
                }
                
                data.add(episodeData);
            }
            
            // JSON object
            JsonObject json = new JsonObject();
            json.put("ProdNums", prodNums);
            json.put("ColHeadings", colHeadings);
            json.put("Data", data);
            
            // json-simple
            result = Jsoner.serialize(json);
            reader.close();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            // JSON string conversion
            JsonObject json = Jsoner.deserialize(jsonString, new JsonObject());
            
            // JSON arrays
            JsonArray prodNums = (JsonArray) json.get("ProdNums");
            JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
            JsonArray data = (JsonArray) json.get("Data");
            
            // OpenCSV writer
            StringWriter stringWriter = new StringWriter();
            CSVWriter writer = new CSVWriter(stringWriter);
            
            // Column headings back to string
            String[] headings = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                headings[i] = (String) colHeadings.get(i);
            }
            
            // CSV header
            writer.writeNext(headings);
            
            for (int i = 0; i < data.size(); i++) {
                JsonArray episodeData = (JsonArray) data.get(i);
                String[] row = new String[episodeData.size() + 1];
                row[0] = (String) prodNums.get(i);
                for (int j = 0; j < episodeData.size(); j++) {
                    Object value = episodeData.get(j);
                    if (j == 1) {
                        row[j + 1] = value.toString();
                    } else if (j == 2) {
                        int episode = ((Number) value).intValue();
                        row[j + 1] = String.format("%02d", episode);
                    } else {
                        row[j + 1] = value.toString();
                    }
                }
                
                // Give the row to OpenCSV
                writer.writeNext(row);
            }
            
            // Completed CSV string
            writer.close();
            result = stringWriter.toString();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
