/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > STORAGE
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-10-22      initial version
 * 박경서   2016-11-03      CryptoJS에 기반해서 작성한 암호화 유틸리티인
 *                          ksm.crypto.js를 사용하여 Storage에 triple DES로
 *                          암호화하여 save 하고 select 시 복호화하도록 코드 보완
 * ========================================================================= */

//============================================================================
// KSM Storage
//============================================================================

// TODO: 비밀키 서버에 저장
var _secret_key_ = '_ksm_secret_key_';

// storage에 item 저장 시 암호화 적용 여부 (현재 암호화 하지 않음)
var applyEncDec = false;

// ------------------------------------------
// Storage Util
// ------------------------------------------

var KSM_STORAGE = {

	// ------------------------------------------
	// Local Storage & Session Storage 컨트롤
	// ------------------------------------------

	/**
	 * localStorage 또는 sessionStorage를 사용한 데이터 조작
	 *
	 * prameter는 다음과 같은 literal object 형식으로 사용
	 *
	 * KSM_STORAGE.storage({
	 *   action  : 'save',     // 작업구분 (save|select|remove|clear|keys|values)
	 *   key     : 'name',     // key 값
	 *   value   : 'kyungseo', // value 값
	 *   storage : 'local'     // storage 대상 구분 (local|session), default는 local
	 * });
	 *
	 */
	storage : function(params) {
		// default 값 설정
		var options = {
			action  : params.action,
			key     : params.key,
			value   : params.value,
			storage : params.storage ? params.storage : 'local'
		};

		var storageObject = KSM_LOCAL_STORAGE;   // default: local storage
		if (options.storage.toUpperCase() === 'SESSION') {
			storageObject = KSM_SESSION_STORAGE; // session storage
		}

		if (! KSM_LOCAL_STORAGE.checkBrowserSupportStorage()) { // browser의 storage 지원 체크
	    	KSM_DIALOG.error(KSM_MESSAGE.systemErrorMessageMap['901']);
	    	return;
	    }

		switch (options.action.toUpperCase()) {
		case 'SAVE':
			storageObject.saveItem(options.key, options.value);
			break;
		case 'SELECT':
			return storageObject.selectItem(options.key);
			break;
		case 'SEARCH': // 예외 케이스: local/session storage 양쪽 다 검색
			// local storage에서 키값을 찾는다.
			var value = KSM_LOCAL_STORAGE.selectItem(options.key);
			if (! value) {
				// 값이 없으면 session storage에서 다시 찾는다.
				value = KSM_SESSION_STORAGE.selectItem(options.key);
			}
			return value;
			break;
		case 'REMOVE':
			storageObject.removeItem(options.key);
			break;
		case 'CLEAR':
			storageObject.clearAll();
			break;
		case 'KEYS':
			return storageObject.keys();
			break;
		case 'VALUES':
			return storageObject.values();
			break;
		default:
			throw new Error(KSM_MESSAGE.systemErrorMessageMap['902']);
			break;
		}
	},

	/**
	 * json object 값을 로컬 스토리지에 저장하는 함수
	 * @param value - object
	 */
	objectSaveLocalStorage : function(value) {
		if (typeof value === 'object') {
			if (value.length < 1) return;
			for (var keyIndex in value) {
				KSM_STORAGE.storage({action:'save', key:keyIndex, value:value[keyIndex], storage:'local'});
			}
		}
		else {
			throw new Error(KSM_MESSAGE.systemErrorMessageMap['903']);
			return false;
		}
	},

	/**
	 * json object 값을 세션 스토리지에 저장하는 함수
	 * @param value - object
	 */
	objectSaveSessionStorage : function(value) {
		if (typeof value === 'object') {
			if (value.length < 1) return;
			for (var keyIndex in value) {
				KSM_STORAGE.storage({action:'save', key:keyIndex, value:value[keyIndex], storage:'session'});
			}
		}
		 else {
			throw new Error(KSM_MESSAGE.systemErrorMessageMap['903']);
			return false;
		}
	}

};

