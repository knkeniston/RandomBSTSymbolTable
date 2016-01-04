import java.util.Random;
import java.util.Stack;
import java.util.Vector;

/**
 * 
 * @author Kelly Keniston
 * kelly.keniston38@gmail.com
 * CS 223 WSUV - Wayne Cochran
 * 
 * This class is a random binary search tree symbol table.
 * It takes in nodes and randomly inserts them for a higher probability of a balanced tree.
 * Also implements an insert, remove, search, and serialize methods.
 * 
 */
public class RandomBSTSymbolTable<K extends Comparable<K>, V> implements SymbolTable<K, V> {
	
	private class Node {
		K key; V val;
		Node left, right;
		int N; // N = cached size of tree (1 for leaf)
		Node (K k, V v) {
			key = k; val = v;
			left = right = null;
			N = 0;
		}
	}
	
	private Node root;
	private static Random rng = new Random(1234);
	
	public RandomBSTSymbolTable() {
		root = null;
	}

	public void insert(K key, V val) {
		root = insertRandom(root, key, val);
	}    

	private Node insertRandom(Node tree, K key, V val) {
		if (tree == null) {
			return new Node(key, val); // N = 1 (set in constructor)
		}
		if (rng.nextDouble()*(tree.N+1) < 1.0) {// with probability 1/(N+1)
			return insertRoot(tree, key, val); // insert new node at root
		}
		int cmp = key.compareTo(tree.key); // else recurse down tree
		if (cmp == 0) {
			tree.key = key; tree.val = val; // replace payload (N unchanged)
		} else if (cmp < 0) {
			tree.left = insertRandom(tree.left, key, val); // insert into left tree
			tree.N = 1 + tree.left.N + size(tree.right); // update N
		} else {
			tree.right = insertRandom(tree.right, key, val); // insert into right tree
			tree.N = 1 + size(tree.left) + tree.right.N; // update N
		}
		return tree;
	}
	
	private Node insertRoot(Node tree, K key, V val) {
		return insertRootAux(tree, key, val);
	}
	
	private Node insertRootAux(Node tree, K key, V val) {
		if (tree == null) {
			return new Node(key, val);
		}
		int cmp = key.compareTo(tree.key);
		if (cmp == 0) {
			tree.key = key;
			tree.val = val;
		} else if (cmp < 0) {
			tree.left = insertRootAux(tree.left, key, val);
			tree = rotateRight(tree);
			tree.N = 1 + size(tree.left) + size(tree.right);
		} else {
			tree.right = insertRootAux(tree.right, key, val);
			tree = rotateLeft(tree);
			tree.N = 1 + size(tree.left) + size(tree.right);
		}
		return tree;
	}

	public V search(K key) {
		return searchAux(root, key);
	}
	
	private V searchAux(Node tree, K key) {
		if (tree == null)
			return null;
		int cmp = key.compareTo(tree.key);
		if (cmp == 0)
			return tree.val;
		return (cmp < 0) ? searchAux(tree.left, key) : searchAux(tree.right, key);
	}

	public V remove(K key) {
		Node wacked = new Node(null, null);
		root = removeAux(root, key, wacked);
		return wacked.val;
	}
	
	private Node removeAux(Node tree, K key, Node wacked) {
		if (tree == null)
			return null;
		int cmp = key.compareTo(tree.key);
		if (cmp == 0) {
			wacked.key = tree.key;
			wacked.val = tree.val;
			return join(tree.right, tree.left);
		} else if (cmp < 0) {
			tree.left = removeAux(tree.left, key, wacked);
		} else {
			tree.right = removeAux(tree.right, key, wacked);
		}
		return tree;
	}
	
	private Node join(Node X, Node Y) {
		if (X == null) return Y;
		if (Y == null) return X;
		if (rng.nextDouble()*(X.N + Y.N) < X.N) { // flip a weighted coin
			X.right = join(X.right, Y);
			X.N = 1 + size(X.left) + size(X.right);
			return X;
		} else {
			Y.left = join(X, Y.left);
			Y.N = 1 + size(Y.left) + size(Y.right);
			return Y;
		}
	}	
	
	private Node rotateRight(Node n) {
		assert n != null;
		assert n.left != null;
		Node tree = n.left;
		n.left = tree.right;
		tree.right = n;
		tree.N = 1 + size(tree.left) + size(tree.right);
		return tree;
	}

	private Node rotateLeft(Node n) {
		assert n != null;
		assert n.right != null;
		Node tree = n.right;
		n.right = tree.left;
		tree.left = n;
		tree.N = 1 + size(tree.left) + size(tree.right);
		return tree;
	}
	
	private int size(Node tree) {
		return (tree == null) ? 0 : tree.N;
	}	
	
	public Vector<String> serialize() {
		Vector<String> vec = new Vector<String>();
		serializeAux(root, vec);
		return vec;
	}
	
	private void serializeAux(Node tree, Vector<String> vec) {
		if (tree == null) {
			vec.addElement(null);
		} else {
			vec.addElement(tree.key.toString() + ":black");
			serializeAux(tree.left, vec);
			serializeAux(tree.right, vec);
		}
	}
}