package base_structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PersistentTreeTest {

    private final int max = 20;

    private PersistentTree<Integer, String> persistentTree;
    private Map<Integer, String> expectedMap;

    @BeforeEach
    void setup() {
        persistentTree = new PersistentBTree<>();
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

    private void verifyTree(Map<Integer, String> expected, PersistentTree<Integer, String> tree) {
        for (Map.Entry<Integer, String> e : expected.entrySet()) {
            assertEquals(e.getValue(), tree.get(e.getKey()));
        }
    }

}