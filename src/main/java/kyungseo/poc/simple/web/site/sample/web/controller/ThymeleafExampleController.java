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

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Controller
@RequestMapping("/view/sample/th")
public class ThymeleafExampleController {

    @GetMapping("/hello")
    public String index(ModelMap map) {
        map.addAttribute("message", "환영합니다.");
        return "view/sample/th/hello";
    }

    @GetMapping("/string")
    public String string(ModelMap map) {
        map.addAttribute("userName", "홍길동");
        return "view/sample/th/string";
    }

    @GetMapping("/if")
    public String ifunless(ModelMap map) {
        map.addAttribute("flag", "yes");
        return "view/sample/th/if";
    }

    @GetMapping("/url")
    public String url(ModelMap map) {
        map.addAttribute("type", "link");
        map.addAttribute("pageId", "springcloud/2023/01/16/");
        map.addAttribute("img", "https://www.kico.co.kr/resources/web/images/logo.png");
        return "view/sample/th/url";
    }

    @GetMapping("/eq")
    public String eq(ModelMap map) {
        map.addAttribute("name", "박경서");
        map.addAttribute("age", 30);
        map.addAttribute("flag", "yes");
        return "view/sample/th/eq";
    }

    @GetMapping("/switch")
    public String switchcase(ModelMap map) {
        map.addAttribute("sex", "woman");
        return "view/sample/th/switch";
    }

    @GetMapping(value = "/currency")
    public String exchange(
      @RequestParam(value = "amount", required = false) String amount,
      @RequestParam(value = "amountList", required = false) List amountList,
      Locale locale) {

        return "view/sample/th/currencies";
    }

}