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

package kyungseo.poc.simple.web.appcore.data.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class StringYnTypeHandler implements TypeHandler<Boolean> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public Boolean getResult(ResultSet rs, String columnName)
			throws SQLException {
		String s = rs.getString(columnName);

		return parseBoolean(s);
	}

	public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
		String s = rs.getString(columnIndex);

		return parseBoolean(s);
	}

	public Boolean getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		String s = cs.getString(columnIndex);

		return parseBoolean(s);
	}

	public void setParameter(PreparedStatement ps, int i, Boolean bool,
			JdbcType jdbcType) throws SQLException {

		ps.setString(i, parseString(bool));
	}

	// "Y" or "N" -> true or false
	private boolean parseBoolean(String s) {

		boolean bool = false;

		if (s == null) {
			return false;
		}

		s = s.trim().toUpperCase();

		if (s.length() == 0) {
			return false;
		}

		// allow "Y" or "N"
		if ("Y".equals(s) == false && "N".equals(s) == false) {
			throw new PersistenceException("value must be \"Y\" or \"N\".");
		}

		bool = "Y".equals(s);

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("\"" + s + "\" -> " + bool);
		}

		return bool;
	}

	// true or false -> "Y" or "N"
	private String parseString(Boolean bool) {

		String s = (bool != null && bool == true) ? "Y" : "N";

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug(bool + " -> " + "\"" + s + "\"");
		}

		return s;
	}

}
