package net.benjaneer.scrabble.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class FutureUtil {
    private static final Logger LOGGER = LogManager.getLogger(FutureUtil.class);

    public static <T> List<T> getForFutures(Stream<Future<T>> futures) {
        return futures.map(x -> {
            try {
                return x.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error while waiting for future", e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public static void waitForFutures(Collection<Future<?>> futures) {
        futures.forEach(x -> {
            try {
                x.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error while waiting for future", e);
            }
        });
    }
}
