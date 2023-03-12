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

package kyungseo.poc.simple.web.config;

import java.util.Set;
import java.util.TreeSet;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.registry.AlgorithmRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class JasyptTest {

    @Value("${jasypt.encryptor.password}")
    private String encryptKey;

    @Autowired
    private StringEncryptor jasyptStringEncryptor;

    private static final String ALGORITHM = "PBEWithMD5AndDES";

    @Test
    public void test01_availableJasyptAlgorithm() throws Exception {
        Set<String> supported = new TreeSet<>();
        Set<String> unsupported = new TreeSet<>();
        for (Object oAlgorithm : AlgorithmRegistry.getAllPBEAlgorithms()) {
            String algorithm = (String) oAlgorithm;
            try {
                SimpleStringPBEConfig pbeConfig = new SimpleStringPBEConfig();
                pbeConfig.setAlgorithm(algorithm);
                pbeConfig.setPassword("changeme");
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setConfig(pbeConfig);

                String encrypted = encryptor.encrypt("foo");
                String decrypted = encryptor.decrypt(encrypted);

                //Assert.assertEquals("foo", decrypted);

                supported.add(algorithm);
            } catch (EncryptionOperationNotPossibleException e) {
                unsupported.add(algorithm);
            }
        }
        System.out.println("Supported");
        supported.forEach(alg -> System.out.println("   " + alg));
        System.out.println("Unsupported");
        unsupported.forEach(alg -> System.out.println("   " + alg));
    }

    @Test
    public void test02_encryptPropertyValueByJasypt() throws Exception {
        String expected = "7Y+s7Iqk7L2USUNU7JqpSldUU2VjcmV0S2V5";
        String enc = this.jasyptStringEncryptor.encrypt(expected);
        String actual = this.jasyptStringEncryptor.decrypt(enc);

        //assertEquals(expected, actual);

        System.out.println(enc);
    }

    @Test
    public void test03_passwordEncryption() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptKey);
        config.setAlgorithm(ALGORITHM);
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        System.out.println("encryptKey : " + encryptKey);
        System.out.println("ALGORITHM : " + ALGORITHM);

        // 암호화할 대상 문자열
        String plainText = "developer";

        String encryptedPassword = encryptor.encrypt(plainText);
        System.out.println("encryptedPassword : " + encryptedPassword + " ( " + plainText + " )");
    }

}
