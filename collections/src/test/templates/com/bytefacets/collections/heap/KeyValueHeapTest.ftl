package com.bytefacets.collections.heap;

import com.bytefacets.collections.arrays.${key.name}Array;
import com.bytefacets.collections.arrays.${value.name}Array;
import com.bytefacets.collections.types.${key.name}Type;
import com.bytefacets.collections.types.${value.name}Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This is a generated class. Changes to the class should be made on the template and the generator re-run.
 */
<#if generics != "">@SuppressWarnings("unchecked")</#if>
class ${key.name}${value.name}HeapTest {
    private ${key.name}${value.name}Heap${instanceGenerics} heap;
    private final int[] keyIds = new int[] { 3, 1, 4, 5, 8, 9, 2, 6, 0, 7 }; // mixed order

    @BeforeEach
    void setUp() throws Exception {
        heap = new ${key.name}${value.name}Heap${instanceGenerics}(4);
        assertEquals(4, heap.getCapacity());
        assertTrue(heap.isEmpty());
    }

    @Test
    void shouldReportEmpty() {
        assertTrue(heap.isEmpty());
        final int entry = heap.insert(k(1), v(1));
        assertFalse(heap.isEmpty());
        heap.removeAt(entry);
        assertTrue(heap.isEmpty());
    }

    @Test
    void shouldNotAllowNullComparator() {
        try {
            heap.setComparator(null);
            fail("Did not fail");
        } catch( Exception ex ) {
            assertTrue(ex.getMessage().contains("null"));
        }
    }

    @Test
    void shouldAllocateEntriesInOrder() {
        for(int i = 0; i < keyIds.length; i++) {
            final int keyId = keyIds[i];
            assertEquals(i, heap.insert(k(keyId), v(keyId*3)));
        }
    }

    @Test
    void shouldAllocateEntriesFromFreeList() {
        for(final int keyId : keyIds) {
            heap.insert(k(keyId), v(keyId*3));
        }
        final int firstEntry = heap.peek();
        heap.removeAt(firstEntry);
        final int allocatedEntry1 = heap.insert(k(37), v(99));
        assertEquals(firstEntry, allocatedEntry1);
    }

    @Test
    void shouldGrow() {
        IntStream.range(0, 60).forEach(keyId -> heap.insert(k(keyId), v(keyId*2)));
        assertEquals(64, heap.getCapacity());
    }

    @Test
    void shouldMaintainKeyValueMapping() {
        final int[] entries = new int[keyIds.length];
        for(int i = 0; i < keyIds.length; i++) {
            final int keyId = keyIds[i];
            entries[i] = heap.insert(k(keyId), v(keyId*3));
        }
        for(int i = 0; i < keyIds.length; i++) {
            final int keyId = keyIds[i];
            assertEquals(k(keyId), heap.getKeyAt(entries[i]));
            assertEquals(v(keyId*3), heap.getValueAt(entries[i]));
        }
    }

    @Test
    void shouldFindFirstEntryForKey() {
        IntStream.of(3, 3, 3, 3, 3).forEach(keyId -> heap.insert(k(keyId), v(0)));
        final int entry = heap.insert(k(5), v(1));
        IntStream.of(2, 2, 6, 6, 4).forEach(keyId -> heap.insert(k(keyId), v(0)));
        assertEquals(entry, heap.getFirstEntry(k(5)));
    }

