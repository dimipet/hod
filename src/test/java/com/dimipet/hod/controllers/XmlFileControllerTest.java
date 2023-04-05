package com.dimipet.hod.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class XmlFileControllerTest {
	
	@Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;
    
    @Autowired
    private MessageSource messageSource;

    @Test
    void checkUploadAndValidateGoodXmlFile() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ClassPathResource resource = new ClassPathResource("excelfiles/20230130T000000-good.xml");
        FileSystemResource file = new FileSystemResource(resource.getFile());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "http://localhost:" + port + "/xml/validate",
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(messageSource.getMessage("xml.uploaded.validated.successfully", null, LocaleContextHolder.getLocale()), responseEntity.getBody());
    }

    @Test
    void checkUploadAndValidateBadXmlFile() throws Exception {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    	
    	ClassPathResource resource = new ClassPathResource("excelfiles/20230130T000000-bad.xml");
    	FileSystemResource file = new FileSystemResource(resource.getFile());
    	
    	MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    	body.add("file", file);
    	
    	HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    	
    	ResponseEntity<String> responseEntity = restTemplate.exchange(
    			"http://localhost:" + port + "/xml/validate",
    			HttpMethod.POST,
    			requestEntity,
    			String.class
    			);
    	
    	Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    	Assertions.assertEquals(messageSource.getMessage("xml.not.validated", null, LocaleContextHolder.getLocale()), responseEntity.getBody());
    }
}