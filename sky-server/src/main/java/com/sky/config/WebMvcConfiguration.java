package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("|> 开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**") //设定拦截请求路径为admin下的所有子url
                .excludePathPatterns("/admin/employee/login"); //但登录请求不拦截
    }

    /**
     * 通过knife4j生成接口文档
     *
     * @return 略
     */
    @Bean
    public Docket docket() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 设置静态资源映射(防止Swagger接口文档被拦截器拦截而无法访问)
     *
     * @param registry 略
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 给SpringMVC自带的消息转换器扩展一个我们自己定义的JSON对象转换器JacksonObjectMapper，该对象转换器中定义了正确的时间日期映射
     * - 目的是让LocalDateTime系列对象能转成格式正确的字符串给前端
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("|> 扩展SpringMVC消息转换器...");
        //新做一个消息转换器
        MappingJackson2HttpMessageConverter myConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，它可将Java对象转为JSON。把它传给消息转换器。
        myConverter.setObjectMapper(new JacksonObjectMapper()); //详见common模块json包下的JacksonObjectMapper
        //把做好的消息转换器add进SpringMVC的消息转换器集合(形参里注入的converters)
        converters.add(0, myConverter); //注意指定其索引为0，保证其被优先使用。否则被add到了List的最后一个
    }
}
