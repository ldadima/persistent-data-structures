package persistent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PersistentMapTest {

    private final int size = 10;

    private PersistentMap<Integer, String> persistentMap;
    private Map<Integer, String> expectedMap;

    @BeforeEach
    void setup() {
        persistentMap = new PersistentMap<>();
        expectedMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            expectedMap.put(i, String.valueOf(i));
            persistentMap.put(i, String.valueOf(i));
        }
    }

    @Test
    void testGetAndRemove() {
        PersistentMap<Integer, String> currMap = persistentMap;
        currMap.remove(size / 2);
        expectedMap.remove(size / 2);
        verifyMap(expectedMap, currMap);
    }

    @Test
    void testPut() {
        persistentMap.put(size, String.valueOf(size));
        expectedMap.put(size, String.valueOf(size));
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testUpdate() {
        PersistentMap<Integer, String> currMap = persistentMap;
        currMap.put(size / 2, "new");
        expectedMap.put(size / 2, "new");
        verifyMap(expectedMap, currMap);
    }

    @Test
    void testComplexCase() {
        PersistentMap<Integer, String> currMap = persistentMap;
        currMap.put(size + 1, String.valueOf(size + 1));
        currMap.remove(size / 2);
        expectedMap.put(size + 1, String.valueOf(size + 1));
        expectedMap.remove(size / 2);
        verifyMap(expectedMap, currMap);
        currMap.remove(size - 1);
        currMap.put(size - 1, "new");
        currMap.remove(size + 1);
        expectedMap.remove(size - 1);
        expectedMap.put(size - 1, "new");
        expectedMap.remove(size + 1);
        verifyMap(expectedMap, currMap);
    }

    @Test
    void testContainsValue() {
        for (int containsKeyValue = 0; containsKeyValue < size; containsKeyValue++) {
            assertTrue(persistentMap.containsValue(expectedMap.get(containsKeyValue)));
        }
        assertFalse(persistentMap.containsValue(String.valueOf(size)));
    }

    @Test
    void testIterator() {
        int i = 0;
        for (Map.Entry<Integer, String> e : persistentMap) {
            i++;
            assertEquals(expectedMap.get(e.getKey()), e.getValue());
        }
        assertEquals(persistentMap.size(), i);
    }

    @Test
    void testEquals() {
        PersistentMap<Integer, String> other = new PersistentMap<>();
        for (Map.Entry<Integer, String> e : expectedMap.entrySet()) {
            other.put(e.getKey(), e.getValue());
        }
        assertEquals(persistentMap, other);
        assertTrue(other.equals(persistentMap));
        assertEquals(persistentMap.hashCode(), other.hashCode());
    }

    @Test
    void testContainsKey() {
        assertTrue(persistentMap.containsKey(1));
    }

    @Test
    void testPutAll() {
        PersistentMap<Integer, String> addPersistentMap = new PersistentMap<>();
        Map<Integer, String> addMap = new HashMap<>();

        for (int i = size; i < size * 2; i++) {
            addPersistentMap.put(i, String.valueOf(i));
            addMap.put(i, String.valueOf(i));
        }

        persistentMap.putAll(addPersistentMap);
        expectedMap.putAll(addMap);
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testKeySet() {
        assertEquals(expectedMap.keySet(), persistentMap.keySet());
    }

    @Test
    void testValues() {
        assertEquals(new ArrayList<>(expectedMap.values()), persistentMap.values());
    }

    @Test
    void testEntrySet() {
        assertEquals(new HashSet<>(expectedMap.entrySet()), persistentMap.entrySet());
    }

    @Test
    void testGetOrDefault() {
        assertEquals(expectedMap.getOrDefault(size, "absent"), persistentMap.getOrDefault(size, "absent"));
    }

    @Test
    void testReplaceAll() {
        BiFunction<Integer, String, String> biFunction = (integer, s) -> s + "_version_1";
        persistentMap.replaceAll(biFunction);
        expectedMap.replaceAll(biFunction);
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testPutIfAbsent() {
        persistentMap.putIfAbsent(size - 1, String.valueOf(size));
        expectedMap.putIfAbsent(size - 1, String.valueOf(size));
        persistentMap.putIfAbsent(size, String.valueOf(size));
        expectedMap.putIfAbsent(size, String.valueOf(size));
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testRemove() {
        assertFalse(persistentMap.remove(size - 1, String.valueOf(size)));
        expectedMap.remove(size - 1, String.valueOf(size));
        assertTrue(persistentMap.remove(size - 2, String.valueOf(size - 2)));
        expectedMap.remove(size - 2, String.valueOf(size - 2));
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testReplaceCheckOld() {
        assertFalse(persistentMap.replace(size - 1, String.valueOf(size), String.valueOf(size + 1)));
        expectedMap.replace(size - 1, String.valueOf(size), String.valueOf(size));
        assertTrue(persistentMap.replace(size - 2, String.valueOf(size - 2), String.valueOf(size)));
        expectedMap.replace(size - 2, String.valueOf(size - 2), String.valueOf(size));
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testReplace() {
        assertEquals(String.valueOf(size - 1), persistentMap.replace(size - 1, String.valueOf(size)));
        expectedMap.replace(size - 1, String.valueOf(size));
        assertNull(persistentMap.replace(size, String.valueOf(size)));
        expectedMap.replace(size, String.valueOf(size));
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testComputeIfAbsent() {
        Function<Integer, String> fun = String::valueOf;
        assertEquals(String.valueOf(size - 1), persistentMap.computeIfAbsent(size - 1, fun));
        assertEquals(String.valueOf(size), persistentMap.computeIfAbsent(size, fun));
        expectedMap.computeIfAbsent(size - 1, fun);
        expectedMap.computeIfAbsent(size, fun);
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testComputeIfPresent() {
        BiFunction<Integer, String, String> biFunction = (integer, s) ->  s + "_version_1";
        assertEquals((size - 1) + "_version_1", persistentMap.computeIfPresent(size - 1, biFunction));
        assertNull(persistentMap.computeIfPresent(size, biFunction));
        expectedMap.computeIfPresent(size - 1, biFunction);
        expectedMap.computeIfPresent(size, biFunction);
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testCompute() {
        BiFunction<Integer, String, String> biFunction = (integer, s) ->  s + "_version_1";
        assertEquals((size - 1) + "_version_1", persistentMap.computeIfPresent(size - 1, biFunction));
        assertNull(persistentMap.computeIfPresent(size, biFunction));
        expectedMap.computeIfPresent(size - 1, biFunction);
        expectedMap.computeIfPresent(size, biFunction);
        verifyMap(expectedMap, persistentMap);
    }

    @Test
    void testMerge() {
        BiFunction<String, String, String> biFunction = (oldValue, newValue) ->  oldValue + ' ' + newValue;
        assertEquals( "9 version_1", persistentMap.merge(size - 1, "version_1", biFunction));
        assertEquals( String.valueOf(size), persistentMap.merge(size, String.valueOf(size), biFunction));
        expectedMap.merge(size - 1, "version_1", biFunction);
        expectedMap.merge(size, String.valueOf(size), biFunction);
        verifyMap(expectedMap, persistentMap);
    }

    private void verifyMap(Map<Integer, String> expected, PersistentMap<Integer, String> map) {
        assertEquals(expected.size(), map.size());
        for (Map.Entry<Integer, String> e : expected.entrySet()) {
            assertEquals(e.getValue(), map.get(e.getKey()));
        }
    }
}