package nas.nas.repository;

import java.sql.SQLException;
import java.util.ArrayList;

import nas.nas.model.FileForUploadWithoutData;
import nas.nas.model.ListFileData;

public interface FileRepository {
    ArrayList<ListFileData> getFiles(final String userUUID) throws SQLException;
    String addFile(final String userUUID, final FileForUploadWithoutData file) throws SQLException;
    void removeFile(final String fileUUID) throws SQLException;
    boolean hasFile(final String userUUID, final String fileUUID) throws SQLException;
    void addPermissions(final String fileUUID, final String targetUserUUID, final String[] newPermissions) throws SQLException;
    void removePermissions(final String fileUUID, final String targetUserUUID) throws SQLException;
}
