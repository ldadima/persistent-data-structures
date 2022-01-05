package persistent_collections;

import base_structure.PersistentBTree;
import base_structure.PersistentTree;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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
