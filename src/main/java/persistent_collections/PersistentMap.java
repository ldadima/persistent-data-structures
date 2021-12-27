package persistent_collections;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

//todo
public class PersistentMap<K, V> extends PersistentCollection<K, V> {

    public int size() {
        return versions.getLast().size();
    }

    public boolean isEmpty() {
        return versions.getLast().isEmpty();
    }

    public boolean containsKey(K key) {
        return versions.getLast().containsKey(key);
    }

    public boolean containsValue(V value) {
        return versions.getLast().containsValue(value);
    }

    public V get(K key) {
        return versions.getLast().get(key);
    }

    public void put(K key, V value) {
        versions.push(versions.getLast().put(key, value));
    }

    public void remove(K key) {
        versions.push(versions.getLast().remove(key));
    }

    public void putAll(PersistentMap<? extends K, ? extends V> m) {
        // for (Map.Entry<K, V> entry : m) {
        //
        // }
    }

    public void clear() {

    }

    public Set<K> keySet() {
        return null;
    }

    public Collection<V> values() {
        return null;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    public V getOrDefault(K key, V defaultValue) {
        return null;
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {

    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {

    }

    public V putIfAbsent(K key, V value) {
        return null;
    }

    public boolean remove(Object key, Object value) {
        return false;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    public V replace(K key, V value) {
        return null;
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return null;
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return null;
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return null;
    }
}
