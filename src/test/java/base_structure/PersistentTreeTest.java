package base_structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PersistentTreeTest {

    private final int t = 5;
    private final int max = t * 4 - 1;

    private PersistentTree<Integer, String> persistentTree;
    private Map<Integer, String> expectedMap;

    @BeforeEach
    void setup() {
        persistentTree = new PersistentBTree<>(t);
        expectedMap = new HashMap<>();
        for (int i = 0; i < max; i++) {
            expectedMap.put(i, String.valueOf(i));
            persistentTree = persistentTree.put(i, String.valueOf(i));
        }
    }

    @Test
    void testGetAndRemovePersistent() {
        for (int removeKey = 0; removeKey < max; removeKey++) {
            persistentTree.remove(removeKey);
            verifyTree(expectedMap, persistentTree);
        }
    }

    @Test
    void testGetAndRemove() {
        PersistentTree<Integer, String> currTree = persistentTree;
        for (int removeKey = 0; removeKey < max; removeKey++) {
            currTree = currTree.remove(removeKey);
            expectedMap.put(removeKey, null);
            verifyTree(expectedMap, currTree);
            expectedMap.put(removeKey, String.valueOf(removeKey));
            currTree = persistentTree;
        }
    }

    @Test
    void testPutPersistent() {
        persistentTree.put(max, String.valueOf(max));
        verifyTree(expectedMap, persistentTree);
    }

    @Test
    void testPut() {
        persistentTree = persistentTree.put(max, String.valueOf(max));
        expectedMap.put(max, String.valueOf(max));
        verifyTree(expectedMap, persistentTree);
    }

    @Test
    void testPutPersistentValue() {
        persistentTree.put(max, String.valueOf(max));
        expectedMap.put(max, null);
        verifyTree(expectedMap, persistentTree);
    }

    @Test
    void testUpdatePersistent() {
        for (int updateKey = 0; updateKey < max; updateKey++) {
            persistentTree.put(updateKey, "new");
            verifyTree(expectedMap, persistentTree);
        }
    }

    @Test
    void testUpdate() {
        PersistentTree<Integer, String> currTree = persistentTree;
        for (int updateKey = 0; updateKey < max; updateKey++) {
            currTree = currTree.put(updateKey, "new");
            expectedMap.put(updateKey, "new");
            verifyTree(expectedMap, currTree);
            currTree = persistentTree;
            expectedMap.put(updateKey, String.valueOf(updateKey));
        }
    }

    @Test
    void testComplexCase() {
        PersistentTree<Integer, String> currTree = persistentTree;
        currTree = currTree.put(t * 4, String.valueOf(t * 4));
        persistentTree = currTree;
        currTree = currTree.remove(t * 2);
        expectedMap.put(t * 4, String.valueOf(t * 4));
        expectedMap.put(t * 2, null);
        verifyTree(expectedMap, currTree);
        currTree = persistentTree;
        expectedMap.put(t * 2, String.valueOf(t * 2));
        currTree = currTree.remove(t * 3 - 1);
        currTree = currTree.put(t * 3 - 1, "new");
        currTree = currTree.remove(t * 4);
        expectedMap.put(t * 3 - 1, "new");
        expectedMap.put(t * 4, null);
        verifyTree(expectedMap, currTree);
    }

    @Test
    void testContainsValue() {
        for (int containsKeyValue = 0; containsKeyValue < max; containsKeyValue++) {
            assertTrue(persistentTree.containsValue(expectedMap.get(containsKeyValue)));
        }
        assertFalse(persistentTree.containsValue(String.valueOf(max)));
    }

    @Test
    void testIterator() {
        int i = 0;
        for (Map.Entry<Integer, String> e : persistentTree) {
            i++;
            assertEquals(expectedMap.get(e.getKey()), e.getValue());
        }
        assertEquals(expectedMap.size(), i);
    }

    private void verifyTree(Map<Integer, String> expected, PersistentTree<Integer, String> tree) {
        for (Map.Entry<Integer, String> e : expected.entrySet()) {
            assertEquals(e.getValue(), tree.get(e.getKey()));
        }
    }

}