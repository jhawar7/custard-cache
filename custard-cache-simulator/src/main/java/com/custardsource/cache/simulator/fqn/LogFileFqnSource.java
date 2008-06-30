package com.custardsource.cache.simulator.fqn;

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

public class LogFileFqnSource implements Iterable<String> {
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

    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private String prefetched = null;
            private int found = 0;
            private final LineNumberReader reader = new LineNumberReader(new BufferedReader(
                    new InputStreamReader(input)));
            final int limit = (maxPopulation > 0) ? maxPopulation : Integer.MAX_VALUE;

            public boolean hasNext() {
                return prefetch() != null;
            }

            private String prefetch() {
                if (prefetched != null) {
                    return prefetched;
                }
                prefetched = readNext();
                return prefetched;
            }

            private String readNext() {
                try {
                    String line = reader.readLine();
                    while (line != null && found < limit) {
                        Matcher m = pattern.matcher(line);
                        if (m.matches()) {
                            found++;
                            String match = m.group(group);
                            LOG.debug("Found FQN #" + found + ": " + match);
                            return match;
                        }

                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }

            public String next() {
                if (prefetched != null) {
                    String result = prefetched;
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
