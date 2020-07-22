/*
Daniel Minami <minamid@sheridancollege.ca>
----------------------------------------------------------------------
 */
package postalcodevalidator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Daniel Minami <minamid@sheridancollege.ca>
 */
public class PostalCodeValidator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String key = "MK52-WR35-FW63-RB98";
        String searchTerm = "1229 Marlborough Court Oakville";
        String lastId = "";
        String searchFor = "";
        String country = "ca";
        String languagePreference = "en";
        Integer maxSuggestions = 1;
        Integer maxResults = 1;
        
        java.util.Hashtable[] rs = new java.util.Hashtable[1];
        
        try {
        rs = AddressComplete_Interactive_Find_v2_10(
                key,
                searchTerm,
                lastId,
                searchFor,
                country,
                languagePreference,
                maxSuggestions,
                maxResults
        );
        } catch (Exception ex) {
            System.out.print(ex);
        }
        String str = rs[0].get("Description").toString();
        System.out.print("Result: " + rs[0].get("Description").toString() + "\n");
        str = str.substring(str.length()-7, str.length());
        
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            System.out.println(matcher.matches());
        }
    }
    
    static public java.util.Hashtable[] AddressComplete_Interactive_Find_v2_10(
            String Key, 
            String SearchTerm, 
            String LastId, 
            String SearchFor, 
            String Country, 
            String LanguagePreference, 
            Integer MaxSuggestions, 
            Integer MaxResults
    ) throws Exception {

        String requestUrl = new String();
        String key = new String();
        String value = new String();

        //Build the url
        requestUrl = "http://ws1.postescanada-canadapost.ca/AddressComplete/Interactive/Find/v2.10/xmla.ws?";
        requestUrl += "&Key=" + java.net.URLEncoder.encode(Key);
        requestUrl += "&SearchTerm=" + java.net.URLEncoder.encode(SearchTerm);
        requestUrl += "&LastId=" + java.net.URLEncoder.encode(LastId);
        requestUrl += "&SearchFor=" + java.net.URLEncoder.encode(SearchFor);
        requestUrl += "&Country=" + java.net.URLEncoder.encode(Country);
        requestUrl += "&LanguagePreference=" + java.net.URLEncoder.encode(LanguagePreference);
        requestUrl += "&MaxSuggestions=" + java.net.URLEncoder.encode(MaxSuggestions.toString());
        requestUrl += "&MaxResults=" + java.net.URLEncoder.encode(MaxResults.toString());

        //Get the data
        java.net.URL url = new java.net.URL(requestUrl);
        java.io.InputStream stream = url.openStream();
        javax.xml.parsers.DocumentBuilder docBuilder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document dataDoc = docBuilder.parse(stream);

        //Get references to the schema and data
        org.w3c.dom.NodeList schemaNodes = dataDoc.getElementsByTagName("Column");
        org.w3c.dom.NodeList dataNotes = dataDoc.getElementsByTagName("Row");

        //Check for an error
        if (schemaNodes.getLength() == 4 && schemaNodes.item(0).getAttributes().getNamedItem("Name").getNodeValue().equals("Error")) {
            throw new Exception(dataNotes.item(0).getAttributes().getNamedItem("Description").getNodeValue());
        };

        //Work though the items in the response
        java.util.Hashtable[] results = new java.util.Hashtable[dataNotes.getLength()];
        for (int rowCounter = 0; rowCounter < dataNotes.getLength(); rowCounter++) {
            java.util.Hashtable rowData = new java.util.Hashtable();
            for (int colCounter = 0; colCounter < schemaNodes.getLength(); colCounter++) {
                key = (String) schemaNodes.item(colCounter).getAttributes().getNamedItem("Name").getNodeValue();
                if (dataNotes.item(rowCounter).getAttributes().getNamedItem(key) == null) {
                    value = "";
                } else {
                    value = (String) dataNotes.item(rowCounter).getAttributes().getNamedItem(key).getNodeValue();
                };
                rowData.put(key, value);
            }
            results[rowCounter] = rowData;
        }
        //Return the results
        return results;
    }
    
}