/* ============================================================================
 * [ Development Templates based on Spring Boot ]
 * ----------------------------------------------------------------------------
 * Copyright 2023 Kyungseo Park <Kyungseo.Park@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================================
 * Author     Date            Description
 * --------   ----------      -------------------------------------------------
 * Kyungseo   2023-03-02      initial version
 * ========================================================================= */

package kyungseo.poc.simple.web.site.common.config;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import kyungseo.poc.simple.web.appcore.AppConstants;
import kyungseo.poc.simple.web.appcore.formatter.LocalDateFormatter;
import kyungseo.poc.simple.web.appcore.formatter.LocalDateTimeFormatter;
import kyungseo.poc.simple.web.appcore.interceptor.LoggerInterceptor;
import kyungseo.poc.simple.web.appcore.interceptor.SessionTimerInterceptor;
import kyungseo.poc.simple.web.appcore.interceptor.UserInterceptor;
import kyungseo.poc.simple.web.appcore.interceptor.ViolationInterceptor;
import kyungseo.poc.simple.web.security.validation.EmailValidator;
import kyungseo.poc.simple.web.security.validation.PasswordMatchesValidator;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Configuration
@ComponentScan(basePackages = { "kyungseo.poc.simple.web.security.web" })
@EnableWebMvc // Spring MVC를 활성화
public class WebMvcConfig implements WebMvcConfigurer {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource messageSource;

    public WebMvcConfig() {
        super();
    }

    private final String[] CLASSPATH_RESOURCE_LOCATIONS = {
         "classpath:/static/"
        ,"classpath:/public/"
        ,"classpath:/resources/"
        ,"classpath:/META-INF/resources/"
        ,"classpath:/META-INF/resources/webjars/"
    };

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // TODO: 단순 View용 포워딩 용도의 URL들, 정리할 것!
        registry.addViewController("/"         ).setViewName(AppConstants.HOME_VIEW_NAME);
        registry.addViewController("/home"     ).setViewName(AppConstants.HOME_VIEW_NAME);
        registry.addViewController("/view/home").setViewName(AppConstants.HOME_VIEW_NAME);

        //registry.addViewController("/").setViewName("forward:" + AppConstants.AUTH_LOGIN_URL);

        registry.addViewController("/view/common/sec/registration");
        registry.addViewController("/view/common/sec/registrationCaptcha");
        registry.addViewController("/view/common/sec/registrationReCaptchaV3");
        registry.addViewController("/view/common/sec/logout");
        registry.addViewController("/view/common/sec/expiredAccount");
        registry.addViewController("/view/common/sec/emailError");
        registry.addViewController("/view/common/sec/invalidSession");
        registry.addViewController("/view/common/sec/successRegister");
        registry.addViewController("/view/common/sec/forgetPassword");
        registry.addViewController("/view/common/sec/updatePassword");
        registry.addViewController("/view/common/sec/changePassword");
        registry.addViewController("/view/common/sec/users");
        registry.addViewController("/view/common/sec/qrcode");

