package com.aconex.cache.simulator.fqn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Fqn;

public class LogFileFqnSource implements Iterable<Fqn> {
    private final InputStream input;
    private final Pattern pattern;
    private final Integer group;
    private final Integer maxPopulation;

    private static final Log LOG = LogFactory.getLog(LogFileFqnSource.class);

    public LogFileFqnSource(InputStream input, Pattern pattern, Integer group, Integer maxPopulation) {
        this.input = input;
        this.pattern = pattern;
        this.group = group;
        this.maxPopulation = maxPopulation;

    }

    public Iterator<Fqn> iterator() {
        return new Iterator<Fqn>() {
            private Fqn prefetched = null;
            private int found = 0;
            private final LineNumberReader reader = new LineNumberReader(new BufferedReader(
                    new InputStreamReader(input)));
            final int limit = (maxPopulation > 0) ? maxPopulation : Integer.MAX_VALUE;

            public boolean hasNext() {
                return prefetch() != null;
            }

            private Fqn prefetch() {
                if (prefetched != null) {
                    return prefetched;
                }
                prefetched = readNext();
                return prefetched;
            }

            private Fqn readNext() {
                try {
                    String line = reader.readLine();
                    while (line != null && found < limit) {
                        Matcher m = pattern.matcher(line);
                        if (m.matches()) {
                            found++;
                            String match = m.group(group);
                            LOG.debug("Found FQN #" + found + ": " + match);
                            return new Fqn(match);
                        }

                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            public Fqn next() {
                if (prefetched != null) {
                    Fqn result = prefetched;
                    prefetched = null;
                    return result;
                }
                return readNext();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
