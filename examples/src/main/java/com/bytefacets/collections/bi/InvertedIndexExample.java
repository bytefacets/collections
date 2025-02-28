// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.bi;

import com.bytefacets.collections.functional.LongConsumer;
import com.bytefacets.collections.hash.LongIndexedSet;
import com.bytefacets.collections.hash.StringIndexedSet;

/** This is a small example using the CompactOneToMany to implement a small inverted index. */
final class InvertedIndexExample {
    private InvertedIndexExample() {}

    public static void main(final String[] args) {
        final InvertedIndex index = new InvertedIndex();
        final String[] symbols = new String[] {"YHOO", "CSCO", "MSFT", "AAPL"};
        final String[] strategy = new String[] {"TWAP", "VWAP", "POV"};
        for (int i = 0; i < 27; i++) {
            index.add(1_000_000 + i, symbols[i % symbols.length], strategy[i % strategy.length]);
        }
        print(index, "MSFT"); // prints orders associated with MSFT
        print(index, "XFOO"); // doesn't print anything bc nothing is "XFOO"
        print(index, "VWAP"); // prints orders associated with VWAP
    }

    private static void print(final InvertedIndex index, final String term) {
        System.out.printf("%s (%d):", term, index.count(term));
        index.iterate(term, orderId -> System.out.print(orderId + " "));
        System.out.println();
    }

    private static class InvertedIndex {
        // range transformation for orders ids which are longs
        private final LongIndexedSet orders = new LongIndexedSet(128);
        // range transformation for terms
        private final StringIndexedSet terms = new StringIndexedSet(128);
        // the index associating the term with the order entry
        private final CompactOneToMany index = new CompactOneToMany(128, 128, true);

        private void add(final long orderId, final String symbol, final String strategy) {
            System.out.printf("Adding %d: symbol=%s, strategy=%s%n", orderId, symbol, strategy);
            // transform the order into 0-based entry
            final int orderEntry = orders.add(orderId);
            // transform the symbol and strategy terms into 0-based entries
            final int symbolEntry = terms.add(symbol);
            final int strategyEntry = terms.add(strategy);
            // associate the order with both terms
            // when using sets in front of a CompactOneToMany or CompactManyToMany,
            //   lefts and rights should be from distinct sets so the entries on each side
            //   are distinct and compact
            index.put(symbolEntry, orderEntry);
            index.put(strategyEntry, orderEntry);
        }

        private int count(final String term) {
            final int termEntry = terms.lookupEntry(term);
            return (termEntry != -1) ? index.withLeft(termEntry).count() : 0;
        }

        private void iterate(final String term, final LongConsumer orderConsumer) {
            // use lookup, not add to query
            final int termEntry = terms.lookupEntry(term);
            if (termEntry != -1) {
                // get a handle to operations on the leftEntry
                index.withLeft(termEntry)
                        .forEachValue(
                                orderEntry -> { // values in the collection are orderEntries
                                    // use the orderEntry for direct access to the original orderId
                                    final long order = orders.getKeyAt(orderEntry);
                                    orderConsumer.accept(order);
                                });
            }
        }
    }
}
