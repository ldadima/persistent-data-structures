package persistent_collections;

import base_structure.PersistentBTree;
import base_structure.PersistentTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

//todo
public class PersistentMap<K, V> extends PersistentCollection<K, V> implements Iterable<Map.Entry<K, V>> {

    public boolean containsKey(K key) {
        if (versions.isEmpty()) {
            return false;
        }
        return versions.getFirst().tree.containsKey(key);
    }

    public boolean containsValue(V value) {
        if (versions.isEmpty()) {
            return false;
        }
        return versions.getFirst().tree.containsValue(value);
    }

    public V get(K key) {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.getFirst().tree.get(key);
    }

    public V put(K key, V value) {
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        V v = currVersion.get(key);
        addNewVersion(currVersion.put(key, value));
        addVersionForParent();
        if (value instanceof PersistentCollection) {
            addCollectionValue((PersistentCollection<?, ?>) value);
        }
        return v;
    }

    public void remove(K key) {
        if (versions.isEmpty()) {
            return;
        }
        addNewVersion(versions.getFirst().tree.remove(key));
        addVersionForParent();
    }

    public void putAll(PersistentMap<? extends K, ? extends V> m) {
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        List<PersistentCollection<?, ?>> collectionList = new ArrayList<>();
        for (Map.Entry<? extends K, ? extends V> entry : m) {
            currVersion = currVersion.put(entry.getKey(), entry.getValue());
            if (entry.getValue() instanceof PersistentCollection) {
                collectionList.add((PersistentCollection<?, ?>) entry.getValue());
            }
        }
        addNewVersion(currVersion);
        addVersionForParent();
        for (PersistentCollection<?, ?> v : collectionList) {
            addCollectionValue(v);
        }
    }

    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        for (Map.Entry<K, V> e : currVersion) {
            set.add(e.getKey());
        }
        return set;
    }

    public Collection<V> values() {
        Collection<V> list = new ArrayList<>();
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        for (Map.Entry<K, V> e : currVersion) {
            list.add(e.getValue());
        }
        return list;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = new HashSet<>();
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        for (Map.Entry<K, V> e : currVersion) {
            set.add(e);
        }
        return set;
    }

    public V getOrDefault(K key, V defaultValue) {
        V v;
        return (((v = get(key)) != null) || containsKey(key))
                ? v
                : defaultValue;
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        for (Map.Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }

            // ise thrown from function is not a cme.
            v = function.apply(k, v);

            try {
                currVersion = currVersion.put(entry.getKey(), v);
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
        }
        addNewVersion(currVersion);
        addVersionForParent();
    }

    public V putIfAbsent(K key, V value) {
        V v = get(key);
        if (v == null) {
            v = put(key, value);
        }

        return v;
    }

    public boolean remove(K key, V value) {
        V curValue = get(key);
        if (!Objects.equals(curValue, value) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        remove(key);
        return true;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        V curValue = get(key);
        if (!Objects.equals(curValue, oldValue) ||
                (curValue == null && !containsKey(key))) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    public V replace(K key, V value) {
        V curValue;
        if (((curValue = get(key)) != null) || containsKey(key)) {
            curValue = put(key, value);
        }
        return curValue;
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            } else {
                remove(key);
                return null;
            }
        } else {
            return null;
        }
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);

        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            // delete mapping
            if (oldValue != null || containsKey(key)) {
                // something to remove
                remove(key);
            }

            return null;
        } else {
            // add or replace old mapping
            put(key, newValue);
            return newValue;
        }
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key);
        V newValue = (oldValue == null) ? value :
                remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            remove(key);
        } else {
            put(key, newValue);
        }
        return newValue;
    }

    @Override
    public int hashCode() {
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        return currVersion.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistentMap)) return false;

        try {
            PersistentMap<?, ?> m = (PersistentMap<?, ?>) o;
            if (m.size() != size())
                return false;

            Set<Map.Entry<K, V>> set1 = entrySet();
            Set<?> set2 = m.entrySet();
            return set1.containsAll(set2);
        } catch (ClassCastException unused) {
            return false;
        }
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        return currVersion.iterator();
    }
}
