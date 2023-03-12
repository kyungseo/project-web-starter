/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > BASE
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-11-22      initial version
 * ========================================================================= */

// ============================================================================
// KSM Namespace
// ============================================================================

// TODO: 어플리케이션에 필요한 전역변수를 이름 하나로 관리
//   -> 다른 어플리케이션이나 위젯 또는 라이브러리들과 연동할 때 발생하는 문제점을 최소화

// KSM, global namespace
// TODO: 즉시 실행함수로 KSM 객체 생성 후 작업할 것!
var KSM = KSM || {};

// Application (Web/App 등 구축 대상 시스템)
KSM.application = KSM.application || {};

// flags
KSM.flags = KSM.flags || {};

// KSM Dialog 창이 오픈되어 있는지의 여부
// backbutton 눌렸을 때 처리 시 사용된다. -> ksm.cordova.js, main.js
KSM.flags.isOpenKsmDialog = false;


// Double Submit 방지를 위한 Request ID 생성
KSM.request = KSM.request || { "id" : self.crypto.randomUUID() };
