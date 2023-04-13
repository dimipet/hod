package com.dimipet.hod.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dimipet.sheetutils.XMLValidator;

@RestController
@RequestMapping("/xml")
public class XmlFileController {
	
	@Value("${spring.application.name}")
    String appName;
	
	@Autowired
    private MessageSource messageSource;

	@GetMapping("/")
	public ResponseEntity<String> getDummy() {
		return ResponseEntity.ok(messageSource.getMessage("ok", null, LocaleContextHolder.getLocale()));
	}

	@PostMapping("/validate")
	public ResponseEntity<String> validateXmlFile(@RequestParam("file") MultipartFile file) {

		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body(messageSource.getMessage("file.empty", null, LocaleContextHolder.getLocale()));
		}

		if (!file.getContentType().equals("application/xml")) {
			return ResponseEntity.badRequest().body(messageSource.getMessage("file.not.xml", null, LocaleContextHolder.getLocale()));
		}
		
		//create the file locally with .tmp suffix
		File convFile = new File(file.getOriginalFilename() + ".tmp");
		try {
			convFile.createNewFile();
			FileOutputStream fout = new FileOutputStream(convFile);
			fout.write(file.getBytes());
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 

		if (!XMLValidator.isValidated(convFile)) {
			return ResponseEntity.badRequest().body(messageSource.getMessage("xml.not.validated", null, LocaleContextHolder.getLocale()));
		}
		
		//delete the local file created
		convFile.delete();
		
		return ResponseEntity.ok(messageSource.getMessage("xml.uploaded.validated.successfully", null, LocaleContextHolder.getLocale()));
	}

}
