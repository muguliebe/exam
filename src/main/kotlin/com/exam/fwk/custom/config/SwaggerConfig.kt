package com.exam.fwk.custom.config

import com.google.common.base.Predicates
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Parameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfig(
        @Value("\${app.version}") val version: String,
        @Value("\${app.host}") val host: String
) {

    @Bean
    @Lazy
    fun api(): Docket {
        val paramBuilder = ParameterBuilder()
        val params = ArrayList<Parameter>()

        // 모든 api 에 authorization 헤더를 추가 합니다.
        paramBuilder.name("Authorization").modelRef(ModelRef("string"))
                .parameterType("header")
                .required(true).build()
        params.add(paramBuilder.build())

        return Docket(DocumentationType.SWAGGER_2)
                .host(host)
                .globalOperationParameters(params)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.exam"))
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .paths(PathSelectors.regex("(?!/error).+"))
                .paths(PathSelectors.regex("(?!/system).+"))
                .build()
                .apiInfo(apiInfo())
                .pathMapping("/")
    }

    fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("exam")
                .description("banking")
                .version(version)
                .build()
    }

}
