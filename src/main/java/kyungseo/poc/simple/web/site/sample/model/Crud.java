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

package kyungseo.poc.simple.web.site.sample.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Builder
@NoArgsConstructor
public class Crud {

    @NotNull
    private long id;

    @NotBlank
    private String title;

    private String body;

    private List<URI> tagUris;

    @JsonCreator
    public Crud(@JsonProperty("id") long id,
            @JsonProperty("title") String title,
            @JsonProperty("body") String body,
            @JsonProperty("tags") List<URI> tagUris) {
        this.id=id;
        this.title = title;
        this.body = body;
        this.tagUris = tagUris == null ? Collections.<URI> emptyList() : tagUris;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTagUris(List<URI> tagUris) {
        this.tagUris = tagUris;
    }

    @JsonProperty("tags")
    public List<URI> getTagUris() {
        return this.tagUris;
    }

}