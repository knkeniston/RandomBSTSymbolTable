import java.util.Vector;
import java.io.*;

public class RandomBSTSymbolTableTest {
        
    private static class Node {
        public String key;
        public Node left, right;
        public Node(String k) {key = k; left = right = null;}
    }
        
    private static class Scanner {
        private int next;
        private Vector<String> vec;
        public Scanner(Vector<String> serializedTree) {
            next = 0;
            vec = serializedTree;
        }
        public boolean hasNext() {return next < vec.size();}
        public String next() {return vec.elementAt(next++);}
    }
        
    private static Node buildTree(Scanner scanner) {
        if (scanner.hasNext()) {
            String str = scanner.next();
            if (str == null)
                return null;
            String[] a = str.split(":"); // a[1] is color if it exists
            Node n = new Node(a[0]);
            n.left = buildTree(scanner);
            n.right = buildTree(scanner);
            return n;
        }
        return null;
    }
        
    private static boolean isBST(Node tree, String min, String max) {
        if (tree == null) return true;
        if (min != null && min.compareTo(tree.key) > 0)
            return false;
        if (max != null && max.compareTo(tree.key) < 0)
            return false;
        return isBST(tree.left, min, tree.key) && 
            isBST(tree.right, tree.key, max);
    }
        
    private static int treeSize(Node tree) {
        return tree == null ? 0 : 1 + treeSize(tree.left) + treeSize(tree.right);
    }
        
    private static int treeHeight(Node tree) {
        return tree == null ? 
            -1 : 1 + Math.max(treeHeight(tree.left), treeHeight(tree.right));
    }
        
    private static boolean isBST(Node tree) {
        return isBST(tree, null, null);
    }
        
    private static boolean treesEqual(Node A, Node B) {
        if (A == null) return B == null;
        if (B == null) return false;
        if (!A.key.equals(B.key)) return false;
        return treesEqual(A.left, B.left) && treesEqual(A.right, B.right);
    }
        
    private Node reconstructTree(RandomBSTSymbolTable<?,?> symtab) {
        Vector<String> serializedTree = symtab.serialize();
        Scanner scanner = new Scanner(serializedTree);
        Node tree = buildTree(scanner);
        return tree;
    }

    public void testSevenStrings() {
        final String strings[] = {
            "now", "is", "the", "winter", "of", "our", "discontent"
        };
        RandomBSTSymbolTable<String,String> symtab = new RandomBSTSymbolTable<>();
        for (int i = 0; i < strings.length; i++)
            symtab.insert(strings[i], strings[i]);
        Node tree = reconstructTree(symtab);
        assertTrue("testSevenStrings", "tree must be BST", isBST(tree));
        int N = treeSize(tree);
        assertEquals("testSevenStrings", "tree must have 7 nodes", N, 7);
    }
    
    public int runCount = 0;
    public int failCount = 0;
        
    private void assertEquals(String test, String string, int n, int i) {
    	runCount++;
    	if (n != i) {
    		failCount++;
    		System.err.println(test + " : " + string + "(" + n + " != " + i + ")");
    	}
    }

    private void assertTrue(String test, String string, boolean bst) {
    	runCount++;
    	if (!bst) {
    		failCount++;
    		System.err.println(test + " : " + string);
    	}
    }

    private void fail(String message) {
    	System.err.println("FAIL!!!!!");
    }


	public void test32StringsWithDuplicateKeys() {
        final String strings[] = {
            "now", "is", "the", "winter", "of", "our", "discontent",
            "made", "glorious", "summer", "by", "this", "sun", "of", "york",
            "and", "all", "the", "clouds", "that", "lourd", "upon", "our",
            "house", "in", "the", "deep", "bosom", "of", "the", "ocean", "buried"
        };
        RandomBSTSymbolTable<String,String> symtab = new RandomBSTSymbolTable<>();
        for (int i = 0; i < strings.length; i++)
            symtab.insert(strings[i], strings[i]);
        Node tree = reconstructTree(symtab);
        assertTrue("test32StringsWithDuplicateKeys", "tree must be BST", isBST(tree));
        int N = treeSize(tree);
        assertEquals("test32StringsWithDuplicateKeys", "tree must have 26 unique nodes", N, 26);
    }
        
    public void testForVariety() {
        final String strings[] = {
            "now", "are", "our", "brows", "bound", "with", "victorious", "wreaths",
            "our", "bruised", "arms", "hung", "up", "for", "monuments",
            "our", "stern", "alarums", "changed", "to", "merry", "meetings",
            "our", "dreadful", "marches", "to", "delightful", "measures",
            "grim", "visaged", "war", "hath", "smoothd", "his", "wrinkled", "front",
            "and", "now", "instead", "of", "mounting", "barded", "steeds",
            "to", "fright", "the", "souls", "of", "fearful", "adversaries",
            "he", "capers", "nimbly", "in", "a", "lady", "chamber",
            "to", "the", "lascivious", "pleasing", "of", "a", "lute"
        };
        RandomBSTSymbolTable<String,String> symtab = new RandomBSTSymbolTable<>();
        for (int i = 0; i < strings.length; i++)
            symtab.insert(strings[i], strings[i]);
        Node tree = reconstructTree(symtab);
        final int numTrees = 4;
        boolean different = false;
        for (int n = 0; n < numTrees; n++) {
            RandomBSTSymbolTable<String,String> symtab2 = new RandomBSTSymbolTable<>();
            for (int i = 0; i < strings.length; i++)
                symtab2.insert(strings[i], strings[i]);
            Node tree2 = reconstructTree(symtab2);
            if (!treesEqual(tree, tree2)) {
                different = true;
                break;
            }
        }
        assertTrue("testForVariety", "Must be creating different trees", different);
    }

