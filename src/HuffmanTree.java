import java.io.PrintStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class HuffmanTree implements Serializable {
    private BinaryTree<HuffData> huffTree;
    private Character currentChar;
    private HashMap<Character, String> encoding = new HashMap<Character, String>();

    public static class HuffData implements Serializable{
        private double weight;
        private Character symbol;

        public HuffData(double weight, Character symbol){
            this.weight = weight;
            this.symbol = symbol;
        }
    }

    private static class CompareHuffmanTrees implements Comparator<BinaryTree<HuffData>> {
        /** Compare two objects
         * @param treeLeft The left-hand object
         * @param treeRight The right-hand object
         * @return -1 if left less than right,
         *      0 if left equals right
         *      and +1 if left greater than right
         */
        public int compare(BinaryTree<HuffData> treeLeft, BinaryTree<HuffData> treeRight){
            double wLeft = treeLeft.getData().weight;
            double wRight = treeRight.getData().weight;
            return Double.compare(wLeft, wRight);
        }
    }

    /**
     * Builds the Huffman tree using the given alphabet and weights.
     * post: huffTree contains a reference to the Huffman tree
     * @param symbols An array of HuffData objects
     */
    public void buildTree(HuffData[] symbols){
        Queue<BinaryTree<HuffData>> theQueue
                = new PriorityQueue<BinaryTree<HuffData>>
                (symbols.length, new CompareHuffmanTrees());
        // Load the queue with leaves.
        for (HuffData nextSymbol : symbols){
            BinaryTree<HuffData> aBinaryTree =
                    new BinaryTree<HuffData>(nextSymbol, null, null);
            theQueue.offer(aBinaryTree);
        }

        //Build the tree
        while(theQueue.size() > 1){
            BinaryTree<HuffData> left = theQueue.poll();
            BinaryTree<HuffData> right = theQueue.poll();
            double wl = left.getData().weight;
            double wr = right.getData().weight;
            HuffData sum = new HuffData(wl + wr, null);
            BinaryTree<HuffData> newTree =
                    new BinaryTree<HuffData>(sum, left, right);
            theQueue.offer(newTree);
        }

        // The queue should no contain only one item.
        huffTree = theQueue.poll();
    }

    /** Outputs the resulting code.
     * @param code The code up to this node
     * @param tree The current node in the tree
     */
    private void printCode(String code,
                           BinaryTree<HuffData> tree){
        HuffData theData = tree.getData();
        if (theData.symbol != null){
            if(theData.symbol.equals(" ")){
                System.out.println("space: " + code);
            }else{
                System.out.println(theData.symbol + ": " + code);
            }
        }else{
            printCode(code + "0", tree.getLeftSubtree());
            printCode(code + "1", tree.getRightSubtree());
        }
    }

    public void printCode(){
        printCode("", huffTree);
    }

    /**
     * A recursive function to search through the tree and add the encoding to a hashmap
     * @param code The current code to get to that character
     * @param tree The subtree we are in
     */
    private void getEncoding(String code, BinaryTree<HuffData> tree){
        HuffData theData = tree.getData();

        if (theData.symbol != null){
            currentChar = theData.symbol;
            encoding.put(currentChar, code);
        }else{
            getEncoding(code + "0", tree.getLeftSubtree());
            getEncoding(code + "1", tree.getRightSubtree());
        }
    }

    /**
     * A starter function to get the Hashmap which is the encoding for the huffman tree
     * @return The hashmap which represents the encoding, from character to bits
     */
    public HashMap<Character, String> getEncoding(){
        HuffData theData = huffTree.getData();
        currentChar = ' ';
        getEncoding("", huffTree);
        return encoding;
    }

    /** Method to decode a message that is input as a string of
     * digit characters '0' and '1'
     * @param codededMessage The input message as a String of zeroes and ones.
     * @return The decoded message as a String
     */
    public String decode(String codededMessage){
        StringBuilder result = new StringBuilder();
        BinaryTree<HuffData> currentTree = huffTree;
        for(int i = 0; i < codededMessage.length(); i++){
            if(codededMessage.charAt(i) == '1'){
                currentTree = currentTree.getRightSubtree();
            }else{
                currentTree = currentTree.getLeftSubtree();
            }
            if(currentTree.isLeaf()){
                HuffData theData = currentTree.getData();
                result.append(theData.symbol);
                currentTree = huffTree;
            }
        }
        return result.toString();
    }
}
