package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateMessageConsumer {
    private final AlbumsUpdater updater;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AlbumsUpdateMessageConsumer(AlbumsUpdater updater) {
        this.updater = updater;
    }

    public void consume(Message<?> message) {
        try {
            logger.debug("Starting albums update");
            updater.update();
            logger.debug("Finished albums update");
        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }
}
