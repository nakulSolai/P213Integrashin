import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType> {

  // each slot has a linked list of key-value pairs
  private LinkedList<Pair>[] table = null;

  @SuppressWarnings("unchecked")
  public HashtableMap(int capacity) {
    this.table = (LinkedList<Pair>[]) new LinkedList[capacity];
  }

  public HashtableMap() {
    this(64);
  }
  

  protected class Pair { 
    public KeyType key;
    public ValueType value;

    public Pair(KeyType key, ValueType value) {
      this.key = key;
      this.value = value;
    }
  }

  // Adds a new key-value pair to the map. Throws if key is null or already present.
  public void put(KeyType key, ValueType value) throws IllegalArgumentException {
    // Check for null key
    if (key == null) throw new NullPointerException("key cannot be null");

    // Resize if load factor >= 80%
    if (this.getSize() + 1 >= .8 * this.getCapacity()) {
      LinkedList<Pair>[] oldTable = this.table;
      this.makeTable(this.getCapacity() * 2);

      // Rehash all existing pairs into new table
      for (LinkedList<Pair> newBucket : oldTable)
        if(newBucket != null)
          for (Pair toRehash : newBucket)
            this.put(toRehash.key, toRehash.value);
    }

    Pair toPut = new Pair(key, value);

    int index = Math.abs(key.hashCode()) % this.getCapacity();

    // Check for duplicate key
    if (this.containsKey(key))
      throw new IllegalArgumentException("this key-value pair is already in the table");

    // Create bucket if needed and add pair
    if (this.table[index] == null) this.table[index] = new LinkedList<Pair>();

    this.table[index].add(toPut);
  }

  // Checks if the map contains the given key.
  public boolean containsKey(KeyType key) {
    // Iterate through all buckets and pairs to find the key
    for (LinkedList<Pair> bucket : this.table)
      if (bucket != null)
        for (Pair toCompare : bucket)
          if (toCompare.key.equals(key))
            return true;

    return false;
  }

  // Retrieves the value mapped to the given key, or throws if not found.
  public ValueType get(KeyType key) throws NoSuchElementException {
    // Check if key exists, then search in the correct bucket
    if (this.containsKey(key)) {
      int index = Math.abs(key.hashCode()) % this.getCapacity();

      for (Pair sameHash : this.table[index])
        if (sameHash.key.equals(key))
          return sameHash.value;
    }

    throw new NoSuchElementException("The key isn't contained in the table");
  }

  // Removes the mapping for the given key and returns its value, or throws if not found.
  public ValueType remove(KeyType key) throws NoSuchElementException {
    // Check if key exists, then remove from the correct bucket
    if (this.containsKey(key)) {
      int index = Math.abs(key.hashCode()) % this.getCapacity();

      for (Pair sameHash : this.table[index])
        if (sameHash.key.equals(key)) {
          ValueType removedValue = sameHash.value;

          this.table[index].remove(sameHash);

          return removedValue;
        }
    }

    throw new NoSuchElementException("The key isn't contained in the table");
  }

  // Removes all key-value pairs from the map.
  public void clear() {
    // Reinitialize the table to empty buckets
    this.makeTable(this.getCapacity());
  }

  // Returns the number of key-value pairs in the map.
  public int getSize() {
    int size = 0;
    // Sum the sizes of all non-null buckets
    for (LinkedList<Pair> bucket : this.table)
      if (bucket != null) size += bucket.size();

    return size;
  }

  // Returns the current capacity of the table (number of buckets).
  public int getCapacity() {
    return this.table.length;
  }

  /**
   * sets table to a new empty array, so that a suppress warining is only needed here
   * 
   * @param newCapacity that capacity of this new table
   */
  @SuppressWarnings("unchecked")
  private void makeTable(int newCapacity) {
    this.table = (LinkedList<Pair>[]) new LinkedList[newCapacity];
  }


  /**
   * Tests inserting and retrieving a single key-value pair.
   * Checks that get returns the correct value after put.
   * Also checks that containsKey and getSize behave as expected.
   */
  @Test
  public void test1() {
    // Create a map with small capacity
    HashtableMap<String, Integer> map = new HashtableMap<>(8);
    // Insert a key-value pair
    map.put("apple", 10);
    // Retrieve the value and check correctness
    assertEquals(10, map.get("apple"), "Value for 'apple' should be 10");
    // Check that containsKey returns true
    assertTrue(map.containsKey("apple"), "Map should contain key 'apple'");
    // Check that size is 1
    assertEquals(1, map.getSize(), "Map size should be 1 after one insertion");
    // Try to get a non-existent key and expect exception
    assertThrows(NoSuchElementException.class, () -> map.get("banana"), "Getting a missing key should throw");
  }

  /**
   * Tests that containsKey returns true for present keys and false for absent keys.
   * Also checks behavior after removal and after inserting multiple keys.
   */
  @Test
  public void test2() {
    HashtableMap<String, String> map = new HashtableMap<>(4);
    // Insert a key-value pair
    map.put("x", "y");
    // Check containsKey for present and absent keys
    assertTrue(map.containsKey("x"), "Map should contain key 'x'");
    assertFalse(map.containsKey("z"), "Map should not contain key 'z'");
    // Insert more keys
    map.put("a", "b");
    map.put("c", "d");
    // Remove one key and check containsKey
    map.remove("x");
    assertFalse(map.containsKey("x"), "Key 'x' should be removed");
    assertTrue(map.containsKey("a"), "Key 'a' should still be present");
    assertTrue(map.containsKey("c"), "Key 'c' should still be present");
  }

  /**
   * Tests that remove deletes a key and returns its value, and that get throws after removal.
   * Also checks that size is updated and removing a non-existent key throws.
   */
  @Test
  public void test3() {
    HashtableMap<Integer, String> map = new HashtableMap<>(4);
    // Insert multiple keys
    map.put(1, "one");
    map.put(2, "two");
    map.put(3, "three");
    // Remove a key and check returned value
    assertEquals("one", map.remove(1), "Remove should return the value for key 1");
    // Check that key is gone
    assertFalse(map.containsKey(1), "Key 1 should be removed");
    // Check that get throws for removed key
    assertThrows(NoSuchElementException.class, () -> map.get(1), "Getting removed key should throw");
    // Check that size is updated
    assertEquals(2, map.getSize(), "Size should be 2 after one removal");
    // Try to remove a non-existent key
    assertThrows(NoSuchElementException.class, () -> map.remove(99), "Removing missing key should throw");
  }

  /**
   * Tests that clear removes all entries and makes the size to zero.
   * Also checks that containsKey and get throw after clear.
   */
  @Test
  public void test4() {
    HashtableMap<String, Integer> map = new HashtableMap<>(4);
    // Insert several keys
    map.put("a", 1);
    map.put("b", 2);
    map.put("c", 3);
    // Clear the map
    map.clear();
    // Check that size is zero
    assertEquals(0, map.getSize(), "Size should be 0 after clear");
    // Check that containsKey returns false for all keys
    assertFalse(map.containsKey("a"), "Key 'a' should not be present after clear");
    assertFalse(map.containsKey("b"), "Key 'b' should not be present after clear");
    assertFalse(map.containsKey("c"), "Key 'c' should not be present after clear");
    // Check that get throws for any key
    assertThrows(NoSuchElementException.class, () -> map.get("a"), "Getting any key after clear should throw");
    assertThrows(NoSuchElementException.class, () -> map.get("b"), "Getting any key after clear should throw");
  }

  /**
   * Tests that the hashtable resizes when enough elements are added.
   * Also checks that all values are still accessible after resize.
   */
  @Test
  public void test5() {
    HashtableMap<Integer, Integer> map = new HashtableMap<>(2);
    // Insert enough elements to trigger resize
    map.put(1, 1);
    map.put(2, 2);
    map.put(3, 3); // should trigger resize
    map.put(4, 4);
    // Check that capacity increased
    assertTrue(map.getCapacity() > 2, "Capacity should increase after resize");
    // Check that all values are accessible
    assertEquals(1, map.get(1), "Value for key 1 should be correct after resize");
    assertEquals(2, map.get(2), "Value for key 2 should be correct after resize");
    assertEquals(3, map.get(3), "Value for key 3 should be correct after resize");
    assertEquals(4, map.get(4), "Value for key 4 should be correct after resize");
    // Check that size is correct
    assertEquals(4, map.getSize(), "Size should be 4 after four insertions");
    // Remove a key and check that others are still present
    map.remove(2);
    assertFalse(map.containsKey(2), "Key 2 should be removed");
    assertEquals(3, map.getSize(), "Size should be 3 after removal");
    assertEquals(3, map.get(3), "Other keys should still be accessible");
  }


  @Override
  public List<KeyType> getKeys() {
    LinkedList<KeyType> linkedKeys = new LinkedList<>();

    for (LinkedList<Pair> bucket : this.table)
      if (bucket != null)
        for (Pair keyAndValue : bucket)
          linkedKeys.add(keyAndValue.key);

    return linkedKeys;
  }

}