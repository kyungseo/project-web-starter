/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-11-22      initial version
 * ========================================================================= */

//============================================================================
// 하이브리드 관련 공통함수 구현 및 Customer Plug-In 구현
//============================================================================


//console.log('Hybrid JS CALL');

/**
 * APP 동작 시 데이터 망 일경우 과금 부과 확인 호출
 * @returns
 */
function fnConfirmLte(index) {
	var networkState = navigator.connection.type;
	if(networkState != Connection.WIFI){
	    if (confirm("WiFi 망이 아닐경우 추가적인 비용이 발생 할 수 있습니다. \n계속 하시겠습니까?")){
	        location.href=index;
	    } else {
	        navigator.app.exitApp();
	    }
	 }
}

/** 데이터망 체크 여부 */
var isLteCheck = true; //WIFI가 아닌 경우 무조건 확인
var isLte = false;

/**
 * 네트웍 이용시 wifi 체크
 * @returns wifi 연결 여부
*/
function fnNetworkCheck(doCheck) { //doCheck false인 경우 컨펌 체크 하지 않음
	var networkState = navigator.connection.type;
    if (networkState == Connection.UNKNOWN || networkState == Connection.NONE) {
        alert("네트워크가 연결되어 있지 않습니다.");
        return false;
    }

	if (networkState != Connection.WIFI) { //WIFI 가 맞다면 TRUE
		//doCheck(false인경우 아래 체크를 안함, true인 경우 한방에 아래 체크수행)
		if (! doCheck) {
			if (isLteCheck) {//상단 선언을 true로 한 경우 그냥 데이터망 쓰겟다. 체크 안함가 //와이파이가 아니라도 그냥 아래 메시지 안타고 쓰겟다
			    isLte = true;
				return true;
			}
		}

		if (confirm('WiFi 망이 아닐경우 추가적인 비용이 발생 할 수 있습니다. \n계속 하시겠습니까?')) {
			isLte = true;
			return true;
		}
		else {
			isLte = false;
			return false;
		}
	}
	else {
		return true;
	}
}

/**
 * Device Number 정보 조회 플러그인 자바스크립트
 * @returns
 */
/*var DeviceNumber = {
    getDeviceNumber: function(success, fail, types) {
        return cordova.exec(success, fail, "DeviceNumberPlugin", "deviceNumber", types);
    }
}*/
