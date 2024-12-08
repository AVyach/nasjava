package nas.nas.model;

public class FileForUploadWithoutData {
    private String fileName;
    private long size; // in Bytes

    public FileForUploadWithoutData(final String fileName, final long size) {
        this.fileName = fileName;
        this.size = size;
    }
    
    public String getFileName() {
        return this.fileName;
    }

    public long getSize() {
        return this.size;
    }
}
