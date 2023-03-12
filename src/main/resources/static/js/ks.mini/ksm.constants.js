/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > CONST
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-10-23      initial version
 * ========================================================================= */

//============================================================================
// KSM Constants
//============================================================================

/*
var KSM_CONST = function() {
	//const HTTP_GET_METHOD     = 'GET';
	//const HTTP_POST_METHOD    = 'POST';
	//const HTTP_PUT_METHOD     = 'PUT';
	//const HTTP_DELETE_METHOD  = 'DELETE';
};

KSM_CONST.prototype.HTTP_GET_METHOD     = 'GET';
KSM_CONST.prototype.HTTP_POST_METHOD    = 'POST';
KSM_CONST.prototype.HTTP_PUT_METHOD     = 'PUT';
KSM_CONST.prototype.HTTP_DELETE_METHOD  = 'DELETE';
*/

const KSM_CONST = {
	HTTP_GET_METHOD     : 'GET',
	HTTP_POST_METHOD    : 'POST',
	HTTP_PUT_METHOD     : 'PUT',
	HTTP_DELETE_METHOD  : 'DELETE'
};

//============================================================================
// Project Constants
//============================================================================

// TODO: porject constants 파일로 분리할 것.

const BACKEND_CONST = {
	API_PROTOCOL        : 'http://',
	API_SECURE_PROTOCOL : 'https://',
	API_HOST            : 'localhost', // api.domain.com
	API_PORT            : '8080',
	API_CONTEXT_ROOT    : '/'
};

const FRONTEND_CONST = {
	FE_PROTOCOL         : 'http://',
	FE_SECURE_PROTOCOL  : 'https://',
	FE_HOST             : 'localhost',  // m.domain.com
	FE_PORT             : '8080',
	FE_CONTEXT_ROOT     : '/',
	FE_FILE_URL    		: 'mobile/file.do?method='
};
