package it.uniroma3.siw.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ImageRepository;

@Service
public class StorageService {


    @Autowired
    private ImageRepository fileDataRepository;

    @Value("${file.upload.path}")
    private String folderPath;

    public Image uploadImageToFileSystem(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename().replace("(", "").replace(")", "").replace(" ", "_");
        String filePath = folderPath + fileName;
        String imagePath = "images/" + fileName;
        
        Image fileData = fileDataRepository.save(Image.builder()
                .name(fileName)
                .type(file.getContentType())
                .imagePath(imagePath).build());
                
        file.transferTo(new File(filePath));
        
        if (fileData != null) {
            return fileData;
        }
        
        return null;
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<Image> fileData = fileDataRepository.findByName(fileName);
        String filePath=fileData.get().getImagePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }



}
