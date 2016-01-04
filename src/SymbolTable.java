public interface SymbolTable<K extends Comparable<K>,V> {
	void insert(K key, V val);
	V search(K key);
	V remove(K key);
}