    public void testBalance() {
        RandomBSTSymbolTable<Integer,Integer> symtab = new RandomBSTSymbolTable<>();
        final int numKeys = 2047; // min height = 11
        for (int i = 0; i < numKeys; i++)
            symtab.insert(i, i);
        Node tree = reconstructTree(symtab);
        int H = treeHeight(tree);
        assertTrue("testBalance", "height of tree with 2047 keys should be below 70 (min = 11, max=2046)", 
                   H <= 70); 
    }
         
    public void testSearch() {
        final String strings[] = {
            "but", "i", "that", "am", "not", "shaped", "for", "sportive", "tricks",
            "nor", "made", "to", "court", "an", "amorous", "looking", "glass",
            "i", "that", "am", "rudely", "stamp'd", "and", "want", "love", "majesty",
            "to", "strut", "before", "a", "wanton", "ambling", "nymph",
            "i", "that", "am", "curtail'd", "of", "this", "fair", "proportion",
            "cheated", "of", "feature", "by", "dissembling", "nature",
            "deformed", "unfinish'd", "sent", "before", "my", "time",
            "into", "this", "nymph", "scarce", "half", "made", "up",
            "and", "that", "so", "lamely", "and", "unfashionable",
            "that", "dogs", "bark", "at", "me", "as", "i", "halt", "by", "them",
            "why", "i", "in", "this", "weak", "piping", "time", "of", "peace",
            "have", "no", "delight", "to", "pass", "away", "the", "time",
            "unless", "to", "spy", "my", "shadow", "in", "the", "sun",
            "and", "descant", "on", "mine", "own", "deformity",
            "and", "therefore", "since", "i", "cannot", "prove", "a", "lover",
            "to", "entertain", "these", "fair", "well", "spoken", "days",
            "i", "am", "determined", "to", "prove", "a", "villain",
            "and", "hate", "the", "idle", "pleasures", "of", "these", "days",
            "plots", "have", "i", "laid", "inductions", "dangerous",
            "by", "drunken", "prophecies", "libels", "and", "dreams"
        };
        RandomBSTSymbolTable<String,String> symtab = new RandomBSTSymbolTable<>();
        for (int i = 0; i < strings.length; i++)
            symtab.insert(strings[i], strings[i]);
        final String searchTermsInTable[] = {
            "villain", "shadow", "deformed", "inductions", "weak",
            "drunken", "prophecies", "unfashionable", "sportive", "tricks"
        };
        for (int i = 0; i < searchTermsInTable.length; i++) {
            String str = (String) symtab.search(searchTermsInTable[i]);
            assertTrue("testSearch", "Key " + str + "  should be found", 
                       str != null && str.equals(searchTermsInTable[i]));
        }
        final String searchTermsNotInTable[] = {
            "romeo", "juliet", "hamlet", "othello", "iago"
        };
        for (int i = 0; i < searchTermsNotInTable.length; i++) {
            String str = (String) symtab.search(searchTermsNotInTable[i]);
            assertTrue("testSearch", "Key '" + searchTermsNotInTable[i] + "' should be *not* found", str == null);
        }
    }

