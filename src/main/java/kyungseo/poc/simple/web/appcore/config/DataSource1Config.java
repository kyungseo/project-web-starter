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
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
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
@ConfigurationProperties(prefix = "spring.datasource." + DatabaseConfig.DATASOURCE1_NAME)
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = DatabaseConfig.DATASOURCE1_NAME + "EntityManagerFactory",
        transactionManagerRef = DatabaseConfig.DATASOURCE1_NAME + "TransactionManager",
        // "kyungseo.poc.simple.web.**.persistence.repository.ds1"
        basePackages = { DatabaseConfig.REPOSITORY_PACKAGE_PREFIX + DatabaseConfig.DATASOURCE1_NAME }
    )
@MapperScan(
        sqlSessionTemplateRef  = DatabaseConfig.DATASOURCE1_NAME + "SqlSessionTemplate",
        // "kyungseo.poc.simple.web.**.mapper.ds1"
        basePackages = { DatabaseConfig.MAPPER_PACKAGE_PREFIX + DatabaseConfig.DATASOURCE1_NAME }
    )
//@PropertySource("classpath:application.properties")
public class DataSource1Config extends DatabaseConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    // "kyungseo.poc.simple.web.**.persistence.entity.ds1"
    private final String[] ENTITY_TO_SCAN =
            new String[] { DatabaseConfig.ENTITY_PACKAGE_PREFIX + DatabaseConfig.DATASOURCE1_NAME };

    @Value("${spring.datasource.ds1.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.ds1.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.datasource.ds1.username}")
    private String userName;

    @Value("${spring.datasource.ds1.password}")
    private String password;

    /*
    // DriverManagerDataSource를 하단 LazyConnectionDataSourceProxy로 대체
    // DataSource =========================================
    @Bean(name = DatabaseConfig.DATASOURCE1_NAME + "DataSource")
    @Primary
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
    @Bean(name = DatabaseConfig.DATASOURCE1_NAME + "DataSource")
    @Primary
    public DataSource ds1DataSource() {
        // LazyConnectionDataSourceProxy를 사용하면 트랜잭션이 시작 되더라도
        // 실제로 커넥션이 필요한 경우에만 데이터소스에서 커넥션을 반환한다.
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        // dataSource.setMinimumIdle(0);  // 최소 커넥션 개수
        // dataSource.setIdleTimeout(10); // 유지시간
        return new LazyConnectionDataSourceProxy(dataSource);

        // 테이블 생성 및 CUD 초기화 작업은 필요시 @Primary에서...
        //Resource initSchema = new ClassPathResource("schema.sql");
        //Resource initData = new ClassPathResource("data.sql");
        //DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema, initData);
        //DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }

    /*
    // Service에서 'ds1DataSource' DI 후 다음 코드로 connestion status를 테스트
    private void logConnectionStatus() {
        HikariPoolMXBean hikariPoolMXBean = ((HikariDataSource) ((LazyConnectionDataSourceProxy) ds1DataSource)
                .getTargetDataSource()).getHikariPoolMXBean();
        LOGGER.info("==================================================");
        LOGGER.info("현재 active인 connection의 수 : {}",hikariPoolMXBean.getActiveConnections());
        LOGGER.info("현재 idle인 connection의 수 : {}",hikariPoolMXBean.getIdleConnections());
        LOGGER.info("==================================================");
    }
    */

    // MyBatis ============================================
    @Bean(name = DatabaseConfig.DATASOURCE1_NAME + "SqlSessionFactory")
    @Primary
    public SqlSessionFactory getSqlSessionFactory(
            @Qualifier(DatabaseConfig.DATASOURCE1_NAME + "DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        setConfigureSqlSessionFactory(sessionFactoryBean, dataSource);
        return sessionFactoryBean.getObject();
    }

    @Bean(name = DatabaseConfig.DATASOURCE1_NAME + "SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate getSqlSessionTemplate(
            @Qualifier(DatabaseConfig.DATASOURCE1_NAME + "SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    // JPA ================================================
    @Bean(name = DatabaseConfig.DATASOURCE1_NAME + "EntityManagerFactory")
    @Primary
    public EntityManagerFactory entityManagerFactory(
            @Qualifier(DatabaseConfig.DATASOURCE1_NAME + "DataSource") DataSource dataSource) {
       LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
       em.setDataSource(dataSource);
       em.setPackagesToScan(this.ENTITY_TO_SCAN);
       em.setPersistenceUnitName(DatabaseConfig.DATASOURCE1_NAME + "PersistanceUnit");
       setConfigureEntityManagerFactory(em);

       return em.getObject();
    }

    @Bean(name = DatabaseConfig.DATASOURCE1_NAME + "TransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier(DatabaseConfig.DATASOURCE1_NAME + "EntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}
