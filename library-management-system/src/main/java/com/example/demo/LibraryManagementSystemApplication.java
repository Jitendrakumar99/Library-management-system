package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Note: Remove the exclude for MailSenderAutoConfiguration when email properties are configured
// Uncomment the line below if you're NOT configuring email (currently disabled by default)
// exclude = {org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration.class}
public class LibraryManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementSystemApplication.class, args);
	}

}
