package persistent_collections;

import base_structure.PersistentBTree;
import base_structure.PersistentTree;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//todo
public class PersistentArray<V> extends PersistentCollection<Integer, V> implements Iterable<V> {
    private int currIndex = 0;

    public boolean contains(V value) {
        if (versions.isEmpty()) {
            return false;
        }
        return versions.getFirst().tree.containsValue(value);
    }

    public int indexOf(V value) {
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            return -1;
        } else {
            currVersion = versions.getFirst().tree;
        }
        for (Map.Entry<Integer, V> e : currVersion) {
            if (e.getValue().equals(value)) {
                return e.getKey();
            }
        }
        return -1;
    }

    public int indexOfRange(V value, int start, int end) {
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            return -1;
        } else {
            currVersion = versions.getFirst().tree;
        }
        for (Map.Entry<Integer, V> e : currVersion) {
            if (e.getKey() < start) continue;
            if (e.getKey() >= end) break;
            if (e.getValue().equals(value)) {
                return e.getKey();
            }
        }
        return -1;
    }

    public V get(Integer index) {
        if (versions.isEmpty()) {
            return null;
        }
        return versions.getFirst().tree.get(index);
    }

    public V set(int index, V element) {
        Objects.checkIndex(index, size());
        if (versions.isEmpty()) {
            return null;
        }
        V old = get(index);
        addNewVersion(versions.getFirst().tree.put(index, element));
        addVersionForParent();
        if (element instanceof PersistentCollection) {
            addCollectionValue((PersistentCollection<?, ?>) element);
        }
        return old;
    }

    public void add(V element) {
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        addNewVersion(currVersion.put(currIndex++, element));
        addVersionForParent();
        if (element instanceof PersistentCollection) {
            addCollectionValue((PersistentCollection<?, ?>) element);
        }
    }

    public void add(int index, V element) {
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        for (int i = currIndex; i > index; i--) {
            V v = currVersion.get(i - 1);
            currVersion = currVersion.put(i, v);
        }
        addNewVersion(currVersion.put(index, element));
        currIndex++;
        addVersionForParent();
        if (element instanceof PersistentCollection) {
            addCollectionValue((PersistentCollection<?, ?>) element);
        }
    }

    public V remove(int index) {
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            return null;
        } else {
            currVersion = versions.getFirst().tree;
        }
        V old = get(index);
        for (int i = index; i < currIndex - 1; i++) {
            V v = currVersion.get(i + 1);
            currVersion = currVersion.put(i, v);
        }
        addNewVersion(currVersion);
        currIndex--;
        return old;
    }

    public boolean remove(V value) {
        for (int i = 0; i < currIndex; i++) {
            if (get(i).equals(value)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean addAll(PersistentArray<? extends V> array) {
        if (array.size() == 0) {
            return false;
        }
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        List<PersistentCollection<?, ?>> collectionList = new ArrayList<>();
        for (V v : array) {
            currVersion = currVersion.put(currIndex++, v);
            if (v instanceof PersistentCollection) {
                collectionList.add((PersistentCollection<?, ?>) v);
            }
        }
        addNewVersion(currVersion);
        addVersionForParent();
        for (PersistentCollection<?, ?> v : collectionList) {
            addCollectionValue(v);
        }
        return true;
    }

    public boolean removeAll(PersistentArray<V> array) {
        return batchRemove(array, false);
    }

    public boolean retainsAll(PersistentArray<V> array) {
        return batchRemove(array, true);
    }

    private boolean batchRemove(PersistentArray<V> array, boolean contains) {
        if (isEmpty()) {
            return false;
        }
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        int removed = 0;
        for (int i = 0; i < currIndex; i++) {
            V v = currVersion.get(i);
            if (array.contains(v) == contains) {
                removed--;
            } else {
                if (removed > 0) {
                    currVersion = currVersion.put(i - removed, v);
                }
            }
        }
        for (int i = currIndex - 1; i > currIndex - removed - 1; i--) {
            currVersion = currVersion.remove(i);
        }
        return true;
    }

    public void forEach(Consumer<? super V> action) {
        Objects.requireNonNull(action);
        for (V v : this) {
            action.accept(v);
        }
    }

    @Override
    public int hashCode() {
        PersistentTree<Integer, V> currVersion;
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
        if (!(o instanceof PersistentArray)) return false;
        PersistentArray<?> m = (PersistentArray<?>) o;
        if (m.size() != size())
            return false;

        PersistentTree<Integer, V> currVersion;
        PersistentTree<Integer, ?> currVersionM;
        if (versions.isEmpty()) {
            return m.versions.isEmpty();
        } else {
            currVersion = versions.getFirst().tree;
            currVersionM = m.versions.getFirst().tree;
        }
        return currVersion.equals(currVersionM);
    }

    @Override
    public Iterator<V> iterator() {
        PersistentTree<Integer, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        return new Iterator<>() {
            private final Iterator<Map.Entry<Integer, V>> innerIterator = currVersion.iterator();
            @Override
            public boolean hasNext() {
                return innerIterator.hasNext();
            }

            @Override
            public V next() {
                return innerIterator.next().getValue();
            }
        };
    }
}
