package observerpatrontwo;

import observerpatrontwo.editor.Editor;
import observerpatrontwo.listeners.EmailNotificationListener;
import observerpatrontwo.listeners.LogOpenListener;

public class Demo {
    public static void main(String[] args) {
        Editor editor = new Editor();
        editor.eventManager.subscribe("open", new LogOpenListener("log.txt"));
        editor.eventManager.subscribe("save", new EmailNotificationListener("admin@example.com"));

        try{
            editor.openFile("file.txt");
            editor.saveFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
