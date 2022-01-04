package persistent_collections;

import base_structure.PersistentBTree;
import base_structure.PersistentTree;

import java.util.Deque;
import java.util.LinkedList;

public abstract class PersistentCollection<K, V> {

    protected final Deque<Version> versions;

    protected final Deque<Version> canceledVersions;

    protected PersistentCollection<?, ?> parent;

    PersistentCollection() {
        this.versions = new LinkedList<>();
        this.canceledVersions = new LinkedList<>();
    }

    public boolean undo() {
        Version last = versions.pollFirst();
        if (last == null) {
            return false;
        }
        boolean res = true;
        if (last.child != null) {
            res = last.child.undo();
        }
        if (res) {
            canceledVersions.push(last);
            return true;
        }
        return false;
    }

    public boolean redo() {
        Version last = canceledVersions.pollFirst();
        if (last == null) {
            return false;
        }
        boolean res = true;
        if (last.child != null) {
            res = last.child.redo();
        }
        if (res) {
            versions.push(last);
            return true;
        }
        return false;
    }

    public void clear() {
        versions.clear();
        canceledVersions.clear();
    }

    protected void addNewVersion(PersistentTree<K, V>  newTree) {
        versions.push(new Version(newTree));
        if (canceledVersions.isEmpty()) {
            return;
        }
        canceledVersions.clear();
    }

    protected void addVersionForParent() {
        if (parent != null) {
            parent.addChildVersion(this);
        }
    }

    protected void addCollectionValue(PersistentCollection<?, ?> val) {
        val.setParent(this);
        for(int i = 0; i < val.versions.size(); i++) {
            versions.push(new Version(versions.getFirst().tree, val));
        }
    }

    private void addChildVersion(PersistentCollection<?, ?> child) {
        PersistentTree<K, V> currVersion;
        if (versions.isEmpty()) {
            currVersion = new PersistentBTree<>();
        } else {
            currVersion = versions.getFirst().tree;
        }
        versions.push(new Version(currVersion, child));
    }

    protected void setParent(PersistentCollection<?, ?> parent) {
        this.parent = parent;
    }

    class Version {
        final PersistentTree<K, V> tree;
        final PersistentCollection<?, ?> child;

        public Version (PersistentTree<K, V> tree) {
            this.tree = tree;
            this.child = null;
        }

        public Version (PersistentTree<K, V> tree, PersistentCollection<?, ?> child) {
            this.tree = tree;
            this.child = child;
        }
    }
}
