package nas.nas.repository.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.stereotype.Component;

import nas.nas.model.FileForUploadWithoutData;
import nas.nas.model.ListFileData;
import nas.nas.repository.FileRepository;

@Component
public class FileRepositoryImpl implements FileRepository {
    private DataSource dataSource;

    public FileRepositoryImpl() {
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
        pgDataSource.setUrl("jdbc:postgresql://localhost:5432/nas?user=postgres&password=root1234");
        this.dataSource = pgDataSource;
    }

    public FileRepositoryImpl(final String host, final String port, final String db, final String user, final String password) {
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
        pgDataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s", 
            host, port, db, user, password));
        this.dataSource = pgDataSource;
    }

    @Override
    public ArrayList<ListFileData> getFiles(String userUUID) throws SQLException {
        Connection conn = null;
        ResultSet res = null;
        ArrayList<ListFileData> files = new ArrayList<ListFileData>();
        try {
            conn = this.dataSource.getConnection();

            PreparedStatement query = conn.prepareStatement("select f.external_uuid, f.name, f.size from file f where (f.id = any(select p.file_id from permission p where p.user_uuid = ? and 'file:viewer' = any(p.roles) or 'file:editor' = any(p.roles)));");
            query.setString(1, userUUID);
            res = query.executeQuery();

            while (res.next()) {
                files.add(new ListFileData(res.getString("external_uuid"), res.getString("name"), res.getInt("size")));
            }

            conn.close();
            
            return files;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public String addFile(String userUUID, FileForUploadWithoutData file) throws SQLException {
        Connection conn = null;
        ResultSet res = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            PreparedStatement query = conn.prepareStatement("insert into file (name, size) values (?, ?) returning external_uuid");
            query.setString(1, file.getFileName());
            query.setLong(2, file.getSize());
            res = query.executeQuery();

            conn.commit();

            res.next();

            return res.getString("external_uuid");
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    @Override
    public void removeFile(final String fileUUID) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            PreparedStatement query = conn.prepareStatement("delete from file where external_uuid = ?");
            query.setString(1, fileUUID);
            query.execute();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    @Override
    public boolean hasFile(final String userUUID, final String fileUUID) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            PreparedStatement getFileId = conn.prepareStatement("select id from file where external_uuid::text = ?");
            getFileId.setString(1, fileUUID);
            ResultSet res = getFileId.executeQuery();
            res.next();

            PreparedStatement query = conn.prepareStatement("select from permission where user_uuid = ? and file_id = ? and 'file:viewer' = any(roles) or 'file:editor' = any(roles)");
            query.setString(1, userUUID);
            query.setInt(2, res.getInt("id"));
            query.execute();

            conn.commit();

            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    @Override
    public void addPermissions(final String fileUUID, final String targetUserUUID, final String[] newPermissions) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            PreparedStatement getFileId = conn.prepareStatement("select id from file where external_uuid::text = ?");
            getFileId.setString(1, fileUUID);
            ResultSet res = getFileId.executeQuery();
            res.next();

            PreparedStatement query = conn.prepareStatement("insert into permission values (?, ?, ?)");
            query.setString(1, targetUserUUID);
            query.setInt(2, res.getInt("id"));
            query.setArray(3, conn.createArrayOf("text", newPermissions));
            query.execute();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }

    @Override
    public void removePermissions(String fileUUID, String targetUserUUID) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            PreparedStatement getFileId = conn.prepareStatement("select id from file where external_uuid::text = ?");
            getFileId.setString(1, fileUUID);
            ResultSet res = getFileId.executeQuery();
            res.next();

            PreparedStatement query = conn.prepareStatement("delete from permission where user_uuid::text = ? and file_id = ?");
            query.setString(1, targetUserUUID);
            query.setInt(2, res.getInt("id"));
            query.execute();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
    }
    
}
