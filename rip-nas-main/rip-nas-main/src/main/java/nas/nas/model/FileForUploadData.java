package nas.nas.model;

public class FileForUploadData {
    private String fileName;
    private long size; // in Bytes
    private byte[] data;

    public FileForUploadData(final String fileName, final long size, final byte[] data) {
        this.fileName = fileName;
        this.size = size;
        this.data = data;
    }

    public String getFileName() {
        return this.fileName;
    }

    public long getSize() {
        return this.size;
    }

    public byte[] getData() {
        return this.data;
    }
}