// TODO FIXME: expiryDuration의 값이 '3600000'인데, KSM_CRYPTO.encryptByDES 할 때 다음과 같은 에러 발생
//
//   Uncaught TypeError: Cannot read properties of undefined (reading 'length')
//       at init.concat (core.js:219:34)
//       at Object._append (core.js:495:24)
//       at Object.finalize (tripledes.js:25:30)
//       at Object.encrypt (tripledes.js:29:371)
//       at Object.encrypt (tripledes.js:25:211)
//       at Object.encryptByDES (ksm.crypto.js:23:35)
//       at Object.saveItem (ksm.storage.js:149:33)
//       at Object.storage (ksm.storage.js:69:18)
//       at Object.fnJwtLoginCallBack [as successFunc] (login:305:19)
//       at Object.<anonymous> (ksm.core.js:182:13)
//
// 임시로 암복호화 시 expiryDuration 키값에 대한 valuems 제외 처리

// ------------------------------------------
// Local Storage
// ------------------------------------------

var KSM_LOCAL_STORAGE = {

	saveItem : function(key, value) {
		if (! this.checkBrowserSupportStorage()) return;

		var plainTextValue = value;
		var cipherTextValue = '';
		if (applyEncDec && value) {
			cipherTextValue = KSM_CRYPTO.encryptByDES(value, _secret_key_);
		}
		else {
			cipherTextValue = plainTextValue;
		}
		//console.log('Local Storage >> save item \n' + key + ' : ' + cipherTextValue);
		localStorage.setItem(key, cipherTextValue);
		//this.doShowAll();
	},

	selectItem : function(key) {
		if (! this.checkBrowserSupportStorage()) return;

		var cipherTextValue = localStorage.getItem(key);
		var plainTextValue = cipherTextValue;
		if (applyEncDec && cipherTextValue) {
			plainTextValue = KSM_CRYPTO.decryptByDES(cipherTextValue, _secret_key_);
		}
		//console.log('Local Storage >> select item \n' + key + ' : ' + cipherTextValue + '(' + plainTextValue + ')');
		return plainTextValue;
	},

	removeItem : function(key) {
		if (! this.checkBrowserSupportStorage()) return;

		localStorage.removeItem(key);
		this.doShowAll();
	},

	clearAll : function() {
		if (! this.checkBrowserSupportStorage()) return;

		localStorage.clear();
	},

	keys : function() {
		if (! this.checkBrowserSupportStorage()) return;

		var arrayKeys = [];
		for (var keyIndex in localStorage) {
			arrayKeys.push(keyIndex);
		}
		return arrayKeys;
	},

	values : function() {
		if (! this.checkBrowserSupportStorage()) return;

		var cipherTextValue = '';
		var plainTextValue = '';
		var valueObject = {};
		for (var key in this.keys()) {
			var cipherTextValue = localStorage.getItem(key);
			var plainTextValue = cipherTextValue;
			if (applyEncDec && cipherTextValue) {
				plainTextValue = KSM_CRYPTO.decryptByDES(cipherTextValue, _secret_key_);
			}
			//console.log('Local Storage >> select item \n' + key + ' : ' + cipherTextValue + '(' + plainTextValue + ')');
			valueObject[keyIndex] = plainTextValue;
		}
		return valueObject;
	},

	doShowAll : function() {
		if (this.checkBrowserSupportStorage()) {
			var key = '';
			var value = '';
			for (var keyIndex in localStorage) {
				key = localStorage.key(keyIndex);
				value = localStorage.getItem(key);
				//console.log(key + ' : ' + value);
			}
		}
		else {
			//console.log('브라우저가 session storage를 지원하지 않습니다.');
		}
	},

	/**
	 * 브라우저 호환성 체크
	 *
	 * 현재 브라우저의 HTML5와 CSS 기능에 대한 지원 여부를 판단하기 위해 Modernizr를
	 * 사용해도 된다.
	 *
	 * if (Modernizr.localstorage) { //지원 } else { //지원하지않음 }
	 */
	checkBrowserSupportStorage : function() {
		if ('localStorage' in window && window['localStorage'] !== null) {
			return true;
		}
		else {
			return false;
		}

		/*
		if (typeof(localStorage) !== "undefined") {
			return true;
		}
		else {
			return false;
		}
		*/
	}

};

