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

package kyungseo.poc.simple.web.site.sample.web.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import kyungseo.poc.simple.web.site.sample.model.Crud;
import kyungseo.poc.simple.web.site.sample.service.CrudMockServiceImpl;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/view/sample/crud")
public class CrudRestController {

    @Autowired
    private CrudMockServiceImpl service;

    // 목록 조회
    @GetMapping("/")
    public List<Crud> read(@RequestBody @Valid Crud vo) {
        return service.readAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<Crud> read(@PathVariable("id") Long id) {
        Crud foundCrud = service.read(id);
        if (foundCrud == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(foundCrud);
        }
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Crud> create(@RequestBody @Valid Crud vo) {
        Crud createdCrud = service.create(vo);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdCrud.getId())
            .toUri();

        return ResponseEntity.created(uri).body(createdCrud);
    }

    @PutMapping("/{id}")
    //@ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Crud> update(@PathVariable("id") Long id, @RequestBody Crud vo) {
        Crud updatedCrud = service.update(id, vo);
        if (updatedCrud == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(updatedCrud);
        }
    }

    /*
    @PatchMapping("/{id}")
    public List<Crud> patch(@PathVariable("id") Long id, @RequestBody Crud vo) {
        List<Crud> returnList = new ArrayList<Crud>();
        vo.setId(id);
        returnList.add(vo);
        return returnList;
    }
    */

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exception")
    public ResponseEntity<Void> requestWithException() {
        throw new RuntimeException("Error in the faulty controller!");
    }

}