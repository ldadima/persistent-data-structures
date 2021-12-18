package base_structure;

public interface PersistentTree<K, V> {

    PersistentTree<K, V> put(K key, V value);

    PersistentTree<K, V> remove(K key);

    V get(K key);

    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    boolean containsValue(V value);
}