//------------------------------------------
// Session Storage
//------------------------------------------

var KSM_SESSION_STORAGE = {

	saveItem : function(key, value) {
		if (! this.checkBrowserSupportStorage()) return;

		var plainTextValue = value;
		var cipherTextValue = '';
		if (applyEncDec && value) {
			cipherTextValue = KSM_CRYPTO.encryptByDES(value, _secret_key_);
		}
		else {
			cipherTextValue = plainTextValue;
		}
		//console.log('Session Storage >> save item \n' + key + ' : ' + cipherTextValue);
		sessionStorage.setItem(key, cipherTextValue);
	},

	selectItem : function(key) {
		if (! this.checkBrowserSupportStorage()) return;

		var cipherTextValue = sessionStorage.getItem(key);
		var plainTextValue = cipherTextValue;
		if (applyEncDec && cipherTextValue) {
			plainTextValue = KSM_CRYPTO.decryptByDES(cipherTextValue, _secret_key_);
		}
		//console.log('Local Storage >> select item \n' + key + ' : ' + cipherTextValue + '(' + plainTextValue + ')');
		return plainTextValue;
	},

	removeItem : function(key) {
		if (! this.checkBrowserSupportStorage()) return;
		sessionStorage.removeItem(key);
		this.doShowAll();
	},

	clearAll : function() {
		if (! this.checkBrowserSupportStorage()) return;
		sessionStorage.clear();
		this.doShowAll();
	},

	keys : function() {
		if (! this.checkBrowserSupportStorage()) return;
		var arrayKeys = [];
		for (var keyIndex in sessionStorage) {
			arrayKeys.push(keyIndex);
		}
		return arrayKeys;
	},

	values : function() {
		if (! this.checkBrowserSupportStorage()) return;

		var cipherTextValue = '';
		var plainTextValue = '';
		var valueObject = {};
		for (var key in this.keys()) {
			var cipherTextValue = sessionStorage.getItem(key);
			var plainTextValue = cipherTextValue;
			if (applyEncDec && cipherTextValue) {
				plainTextValue = KSM_CRYPTO.decryptByDES(cipherTextValue, _secret_key_);
			}
			//console.log('Local Storage >> select item \n' + key + ' : ' + cipherTextValue + '(' + plainTextValue + ')');
			valueObject[keyIndex] = plainTextValue;
		}
		return valueObject;
	},

	doShowAll : function() {
		if (this.checkBrowserSupportStorage()) {
			var key = '';
			var value = '';
			for (var keyIndex in sessionStorage) {
				key = sessionStorage.key(keyIndex);
				value = sessionStorage.getItem(key);
				//console.log(key + ' : ' + value);
			}
		}
		else {
			//console.log('브라우저가 session storage를 지원하지 않습니다.');
		}
	},

	/**
	 * 브라우저 호환성 체크
	 *
	 * 현재 브라우저의 HTML5와 CSS 기능에 대한 지원 여부를 판단하기 위해 Modernizr를
	 * 사용해도 된다.
	 *
	 * if (Modernizr.sessionstorage) { //지원 } else { //지원하지않음 }
	 */
	checkBrowserSupportStorage : function() {
		if ('sessionStorage' in window && window['sessionStorage'] !== null) {
			return true;
		}
		else {
			return false;
		}

		/*
		if (typeof(sessionStorage) !== "undefined") {
			return true;
		}
		else {
			return false;
		}
		*/
	}

};
