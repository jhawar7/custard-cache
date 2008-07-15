package com.custardsource.cache.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.distribution.DistributionFactory;
import org.apache.commons.math.distribution.IntegerDistribution;

import com.custardsource.cache.policy.CacheManager;
import com.custardsource.cache.policy.associative.DirectMappedCacheManager;
import com.custardsource.cache.policy.associative.DirectMappedConfiguration;
import com.custardsource.cache.policy.mq.MQCacheManager;
import com.custardsource.cache.policy.mq.MQConfiguration;
import com.custardsource.cache.policy.replacement.AdaptiveReplacementCacheManager;
import com.custardsource.cache.policy.replacement.AdaptiveReplacementConfiguration;
import com.custardsource.cache.policy.replacement.FixedReplacementCacheManager;
import com.custardsource.cache.policy.replacement.FixedReplacementConfiguration;
import com.custardsource.cache.policy.simple.FifoCacheManager;
import com.custardsource.cache.policy.simple.FifoConfiguration;
import com.custardsource.cache.policy.simple.LfuCacheManager;
import com.custardsource.cache.policy.simple.LfuConfiguration;
import com.custardsource.cache.policy.simple.LruCacheManager;
import com.custardsource.cache.policy.simple.LruConfiguration;
import com.custardsource.cache.policy.twoq.TwoQCacheManager;
import com.custardsource.cache.policy.twoq.TwoQConfiguration;
import com.custardsource.cache.simulator.fqn.IntegerDistributionFqnSource;
import com.custardsource.cache.simulator.fqn.LogFileFqnSource;
import com.custardsource.cache.simulator.fqn.RegexLogFileFqnSource;

public class CacheSimulatorShell {

    public static void main(String[] args) throws Exception {
        Options options = createOptions();

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
            final int cacheSize = Integer.parseInt(cmd.getOptionValue("c"));

            runTest(getConfiguration(new MQCacheManager<String>(new MQConfiguration(cacheSize)),
                    cmd));
            runTest(getConfiguration(
                    new TwoQCacheManager<String>(new TwoQConfiguration(cacheSize, 0.3f)), cmd));
            runTest(getConfiguration(new AdaptiveReplacementCacheManager<String>(
                    new AdaptiveReplacementConfiguration(cacheSize)), cmd));
            runTest(getConfiguration(new FixedReplacementCacheManager<String>(
                    new FixedReplacementConfiguration(cacheSize, 0.1)), cmd));
            runTest(getConfiguration(
                    new FifoCacheManager<String>(new FifoConfiguration(cacheSize)), cmd));
            runTest(getConfiguration(
                    new LruCacheManager<String>(new LruConfiguration(cacheSize)), cmd));
            runTest(getConfiguration(
                    new LfuCacheManager<String>(new LfuConfiguration(cacheSize)), cmd));
            runTest(getConfiguration(
                    new DirectMappedCacheManager<String>(new DirectMappedConfiguration<String>(cacheSize)), cmd));
            // TODO MRU Policy
        } catch (ParseException e) {
            new HelpFormatter().printHelp(100, "TreeCacheHitSimulator", "", options, "");
            System.exit(1);
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        Option o;
        OptionGroup parserGroup = new OptionGroup();

        o = new Option("f", "fqn-file", true, "");
        o.setArgName("filename");
        parserGroup.addOption(o);

        o = new Option("p", "fqn-poisson", true, "use a poisson provider to generate FQNs");
        o.setArgName("population");
        parserGroup.addOption(o);

        o = new Option("b", "fqn-binomial", true, "use a binomial provider to generate FQNs");
        o.setArgName("population");
        parserGroup.addOption(o);

        parserGroup.setRequired(true);
        options.addOptionGroup(parserGroup);

        o = new Option("i", "iterations", true,
                "# of cache retrievals to do (defaults to population size)");
        o.setArgName("count");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("c", "cache-size", true, "the maximum size of the cache region");
        o.setArgName("size");
        o.setRequired(true);
        options.addOption(o);

        o = new Option("m", "match-pattern", true,
                "regex to use to find FQNs in the log file (default .*) [only if --fqn-file]");
        o.setArgName("pattern");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("g", "match-group", true,
                "group found in log file regex which becomes an FQN (default 0) [only if --fqn-file]");
        o.setArgName("group #");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("o", "binomial-probability", true,
                "'p' used to generate binomial distribution (default 0.5) [only if --fqn-binomial]");
        o.setArgName("probability");
        options.addOption(o);

        o = new Option("z", "zip", false, "input file is a zip file [only if --fqn-file]");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("l", "limit-file", true,
                "read a limited number of lines from the file [only if --fqn-file]");
        o.setRequired(false);
        options.addOption(o);

        o = new Option("s", "show-progress", true, "show progress every");
        o.setRequired(false);
        o.setArgName("every");
        options.addOption(o);

        return options;
    }

    private static void runTest(SimulatorConfiguration config) throws Exception {
        CacheHitSimulator simulator = new CacheHitSimulator(config);
        simulator.testPerformance();
    }

    private static SimulatorConfiguration getConfiguration(CacheManager<String> policy,
            CommandLine cmd) {
        final int showProgressEvery = Integer.parseInt(StringUtils.defaultString(cmd
                .getOptionValue("s"), "100"));

        try {
            SimulatorConfiguration config = new SimulatorConfiguration(policy, getFqnSource(cmd),
                    showProgressEvery);
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Iterable<String> getFqnSource(CommandLine cmd) throws FileNotFoundException,
            IOException {
        Integer iterations = null;

        String iterationOption = cmd.getOptionValue("i");
        if (iterationOption != null) {
            iterations = Integer.valueOf(iterationOption);
        }

        if (cmd.hasOption("p")) {
            int populationSize = Integer.parseInt(cmd.getOptionValue("p"));
            if (iterations == null) {
                iterations = populationSize;
            }
            IntegerDistribution distribution = DistributionFactory.newInstance()
                    .createPoissonDistribution(populationSize / 2);
            return new IntegerDistributionFqnSource(distribution, getStandardRandomnessSource(),
                    populationSize, iterations);
        } else if (cmd.hasOption("b")) {
            int populationSize = Integer.parseInt(cmd.getOptionValue("b"));
            if (iterations == null) {
                iterations = populationSize;
            }
            double probability = Double.valueOf(cmd.getOptionValue("o", "0.5"));
            IntegerDistribution distribution = DistributionFactory.newInstance()
                    .createBinomialDistribution(populationSize, probability);
            return new IntegerDistributionFqnSource(distribution, getStandardRandomnessSource(),
                    populationSize, iterations);
        } else if (cmd.hasOption("f")) {
            File f = new File(cmd.getOptionValue("f"));
            Integer limit = Integer.valueOf(cmd.getOptionValue("l", "0"));
            InputStream stream = new FileInputStream(f);
            if (cmd.hasOption("z")) {
                ZipInputStream zipStream = new ZipInputStream(stream);
                zipStream.getNextEntry();
                stream = zipStream;
            }
            if (cmd.hasOption("m")) {
                Pattern pattern = Pattern.compile(cmd.getOptionValue("m", ".*"));
                Integer group = Integer.valueOf(cmd.getOptionValue("g", "0"));
                return new RegexLogFileFqnSource(stream, pattern, group, limit);
            }

            return new LogFileFqnSource(stream, limit);
        }

        throw new IllegalStateException("Cannot create fqn source");
    }

    private static Random getStandardRandomnessSource() {
        return new Random(0xDEFEC8);
    }
}
