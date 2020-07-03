package com.gi.rhapp.utilities;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Component
public class FilenameUtils {
    // helper function to get extension from file name
    public static String getExtension (MultipartFile file) {
        return file.getOriginalFilename().split("\\.")[1];
    }

    // helper function to get file name without extention
    public static String getFilename (MultipartFile file) {
        return file.getOriginalFilename().split("\\.")[0];
    }
}
