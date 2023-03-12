/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : GW Menu 목록 및 metadata
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-10-27      initial version
 * ========================================================================= */

//============================================================================
// Menu Class
//============================================================================

var Menu = function(id, title, parentId, htmlUrl, titleImage) {
    this.id = id;
    this.title = '▣ ' + title;
    this.parentId = parentId;
    this.htmlUrl = htmlUrl;
    this.htmlFilePath = (function() {
    	var lastSlashIndex = htmlUrl.lastIndexOf('/');
    	var filePath;
    	if (lastSlashIndex !== -1) {
    		filePath = htmlUrl.substring(0, lastSlashIndex + 1);
    	}
        return filePath;
    })();
    this.htmlFileName = (function() {
    	var lastSlashIndex = htmlUrl.lastIndexOf('/');
    	var lastQuestionMarkIndex = htmlUrl.lastIndexOf('?');
    	var fileName;
    	if (lastSlashIndex !== -1) {
    		if (lastQuestionMarkIndex !== -1) {
    			fileName = htmlUrl.substring(lastSlashIndex + 1, lastQuestionMarkIndex);
    		}
    		else {
    			fileName = htmlUrl.substring(lastSlashIndex + 1, htmlUrl.length);
    		}
    	}
    	return fileName;
    })();
    this.parameters = (function() {
    	var lastQuestionMarkIndex = htmlUrl.lastIndexOf('?');
    	// TODO parameter key/value의 집합을 literal object 형태로 리털하도록 할 것.
    	var paramString;
    	if (lastQuestionMarkIndex !== -1) {
    		paramString = htmlUrl.substring(lastQuestionMarkIndex + 1, htmlUrl.length);
    	}
    	return paramString;
    })();
    this.titleImage = titleImage;
};

//============================================================================
// Menu Map
//============================================================================

