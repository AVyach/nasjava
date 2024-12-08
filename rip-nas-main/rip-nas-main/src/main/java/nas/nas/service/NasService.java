package nas.nas.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nas.nas.exception.ExternalServiceError;
import nas.nas.model.FileForUploadData;
import nas.nas.model.FileForUploadWithoutData;
import nas.nas.model.ListFileData;
import nas.nas.model.ShareData;
import nas.nas.repository.FileRepository;
import nas.nas.repository.postgres.FileRepositoryImpl;
import sso.GetUserUUIDByNameRequest;
import sso.GetUserUUIDByNameResponse;
import sso.SSOGrpc;

public class NasService {
    private SSOGrpc.SSOBlockingStub authServer;
    private String uploadsDir = "uploads";
    private FileRepository fileRepo;
 
    public NasService() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 4001)
        .usePlaintext()
        .build();

        authServer = SSOGrpc.newBlockingStub(channel);

        fileRepo = new FileRepositoryImpl(
            "localhost",
            "5432",
            "nas",
            "postgres",
            "postgres"
        );

        new File(this.uploadsDir).mkdirs();
    }

    public boolean uploadFiles(final String userUUID, final ArrayList<FileForUploadData> files) {
        String fileUUID = "";
        try {
            for (FileForUploadData file : files) {
                fileUUID = this.fileRepo.addFile(userUUID, new FileForUploadWithoutData(file.getFileName(), file.getSize()));
                this.fileRepo.addPermissions(fileUUID, userUUID, new String[]{"file:owner", "file:viewer"});
    
                File newFile = new File(Paths.get(this.uploadsDir, fileUUID).toString());
                try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
                    outputStream.write(file.getData());
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);

            try {
                this.fileRepo.removeFile(fileUUID);
            } catch (SQLException e2) {
                System.err.println(e2);
            }
        }

        return false;
    }

    public ArrayList<ListFileData> getFilesList(final String userUUID) {
        ArrayList<ListFileData> files = new ArrayList<>();

        try {
            files = this.fileRepo.getFiles(userUUID);
        } catch (SQLException e) {
            System.err.println(e);
            return null;
        }

        return files;
    }

    public boolean share(final ShareData shareData) throws ExternalServiceError {
        try {
            GetUserUUIDByNameResponse resp;
            switch (shareData.getAction()) {
                case "add":
                    resp = this.authServer.getUserUUIDByName(GetUserUUIDByNameRequest.newBuilder()
                        .setUsername(shareData.getTargetUserName())
                        .build());

                    if (resp.getRespStatus().getStatus() == 0) {
                        this.fileRepo.addPermissions(shareData.getTargetFileUUID(), resp.getUserUuid(), new String[]{shareData.getNewPermission()});
                    } else {
                        throw new ExternalServiceError(String.format("%d", resp.getRespStatus().getStatus()));
                    }
                    break;
                
                case "remove":
                    resp = this.authServer.getUserUUIDByName(GetUserUUIDByNameRequest.newBuilder()
                        .setUsername(shareData.getTargetUserName())
                        .build());

                    if (resp.getRespStatus().getStatus() == 0) {
                        this.fileRepo.removePermissions(shareData.getTargetFileUUID(), resp.getUserUuid());
                    } else {
                        throw new ExternalServiceError(String.format("%d", resp.getRespStatus().getStatus()));
                    }
                    break;
            }

            return true;
        } catch(SQLException | ExternalServiceError e) {
            System.err.println(e);
            return false;
        }
    }

    public byte[] getFile(final String userUUID, final String fileUUID) {
        try {
            if (!this.fileRepo.hasFile(userUUID, fileUUID)) {
                return null;
            }

            return Files.readAllBytes(Paths.get(this.uploadsDir, fileUUID));
        } catch (SQLException | IOException e) {
            System.err.println(e);
            return null;
        }
    }
}