        // 우선순위를 가장 높게...
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return (factory) -> {
            factory.setRegisterDefaultServlet(true);
            factory.addErrorPages(
                    new ErrorPage(HttpStatus.BAD_REQUEST,           AppConstants.ERROR_400_URL),
                    new ErrorPage(HttpStatus.UNAUTHORIZED,          AppConstants.ERROR_401_URL),
                    new ErrorPage(HttpStatus.FORBIDDEN,             AppConstants.ERROR_403_URL),
                    new ErrorPage(HttpStatus.NOT_FOUND,             AppConstants.ERROR_404_URL),
                    new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, AppConstants.ERROR_500_URL));
        };
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        // 고정적인 리소스에 대한 요청을 직접 처리하지 않고, 서블릿 컨테이너의 디폴트 서블릿 전달 요청
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/**").addResourceLocations(this.CLASSPATH_RESOURCE_LOCATIONS);
            //.addResourceHandler("/resources/**").addResourceLocations("/", "/resources/");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // 전송된 lang 매개변수 의 값을 기반으로 로케일을 변경
        // <a href="?lang=en">English</a>
        // <a href="?lang=ko_KR">한글</a>
        // <a href="?lang=es_ES">Spanish</a>
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName(AppConstants.LOCALE_CHANGE_PARAM_NAME);
        registry.addInterceptor(localeChangeInterceptor);

        // Double Submission 방지
        registry.addInterceptor(new ViolationInterceptor());
        //registry.addInterceptor(new ViolationInterceptor()).addPathPatterns("/**");

        // Request 정보 로깅
        registry.addInterceptor(new LoggerInterceptor());

        // session 또는 model에 Username 세팅
        registry.addInterceptor(new UserInterceptor());

        // session.lastAccessedTime 이후 일정시간 동안 아무 작업이 없으면 로그아웃 시킴
        registry.addInterceptor(new SessionTimerInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
        registry.addFormatter(new LocalDateTimeFormatter());

        NumberStyleFormatter numberFormatter = new NumberStyleFormatter();
        numberFormatter.setPattern("#,###,###,###.##");
        registry.addFormatter(numberFormatter);
    }

    // LocalValidatorFactoryBean을 등록할 경우,
    // application.properties에 다음 설정을 추가해야한다.
    //
    // spring.jpa.properties.javax.persistence.validation.mode=none
    //
    // 참고: https://stackoverflow.com/questions/23604540/spring-boot-how-to-properly-inject-javax-validation-validator/23615478#23615478
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }
    /*
    @Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean(final MessageSource messageSource) {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setValidationMessageSource(messageSource);
		return localValidatorFactoryBean;
	}
	*/

	@Bean
	public ObjectFactory<Object> objectFactory() throws Exception {
		final ObjectFactoryCreatingFactoryBean objectFactory = new ObjectFactoryCreatingFactoryBean();
		objectFactory.setTargetBeanName(AppConstants.SESSION_SCOPE_MODEL_NAME);
		return objectFactory.getObject();
	}

	/*
	@Bean
	public FilterRegistrationBean<HttpMethodOverrideFilter> filterRegistrationBean() {
		FilterRegistrationBean<HttpMethodOverrideFilter> filter = new FilterRegistrationBean<>();
		filter.setFilter(httpMethodOverrideFilter());
		return filter;
	}

    @Bean
    public HttpMethodOverrideFilter httpMethodOverrideFilter() {
        return new HttpMethodOverrideFilter();
    }
	*/

    // CookieLocaleResolver
    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.KOREAN);
        //cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }
    // SessionLocaleResolver
    /*
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        return sessionLocaleResolver;
    }
    */

    /*
    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/jsp/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);

        return viewResolver;
    }
    */

    @Bean
    public EmailValidator usernameValidator() {
        return new EmailValidator();
    }

    @Bean
    public PasswordMatchesValidator passwordMatchesValidator() {
        return new PasswordMatchesValidator();
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    // Thymeleaf에서 sec속성을 사용할 수 있도록 SpringSecurityDialect를 Bean으로 등록
    @Bean
    public SpringSecurityDialect securityDialect(){
        return new SpringSecurityDialect();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    /*
    // REST 서비스를 위한 MessageConverter 재정의 - START
    //private final Gson gson = new Gson();
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
        final Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder
            .indentOutput(true)
            .dateFormat(new SimpleDateFormat(AppConstants.DATETIME_FORMAT));
        messageConverters.add(new MappingJackson2HttpMessageConverter(
                builder.build()));
        messageConverters.add(new MappingJackson2XmlHttpMessageConverter(
                builder.createXmlMapper(true).build()));

        //GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        //gsonHttpMessageConverter.setGson(gson);
        //messageConverters.add(gsonHttpMessageConverter);

        messageConverters.add(createXmlHttpMessageConverter());
        //messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
    }

    private HttpMessageConverter<Object> createXmlHttpMessageConverter() {
        final MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();

        final XStreamMarshaller xstreamMarshaller = new XStreamMarshaller();
        xmlConverter.setMarshaller(xstreamMarshaller);
        xmlConverter.setUnmarshaller(xstreamMarshaller);

        return xmlConverter;
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        //configurer
        //    .favorParameter(true)
        //    .parameterName("mediaType")
        //    .ignoreAcceptHeader(true)
        //    .defaultContentType(MediaType.APPLICATION_JSON)
        //    .mediaType("xml", MediaType.APPLICATION_XML)
        //    .mediaType("json", MediaType.APPLICATION_JSON);

        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
    // REST 서비스를 위한 MessageConverter 재정의 - END
    */

}
