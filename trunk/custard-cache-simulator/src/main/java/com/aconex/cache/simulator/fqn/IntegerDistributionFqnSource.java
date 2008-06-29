package com.aconex.cache.simulator.fqn;

import java.util.Iterator;
import java.util.Random;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.IntegerDistribution;
import org.jboss.cache.Fqn;

public class IntegerDistributionFqnSource implements Iterable<Fqn> {
    final Random randomSource;
    final int maximum;
    final int iterations;
    private IntegerDistribution distribution;

    public IntegerDistributionFqnSource(IntegerDistribution distribution, Random random,
            int maximum, int iterations) {
        this.distribution = distribution;
        this.randomSource = random;
        this.maximum = maximum;
        this.iterations = iterations;
    }

    private int nextValue() {
        try {
            return distribution.inverseCumulativeProbability(randomSource.nextDouble());
        } catch (MathException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator<Fqn> iterator() {
        return new Iterator<Fqn>() {
            int iteration = 0;

            public boolean hasNext() {
                return (iteration <= iterations);
            }

            public Fqn next() {
                iteration++;
                return new Fqn(nextValue());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }
}