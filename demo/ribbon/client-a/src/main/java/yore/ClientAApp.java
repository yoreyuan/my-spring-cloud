package yore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * java -jar client-a-1.0-SNAPSHOT.jar --spring.profiles.active=node1
 *
 * Created by yore on 2019/7/7 18:59
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
public class ClientAApp {

    public static void main(String[] args) {
//        SpringApplication.run(ClientAApp.class, args);

        // --spring.profiles.active=node1，每次启动将node1改为node2或node3，就能启动多个服务实例
        SpringApplication.run(ClientAApp.class, "--spring.profiles.active=node1");
    }


    public static final String VERSION = "1.0.0";
    public static final String SWAGGER_SCAN_BASE_PACKAGE = "yore.controller";

    @Bean
    public Docket swaggerPersonApi10() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(
                        new ApiInfoBuilder()
                                .version(VERSION)
                                .title("Original Service API")
                                .description("Original Service API v" + VERSION)
                                .build()
                );
    }

}
