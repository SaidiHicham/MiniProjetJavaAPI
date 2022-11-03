package com.miniProjet.projet.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//import jdk.internal.loader.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;


public class FileUploadUtil {

    @Async
    public String saveFile(String fileName, MultipartFile multipartFile) throws IOException {


        //Path uploadPath = Paths.get("Files-Upload");
        Path uploadPath =  Paths.get("src/main/resources/json/");



        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileCode = RandomStringUtils.randomAlphanumeric(16);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            //Path filePath = uploadPath.resolve(fileCode + "-" + fileName);
            File fileToDelete = new File("src/main/resources/User.json");
            boolean success = fileToDelete.delete();
            Path filePath = uploadPath.resolve("User.json");
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }

        return fileCode;
    }
}
