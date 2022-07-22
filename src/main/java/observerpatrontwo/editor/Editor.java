package observerpatrontwo.editor;

import observerpatrontwo.publisher.EventManager;

import java.io.File;

public class Editor {
    public final EventManager eventManager;
    private File file;

    public Editor() {
        this.eventManager=new EventManager("open","save");
    }

    public void openFile(String fileName) {
        this.file = new File(fileName);
        eventManager.publish("open", file);
    }

    public void saveFile() throws Exception {

      if (this.file != null) {
        eventManager.publish("save", file);
      }else{
          throw new Exception("File is not opened");
      }
    }
}
