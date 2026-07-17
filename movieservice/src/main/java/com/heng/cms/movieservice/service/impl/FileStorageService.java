package com.heng.cms.movieservice.service.impl;


import com.heng.cms.movieservice.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
	private final Path uploadPath = Paths.get("uploads/posters");

	public FileStorageService() {
		if(!Files.exists(uploadPath)) {
			try {
				Files.createDirectories(uploadPath);
			} catch (IOException e) {
				throw new BadRequestException("Could not create directory");
			}
		}
	}
	public String storeFile(MultipartFile file) {
		
		
		try {
			if(file.isEmpty()) {
				throw new BadRequestException("File is empty");
			}
			if( file.getContentType() == null || !file.getContentType().startsWith("image")) {
				throw new BadRequestException("Invalid file type");
			}
			
			String newFileName = getNewFileName(Objects.requireNonNull(file.getOriginalFilename()));
			
			Path targetLocation = uploadPath.resolve(newFileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return "uploads/posters/" + newFileName;
		} catch (IOException e) {
			throw new BadRequestException("Could not store file");
		}
	}


	public  String getNewFileName(String originalName) {
		String extension = "";
		int index = originalName.lastIndexOf(".");
		if( index>0 ) {
			extension = originalName.substring(index);
		}
		return UUID.randomUUID().toString() + extension;
	}
	
}
