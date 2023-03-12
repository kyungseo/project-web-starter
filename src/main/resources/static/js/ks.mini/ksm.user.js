/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : 현재 시스템에 로그인한 사용자에 대한 정보를 쉽게 사용할 수
 *               있도록 USER_INFO object를 제공한다.
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-10-22      initial version
 * ========================================================================= */

//============================================================================
// KSM User
//============================================================================

// ------------------------------------------
// KSM_ROLES Constants
// ------------------------------------------

var KSM_ROLES = {
	ADMIN     : 'ROLE_ADMIN',
	STAFF     : 'ROLE_STAFF',
	USER      : 'ROLE_USER',
	PM        : 'ROLE_PM',
	DEVELOPER : 'ROLE_DEVELOPER',
	ANONYMOUS : 'ROLE_ANONYMOUS'
};

//------------------------------------------
// User Information
//------------------------------------------

var USER_INFO = {

	id             : KSM_STORAGE.storage({action:'search', key:'id'}),
	name           : KSM_STORAGE.storage({action:'search', key:'name'}),
	accessToken    : KSM_STORAGE.storage({action:'search', key:'accessToken'}),
	refreshToken   : KSM_STORAGE.storage({action:'search', key:'refreshToken'}),
	expiryDuration : KSM_STORAGE.storage({action:'search', key:'expiryDuration'}),
	loginDate      : KSM_STORAGE.storage({action:'search', key:'loginDate'}),
	isLogin        : function() {
        // TODO FIXME accessToken의 유효시간 경과 체크 후
        // 만료된 경우 alert하고 /refresh 할 것!!!
		var accessToken = KSM_STORAGE.storage({action:'search', key:'accessToken'});
		return (accessToken) ? true : false;
	},
	roles      : KSM_STORAGE.storage({action:'search', key:'roles'}),
	hasRole        : function(role) {
        console.log('role: ' + role);
		// 다음과 같이 사용할 것
		// if (USER_INFO.hasRole(KSM_ROLES.ADMIN)) { ... }
		var userRoles = KSM_STORAGE.storage({action:'search', key:'roles'}).split(',');
		for (var i = 0, length = userRoles.length; i < length; i++) {
            console.log("userRoles[" + i + "]: " + userRoles[i]);
			if (role === userRoles[i]) return true;
		}
		return false;
	},
	toString   : function() {
		var allInfo = 	'{ id : ' + KSM_STORAGE.storage({action:'search', key:'id'}) + ', ' +
			'name : ' + KSM_STORAGE.storage({action:'search', key:'name'}) + ', ' +
			'accessToken : ' + KSM_STORAGE.storage({action:'search', key:'accessToken'}) + ', ' +
			'refreshToken : ' + KSM_STORAGE.storage({action:'search', key:'refreshToken'}) + ', ' +
			'expiryDuration : ' + KSM_STORAGE.storage({action:'search', key:'expiryDuration'}) + ', ' +
			'roles : ' + KSM_STORAGE.storage({action:'search', key:'roles'}) + ', ' +
			'loginDate : ' + KSM_STORAGE.storage({action:'search', key:'loginDate'}) + ' }';

		return allInfo;
	}

};
