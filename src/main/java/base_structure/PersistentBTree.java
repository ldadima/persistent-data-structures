package base_structure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class PersistentBTree<K, V> implements PersistentTree<K, V> {

    private final int t;

    private final int size;

    private final Comparator<K> keyComparator;

    private final BTreeNode head;

    public PersistentBTree() {
        this.t = 5;
        this.size = 0;
        this.head = null;
        this.keyComparator = null;
    }

    public PersistentBTree(int t) {
        this.t = t;
        this.size = 0;
        this.head = null;
        this.keyComparator = null;
    }

    public PersistentBTree(int t, Comparator<K> comparator) {
        this.t = t;
        this.size = 0;
        this.head = null;
        this.keyComparator = comparator;
    }

    private PersistentBTree(int t, int size, BTreeNode head, Comparator<K> comparator) {
        this.t = t;
        this.size = size;
        this.head = head;
        this.keyComparator = comparator;
    }

    @Override
    public PersistentTree<K, V> put(K key, V value) {
        BTreeEntry entry = new BTreeEntry(key, value);
        if (head == null) {
            BTreeNode newHead = new BTreeNode();
            newHead.entries.add(entry);
            return new PersistentBTree<>(t, size + 1, newHead, keyComparator);
        }

        BTreeNode newHead = new BTreeNode(head);
        BTreeNode curr = newHead;
        List<PathEntry> path = new ArrayList<>();
        path.add(new PathEntry(curr, 0));

        while (curr != null) {
            BTreeNode.GetResult result = curr.getNextNodeByKey(key);
            if (result.isThisEntry()) {
                result.entry.value = value;
                return new PersistentBTree<>(t, size, newHead, keyComparator);
            }
            if (curr.isLeaf()) {
                curr.entries.add(new BTreeEntry(key, value));
            }
            curr = getNextTreeNode(curr, path, result);
        }
        for (int i = path.size() - 1; i >= 0; i--) {
            PathEntry pathEntry = path.get(i);
            BTreeNode right = pathEntry.node;
            if (right.entries.size() == 2 * t - 1) {
                BTreeNode left = new BTreeNode();
                for (int j = 0; j < t - 1; j++) {
                    BTreeEntry f = right.entries.first();
                    right.entries.remove(f);
                    left.entries.add(f);
                }
                if (!right.isLeaf()) {
                    left.nodes = new ArrayList<>(right.nodes.subList(0, t));
                    right.nodes = new ArrayList<>(right.nodes.subList(t, 2 * t));
                }
                BTreeNode parent;
                if (i == 0) {
                    parent = new BTreeNode();
                    parent.nodes.add(right);
                    newHead = parent;
                } else {
                    parent = path.get(i-1).node;
                }
                BTreeEntry f = right.entries.first();
                parent.entries.add(f);
                right.entries.remove(f);
                parent.nodes.add(pathEntry.parentIndex, left);
            }
        }
        return new PersistentBTree<>(t, size + 1, newHead, keyComparator);
    }

    private BTreeNode getNextTreeNode(BTreeNode curr, List<PathEntry> path, BTreeNode.GetResult result) {
        BTreeNode prev;
        prev = curr;
        curr = result.node == null ? null : new BTreeNode(result.node);
        if (curr != null) {
            prev.nodes.set(result.entryIndex, curr);
            path.add(new PathEntry(curr, result.entryIndex));
        }
        return curr;
    }

    // todo
    @Override
    public PersistentTree<K, V> remove(K key) {
        if (head == null) {
            return this;
        }
        BTreeNode newHead = new BTreeNode(head);
        BTreeNode prev;
        BTreeNode curr = newHead;
        List<PathEntry> path = new ArrayList<>();
        path.add(new PathEntry(curr, 0));

        while (curr != null) {
            BTreeNode.GetResult result = curr.getNextNodeByKey(key);
            if (result.isThisEntry()) {
                if (curr.isLeaf()) {
                    curr.entries.remove(result.entry);
                } else {
                    BTreeNode ancestor = curr;
                    curr = new BTreeNode(ancestor.nodes.get(result.entryIndex + 1));
                    path.add(new PathEntry(curr, result.entryIndex));
                    ancestor.nodes.set(result.entryIndex + 1, curr);
                    while (!curr.isLeaf()) {
                        prev = curr;
                        curr = new BTreeNode(prev.nodes.get(0));
                        prev.nodes.set(0, curr);
                        path.add(new PathEntry(curr, 0));
                    }
                    ancestor.entries.remove(result.entry);
                    BTreeEntry f = curr.entries.first();
                    ancestor.entries.add(f);
                    curr.entries.remove(f);
                    break;
                }
            }
            curr = getNextTreeNode(curr, path, result);
        }

        if (curr == null) {
            return this;
        }

        BTreeNode currNode = path.get(path.size() - 1).node;
        for (int i = path.size() - 1; i > 0; i--) {
            if (currNode.entries.size() < t - 1) {
                int index = path.get(i).parentIndex;
                BTreeNode parent = path.get(i - 1).node;
                Iterator<BTreeEntry> iterator = parent.entries.iterator();
                BTreeEntry separator = iterator.next();
                for (int j = 0; j < index; j++) {
                    separator = iterator.next();
                }
                if (index != 0) {
                    BTreeNode leftBro = parent.nodes.get(index - 1);
                    if (leftBro.entries.size() > t - 1) {
                        parent.entries.remove(separator);
                        BTreeEntry l = leftBro.entries.last();
                        leftBro.entries.remove(l);
                        parent.entries.add(l);
                        currNode.entries.add(separator);
                        return new PersistentBTree<>(t, size - 1, newHead, keyComparator);
                    }
                }

                if (index + 1 != parent.nodes.size()) {
                    BTreeNode rightBro = parent.nodes.get(index + 1);
                    if (rightBro.entries.size() > t - 1) {
                        parent.entries.remove(separator);
                        BTreeEntry f = rightBro.entries.first();
                        rightBro.entries.remove(f);
                        parent.entries.add(f);
                        currNode.entries.add(separator);
                        return new PersistentBTree<>(t, size - 1, newHead, keyComparator);
                    }
                    currNode.entries.add(separator);
                    parent.entries.remove(separator);
                    currNode.entries.addAll(rightBro.entries);
                    currNode.nodes.addAll(rightBro.nodes);
                    parent.nodes.remove(index + 1);
                }

                if (index != 0 && index + 1 == parent.nodes.size()) {
                    BTreeNode leftBro = parent.nodes.get(index - 1);
                    leftBro.entries.add(separator);
                    parent.entries.remove(separator);
                    leftBro.entries.addAll(currNode.entries);
                    leftBro.nodes.addAll(currNode.nodes);
                    parent.nodes.remove(index);
                }

                currNode = parent;
            } else {
                break;
            }
        }

        if (newHead.entries.isEmpty()) {
            newHead = newHead.nodes.get(0);
        }

        return new PersistentBTree<>(t, size - 1, newHead, keyComparator);
    }

    @Override
    public V get(K key) {
        BTreeNode curr = head;
        while (curr != null) {
            BTreeNode.GetResult result = curr.getNextNodeByKey(key);
            if (result.isThisEntry()) {
                return result.entry.value;
            }
            curr = result.node;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) == null;
    }

    // todo
    @Override
    public boolean containsValue(V value) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public final int compare(Object k1, Object k2) {
        return keyComparator == null ? ((Comparable<? super K>) k1).compareTo((K) k2)
                : keyComparator.compare((K) k1, (K) k2);
    }

    private class PathEntry {
        BTreeNode node;
        int parentIndex;

        public PathEntry(BTreeNode node, int parentIndex) {
            this.node = node;
            this.parentIndex = parentIndex;
        }
    }

    private class BTreeNode {
        SortedSet<BTreeEntry> entries;
        List<BTreeNode> nodes;

        public BTreeNode() {
            entries = new TreeSet<>();
            nodes = new ArrayList<>();
        }

        public BTreeNode(BTreeNode node) {
            this.entries = node.entries;
            this.nodes = node.nodes;
        }

        public boolean isLeaf() {
            return nodes.isEmpty();
        }

        public GetResult getNextNodeByKey(K key) {
            int cur = 0;
            for (BTreeEntry entry : entries) {
                int c = compare(entry.key, key);
                if (c == 0) {
                    return new GetResult(entry, null, cur);
                }
                if (c > 0) {
                    break;
                } else {
                    cur++;
                }
            }
            if (this.isLeaf()) {
                return new GetResult(null, null, cur);
            } else {
                return new GetResult(null, nodes.get(cur), cur);
            }
        }

        class GetResult {
            BTreeEntry entry;
            BTreeNode node;
            int entryIndex;

            public GetResult(BTreeEntry entry, BTreeNode node, int entryIndex) {
                this.entry = entry;
                this.node = node;
                this.entryIndex = entryIndex;
            }

            boolean isThisEntry() {
                return entry != null;
            }
        }
    }

    private class BTreeEntry implements Comparable<BTreeEntry> {
        K key;
        V value;

        public BTreeEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public BTreeEntry(BTreeEntry entry) {
            this.key = entry.key;
            this.value = entry.value;
        }

        @Override
        public int compareTo(BTreeEntry o) {
            return compare(this.key, o.key);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PersistentBTree.BTreeEntry)) return false;
            BTreeEntry entry = (BTreeEntry) o;
            return Objects.equals(key, entry.key) && Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
