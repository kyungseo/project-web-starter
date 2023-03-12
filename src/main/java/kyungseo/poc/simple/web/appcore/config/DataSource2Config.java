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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource." + DatabaseConfig.DATASOURCE2_NAME)
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = DatabaseConfig.DATASOURCE2_NAME + "EntityManagerFactory",
        transactionManagerRef = DatabaseConfig.DATASOURCE2_NAME + "TransactionManager",
        // "kyungseo.poc.simple.web.**.persistence.repository.ds2"
        basePackages = { DatabaseConfig.REPOSITORY_PACKAGE_PREFIX + DatabaseConfig.DATASOURCE2_NAME }
    )
@MapperScan(
        sqlSessionTemplateRef  = DatabaseConfig.DATASOURCE2_NAME + "SqlSessionTemplate",
        // "kyungseo.poc.simple.web.**.mapper.ds2"
        basePackages = { DatabaseConfig.MAPPER_PACKAGE_PREFIX + DatabaseConfig.DATASOURCE2_NAME }
    )
//@PropertySource("classpath:application.properties")
public class DataSource2Config extends DatabaseConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    // "kyungseo.poc.simple.web.**.persistence.entity.ds2"
    private final String[] ENTITY_TO_SCAN =
            new String[] { DatabaseConfig.ENTITY_PACKAGE_PREFIX + DatabaseConfig.DATASOURCE2_NAME };

    @Value("${spring.datasource.ds2.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.ds2.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.datasource.ds2.username}")
    private String userName;

    @Value("${spring.datasource.ds2.password}")
    private String password;

    /*
    // DriverManagerDataSource를 하단 LazyConnectionDataSourceProxy로 대체
    // DataSource =========================================
    @Bean(name = DatabaseConfig.DATASOURCE2_NAME + "DataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }
    */

    // DataSource =========================================
    @Bean(name = DatabaseConfig.DATASOURCE2_NAME + "DataSource")
    public DataSource ds2DataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        // dataSource.setMinimumIdle(0);  // 최소 커넥션 개수
        // dataSource.setIdleTimeout(10); // 유지시간
        return new LazyConnectionDataSourceProxy(dataSource);
    }

    /*
    // Service에서 'ds2DataSource' DI 후 다음 코드로 connestion status를 테스트
    private void logConnectionStatus() {
        HikariPoolMXBean hikariPoolMXBean = ((HikariDataSource) ((LazyConnectionDataSourceProxy) ds2DataSource)
                .getTargetDataSource()).getHikariPoolMXBean();
        LOGGER.info("==================================================");
        LOGGER.info("현재 active인 connection의 수 : {}",hikariPoolMXBean.getActiveConnections());
        LOGGER.info("현재 idle인 connection의 수 : {}",hikariPoolMXBean.getIdleConnections());
        LOGGER.info("==================================================");
    }
    */

    // MyBatis ============================================
    @Bean(name = DatabaseConfig.DATASOURCE2_NAME + "SqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(
            @Qualifier(DatabaseConfig.DATASOURCE2_NAME + "DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        setConfigureSqlSessionFactory(sessionFactoryBean, dataSource);
        return sessionFactoryBean.getObject();
    }

    @Bean(name = DatabaseConfig.DATASOURCE2_NAME + "SqlSessionTemplate")
    public SqlSessionTemplate getSqlSessionTemplate(
            @Qualifier(DatabaseConfig.DATASOURCE2_NAME + "SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    // JPA ================================================
    @Bean(name = DatabaseConfig.DATASOURCE2_NAME + "EntityManagerFactory")
    public EntityManagerFactory entityManagerFactory(
            @Qualifier(DatabaseConfig.DATASOURCE2_NAME + "DataSource") DataSource dataSource) {
       LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
       em.setDataSource(dataSource);
       em.setPackagesToScan(this.ENTITY_TO_SCAN);
       em.setPersistenceUnitName(DatabaseConfig.DATASOURCE2_NAME + "PersistanceUnit");
       setConfigureEntityManagerFactory(em);

       return em.getObject();
    }

    @Bean(name = DatabaseConfig.DATASOURCE2_NAME + "TransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier(DatabaseConfig.DATASOURCE2_NAME + "EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    /*
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    */

}
