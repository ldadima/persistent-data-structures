package persistent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComplexPersistentCollectionTest {

    private final int size = 10;

    private PersistentMap<Integer, PersistentArray<String>> persistentMap;
    private Map<Integer, ArrayList<String>> expectedMap;

    @BeforeEach
    void setup() {
        persistentMap = new PersistentMap<>();
        expectedMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            PersistentArray<String> persistentArray = new PersistentArray<>();
            ArrayList<String> expectedArray = new ArrayList<>();

            for (int j = 0; j < size; j++) {

                persistentArray.add(String.valueOf(j));
                expectedArray.add(String.valueOf(j));
            }
            expectedMap.put(i, expectedArray);
            persistentMap.put(i, persistentArray);
        }
    }

    @Test
    void testUndo() {
        expectedMap.get(size - 1).remove(size - 1);
        persistentMap.undo();
        verifyComplexMap(expectedMap, persistentMap);
    }

    @Test
    void testUndo2() {
        persistentMap.get(size - 2).remove(size - 3);
        persistentMap.undo();
        verifyComplexMap(expectedMap, persistentMap);
    }

    @Test
    void testRedo() {
        persistentMap.get(size - 2).undo();
        persistentMap.get(size - 3).undo();
        persistentMap.redo();
        expectedMap.get(size - 2).remove(size - 1);
        verifyComplexMap(expectedMap, persistentMap);

    }

    private void verifyComplexMap(Map<Integer, ArrayList<String>> expected, PersistentMap<Integer, PersistentArray<String>> map) {
        assertEquals(expected.size(), map.size());
        for (Map.Entry<Integer, ArrayList<String>> e : expected.entrySet()) {
            verifyArray(e.getValue(), map.get(e.getKey()));
        }
    }

    private void verifyArray(ArrayList<String> expected, PersistentArray<String> array) {
        assertEquals(expected.size(), array.size());
        for (String s : expected) {
            assertTrue(array.contains(s));
        }
    }
}
