package io.renatofreire.passwordmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class PasswordManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasswordManagerApplication.class, args);
    }

}
