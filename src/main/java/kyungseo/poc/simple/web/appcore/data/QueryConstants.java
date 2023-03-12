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

package kyungseo.poc.simple.web.appcore.data;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public final class QueryConstants {

	public static final String QUESTIONMARK = "?";

	public static final String OFFSET = "offset";
	public static final String LIMIT = "limit";
	public static final String SORT_BY = "sortBy";
	public static final String SORT_ORDER = "sortOrder";
	public static final String Q_SORT_BY = QUESTIONMARK + SORT_BY + QueryConstants.OP;
	public static final String S_ORDER = QueryConstants.SEPARATOR_AMPER + QueryConstants.SORT_ORDER + QueryConstants.OP;
	public static final String S_ORDER_ASC = S_ORDER + "ASC";
	public static final String S_ORDER_DESC = S_ORDER + "DESC";

	public static final String ANY_SERVER = "%";
	public static final String ANY_CLIENT = "*";
	public static final String QUERY_PREFIX = QUESTIONMARK + "q=";
	public static final String Q_PARAM = "q";
	public static final String SEPARATOR = ",";
	public static final String SEPARATOR_AMPER = "&";
	public static final String OP = "=";
	public static final String NEGATION = "~";

	public static final String ID = "id";
	public static final String UUID = "uuid";

	// QueryID
    public static final String CREATE_QUERY_ID          = ".create";
    public static final String UPDATE_QUERY_ID          = ".update";
    public static final String DELETE_QUERY_ID          = ".delete";
    public static final String GET_QUERY_ID             = ".get";
    public static final String GET_LIST_QUERY_ID        = ".getList";
    public static final String GET_LIST_COUNT_QUERY_ID  = ".getListCnt";
    public static final String GET_ALL_QUERY_ID         = ".getAll";
    public static final String GET_ALL_COUNT_QUERY_ID   = ".getAllCnt";
    public static final String GET_LIST_EXCEL_QUERY_ID  = ".getList4Excel";

    // MyBatis
    public static final String PARAMETER_MAP_ID = "parameterMap";
    public static final String RESULT_MAP_ID = "resultMap";

	private QueryConstants() {
		throw new AssertionError();
	}

	//

}
