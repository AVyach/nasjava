package nas.nas.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nas.nas.exception.ExternalServiceError;
import nas.nas.model.FileForUploadData;
import nas.nas.model.ListFileData;
import nas.nas.model.ShareData;
import nas.nas.model.TokenPayload;
import nas.nas.service.AuthService;
import nas.nas.service.NasService;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/files")
public class NasController {
    private AuthService authService = new AuthService();
    private NasService nasService = new NasService();

    @PostMapping("/upload")
    public boolean uploadFile(@RequestParam("files") MultipartFile[] files, @CookieValue("token") String token) {
        try {
            TokenPayload tokenPayload = this.authService.check(token);

            if (!tokenPayload.isTokenValid()) {
                return false;
            }

            ArrayList<FileForUploadData> filesForUpload = new ArrayList<FileForUploadData>();
            for (MultipartFile file : files) {
                filesForUpload.add(new FileForUploadData(file.getOriginalFilename(), file.getSize(), file.getBytes()));
            }
            if (!this.nasService.uploadFiles(tokenPayload.getUuid(), filesForUpload)) {
                return false;
            }

            return true;
        } catch (ExternalServiceError | IOException e) {
            System.out.println(e);

            return false;
        }
    }

    @GetMapping("/recieve")
    public ArrayList<ListFileData> getFiles(@CookieValue("token") String token) {
        try {
            TokenPayload tokenPayload = this.authService.check(token);

            if (!tokenPayload.isTokenValid()) {
                return null;
            }

            return nasService.getFilesList(tokenPayload.getUuid());
        } catch (ExternalServiceError e) {
            System.out.println(e);

            return null;
        }
    }

    @PostMapping("/share")
    public void shareFile(@RequestBody ShareData shareData, @CookieValue("token") String token) {
        try {
            TokenPayload tokenPayload = this.authService.check(token);

            if (!tokenPayload.isTokenValid()) {
                return;
            }

            this.nasService.share(shareData);
        } catch (ExternalServiceError e) {
            System.out.println(e);
        }
    }

    @GetMapping("/{uuid}")
    public byte[] getFile(@PathVariable(value="uuid") String fileUUID, @CookieValue("token") String token) {
        try {
            TokenPayload tokenPayload = this.authService.check(token);

            if (!tokenPayload.isTokenValid()) {
                return null;
            }

            return this.nasService.getFile(tokenPayload.getUuid(), fileUUID);
        } catch (ExternalServiceError e) {
            System.out.println(e);
            return null;
        }
    }
}
