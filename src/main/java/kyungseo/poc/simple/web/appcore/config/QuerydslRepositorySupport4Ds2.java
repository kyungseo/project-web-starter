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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Repository
public abstract class QuerydslRepositorySupport4Ds2 extends QuerydslRepositorySupport {

    public QuerydslRepositorySupport4Ds2(Class<?> domainClass) {
        super(domainClass);
    }

    //@PersistenceContext(unitName = DatabaseConfig.DATASOURCE2_NAME + "EntityManagerFactory")
    @PersistenceContext(unitName = DatabaseConfig.DATASOURCE2_NAME + "EntityManager")
    public void setEntityManager(EntityManager ds2EntityManager) {
        super.setEntityManager(ds2EntityManager);
    }

}