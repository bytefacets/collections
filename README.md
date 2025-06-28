[![Sponsor](https://img.shields.io/badge/Sponsor-%E2%9D%A4-lightgrey?logo=github)](https://github.com/sponsors/bytefacets)

# ByteFacet Collections
The ByteFacet Collections java library contains a set of collections which have important
differences from the common JDK collections, and even from other collections libraries:
1. Primitive based
2. Index-enabled

### Primitive-Based
That they are primitive based is an obvious benefit over the standard JDK collections. The 
main advantages here are around memory:
- primitive types use less memory than Objects
- using fewer Objects reduces work for the garbage collector

For example, a typical set like the IntIndexedSet, which is very much like a hash set uses
only 5 objects (3 int[], a hash function reference, and a equality function reference).

The *IndexedSet collections are hash-table-based, so perform slightly worse than the FastUtil 
primitive collections which use open hashing. The trade-off is the stability of where
the key resides during resizing operations, an important aspect of the next feature. 

### Indexed-Enabled
Several of the collections in the library produce an "entry" when an element is added to them.
This entry has a number of simple, but powerful properties.

- Entries are 0-based and monotonically incrementing
- Entries are re-used when there are removals
- Entries are stable across resizing

Combined, these characteristics allow the following:

- Range transformation, e.g. turn any arbitrary value into an entry
- Array indexing, e.g. entries fit nicely indexing compactly into an array
- Direct access into the collection using the entry, e.g. access keys or values without rehashing

### Beyond Sets and Maps
In addition to the standard collection types of Sets and Maps, the library also provides Vector, 
Heap, and Deque implementations, as well as some bidirectional collections (CompactOneToMany and 
CompactManyToMany), and some straightforward storage collections, such as *Store, and *MatrixStore.

#### Heaps
The heap implementation is map-like so that you can associate a value with the ordered element.
Another difference with the JDK PriorityQueue is that with the ByteFacets Heap, you can store
an element's entry in the heap for direct access.


#### CompactOneToMany, CompactManyToMany
These collections offer relationship mapping and navigability. "Compact" here means that the 
collection is most efficient when left and right entries are 0-based integers due their use of 
arrays to track relationships. Because of this, these collections often work in combination with 
the set and map collections to provide a mechanism to track relationships between members of those 
collections.

#### Store, MatrixStore
The stores provide a simple index-based abstraction for storing values, in this case arrays, but 
in could be off-heap memory, via new jdk-21 features or through something like Apache Arrow. 
*Store types handle growing to accommodate new indecies. If Chunked, the store will also grow 
efficiently by extending the storage in blocks, reusing existing blocks in the new structure. 

The MatrixStores enable row-like features to store multiple fields of the same type close to each
other in the backing storage.




