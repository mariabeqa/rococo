package guru.qa.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RococoGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RococoGatewayApplication.class, args);
    }

}
