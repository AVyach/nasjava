package nas.nas.model;

public class ListFileData {
    private String uuid;
    private String fileName;
    private int size; // in Bytes

    public ListFileData(final String uuid, final String fileName, final int size) {
        this.uuid = uuid;
        this.fileName = fileName;
        this.size = size;
    }

    @Override
    public String toString() {
        return this.fileName;
    }

    public String getUUID() {
        return this.uuid;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getSize() {
        return this.size;
    }
}
