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

package kyungseo.poc.simple.web.site.sample.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kyungseo.poc.simple.web.site.sample.model.Crud;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Service
//@Transactional
public class CrudMockServiceImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public static ModelMapper modelMapper;

    // DB repository mock
    private Map<Long, Crud> repository = Arrays.asList(
        new Crud[] {
                Crud.builder()
                    .id(1L)
                    .title("제목1")
                    .body("본문1")
                    .build(),
                    Crud.builder()
                    .id(2L)
                    .title("제목2")
                    .body("본문2")
                    .build(),
                    Crud.builder()
                    .id(3L)
                    .title("제목3")
                    .body("본문3")
                    .build()
        }).stream()
        .collect(Collectors.toConcurrentMap(s -> s.getId(), Function.identity()));

    // DB id sequence mock
    private AtomicLong sequence = new AtomicLong(3);

    public List<Crud> readAll() {
        return repository.values().stream().collect(Collectors.toList());
    }

    public Crud read(final Long id) {
        return repository.get(id);
    }

    public Crud create(Crud vo) {
        long key = sequence.incrementAndGet();
        vo.setId(key);
        repository.put(key, vo);
        return vo;
    }

    public Crud update(Long id, Crud vo) {
        vo.setId(id);
        Crud oldUser = repository.replace(id, vo);
        return oldUser == null ? null : vo;
    }

    public void delete(final Long id) {
        repository.remove(id);
    }

}
