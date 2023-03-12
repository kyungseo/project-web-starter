
/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > CORE
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-10-22      initial version
 * 박경서   2023-03-02      KSM_CORE.ajax 수정: Double Submit 방지 기능 추가
 * ========================================================================= */

// ============================================================================
// $.fn에 serializeObject 추가
// ============================================================================

// form 객체를 JSON Object로 변환하기 위한 용도
//   ex) $("form").serializeObject();
$.fn.serializeObject = function() {
    "use strict";

    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

/*
$(function(){
    "use strict";

    $.ajaxPrefilter(function(options, originalOptions, jqXHR) {
        // 인증 헤더 추가
        const jwt = sessionStorage.getItem("jwt");
        if (jwt !== null) {
            jqXHR.setRequestHeader("Authentication", jwt);
        }
        // REST 요청을 위한 Http Method Override
        if (options.type === "put" || options.type === "patch" || options.type === "delete") {
            var headerValue = options.type;
            jqXHR.setRequestHeader("X-HTTP-Method-Override", headerValue);
            options.type = "post";
        }
    });
});
*/

// ============================================================================
// Global Ajax Event Handlers | error handler 추가
// ============================================================================

// jQuery 문서 상 더이상 ajaxSetup을 사용하지 말 것을 강력히 권고하고 있음.
//   - http://api.jquery.com/ajaxError/
$(document).ajaxError(function(e, jqXHR, ajaxSettings, thrownError) {
	console.log('\t' + thrownError + ' (HTTP CODE: ' + jqXHR.status + ') - ' + ajaxSettings.url);

	var message = parseAjaxErrorMessage(jqXHR, thrownError);
	console.log('ajaxError >> errorMessage: ' + message);

	// Storage에 에러 메시지 저장 <- error.html 페이지에서 사용함
	try {
		KSM_STORAGE.storage({action:'save', key:'ajaxErrorMessage', value:message});
	}
	catch(e) {
		console.log('ajaxError >> Storage 저장 실패: ' + e.message);
	}
});

var parseAjaxErrorMessage = function(jqXHR, thrownError) {
	var message = '';

	if (jqXHR.status) {
		message = KSM_MESSAGE.statusErrorMessageMap[jqXHR.status];
		if (! message) {
			message = KSM_MESSAGE.statusErrorMessageMap['999'];
		}
	}
	else if (thrownError === 'parsererror') {
		message = "Error. JSON Request 파싱 실패";
	}
	else if (thrownError === 'timeout') {
		message = "Request 시간 초과";
	}
	else if (thrownError === 'abort') {
		message = "서버로부터 Request 거부";
	}
	else {
		message = KSM_MESSAGE.statusErrorMessageMap['999'];
	}
	return message;
};

//============================================================================
// KSM Basic Object
//============================================================================

var KSM_CORE = {

	// ------------------------------------------
	// ajax 컨트롤
	// ------------------------------------------

	/**
	 * ajax에서 Differed Object를 리턴하도록 함으로써, Promise 객체를 사용한
	 * 비동기 프로그래밍에 대한 처리가 가능하도록 함. (Common JS Promises/A 스펙)
	 * 또한, Differed Object를 사용하여 '콜백 피라미드 악몽(callback pyramid
	 * of doom)'을 지양할 수 있음. Chaining 가능함.
	 *
	 * 예제: promise를 활용해 다음과 같이 코딩할 수 있다.
	 *
	 *  - 추가 콜백
	 *    jqxhr.done(otherfunction1);
	 *    jqxhr.done(otherfunction2);
	 *    jqxhr.done(otherfunction3);
	 *
	 *  - 순서 제어
	 *    var jqxhr1 = KSM_CORE.ajax({...});
	 *    var jqxhr2 = KSM_CORE.ajax({...});
	 *    $.when(jqxhr1, jqxhr2).done(function(xhrObject1, xhrObject2) {
	 *        // 두개의 ajax 호출이 모두 성공적으로 종료된 이후에 호출됨
	 *        // Handle both XHR objects
	 *    });
	 */
	ajax : function(params) {
		if (! params.url) {
			return false; // 최소한 API URL 정보는 있어야 함.
		}

		// default 값 설정
		var options = {
            // API URL
            url : KSM_UTIL.getApiFullyQualifiedDomainName(params.url),
            // HTTP Method - GET | POST | PULL | DELETE
            type : params.type ? params.type : 'GET',
            // Data Type - xml | json | jsonp | text | html
            dataType : params.dataType ? params.dataType : "json",
            // HTTP 헤더 - Double Submit 방지를 위한 requestId를 header에 저장
            headers : params.headers ? params.headers : { '_requestId_'  : KSM.request.id },
            // 'application/json' | 'application/x-www-form-urlencoded'
			contentType: params.contentType ? params.contentType : 'application/x-www-form-urlencoded',
			// API에 전송하기 위한 데이터
			//data : params.dataType === 'json' ? JSON.stringify(params.data) : params.data,
			data : params.data ? params.data : {},
			// pre-submit callback function
			beforeSend : function(jqXHR, settings) {
				if (options.loadingEffect) {
					KSM_UTIL.loadingEffect(true); // 로딩 효과 시작
				}
				if (params.beforeSend) { // 사용자정의 전처리 함수가 정의되었다면,
					params.beforeSend(params.data, jqXHR, settings);
				}
				else { // 사용자정의 전처리 함수가 없다면, 기본 전처리 수행
					//performBeforeSend(params.data, jqXHR, settings);
				}
			},
			// post-submit callback function
			successFunc : params.success,
			// error가 발생했을때의 callback function
			errorFunc : params.error,
			// request가 종료되면(successFunc, errorFunc 콜백 종료 후) 호출되는 callback function.
			completeFunc : params.complete,

			// 비동기방식 호출 여부
			asyn : (params.asyn !== undefined) ? params.asyn : true,
			// 요청시 JWT 인증토큰 포함 여부
			jwtToken : (params.jwtToken !== undefined) ? params.jwtToken : false,
			// 로딩 효과 표시 여부
			loadingEffect : (params.loadingEffect !== undefined) ? params.loadingEffect : false,
			// error 발생 시 에러페이지로 리다이렉트 여부 - 에러페이지로 넘어가기
			redirectErrorPage : (params.redirectErrorPage !== undefined) ? params.redirectErrorPage : false,
			// 타임아웃 설정
			timeout : params.timeout ? params.timeout : 30000 // TODO: 3000(3초)로 조정할 것!
		};

		// JWT TOKEN을 사용한 인증 헤더 설정 - "Authorization": "Bearer " + JWT String
		if (options.jwtToken) {
			options.headers.Authorization =
				'Bearer ' + KSM_STORAGE.storage({action:'search', key:'accessToken'});
		}

		// log for debugging...
		this.logDebugInfo(options);

		// Deprecation Notice
		//  - http://api.jquery.com/jquery.ajax/
		//    The jqXHR.success(), jqXHR.error(), and jqXHR.complete() callbacks are removed as of jQuery 3.0.
		//    You can use jqXHR.done(), jqXHR.fail(), and jqXHR.always() instead.
		//  - http://www.htmlgoodies.com/beyond/javascript/making-promises-with-jquery-deferred.html
		//
		// ajax event 순서
		//   - 성공일 경우 : success > complete > done > always
		//   - 실패일 경우 : error > complete > fail > always

		var jqxhr = $.ajax(options)
		.done(function(data, textStatus, jqXHR) {
			/*
			if (! data) {
				// 데이터 체크
				KSM_UTIL.msgLayerPopup(KSM_MESSAGE.applicationMessageMap['105']);
			}
			*/

			if (options.successFunc) {
				// 사용자정의 후처리 함수가 정의되었다면,
				options.successFunc(data, textStatus, jqXHR);
			}
			else {
				// 사용자정의 후처리 함수가 없다면, 기본 후처리 수행
				//performAfterSend(data, textStatus, jqXHR);
			}
		})
		.fail(function(jqXHR, textStatus, errorThrown) {
			if (options.errorFunc) { // 사용자정의 에러처리 함수가 정의되었다면,
				options.errorFunc(jqXHR, textStatus, errorThrown);
				return;
			}
			else { // 사용자정의 에러처리 함수가 없다면, 기본 에러처리 수행
				//performErrorHandler(jqXHR, textStatus, errorThrown);
			}

			if (options.redirectErrorPage) { // 에러 메시지 처리 및 페이지 처리
				location.href = '/view/common/error/error'; // error.html
				return;
			}
			else {
				var isConnectedNetwork = true;
				// ksm.cordova.js 종속성 제외: KSM_DEVICE_API 사용 불가
				// cordova 기반의 hybrid app 개발 프로젝트의 경우 주석 해제할 것
				/*
				try {
					isConnectedNetwork = KSM_DEVICE_API.fnNetworkOffCheck();
				} catch(e) {}
				*/

				if (isConnectedNetwork) { // 네트워크 연결이 정상이라면
					//console.log('jqXHR.status: ' + jqXHR.status + ', status: ' + status + ', errorThrown: ' + errorThrown);
					var message = parseAjaxErrorMessage(jqXHR, errorThrown);

					// 기존 '에러 페이지'로 이동 -> '에러 팝업'으로 변경
					KSM_DIALOG.error(message);
				}
			}
		})
		.always(function(data, textStatus) {
			if (options.loadingEffect) {
				KSM_UTIL.loadingEffect(false); // 로딩 효과 종료
			}

			if (options.completeFunc) { // 사용자정의 완료처리 함수가 정의되었다면,
				options.completeFunc(data, textStatus);
			}
			else { // 사용자정의 완료처리 함수가 없다면, 기본 완료처리 수행
				//performComplete(options.url, data, textStatus);
			}

            // requestId 값 갱신!
			KSM.request.id = self.crypto.randomUUID(); // Double Submit 방지
		});

		// return Deferred Object
		return jqxhr;
	},

	// ------------------------------------------
	// 기타
	// ------------------------------------------

	/**
	 * 화면 이동 처리
	 */
	movePage : function(url) {
		location.href = url;
	},

	logDebugInfo: function(options) {
        // [ 사용자 정보 ]
		if (USER_INFO.isLogin()) {
			//console.log('>> 사용자 정보: \n' + USER_INFO.toString());
		}

        // [ API 호출 정보 ]
		//console.log('>> PARAMS: \n' + JSON.stringify(params));
		console.log('>> API 호출 정보: \n' + JSON.stringify(options));

		// [ User Agent 정보
		/*
		var parser = new UAParser();
		var result = parser.getResult();
		console.log('>> User Agent Info: \n' +
				'\t' + result.browser.name + ', ' + result.browser.version + '\n' +
				'\t' + result.device.type + ', ' + result.device.model + ', ' + result.device.vendor + '\n' +
				'\t' + result.os.name + ', ' + result.os.version + '\n' +
				'\t' + result.engine.name + ', ' + result.engine.version + '\n' +
				'\t' + result.cpu.architecture + '\n');
	   */
	}

};
