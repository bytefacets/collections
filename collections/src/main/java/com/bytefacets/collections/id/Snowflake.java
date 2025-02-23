package com.bytefacets.collections.id;

import static java.util.Objects.requireNonNull;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

// https://github.com/callicoder/java-snowflake/blob/master/src/main/java/com/callicoder/snowflake/Snowflake.java
public final class Snowflake {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int UNUSED_BITS = 1; // Sign bit, Unused (always set to 0)
    private static final int EPOCH_BITS = 41;
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

    // Custom Epoch
    private static final long DEFAULT_CUSTOM_EPOCH = 1676731200000L;

    private final long nodeId;
    private final long customEpoch;
    private final LongSupplier clock;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    // Create Snowflake with a nodeId and custom epoch
    Snowflake(final long nodeId, final long customEpoch, final LongSupplier epochMillisProvider) {
        if (nodeId < 0 || nodeId > maxNodeId) {
            throw new IllegalArgumentException(
                    String.format("NodeId must be between %d and %d", 0, maxNodeId));
        }
        this.nodeId = nodeId;
        this.customEpoch = customEpoch;
        this.clock = requireNonNull(epochMillisProvider, "clock");
    }

    // Create Snowflake with a nodeId
    Snowflake(final long nodeId, final LongSupplier epochMillisProvider) {
        this(nodeId, DEFAULT_CUSTOM_EPOCH, epochMillisProvider);
    }

    // Let Snowflake generate a nodeId
    public static Snowflake snowflake(
            final LongSupplier epochMillisProvider, final Supplier<String> uniqueNodeNameSupplier) {
        return new Snowflake(
                createNodeId(uniqueNodeNameSupplier), DEFAULT_CUSTOM_EPOCH, epochMillisProvider);
    }

    public static Snowflake snowflake(final LongSupplier epochMillisProvider) {
        return snowflake(epochMillisProvider, DEFAULT_UNIQUE_NODE_NAME_SUPPLIER);
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        final long id =
                currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS)
                        | (nodeId << SEQUENCE_BITS)
                        | sequence;

        return id;
    }

    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private long timestamp() {
        return clock.getAsLong() - customEpoch;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(final long currentTimestamp) {
        long nextTimestamp = currentTimestamp;
        while (nextTimestamp == lastTimestamp) {
            nextTimestamp = timestamp();
        }
        return nextTimestamp;
    }

    private static long createNodeId(final Supplier<String> uniqueNodeNameSupplier) {
        return uniqueNodeNameSupplier.get().hashCode() & maxNodeId;
    }

    public long[] parse(final long id) {
        final long maskNodeId = ((1L << NODE_ID_BITS) - 1) << SEQUENCE_BITS;
        final long maskSequence = (1L << SEQUENCE_BITS) - 1;

        final long timestamp = (id >> (NODE_ID_BITS + SEQUENCE_BITS)) + customEpoch;
        final long nodeId = (id & maskNodeId) >> SEQUENCE_BITS;
        final long sequence = id & maskSequence;

        return new long[] {timestamp, nodeId, sequence};
    }

    @Override
    public String toString() {
        return "Snowflake Settings [EPOCH_BITS="
                + EPOCH_BITS
                + ", NODE_ID_BITS="
                + NODE_ID_BITS
                + ", SEQUENCE_BITS="
                + SEQUENCE_BITS
                + ", CUSTOM_EPOCH="
                + customEpoch
                + ", NodeId="
                + nodeId
                + "]";
    }

    static final Supplier<String> DEFAULT_UNIQUE_NODE_NAME_SUPPLIER =
            () -> {
                try {
                    final StringBuilder sb = new StringBuilder();
                    final Enumeration<NetworkInterface> networkInterfaces =
                            NetworkInterface.getNetworkInterfaces();
                    while (networkInterfaces.hasMoreElements()) {
                        final NetworkInterface networkInterface = networkInterfaces.nextElement();
                        final byte[] mac = networkInterface.getHardwareAddress();
                        if (mac != null) {
                            for (byte macPort : mac) {
                                sb.append(String.format("%02X", macPort));
                            }
                        }
                    }
                    return sb.toString();
                } catch (Exception ex) {
                    return Long.toString(secureRandom.nextLong(), 36);
                }
            };
}
