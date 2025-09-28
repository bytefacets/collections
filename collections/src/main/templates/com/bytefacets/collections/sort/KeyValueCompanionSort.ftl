<#ftl strip_whitespace=true>
// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.sort;

import com.bytefacets.collections.types.${key.name}Type;

/**
 * Sorts the "primary" array and performs the same permutation on a companion array. The sort function is
 * from an older (and simpler) version of JDK's Arrays.sort.
 */
public final class ${key.name}${value.name}CompanionSort {
    private ${key.name}${value.name}CompanionSort(){}

    /**
     * Sun's JDK QuickSort implementation with two arrays.
     */
    public static void sort(${key.arrayType}[] pri, ${value.arrayType}[] sec, int off, int len, ${key.name}Type.Cmp cmp) {
        // Insertion sort on smallest arrays
        if( off + len > pri.length )
            throw new IllegalArgumentException(
                "offset+len > primary.length: offset="+off+" len="+len+" array.length="+pri.length);
        if( off + len > sec.length )
            throw new IllegalArgumentException(
                "offset+len > secondary.length: offset="+off+" len="+len+" array.length="+sec.length);
        if( off < 0 || len < 0 )
            throw new IllegalArgumentException("offset or len is negative: offset="+off+" len="+len);


        if( len < 7 ) {
            for( int i = off; i < len + off; i++ ) {
                for( int j = i; j > off && cmp.compare( pri[ j - 1 ], pri[ j ] ) > 0; j-- ) {
                    swap( pri, sec, j, j - 1 );
                }
            }
            return;
        }

        // Choose a partition element, v
        int m = off + ( len >> 1 ); // Small arrays, middle element
        if( len > 7 ) {
            int l = off;
            int n = off + len - 1;
            if( len > 40 ) {
                // Big arrays, pseudomedian of 9
                int s = len / 8;
                l = med3( pri, l, l + s, l + 2 * s, cmp );
                m = med3( pri, m - s, m, m + s , cmp);
                n = med3( pri, n - 2 * s, n - s, n , cmp );
            }
            m = med3( pri, l, m, n , cmp); // Mid-size, med of 3
        }
        ${key.arrayType} v = pri[ m ];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while( true ) {
            while( b <= c && cmp.compare( pri[ b ], v ) <= 0 ) {
                if( cmp.compare( pri[ b ], v ) == 0 )
                    swap( pri, sec, a++, b );
                b++;
            }
            while( c >= b && cmp.compare( pri[ c ], v ) >= 0 ) {
                if( cmp.compare( pri[ c ], v ) == 0 )
                    swap( pri, sec, c, d-- );
                c--;
            }
            if( b > c )
                break;
            swap( pri, sec, b++, c-- );
        }

        // Swap partition elements back to middle
        int s, n = off + len;
        s = Math.min( a - off, b - a );
        vecswap( pri, sec, off, b - s, s );
        s = Math.min( d - c, n - d - 1 );
        vecswap( pri, sec, b, n - s, s );

        // Recursively sort non-partition-elements
        if( ( s = b - a ) > 1 )
            sort( pri, sec, off, s, cmp );
        if( ( s = d - c ) > 1 )
            sort( pri, sec, n - s, s, cmp );
    }

    /**
     * Swaps x[a] with x[b].
     */
    private static void swap(${key.arrayType}[] pri, ${value.arrayType}[] sec, int a, int b) {
        ${key.arrayType} oldPrimaryA = pri[ a ];
        ${value.arrayType} oldSecondaryA = sec[ a ];

        sec[ a ] = sec[ b ];
        pri[ a ] = pri[ b ];

        pri[ b ] = oldPrimaryA;
        sec[ b ] = oldSecondaryA;
    }

    /**
     * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
     */
    private static void vecswap(${key.arrayType}[] pri, ${value.arrayType}[] sec, int a, int b, int n) {
        for( int i = 0; i < n; i++, a++, b++ ) {
            swap( pri, sec, a, b );
        }
    }

    /**
     * Comparison helper function
     */
    private static int med3(${key.arrayType}[] x, int a, int b, int c, ${key.name}Type.Cmp cmp) {
        return ( cmp.compare( x[ a ], x[ b ] ) < 0 ? ( cmp.compare( x[ b ], x[ c ] ) < 0 ? b
                : cmp.compare( x[ a ], x[ c ] ) < 0 ? c : a ) : ( cmp.compare( x[ b ], x[ c ] ) > 0 ? b
                : cmp.compare( x[ a ], x[ c ] ) > 0 ? c : a ) );
    }
}
