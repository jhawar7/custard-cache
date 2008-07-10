package com.custardsource.cache.policy.replacement;

import junit.framework.TestCase;

public class FixedReplacementConfigurationTest extends TestCase {
    public void testCannotCreateWithZeroT1() {
        try {
            new FixedReplacementConfiguration(100, 0);
            fail("Should have failed with 0 t1 size");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testCannotCreateWithNegativeT1() {
        try {
            new FixedReplacementConfiguration(100, -1);
            fail("Should have failed with -1 t1 size");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testCannotCreateWithT1EqualToSize() {
        try {
            new FixedReplacementConfiguration(100, 100);
            fail("Should have failed with full t1 size");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

}