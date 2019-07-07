package yore.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by yore on 2019/7/6 15:53
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    public static final String SWAGGER_SCAN_BASE_PACKAGE = "yore.controller";
    public static final String VERSION = "1.0.0";


//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
//                .apis(RequestHandlerSelectors
//                        .basePackage("yore.controller"))
//                .paths(PathSelectors.any()).build();
//    }
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder().title("Feign多参数传递问题").description("Feign多参数传递问题")
//                .contact(new Contact("", "", "Software_King@qq.com"))
////                .contact()
//                .version("1.0").build();
//    }

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                /**api接口包扫描路径*/
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
                /**可以根据url路径设置哪些请求加入文档，忽略哪些请求*/
                .paths(PathSelectors.any())
                .build();

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                /**设置文档的标题*/
                .title("Swagger2 接口文档")
                /**设置文档的描述*/
                .description("Feign多参数传递问题")
                /**设置文档的联系方式*/
                .contact(new Contact("yore", "http://www.my.com", "xxxx.@email.com"))
                /**设置文档的License信息->1.3 License information*/
                .termsOfServiceUrl("www.xxx.com")
                .license("Lincese 1.0")
                .licenseUrl("http://www.lincese.xxx.com")
                /**设置文档的版本信息-> 1.1 Version information*/
                .version(VERSION)
                .build();
    }


}