    public void testRemove() {
        final String strings[] = {
            "to", "be", "or", "not", "to", "be", "that", "is", "the", "question", 
            "whether","tis", "nobler", "in", "the", "mind", "to", "suffer", "the", "slings", 
            "and","arrows", "of", "outrageous", "fortune", "or", "to", "take", "arms", 
            "against","a", "sea", "of", "troubles", "and", "by", "opposing", "end", "them", 
            "to","die", "to", "sleep", "no", "more", "and", "by", "a", "sleep", 
            "to","say", "we", "end", "the", "heart", "ache", "and", "the", "thousand", "natural", 
            "shocks", "that", "flesh", "is", "heir", "to", "tis", "a", "consummation", "devoutly", 
            "to", "be", "wish'd", "to", "die", "to", "sleep", "to", "sleep", "perchance", 
            "to", "dream", "ay", "there", "the", "rub", "for", "in", "that", "sleep", 
            "of", "death", "what", "dreams", "may", "come", "when", "we", "have", "shuffled", 
            "off", "this", "mortal", "coil", "must", "give", "us", "pause", "there", 
            "the", "respect", "that", "makes", "calamity", "of", "so", "long", "life", "for", 
            "who", "would", "bear", "the", "whips", "and", "scorns", "of", "time", "the", 
            "oppressor", "wrong", "the", "proud", "man", "contumely", "the", "pangs", "of", 
            "despised","love", "the", "law", "delay", "the", "insolence", "of", "office", 
            "and", "the", "spurns", "that", "patient", "merit", "of", "the", "unworthy", 
            "takes", "when", "he", "himself", "might", "his", "quietus", "make", "with", "a", 
            "bare", "bodkin", "who", "would", "fardels", "bear", "to", "grunt", "and", 
            "sweat", "under", "a", "weary", "life", "but", "that", "the", "dread", "of", 
            "something", "after", "death", "the", "undiscover'd", "country", "from", "whose", 
            "bourn", "no", "traveller", "returns", "puzzles", "the", "will", "and", "makes", 
            "us", "rather", "bear", "those", "ills", "we", "have", "than", "fly", "to", 
            "others", "that", "we", "know", "not", "of", "thus", "conscience", "does", "make", 
            "cowards", "of", "us", "all", "and", "thus", "the", "native", "hue", 
            "of", "resolution", "is", "sicklied", "o'er", "with", "the", "pale", "cast", 
            "of", "thought", "and", "enterprises", "of", "great", "pith", "and", "moment", 
            "with", "this", "regard", "their", "currents", "turn", "awry", "and", "lose", "the", 
            "name", "of", "action", "soft", "you", "now", "the", "fair", "ophelia", "nymph", 
            "in", "thy", "orisons", "be", "all", "my", "sins", "remember'd"
        };
        RandomBSTSymbolTable<String,String> symtab = new RandomBSTSymbolTable<>();
        for (int i = 0; i < strings.length; i++)
            symtab.insert(strings[i], strings[i]);
        final String searchTermsInTable[] = {
            "name", "resolution", "traveller", "puzzles", "the", "consummation", "devoutly"
        };
        for (int i = 0; i < searchTermsInTable.length; i++) {
            String str = (String) symtab.search(searchTermsInTable[i]);
            assertTrue("testRemove", "Key '" + str + "'  should be found", 
                       str != null && str.equals(searchTermsInTable[i]));
        }
        for (int i = 0; i < searchTermsInTable.length; i++) {
            String str = (String) symtab.remove(searchTermsInTable[i]);
            assertTrue("testRemove", "Deleted key '" + str + "'  should be returned", 
                        str != null && str.equals(searchTermsInTable[i]));
            str = (String) symtab.search(searchTermsInTable[i]);
            assertTrue("testRemove", "Deleted key '" + searchTermsInTable[i] + "'  should be *not* found", 
                        str == null);
        }
    }

    private static class Counter {
        private int count;
        public Counter() {count=0;}
        public int val() {return count;}
        public void inc() {++count;}
    };

    public void testMobyDick() {
        RandomBSTSymbolTable<String,Counter> symtab = new RandomBSTSymbolTable<>();
        try {
        	BufferedReader in = new BufferedReader
                (new InputStreamReader(new FileInputStream("mobydick-words.txt")));
            String line; 
            while ((line = in.readLine()) != null) {
                Counter counter = (Counter) symtab.search(line);
                if (counter == null) {
                    counter = new Counter();
                    counter.inc();
                    symtab.insert(line, counter);
                } else 
                    counter.inc();
            }
            in.close();
        } catch (Exception e) {
            fail(e.getMessage());
        } 
        final String[] words = {
            "the", "of", "and", "a", "to", "in", "that", "his", "it", "i", "he", "but", 
            "as", "with", "is", "was", "for", "all", "this", "at", "by", "whale", "not", 
            "from", "him", "so", "on", "be", "one", "you", "there", "now", "had", "have", 
            "or", "were", "they", "like", "me", "then"
        };
        final int[] counts  = {
            14175, 6469, 6325, 4629, 4539, 4077, 3041, 2495, 2494, 1980, 1853, 1805, 1720, 
            1692, 1688, 1627, 1593, 1514, 1382, 1304, 1175, 1150, 1141, 1072, 1058, 1053, 
            1040, 1032, 907, 866, 851, 779, 767, 754, 689, 676, 643, 639, 630, 628
        };
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            Counter counter = (Counter) symtab.search(w);
            assertTrue("testMobyDick", "'" + w + "' should have been found!", counter != null);
            assertTrue("testMobyDick", "'" + w + "' should occur " + counts[i] + " times (not " +
                       counter.val() + " times)",
                       counter.val() == counts[i]);
        }
    }


	public static void main(String[] args) {      
		RandomBSTSymbolTableTest test = new RandomBSTSymbolTableTest();
		
		test.testSevenStrings();
		test.test32StringsWithDuplicateKeys();
		test.testForVariety();
		test.testBalance();
		test.testSearch();
		test.testRemove();
		test.testMobyDick();

		int runs = test.runCount;;
		int fails = test.failCount;
		int successes = runs - fails;
		System.out.println(successes + "/" + runs + " succeeded.");
    }
}