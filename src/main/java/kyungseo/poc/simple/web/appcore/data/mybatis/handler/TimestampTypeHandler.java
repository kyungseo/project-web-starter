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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
public class TimestampTypeHandler implements TypeHandler<Date> {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	// simple date format
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Date getResult(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return parseDate(s);
	}

	public Date getResult(ResultSet rs, int columnIndex) throws SQLException {
		String s = rs.getString(columnIndex);
		return parseDate(s);
	}

	public Date getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		String s = cs.getString(columnIndex);
		return parseDate(s);
	}

	public void setParameter(PreparedStatement ps, int i, Date d,
			JdbcType jdbcType) throws SQLException {
		ps.setTimestamp(i, new Timestamp(d.getTime()));
	}

	// string "yyyy-MM-dd HH:mm:ss" -> date
	private Date parseDate(String s) {

		if (s == null) {
			return null;
		}

		s = s.trim();

		if (s.length() == 0) {
			return null;
		}

		Date d = null;
		try {
			d = sdf.parse(s);
		} catch (ParseException e) {
		    LOGGER.warn(e.getMessage());
		}

		if (LOGGER.isDebugEnabled()) {
		    LOGGER.info("string [\"" + s + "\"] -> date [" + d + "]");
		}

		return d;
	}

}