var AppMenus = function() {
	// htmlUrl은 '/templates' 위치를 기준으로 한 상대경로를 지정
	const mainHome                      = '/view/home';                                 // 메인 홈 페이지
    const mainError                     = '/view/common/error/error';                   // 에러 페이지

	const jwtLogin                      = '/view/common/jwt/login';                     // JWT 로그인 페이지
	const secLogin                      = '/view/common/sec/login';                     // Spring Security 로그인 페이지
	const secLogout                     = '/view/common/sec/logout';                    // Spring Security 로그아웃

	const secRegistration               = '/view/common/sec/registration';              // 회원 등록
	const secRegistrationCaptcha        = '/view/common/sec/registrationCaptcha';       // 회원 등록 (Captcha)
	const secRegistrationReCaptchaV3    = '/view/common/sec/registrationReCaptchaV3';   // 회원 등록 (ReCaptchaV3)
	const secRegistrationConfirm        = '/view/common/sec/registrationConfirm';       // 회원 등록 확인
	const secSuccessRegister            = '/view/common/sec/successRegister';           // 회원 등록 등록 성공
	const secQrcode                     = '/view/common/sec/qrcode';                    // 회원 등록 (QR Code)
	const secChangePassword             = '/view/common/sec/changePassword';            // 비밀번호 변경
	const secForgetPassword             = '/view/common/sec/forgetPassword';            // 비밀번호 분실
	const secUpdatePassword             = '/view/common/sec/updatePassword';            // 비밀번호 갱신
	const secUsers                      = '/view/common/sec/users';                     // 사용자 목록 (Session)
	const secRoleHierarchy              = '/view/common/sec/roleHierarchy';             // 사용자 목록 (Session)
	const secTwoFactor                  = '/view/common/sec/twoFactor';                 // 2FA 설정
	const secEmailError                 = '/view/common/sec/emailError';                // EMail 에러
	const secExpiredAccount             = '/view/common/sec/expiredAccount';            // 사용자 계정 만료
	const secInvalidSession             = '/view/common/sec/innvalidSession';           // 잘못된 Session
	const secBadUser                    = '/view/common/sec/badUser';                   // 잘못된 사용자
	const secNotice                     = '/view/common/sec/notice';                    // 알림

    const adminHome                     = '/view/admin/home';                           // 관리자 홈
    const staffHome                     = '/view/staff/home';                           // 스탭 홈
    const userHome                      = '/view/user/home';                            // 사용자 홈

    // 사용자 관리 페이지들
    const admUsers                      = '/view/admin/usermgmt/users';                 // 사용자 관리
    const admUserForm                   = '/view/admin/usermgmt/user_form';             // 사용자 정보 수정

    const dummy                         = '/';                                          // dummy page

    // new Menu(id, title, parentId, htmlUrl, titleImage)
	this.menuMap = {
        // Main Home
		'mainRoot'            : new Menu('main_root',       'KYUNGSEO.PoC', null, mainHome, null),
		'mainHome'            : new Menu('main_home',       '사이트 홈', 'main_root', mainHome, null),
		'mainError'           : new Menu('main_error',      '사이트 이용에 불편을 드려 죄송합니다.', 'main_root', mainError, null),

        // Common
		'comSecRoot'          : new Menu('com_root',       'Simpe Web Development Template', null, mainHome, null),
		'comSecLogin'         : new Menu('com_login',      '로그인', 'com_root', secLogin, null),
		'comSecLogout'        : new Menu('com_logout',     '로그아웃', 'com_root', secLogout, null),
		'comSecReg'           : new Menu('com_reg',        '회원 등록', 'com_root', secRegistration, null),
		'comSecReg_cap'       : new Menu('com_reg_cap',    '회원 등록', 'com_root', secRegistrationCaptcha, null),
		'comSecReg_recap'     : new Menu('com_reg_recap',  '회원 등록', 'com_root', secRegistrationReCaptchaV3, null),
		'comSecReg_cfrm'      : new Menu('com_reg_cfrm',   '회원 등록 확인', 'com_root', secRegistrationConfirm, null),
		'comSecReg_succ'      : new Menu('com_reg_succ',   '회원 등록 성공', 'com_root', secSuccessRegister, null),
		'comSecReg_qr'        : new Menu('com_reg_qr',     '회원 등록 (QR Code)', 'com_root', secQrcode, null),
		'comSecChg_pw'        : new Menu('com_chg_pw',     '비밀번호 변경', 'com_root', secChangePassword, null),
		'comSecFrg_pw'        : new Menu('com_frg_pw',     '비밀번호 리셋', 'com_root', secForgetPassword, null),
		'comSecUd_pw'         : new Menu('com_ud_pw',      '비밀번호 갱신', 'com_root', secUpdatePassword, null),
		'comSecUsers'         : new Menu('com_users',      '사용자 목록 (Session)', 'com_root', secUsers, null),
		'comSecRoleh'         : new Menu('com_roleh',      '역할 계층', 'com_root', secRoleHierarchy, null),
		'comSec2fa'           : new Menu('com_2fa',        '2FA 설정', 'com_root', secTwoFactor, null),
		'comSecEmail_err'     : new Menu('com_email_err',  'EMail 에러', 'com_root', secEmailError, null),
		'comSecExpr_acc'      : new Menu('com_expr_acc',   '사용자 계정 만료', 'com_root', secExpiredAccount, null),
		'comSecInvalid_ss'    : new Menu('com_invalid_ss', 'Session 정보', 'com_root', secInvalidSession, null),
		'comSecBad_usr'       : new Menu('com_bad_user',   '사용자 정보 오류', 'com_root', secBadUser,  null),
		'comSecNotice'        : new Menu('com_notice',     '알림', 'admin_root', secNotice, null),

		// Admin
		'admRoot'             : new Menu('admin_root',     '관리자 메인', null, adminHome, null),
		'admHome'             : new Menu('admin_home',     '관리자 홈', 'admin_root', adminHome, null),
		'admUsers'            : new Menu('admUsers',       '사용자 관리', 'admin_root', admUsers, null),
		'admUserForm'         : new Menu('admUserForm',    '사용자 정보', 'admin_root', admUserForm,  null),

		// User
		'usrRoot'             : new Menu('user_root',      '사용자 메인', null, userHome, null),
		'usrHome'             : new Menu('user_home',      '사용자 홈', 'user_root', userHome, null),

		// dummy
		'dummyRoot'           : new Menu('dummy_root',     'dummy', null, dummy, null)
	};
};

AppMenus.prototype.getMenu = function(menuId) {
	//var menuItem = this.menuMap.get(menuId);
	var menuItem = this.menuMap[menuId];
	return menuItem;
};

//============================================================================
// KSM Menus
//============================================================================

// 메뉴 생성
KSM.application.menus = new AppMenus();
