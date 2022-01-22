package persistent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PersistentCollectionTest {

    private final int size = 10;

    private PersistentMap<Integer, String> persistentMap;
    private Map<Integer, String> expectedMap;

    private PersistentArray<String> persistentArray;
    private ArrayList<String> expectedArray;

    @BeforeEach
    void setup() {
        persistentMap = new PersistentMap<>();
        expectedMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            expectedMap.put(i, String.valueOf(i));
            persistentMap.put(i, String.valueOf(i));
        }

        persistentArray = new PersistentArray<>();
        expectedArray = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            expectedArray.add(String.valueOf(i));
            persistentArray.add(String.valueOf(i));
        }
    }

    @Test
    void testUndo() {
        expectedMap.remove(size - 1);
        persistentMap.undo();
        verifyMap(expectedMap, persistentMap);

        expectedArray.remove(size - 1);
        persistentArray.undo();
        verifyArray(expectedArray, persistentArray);
    }

    @Test
    void testRedo() {
        persistentMap.undo();
        persistentMap.undo();
        persistentMap.redo();
        expectedMap.remove(size - 1);
        verifyMap(expectedMap, persistentMap);
        persistentMap.put(size - 1, String.valueOf(size - 1));
        assertFalse(persistentMap.redo());


        persistentArray.undo();
        persistentArray.undo();
        persistentArray.redo();
        persistentArray.add(String.valueOf(size - 1));
        verifyArray(expectedArray, persistentArray);
        assertFalse(persistentArray.redo());
    }

    private void verifyMap(Map<Integer, String> expected, PersistentMap<Integer, String> map) {
        for (Map.Entry<Integer, String> e : expected.entrySet()) {
            assertEquals(e.getValue(), map.get(e.getKey()));
        }
    }

    private void verifyArray(ArrayList<String> expected, PersistentArray<String> array) {
        assertEquals(expected.size(), array.size());
        for (String s : expected) {
            assertTrue(array.contains(s));
        }
    }
}