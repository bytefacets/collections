// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections;

public final class NumUtils {
    private NumUtils() {}

    public static int nextPowerOf2(final int v) {
        return 1 << 32 - Integer.numberOfLeadingZeros(v - 1);
    }
}
