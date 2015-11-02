package pl.bsb.b2btester.web.helper;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import org.apache.commons.io.FilenameUtils;
import org.omnifaces.util.Messages;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import pl.bsb.b2btester.type.FileType;


/**
 * Klasa udostepniająca metody związane z importem pliku.
 * 
 * @author swarczak
 */
public abstract class FileImportHelper implements Serializable{
    
    protected String fileName;
    protected boolean importComplete;
    
    protected abstract void importFile(InputStream stream) throws Exception;
    protected abstract List<FileType> getFileTypes();
    protected abstract void clean();
    protected abstract Logger getLogger();
    protected abstract ResourceBundle getBudle();
    
    public void init() {
        doClean();
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        getLogger().info("FileUpload: fileName={}", event.getFile().getFileName());
        doClean();
        fileName = FilenameUtils.getName(event.getFile().getFileName());
        try {
            importFile(event.getFile().getInputstream());
            importComplete = true;
        } catch (Exception ex) {
            getLogger().error("Impot certificate error: ", ex);
            Messages.addError("saveNewServer", getBudle().getString("importFile.error.io"));
        }
    }
    
    private void doClean() {
        fileName = null;
        importComplete = false;
        clean();
    }
    
    public String getFilePattern() {
        return FileType.getExtensionsPattern(getFileTypes());
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isImportComplete() {
        return importComplete;
    }

    public void setImportComplete(boolean importComplete) {
        this.importComplete = importComplete;
    }
}
