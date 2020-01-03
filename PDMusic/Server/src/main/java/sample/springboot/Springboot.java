package sample.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sample.communication.files.ServerFileManager;

@SpringBootApplication
public class Springboot {

    public static void main(String[] args) {
        new ServerFileManager();
        SpringApplication.run(Springboot.class, args);
    }

}
