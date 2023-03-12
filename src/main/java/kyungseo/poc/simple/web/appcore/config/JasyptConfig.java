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

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Configuration
public class JasyptConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /*
     * 복호화 키값(jasypt.encryptor.password)는 Application 실행 시 외부 Environment 를 통해 주입받을 경우,
     * (@SpringBootApplication 어노테이션이 있는) Application.java 클래스 파일에
     * @EnableEncryptableProperties 어노테이션을 추가로 삽입해야 한다.
     *
     * 이후 해당 키 값을 tomcat의 arguments 에 설정하도록 한다.
     * -Djasypt.encryptor.password="XXXXXXXXXXXXXXXXXXXX"
     *
     * JAR 예 -Djasypt.encryptor.password=XXXXXXXXXXXXXXXXXXXX
     */
    @Value("${jasypt.encryptor.password}")
    private String encryptKey;

    //private static final String ALGORITHM = "PBEWithHmacSHA512AndAES_256";
    private static final String ALGORITHM = "PBEWithMD5AndDES";

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptKey); // 암호화 시 사용하는 키값
        config.setAlgorithm(ALGORITHM); // 암호화 알고리즘
        config.setKeyObtentionIterations(1000); // 반복할 해싱 횟수
        config.setPoolSize("1"); // 인스턴스 pool
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스
        //config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64"); // 인코딩 방식

        encryptor.setConfig(config);
        return encryptor;
    }

}
