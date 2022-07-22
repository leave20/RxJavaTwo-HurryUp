package observerpatrontwo.listeners;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class LogOpenListener implements EventListener {
    private final File loggerFile;

    public LogOpenListener(String filename) {
        this.loggerFile = new File(filename);
    }

    @Override
    public void update(String eventType, File file) {
        log.info("Save to log " + loggerFile + ": Someone has performed " + eventType);
    }
}
