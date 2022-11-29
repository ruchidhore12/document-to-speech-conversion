/*
 * Copyright 2022 Ruchi Dhore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */

package com.example.backendapp2.controller;

import com.example.backendapp2.service.S3FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin("*")
@RestController
public class FileController {

    @Autowired
    private S3FileUploadService service;

    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void getFile(@RequestParam MultipartFile file) throws IOException {
        file.transferTo(getFilePath(file));
        uploadFileToS3(file);
    }

    @GetMapping(value = "/file/find/")
    public Map<String, String> getAudio(@RequestParam("filename") String filename) {
        Map<String, String> map = new HashMap<>();
        if (filename.length() > 1) {
            if (service.search("/tmp/" + filename)) {
                map.put("found", "audio file present");
            } else {
                map.put("found", "audio file not generated yet");
            }
        } else {
            map.put("found", "filename is empty");
        }
        return map;
    }

    private void uploadFileToS3(MultipartFile file) {
        String uploadedFileName = service.uploadFile(file);
        System.out.println("Uploaded file's name :: " + uploadedFileName);
    }

    private Path getFilePath(MultipartFile file) {
        Path currentRelativePath = Paths.get("");
        String path = currentRelativePath.toAbsolutePath().toString();
        return Paths.get(path, file.getOriginalFilename());
    }
}
