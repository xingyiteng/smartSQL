package com.iteng.startup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger配置
 * @author iteng
 * @date 2023-12-29 18:59
 */
@Configuration
@Profile({"dev"})
public class SwaggerConfig {

    @Bean(value = "swaggerApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 这里一定要标注你控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.iteng.startup.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("smartSQL")
                .description("smartSQL接口文档")
                .termsOfServiceUrl("http://localhost:8080/api/doc.html")
                .contact(new Contact("iteng","https://blog.csdn.net/weixin_44147535","iteng@qq.com"))
                .version("1.0")
                .build();
    }
}
