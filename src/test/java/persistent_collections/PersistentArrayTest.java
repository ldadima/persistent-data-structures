package persistent_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PersistentArrayTest {

    private final int size = 10;

    private PersistentArray<String> persistentArray;
    private ArrayList<String> expectedArray;

    @BeforeEach
    void setup() {
        persistentArray = new PersistentArray<>();
        expectedArray = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            expectedArray.add(String.valueOf(i));
            persistentArray.add(String.valueOf(i));
        }
    }

    @Test
    void testAdd() {
        persistentArray.add(String.valueOf(size));
        expectedArray.add(String.valueOf(size));
        verifyArray(expectedArray, persistentArray);
    }

    @Test
    void testSet() {
        PersistentArray<String> currArray = persistentArray;
        currArray.set(size / 2, "new");
        expectedArray.set(size / 2, "new");
        verifyArray(expectedArray, currArray);
    }

    @Test
    void testIterator() {
        int i = 0;
        for (String s : persistentArray) {
            i++;
            assertTrue(expectedArray.contains(s));
        }
        assertEquals(i, persistentArray.size());
    }

    @Test
    void testEquals() {
        PersistentArray<String> other = new PersistentArray<>();
        for (String s : expectedArray) {
            other.add(s);
        }
        assertEquals(persistentArray, other);
        assertTrue(other.equals(persistentArray));
        assertEquals(persistentArray.hashCode(), other.hashCode());
    }

    @Test
    void testAddAll() {
        PersistentArray<String> addPersistentArray = new PersistentArray<>();
        ArrayList<String> addArray = new ArrayList<>();

        for (int i = size; i < size * 2; i++) {
            addPersistentArray.add(String.valueOf(i));
            addArray.add(String.valueOf(i));
        }

        persistentArray.addAll(addPersistentArray);
        expectedArray.addAll(addArray);
        verifyArray(expectedArray, persistentArray);
    }

    @Test
    void testRemove() {
        assertFalse(persistentArray.remove(String.valueOf(size)));
        expectedArray.remove(String.valueOf(size));
        assertTrue(persistentArray.remove(String.valueOf(size - 2)));
        expectedArray.remove(String.valueOf(size - 2));

        assertNull(persistentArray.remove(size));
        expectedArray.remove(String.valueOf(size));
        assertEquals(String.valueOf(size - 3), persistentArray.remove(size - 3));
        expectedArray.remove(size - 3);

        verifyArray(expectedArray, persistentArray);
    }

    @Test
    void testContains() {
        assertFalse(persistentArray.contains(String.valueOf(size)));
        assertTrue(persistentArray.contains(String.valueOf(size - 1)));
    }

    @Test
    void testIndexOf() {
        assertEquals(-1, persistentArray.indexOf(String.valueOf(size)));
        assertEquals(size - 1, persistentArray.indexOf(String.valueOf(size - 1)));
    }

    @Test
    void testIndexOfRange() {
        assertEquals(-1, persistentArray.indexOfRange(String.valueOf(size / 3), 1, size / 4));
        assertEquals( size / 3, persistentArray.indexOfRange(String.valueOf(size / 3), 0, size / 2));
    }

    @Test
    void testGet() {
        assertNull(persistentArray.get(size));
        assertEquals(String.valueOf(size - 1), persistentArray.get(size - 1));
    }

    @Test
    void testAddIndex() {
        persistentArray.add(size, String.valueOf(size));
        expectedArray.add(size, String.valueOf(size));
        persistentArray.add(size - 1, String.valueOf(size - 2));
        expectedArray.add(size - 1, String.valueOf(size - 2));
        verifyArray(expectedArray, persistentArray);
    }

    @Test
    void testRemoveAll() {
        PersistentArray<String> removePersistentArray = new PersistentArray<>();
        ArrayList<String> removeArray = new ArrayList<>();

        for (int i = size / 2; i < size; i++) {
            removePersistentArray.add(String.valueOf(i));
            removeArray.add(String.valueOf(i));
        }

        persistentArray.removeAll(removePersistentArray);
        expectedArray.removeAll(removeArray);
        verifyArray(expectedArray, persistentArray);
    }

    @Test
    void testRetainsAll() {
        PersistentArray<String> retainsPersistentArray = new PersistentArray<>();
        ArrayList<String> retainsArray = new ArrayList<>();

        for (int i = size / 2; i < size; i++) {
            retainsPersistentArray.add(String.valueOf(i));
            retainsArray.add(String.valueOf(i));
        }

        persistentArray.retainsAll(retainsPersistentArray);
        expectedArray.retainAll(retainsArray);
        verifyArray(expectedArray, persistentArray);
    }

    private void verifyArray(ArrayList<String> expected, PersistentArray<String> array) {
        assertEquals(expected.size(), array.size());
        for (String s : expected) {
            assertTrue(array.contains(s));
        }
    }
}