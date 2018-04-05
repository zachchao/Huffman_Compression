import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Driver {
    public static void main(String[] args) throws Exception {
        HashMap<Character, String> encoding = new HashMap<Character, String>();
        HashMap<Character, Integer> counts = new HashMap<Character, Integer>();
        int originalFileLength = 0, encodedFileLength = 0;
        String fileName = "testFile.txt";
        String encodedFileName = "testOutFile.txt";

        TextFileGenerator http = new TextFileGenerator();
        http.get("https://en.wikipedia.org/wiki/Rectifier_(neural_networks)", fileName);

        HuffmanTree huff = new HuffmanTree();

        //Go through the file and count the characters to get the weights
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                char[] lineChars = line.toCharArray();
                for(char c : lineChars){
                    //Add one to the count so we know how many characters long the original file is
                    originalFileLength += 1;
                    if(counts.get(c) != null) {
                        int currentWeight = counts.get(c);
                        counts.put(c, currentWeight + 1);
                    }else{
                        counts.put(c, 1);
                    }
                }
            }
            // We are now done looping through each line and counting characters
            // So we can multiply by sixteen to get the bit size
            originalFileLength *= 16;
        }

        System.out.println("Character count map :" + counts);

        //Create our huffData objects
        HuffmanTree.HuffData huffData[] = new HuffmanTree.HuffData[counts.size()];
        int i = 0;
        for(Map.Entry<Character, Integer> entry : counts.entrySet()){
            huffData[i++] = new HuffmanTree.HuffData(entry.getValue(), entry.getKey());
        }
        //Build the tree
        huff.buildTree(huffData);

        //Get the encoding
        encoding = huff.getEncoding();
        System.out.println("Encoding map :" + encoding);

        //Use the encoding the make a new encoded file
        OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(encodedFileName), "utf-8");
        Writer writer = new BufferedWriter(outputStream);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                char[] lineChars = line.toCharArray();
                for(char c : lineChars){
                    writer.write(encoding.get(c));
                    //Add the length of the encoding we are adding
                    encodedFileLength += String.valueOf(encoding.get(c)).length();
                }
            }
            //Close everything
            writer.close();
            outputStream.close();
            System.out.println("Copied to file");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Original File was " + originalFileLength + " bits.");
        System.out.println("Encoded File was " + encodedFileLength + " bits");
    }
}
