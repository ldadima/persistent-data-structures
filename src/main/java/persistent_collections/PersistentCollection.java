package persistent_collections;

import base_structure.PersistentTree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public abstract class PersistentCollection<K, V> {

    private final Deque<PersistentTree<K, V>> versions;

    private final Deque<PersistentTree<K, V>> canceledVersions;

    PersistentCollection() {
        this.versions = new LinkedList<>();
        this.canceledVersions = new LinkedList<>();
    }

    public void redo() {
        PersistentTree<K, V> last = versions.pollLast();
        canceledVersions.addLast(last);
    }

    public void undo() {
        PersistentTree<K, V> last = canceledVersions.pollLast();
        versions.addLast(last);
    }
}
