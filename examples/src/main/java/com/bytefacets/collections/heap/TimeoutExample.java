// SPDX-FileCopyrightText: Copyright (c) 2025 Byte Facets
// SPDX-License-Identifier: MIT
package com.bytefacets.collections.heap;

import com.bytefacets.collections.queue.IntDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This example demonstrates usage of LongIntHeap when used to schedule timeouts on something.
 *
 * <p>The example uses standard JDK threading to coordinate the arrival, acknowledgement, and
 * timeouts.
 */
final class TimeoutExample {
    private static final long DEFAULT_WAIT_MS = 100;
    private static final long TIMEOUT_MS = 1600;

    private TimeoutExample() {}

    public static void main(final String[] args) throws InterruptedException {
        // latch to help us control the lifecycle of this example;
        // 4 so that it exits after 4 timeouts
        final CountDownLatch latch = new CountDownLatch(4);

        // a manager that will report timeouts
        final TimeoutManager manager = new TimeoutManager(latch);

        // something that will mock some "acknowledgement" of each item
        // so that the timeout becomes obsolete for the item
        final MockAcknowledger acknowledger = new MockAcknowledger(manager);

        // something that will register new items
        final ItemArrival itemArrival = new ItemArrival(manager, acknowledger);

        final var pool = Executors.newScheduledThreadPool(2);
        // push new items into the "system"
        pool.scheduleAtFixedRate(itemArrival::newItemArrived, 1, 10, TimeUnit.MILLISECONDS);
        // acknowledge the items we decided
        pool.scheduleAtFixedRate(acknowledger::acknowledgeNext, 1, 37, TimeUnit.MILLISECONDS);
        // emit "timeouts" on anything that lingers too long
        new Thread(manager).start();
        latch.await(); // wait for 4 things to timeout
    }

    private static final class ItemArrival {
        private final AtomicInteger nextId = new AtomicInteger(0);
        private final TimeoutManager timeoutManager;
        private final MockAcknowledger acknowledger;

        private ItemArrival(
                final TimeoutManager timeoutManager, final MockAcknowledger acknowledger) {
            this.timeoutManager = timeoutManager;
            this.acknowledger = acknowledger;
        }

        private void newItemArrived() {
            // some new thing has arrived
            final int id = nextId.getAndIncrement();
            // we want it to be acknowledged within TIMEOUT_MS
            final long expire = System.currentTimeMillis() + TIMEOUT_MS;
            // we register the item with the timeout manager
            final int entry = timeoutManager.addNewItem(id, expire);
            if (id % 50 != 0) { // every 50, we'll not "acknowledge" the item and they'll time out
                // but for everything else, we'll mock an acknowledgment soon;
                acknowledger.registerLaterAcknowledgement(entry);
            } else {
                System.out.printf("registering %d expecting to timeout at %d%n", id, expire);
            }
        }
    }

    private static final class MockAcknowledger {
        private final IntDeque mockHandled = new IntDeque(16);
        private final TimeoutManager manager;
        private int ackCount = 0;
        private boolean printed = true;

        private MockAcknowledger(final TimeoutManager manager) {
            this.manager = manager;
        }

        void registerLaterAcknowledgement(final int entry) {
            synchronized (mockHandled) {
                mockHandled.addLast(entry);
            }
        }

        // called by an executor thread to acknowledge stuff we've accumulated
        private void acknowledgeNext() {
            synchronized (mockHandled) {
                while (!mockHandled.isEmpty()) {
                    manager.acknowledge(mockHandled.removeFirst());
                    ackCount++;
                    printed = false;
                }
                // show us that we're doing something
                if (ackCount % 10 == 0 && !printed) {
                    System.out.printf("acknowledged: %d%n", ackCount);
                    printed = true;
                }
            }
        }
    }

    // polls its heap
    private static final class TimeoutManager implements Runnable {
        private final LongIntHeap heap = new LongIntHeap(16);
        private final CountDownLatch latch;

        private TimeoutManager(final CountDownLatch latch) {
            this.latch = latch;
        }

        // the entry returned by addNewItem is now reported back as acknowledged
        private void acknowledge(final int entry) {
            synchronized (heap) {
                heap.removeAt(entry);
            }
        }

        // adds a new item with an expiration time
        // returns an entry that can be used to access the item in the heap
        private int addNewItem(final int value, final long expireEpochMs) {
            synchronized (heap) {
                return heap.insert(expireEpochMs, value);
            }
        }

        @Override
        public void run() {
            while (latch.getCount() != 0) {
                final long delayMs;
                synchronized (heap) {
                    delayMs = process();
                }
                waitForNext(delayMs);
            }
        }

        // process things that have timed out
        // returns a wait period which is the time until the next expiration, or a default
        private long process() {
            while (!heap.isEmpty()) {
                final long now = System.currentTimeMillis();
                final int firstEntry = heap.peek();
                final long next = heap.getKeyAt(firstEntry);
                if (next < now) { // this entry has expired
                    reportTimeout(firstEntry, now, next);
                } else {
                    return next - now; // this is how long we'll wait for
                }
            }
            return DEFAULT_WAIT_MS;
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        private void waitForNext(final long delayMs) {
            try {
                latch.await(delayMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void reportTimeout(final int firstEntry, final long now, final long next) {
            final int value = heap.getValueAt(firstEntry);
            System.out.printf(
                    "%d timeout observed for item %s, %dms past due%n", now, value, now - next);
            heap.removeAt(firstEntry);
            latch.countDown(); // using this to manage the example's exit
        }
    }
}
