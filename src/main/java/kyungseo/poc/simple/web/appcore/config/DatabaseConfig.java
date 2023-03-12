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

package kyungseo.poc.simple.web.appcore.config;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public abstract class DatabaseConfig extends HikariConfig {

    public static final String DATASOURCE1_NAME = "ds1";
    public static final String DATASOURCE2_NAME = "ds2";

    // TODO Repository 패키지 경로: 프로젝트에 맞춰 수정 필요
    public static final String MAPPER_PACKAGE_PREFIX     = "kyungseo.poc.simple.web.**.mapper.";
    public static final String REPOSITORY_PACKAGE_PREFIX = "kyungseo.poc.simple.web.**.repository.";
    public static final String ENTITY_PACKAGE_PREFIX     = "kyungseo.poc.simple.web.**.entity.";

    private final String MAPPER_LOCATION_PATTERN = "classpath*:mybatis/mapper/**/*Mapper.xml";

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    // 주의! Production 환경인 경우 무조건 'none' 값으로 설정할 것!
    @Value("${spring.jpa.properties.hibernate.hbm2ddl.auto}")
    private String hibernateDdlAuto;

    @Value("${spring.jpa.properties.hibernate.format_sql}")
    private String hibernateFormatSql;

    @Value("${spring.jpa.show-sql}")
    private String showSql;

    protected void setConfigureEntityManagerFactory(LocalContainerEntityManagerFactoryBean factory) {
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(ImmutableMap.of(
                "hibernate.hbm2ddl.auto", hibernateDdlAuto,
                "hibernate.dialect", hibernateDialect,
                "hibernate.show_sql", showSql,
                "hibernate.format_sql", hibernateFormatSql
        ));
        factory.afterPropertiesSet();
    }

    protected void setConfigureSqlSessionFactory(SqlSessionFactoryBean sessionFactoryBean, DataSource dataSource) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_LOCATION_PATTERN));
    }

}