    /**
     * Removing an entry that is not active causes an exception.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 99})
    void shouldThrowWhenInvalidRemove(final int badEntry) {
        try {
            heap.removeAt(badEntry);
            fail("Did not fail trying to remove an invalid entry");
        } catch(Exception ex) {
            final String substring = badEntry == 0 ? "does not exist" : "entry is out of bounds";
            assertTrue(ex.getMessage().contains(substring));
            assertTrue(ex.getMessage().contains(Integer.toString(badEntry)));
        }
    }

    @Test
    void shouldThrowWhenPeekingOnEmpty() {
        try {
            heap.peek();
            fail("Did not fail attempting invalid operation");
        } catch(Exception ex) {
            assertTrue(ex.getMessage().contains("empty"));
        }
    }
    
    @Test
    void shouldRepositionKeyWhenReset() {
        IntStream.of(keyIds).filter(i -> i != 0).forEach(id -> heap.insert(k(id), v(id*2)));
        final int entryForK5 = heap.getFirstEntry(k(5));
        heap.resetKey(entryForK5, k(0));
        assertEquals(entryForK5, heap.peek());
        assertEquals(k(0), heap.getKeyAt(entryForK5)); // new key
        assertEquals(v(10), heap.getValueAt(entryForK5)); // maintains the value here
    }

    @Nested
    class ComparatorTests {
        @Test
        void shouldThrowWhenNullComparator() {
            try {
                heap.setComparator( null );
                fail("Did not fail setting a null comparator");
            } catch(Exception ex) {
                assertTrue(ex.getMessage().contains("null"));
            }
        }

        @Test
        void shouldBeNoOpWhenSettingComparatorWithItself() {
            for(int id : keyIds) {
                heap.insert(k(id), v(id*2));
            }
            final int modBefore = heap.modCount();
            heap.setComparator(${key.name}Type.Asc);
            assertEquals(modBefore, heap.modCount());
        }

        @Test
        void shouldMaintainOrder() {
            for(int id : keyIds) {
                heap.insert(k(id), v(id*2));
            }
            for(int id = 0; id <= 9; id++) {
                final int entry = heap.peek();
                assertEquals(k(id), heap.getKeyAt(entry));
                assertEquals(v(id*2), heap.getValueAt(entry));
                heap.removeAt(entry);
            }
        }

        @Test
        void shouldMaintainOrderWithDuplicates() {
            for(int id : keyIds) {
                if(id != 0) {
                    heap.insert(k(id), v(id*1));
                    heap.insert(k(id), v(id*2));
                    heap.insert(k(id), v(id*3));
                }
            }
            for(int id = 1; id <= 9; id++) {
                final Set<Object> keys = new HashSet<>();
                final Set<Object> vals = new HashSet<>();
                for(int mult = 1; mult <= 3; mult++) {
                    final int entry = heap.peek();
                    keys.add(heap.getKeyAt(entry));
                    vals.add(heap.getValueAt(entry));
                    heap.removeAt(entry);
                }
                assertEquals(Set.of(k(id)), keys);
                // in the event we're testing Bool values
                assertEquals(new HashSet<>(List.of(v(id*1), v(id*2), v(id*3))), vals);
            }
        }

        @Test
        void shouldReverseOrderWhenHeapHasEntries() {
            for(int id : keyIds) {
                heap.insert(k(id), v(id*2));
            }
            heap.setComparator(${key.name}Type.Desc);
            for(int id = 9; id >= 0; id--) {
                final int entry = heap.peek();
                assertEquals(k(id), heap.getKeyAt(entry));
                assertEquals(v(id*2), heap.getValueAt(entry));
                heap.removeAt(entry);
            }
        }
    }

    @Nested
    class DefaultKeyTests {
        @Test
        void shouldUseDefaultKey() {
            IntStream.range(0, heap.getCapacity()).
                forEach(i -> assertEquals(heap.getDefaultKey(), heap.getKeyAt(i)));
        }

        @Test
        void shouldReplaceDefaultKey() {
            final var newDefault = k(3);
            heap.setDefaultKey(newDefault);
            IntStream.range(0, heap.getCapacity()).
                forEach(i -> assertEquals(newDefault, heap.getKeyAt(i)));
        }

        @Test
        void shouldUseDefaultKeyWhenGrowing() {
            final var newDefault = k(3);
            heap.setDefaultKey(newDefault);
            IntStream.range(0, 33).forEach(id -> heap.insert(k(id), v(id)));
            IntStream.range(33, heap.getCapacity()).
                forEach(i -> assertEquals(newDefault, heap.getKeyAt(i)));
        }

        @Test
        void shouldUseDefaultKeyWhenClearing() {
            final var newDefault = k(3);
            heap.setDefaultKey(newDefault);
            IntStream.range(0, 80).forEach(id -> heap.insert(k(id), v(id)));
            heap.clear();
            IntStream.range(0, heap.getCapacity()).
                forEach(i -> assertEquals(newDefault, heap.getKeyAt(i)));
        }

        @Test
        void shouldNotAllowReplacingDefaultKeyWhenNotEmpty() {
            heap.insert(k(1), v(1));
            try {
                heap.setDefaultKey(k(3));
                fail("Did not fail trying to set default key on non-empty collection.");
            } catch(Exception ex) {
                assertTrue(ex.getMessage().contains("SetDefaultKey"));
                assertTrue(ex.getMessage().contains("not empty"));
            }
        }
    }

    @Nested
    class DefaultValueTests {
        @Test
        void shouldUseDefaultValue() {
            IntStream.range(0, heap.getCapacity()).
                forEach(i -> assertEquals(heap.getDefaultValue(), heap.getValueAt(i)));
        }

        @Test
        void shouldReplaceDefaultValue() {
            final var newDefault = v(3);
            heap.setDefaultValue(newDefault);
            IntStream.range(0, heap.getCapacity()).
                forEach(i -> assertEquals(newDefault, heap.getValueAt(i)));
        }

        @Test
        void shouldUseDefaultKeyWhenGrowing() {
            final var newDefault = v(3);
            heap.setDefaultValue(newDefault);
            IntStream.range(0, 33).forEach(id -> heap.insert(k(id), v(id)));
            IntStream.range(33, heap.getCapacity()).
                forEach(i -> assertEquals(newDefault, heap.getValueAt(i)));
        }

        @Test
        void shouldUseDefaultKeyWhenClearing() {
            final var newDefault = v(3);
            heap.setDefaultValue(newDefault);
            IntStream.range(0, 80).forEach(id -> heap.insert(k(id), v(id)));
            heap.clear();
            IntStream.range(0, heap.getCapacity()).
                forEach(i -> assertEquals(newDefault, heap.getValueAt(i)));
        }

        @Test
        void shouldNotAllowReplacingDefaultKeyWhenNotEmpty() {
            heap.insert(k(1), v(1));
            try {
                heap.setDefaultValue(v(3));
                fail("Did not fail trying to set default value on non-empty collection.");
            } catch(Exception ex) {
                assertTrue(ex.getMessage().contains("SetDefaultValue"));
                assertTrue(ex.getMessage().contains("not empty"));
            }
        }
    }

    private ${key.arrayType} k(int value) {
        <#if key.arrayType == "String">return Character.toString((char)(value + 'A'));
        <#else>return ${key.name}Type.castTo${key.name}( value );
        </#if>
    }

    private ${value.arrayType} v( int value ) {
        <#if value.arrayType == "String">return Character.toString((char)(value + 'A'));
        <#else>return ${value.name}Type.castTo${value.name}( value );
        </#if>
    }
}