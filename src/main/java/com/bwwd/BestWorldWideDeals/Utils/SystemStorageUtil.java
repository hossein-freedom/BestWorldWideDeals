package com.bwwd.BestWorldWideDeals.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Repository
public class SystemStorageUtil {

    private static Logger logger = LoggerFactory.getLogger(SystemStorageUtil.class);

    public static void init() {
        try {
            Files.createDirectory(Constants.SYSTEM_IMAGES_ROOT_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public static String  save(String directory, MultipartFile file) {
        logger.info(String.format("Received request to save file => %s to system",file.getName()));
        try {
            if(!Files.isDirectory(Constants.SYSTEM_IMAGES_ROOT_PATH.resolve(directory))){
                Files.createDirectory(Constants.SYSTEM_IMAGES_ROOT_PATH.resolve(directory));
            }
            Files.copy(file.getInputStream(),
                    Constants.SYSTEM_IMAGES_ROOT_PATH.resolve(String.format("%s/%s",directory,file.getOriginalFilename())));
            logger.info(String.format("Successfully  saved file => %s to system in the directory %s."
                    ,file.getName(), directory));
            return Constants.SYSTEM_IMAGES_ROOT_PATH.resolve(String.format("%s/%s",directory,file.getOriginalFilename())).toString();
        } catch (Exception e) {
            logger.error(String.format("Failed to save file => %s to system in the directory %s."
                    ,file.getName(), directory));
            e.printStackTrace();
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public static void deleteAllInDirectory(Path path) {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    public static void deleteAll() {
        FileSystemUtils.deleteRecursively(Constants.SYSTEM_IMAGES_ROOT_PATH.toFile());
    }


}
