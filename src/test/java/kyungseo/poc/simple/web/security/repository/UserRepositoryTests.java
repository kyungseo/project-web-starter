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

package kyungseo.poc.simple.web.security.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTests {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /*
	@Resource
    private UserRepository userRepository;

	@Test
	public void testSave() {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		String formattedDate = dateFormat.format(date);

		userRepository.save(new User("aa", "aa123456","aa@126.com", "aa"));
		userRepository.save(new User("bb", "bb123456","bb@126.com", "bb"));
		userRepository.save(new User("cc", "cc123456","cc@126.com", "cc"));

//		Assert.assertEquals(3, userRepository.findAll().size());
//		Assert.assertEquals("bb", userRepository.findByUserNameOrEmail("bb", "bb@126.com").getNickName());
//		userRepository.delete(userRepository.findByUserName("aa"));
	}


	@Test
	public void testBaseQuery() {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		String formattedDate = dateFormat.format(date);
		User user=new User("ff", "ff123456","ff@126.com", "ff");
		userRepository.findAll();
		userRepository.findById(3L);
		userRepository.save(user);
//		user.setId(2L);
		userRepository.delete(user);
		userRepository.count();
		userRepository.existsById(3L);
	}

	@Test
	public void testCustomSql() {
		userRepository.modifyById("neo",3L);
		userRepository.deleteById(3L);
		userRepository.findByEmail("ff@126.com");
	}

    @Test
    public void testPageQuery()  {
        int page=1,size=2;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        userRepository.findALL(pageable);
        userRepository.findByNickName("aa", pageable);
    }
    */
}