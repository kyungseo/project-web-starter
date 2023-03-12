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

package kyungseo.poc.simple.web.appcore.data.mybatis.interceptor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

/**
 * @author 박경서 (Kyungseo.Park@gmail.com)
 * @version 1.0
 */
@Intercepts({ @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = { Statement.class }) })
public class ResultSetInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		List actual = new ArrayList();

		Object[] args = invocation.getArgs();
		Statement statement = (Statement) args[0];
		ResultSet rs = statement.getResultSet();

		while (rs == null) {
			if (statement.getMoreResults()) {
				rs = statement.getResultSet();
			}
			else {
				if (statement.getUpdateCount() == -1) {
					break;
				}
			}
		}

		if (rs != null) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			Object[] metaData = new Object[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				Map columnMeta = new LinkedHashMap();
				String columnName = rsmd.getColumnName(i);
				String columnLabel = rsmd.getColumnLabel(i);
				String columnType = rsmd.getColumnTypeName(i);
				columnMeta.put("columnName", columnName);
				columnMeta.put("columnLabel", columnLabel);
				columnMeta.put("columnType", columnType);
				metaData[i - 1] = columnMeta;
			}
			actual.add(metaData);
		}

		/*
		if (rs != null) {
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			Map<String, Object> metaData = new HashMap<String, Object>();
			metaData.put(NxConstant.ONLY_METADATA_RETURN, NxConstant.ONLY_METADATA_RETURN);
			for (int i = 1; i <= columnCount; i++) {
				metaData.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
			}
			actual.add(metaData);
		}
		*/

		Object results = invocation.proceed();
		actual.add(results);
		return actual;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// To change body of implemented methods use File | Settings | File Templates.
	}

}
