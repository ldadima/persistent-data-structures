package base_structure;

import java.util.Iterator;
import java.util.Map;

public interface PersistentTree<K, V> extends Iterable<Map.Entry<K, V>>{

    PersistentTree<K, V> put(K key, V value);

    PersistentTree<K, V> remove(K key);

    V get(K key);

    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    boolean containsValue(V value);

    Iterator<Map.Entry<K, V>> iterator();
}
