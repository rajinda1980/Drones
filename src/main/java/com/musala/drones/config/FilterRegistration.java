package com.musala.drones.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter configuration class
 *
 * @author Rajinda
 * @version 1.0
 * @since 17/03/2024
 */
@Configuration
public class FilterRegistration {

    private ContentTypeFilter contentTypeFilter;

    public FilterRegistration(ContentTypeFilter contentTypeFilter) {
        this.contentTypeFilter = contentTypeFilter;
    }

    /**
     * Custom filter registration
     */
    @Bean
    public FilterRegistrationBean<ContentTypeFilter> registeringFilter() {
        FilterRegistrationBean<ContentTypeFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(contentTypeFilter);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        filterFilterRegistrationBean.setOrder(1);
        return filterFilterRegistrationBean;
    }
}
