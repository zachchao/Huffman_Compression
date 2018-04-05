import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TextFileGenerator {
    private final String USER_AGENT = "Mozilla/5.0";

    /**
     * A getter of raw html from a url, outputting to a file
     * @param url The url to fetch
     * @param fileName The file we want to output into
     * @throws IOException
     */
    public void get(String url, String fileName) throws IOException {
        URL http = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) http.openConnection();

        // optional default is GET
        conn.setRequestMethod("GET");
        //add request header
        conn.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        //Only execute if we had a 200 response code
        if (responseCode == 200) {
            try {
                //Output stream for the file
                OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8");
                Writer writer = new BufferedWriter(outputStream);
                //Input stream from the webpage
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                //Write to the file
                while ((inputLine = in.readLine()) != null) {
                    writer.write(cleanString(inputLine));
                }
                //Close everything
                writer.close();
                outputStream.close();
                in.close();
                System.out.println("Copied to file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Invalid response code.");
        }
        conn.disconnect();
    }

    /**
     * Take in an unParsed String and clean it, only leaving characters
     * that are in the whiteList
     * @param unParsed The unparsedLine
     * @return The cleaned line
     */

    public String cleanString(String unParsed){
        StringBuilder returnString = new StringBuilder();
        char[] charRay = unParsed.toCharArray();
        // Loop through each and if it is within the whitelist
        // we must add it to the StringBuilder
        for(char c : charRay){
            //If its ascii is within the whiteList
            int ascii = (int) c;
            if(withinWhiteList(ascii)){
                returnString.append(c);
            }
        }
        return returnString.toString();
    }

    /**
     * Check to see if the ascii value is within our whiteList
     * @param ascii The ascii of the character we are checking
     * @return True if it is within the whitelist, false if not
     */
    public boolean withinWhiteList(int ascii){
        // We want to include the following characters with their ascii values.
        // Space - 32
        // Exclam - 33
        // Period - 46
        // Digits - 48-57
        // Question - 63
        // Capitals - 65 - 90
        // Lowercase - 97 - 122

        int[] induvidualAsciiValues = {32,33,46,63};
        // If it is a space, exclamation point, period or question mark
        for(int i : induvidualAsciiValues){
            if(ascii == i){
                return true;
            }
        }
        //If it is a digit, captial letter or lower case letter
        return ((ascii >= 48 && ascii <= 57) || (ascii >= 65 && ascii <= 90)
                || (ascii >= 97 && ascii <= 122));
    }
}