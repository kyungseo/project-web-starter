/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > MESSAGE
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-10-22      initial version
 * ========================================================================= */

//============================================================================
// Mesages
//============================================================================

var KSM_MESSAGE = {

	// 시스템 Core와 관련한 메지시 map
	systemErrorMessageMap : {
		'901': "현재 브라우저는 HTML5의 local storage 기능을 지원하지 않습니다.",
		'902': "유효하지않은 ACTION 값입니다.",
		'903': "유효하지않은 DATATYPE입니다."
	},

	// status와 관련한 메지시 map
	//   - https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
	statusErrorMessageMap : {
		  '0': "Not connected. 네트워크 연결을 확인하시기 바랍니다.",
		'400': "Bad Request. 잘못된 요청입니다.",
		'401': "Unauthorized. 인증되지 않은 사용자입니다. ",
		'403': "Forbidden. 권한이 없습니다.",
		'404': "Not Found. 요청한 서비스(API)를 찾을 수 없습니다.",
		'415': "Unsupported Media Type. 지원하지 않는 미디어 형식입니다.",
		'500': "Internal Server Error. 서버에서 예기치 않은 에러가 발생하였습니다.",
		'503': "Service Unavailable. 서비스가 불가합니다.",
		'999': "Unknown Error. 알 수 없는 에러가 발생하였습니다."
	},

	// Application(Project) 업무와 관련한 메시지 map
	applicationMessageMap : {
		'101': "정상적으로 조회되었습니다.",
		'105': "데이터가 존재하지 않습니다.",
		'201': "세션 시간이 만료되었습니다."
	}

};
