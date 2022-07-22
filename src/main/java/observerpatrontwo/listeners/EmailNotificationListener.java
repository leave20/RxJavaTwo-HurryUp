package observerpatrontwo.listeners;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class EmailNotificationListener implements EventListener{

    private final String email;

    public EmailNotificationListener(String email) {
        this.email = email;
    }

    @Override
    public void update(String eventType, File file) {
      log.info("Email to"+ email+": Something has performed on file: "+file.getName());
    }
}
