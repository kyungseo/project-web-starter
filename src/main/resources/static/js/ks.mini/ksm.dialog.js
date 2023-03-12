/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : message를 보여주기 위한 dialog 클래스이다.
 *
 *               dialog를 보여주기 위한 표준을 제시함으로써 개발자들의 메시징
 *               을 통일한다.
 *
 *               현재 주로 Bootstrap Dialog를 wrapping하는 방식으로 구현하였으며,
 *               필요한 경우 개발자 코드의 변경없이 이 클래스를 수정함으로써
 *               시스템의 전체적인 메시징 방식을 변경할 수 있다.
 *
 * ※ Bootstrap Dialog의 자세한 사항은 다음 사이트를 참고하라.
 *   - https://nakupanda.github.io/bootstrap3-dialog
 *   - https://github.com/nakupanda/bootstrap3-dialog
 *   - http://www.w3schools.com/bootstrap/bootstrap_buttons.asp
 *     button class > btn-default, btn-primary, btn-success, btn-info,
 *                    btn-warning, btn-danger, btn-link
 *
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-11-20      initial version
 * 박경서   2023-03-02      confirm 함수 수정
 * ========================================================================= */

BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_DEFAULT] = '정보';
BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_INFO]    = '정보';
BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_PRIMARY] = '정보';
BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_SUCCESS] = '성공';
BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_WARNING] = '경고';
BootstrapDialog.DEFAULT_TEXTS[BootstrapDialog.TYPE_DANGER]  = '위험';
BootstrapDialog.DEFAULT_TEXTS['OK']                         = '확인';
BootstrapDialog.DEFAULT_TEXTS['CANCEL']                     = '취소';
BootstrapDialog.DEFAULT_TEXTS['CONFIRM']                    = '승인';

var DIALOG_TYPES = {
	DEFAULT : BootstrapDialog.TYPE_DEFAULT,
	INFO    : BootstrapDialog.TYPE_INFO,
	PRIMARY : BootstrapDialog.TYPE_PRIMARY,
	SUCCESS : BootstrapDialog.TYPE_SUCCESS,
	WARNING : BootstrapDialog.TYPE_WARNING,
	DANGER  : BootstrapDialog.TYPE_DANGER
};

var DIALOG_SIZE = {
	NORMAL  : BootstrapDialog.SIZE_NORMAL,
	SMALL   : BootstrapDialog.SIZE_SMALL,
	WIDE    : BootstrapDialog.SIZE_WIDE,
	LARGE   : BootstrapDialog.SIZE_LARGE
};

var KSM_DIALOG = {

	/**
	 * 다양한 옵션을 사용하여 메시지를 보여주기 위한 창을 띄운다.
	 *
	 * ※ 하단의 confirm, alert, error, info, validate 등의 함수들은 show 함수의
	 * shortcut methods라고 할 수 있다.
	 *
	 * @param params - Key-value object
	 *
	 * 코드 샘플
	 *
	 * 		BootstrapDialog.show({
	 * 		    type: type,
	 * 		    title: 'Message type: ' + type,
	 * 		    message: 'What to do next?',
	 * 		    buttons: [{
	 * 		        label: 'Button 1',
	 * 		        // no title as it is optional
	 * 		        cssClass: 'btn-primary',
	 * 		        action: function(){
	 * 		            alert('Hi Orange!');
	 * 		        }
	 * 		    }, {
	 * 		        label: 'Button 3',
	 * 		        title: 'Mouse over Button 3',
	 * 		        cssClass: 'btn-warning'
	 * 		    }, {
	 * 		        label: 'Close',
	 * 		        action: function(dialogItself){
	 * 		            dialogItself.close();
	 * 		        }
	 * 		    }]
	 * 		});

	 *   다음 사이트의 샘플을 확인하도록 한다.
	 *   - https://nakupanda.github.io/bootstrap3-dialog
	 *
	 */
	show : function(params) {
		var options = {
			title: params.title,
			message: params.message,
			buttons: params.buttons,
			data: params.data,
			callback: params.callback,
			type: params.type ? params.type : DIALOG_TYPES.DEFAULT,
			size: params.size ? params.size : DIALOG_SIZE.NORMAL,
			closable: (params.closable !== undefined) ? params.closable : true,
			draggable: (params.draggable !== undefined) ? params.draggable : false,
	        nl2br: (params.nl2br !== undefined) ? params.nl2br : true,
	        animate: (params.animate !== undefined) ? params.animate : true,
       		btnsOrder: params.btnsOrder ? params.btnsOrder : BootstrapDialog.BUTTONS_ORDER_CANCEL_OK // BootstrapDialog.BUTTONS_ORDER_OK_CANCEL
		};

		var dialogInstance = BootstrapDialog.show(options);
		return dialogInstance;
	},

	/**
	 * '확인창'을 띄운다.
	 *
	 * @param msg - string
	 * @param callbackFunctionForOK - function
	 * @param callbackFunctionForCancel - function
	 * @param params - Key-value object
	 * @param btnOKLabel - string
	 * @param btnCancelLabel - string
	 *
	 * 코드 샘플
	 *
	 *  KSM_DIALOG.confirm({
     *      title               : '확인',
     *      message             : message,
     *      callbackForOk       : function() {
     *        //
     *      },
     *      callbackForCancel   : function() {
     *        //
     *      },
     *      data                : {},
     *      btnOKLabel          : "확인",
     *      btnCancelLabel      : "취소",
     *      type: DIALOG_TYPES.PRIMARY
     *   });
	 *
	 */
	//confirm : function(msg, callbackFunctionForOK, callbackFunctionForCancel, params, btnOKLabel, btnCancelLabel) {
    confirm : function(params) {
		KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
		var dialogInstance = BootstrapDialog.confirm({
            type: params.type !== null ? params.type : DIALOG_TYPES.PRIMARY,
            title: params.title !== null ? params.title : '확인',
            message: params.message !== null ? params.message : '',
            data: params.data !== null ? params.data : {},
		    callback: function(result) {
		    	// result: 사용자가 확인 버튼 클릭하면 true, 다이렉트로 닫으면 false
		    	if (result) {
		    		// 확인 버튼(btnOK)를 클릭한 경우
		    		if (typeof params.callbackForOk === 'function') {
		    			params.callbackForOk(params.data !== null ? params.data : {});
		    		}
		    	}
		    	else {
		    		// 확인 않고 confirm창을 닫은 경우
		    		if (typeof params.callbackForCancel === 'function') {
		    			params.callbackForCancel(params.data !== null ? params.data : {});
		    		}
		    	}
		    	KSM.flags.isOpenKsmDialog = false;
			},
		    btnCancelLabel: params.btnCancelLabel ? params.btnCancelLabel : '취소',
		    btnOKLabel: params.btnOKLabel ? params.btnOKLabel : '확인',
			closable: true
		});

		return dialogInstance;
		/*
		var dialog = new BootstrapDialog({
		    title: '확인',
		    message: msg,
		    type: BootstrapDialog.TYPE_WARNING,
		    closable: true,
		    btnCancelLabel: '취소',
		    btnOKLabel: '확인',
		    btnOKClass: 'btn-warning', // class를 정의하지 않으면 dialog type이 사용된다.
			data: params ? params : null,
		    callback: function(result) {
		    	// result: 사용자가 확인 버튼 클릭하면 true, 다이렉트로 닫으면 false
		    	if (result) {
		    		if (typeof dialog.getData('callback') === 'function') {
		    			callbackFunction(params);
		    		}
		    	}
		    	else {
		    	}
			}
        });
        dialog.realize();
        $('#modal-container').append(dialog.getModal());
        dialog.open();
        */
	},

	/**
	 * '경고창'을 띄운다.
	 *
	 * @param msg - string
	 * @param callbackFunction - function
	 * @param params - Key-value object
	 *
	 * 코드 샘플
	 *
	 *   KSM_DIALOG.alert('데이터가 존재하지 않습니다.');
	 *
	 *   KSM_DIALOG.alert('LTE의 경우 요금이 발생할 수 있습니다.', function() {
	 *       alert('콜백 처리');
	 *   });
	 *
	 *   KSM_DIALOG.alert('데이터가 존재하지 않습니다.', function(data) {
	 *       // callback
	 *       alert('data.keyOne: ' + data.keyOne);
	 *   }, {
	 *       keyOne: 'value1',
	 *       keyTwo: 'value2'
	 *   });
	 *
	 */
	alert : function(msg, callbackFunction, params) {
		KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
		var dialogInstance = BootstrapDialog.alert({
            type: DIALOG_TYPES.WARNING,
            title: '경고',
            message: msg,
            data: (params !== null) ? params : {},
		    callback: function(result) {
		    	// result: 사용자가 확인 버튼 클릭하면 true, 다이렉트로 닫으면 false
		    	if (result) {
		    		if (typeof callbackFunction === 'function') {
		    			callbackFunction(params);
		    		}
		    	}
		    	else {
		    		// 확인 없이 confirm창을 닫은 경우임
		    	}
		    	KSM.flags.isOpenKsmDialog = false;
			},
			buttonLabel: '닫기',
			closable: true
		});

		return dialogInstance;
	},
	confirmAlert : function(msg, callbackFunction, params) {
		KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
		var dialogInstance = BootstrapDialog.alert({
			type: DIALOG_TYPES.WARNING,
			title: '경고',
			message: msg,
			data: (params !== null) ? params : {},
					callback: function(result) {
						// result: 사용자가 확인 버튼 클릭하면 true, 다이렉트로 닫으면 false
						if (result) {
							if (typeof callbackFunction === 'function') {
								callbackFunction(params);
							}
						}
						else {
							// 확인 없이 confirm창을 닫은 경우임
						}
						KSM.flags.isOpenKsmDialog = false;
					},
					buttonLabel: '확인',
					closable: true
		});

		return dialogInstance;
	},

	/**
	 * '에러창'을 띄운다.
	 *
	 * @param msg - string
	 * @param callbackFunction - function
	 * @param params - Key-value object
	 *
	 * 코드 샘플
	 *
	 *   KSM_DIALOG.error('로그인이 실패하였습니다.');
	 *
	 *   KSM_DIALOG.error('파일업로드가 실패하였습니다.', function() {
	 *       alert('콜백 처리');
	 *   });
	 *
	 *   KSM_DIALOG.error('조회가 실패하였습니다.', function(data) {
	 *       // callback
	 *       alert('data.keyOne: ' + data.keyOne);
	 *   }, {
	 *       keyOne: 'value1',
	 *       keyTwo: 'value2'
	 *   });
	 *
	 */
	error : function(msg, callbackFunction, params) {
		KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
		var dialogInstance = this.show({
            type: DIALOG_TYPES.DANGER,
            title: '에러',
            message: msg,
            data: (params !== null) ? params : {},
		    callback: function(result) {
		    	// result: 사용자가 확인 버튼 클릭하면 true, 다이렉트로 닫으면 false
		    	if (result) {
		    		if (typeof callbackFunction === 'function') {
		    			callbackFunction(params);
		    		}
		    	}
		    	else {
		    		// 확인 없이 confirm창을 닫은 경우임
		    	}
		    	KSM.flags.isOpenKsmDialog = false;
			},
            buttons: [{
                label: '닫기',
                cssClass: 'btn-danger',
                action: function(dialogItself) {
                    dialogItself.close();
                }
            }],
			closable: true
		});

		return dialogInstance;
	},

	/**
	 * '정보창'을 띄운다.
	 *
	 * @param msg - string
	 * @param callbackFunction - function
	 * @param params - Key-value object
	 *
	 * 코드 샘플
	 *
	 *   KSM_DIALOG.info('이 서비스는 준비중입니다.');
	 *
	 *   KSM_DIALOG.info('메시지가 도착하였습니다.', function() {
	 *       alert('콜백 처리');
	 *   });
	 *
	 *   KSM_DIALOG.info('구현중인 기능입니다.', function(data) {
	 *       // callback
	 *       alert('data.keyOne: ' + data.keyOne);
	 *   }, {
	 *       keyOne: 'value1',
	 *       keyTwo: 'value2'
	 *   });
	 *
	 */
    info : function(msg, callbackFunction, params) {
        KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
        var dialogInstance = this.show({
            type: DIALOG_TYPES.INFO,
            title: '알림',
            message: msg,
            data: (params !== null) ? params : {},
            callback: function(result) {
                // result: 사용자가 확인 버튼 클릭하면 true, 다이렉트로 닫으면 false
                if (result) {
                    if (typeof callbackFunction === 'function') {
                        callbackFunction(params);
                    }
                }
                else {
                    // 확인 없이 confirm창을 닫은 경우임
                }
                KSM.flags.isOpenKsmDialog = false;
            },
            buttons: [{
                label: '닫기',
                cssClass: 'btn-info',
                action: function(dialogItself) {
                    dialogItself.close();
                }
            }],
            closable: true
        });

        return dialogInstance;
    },

    success : function(msg, callbackFunction) {
        KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
        var dialogInstance = this.show({
            type: DIALOG_TYPES.SUCCESS,
            title: '성공',
            message: msg,
            buttons: [{
                label: '닫기',
                cssClass: 'btn-info',
                action: function(dialogItself) {
                    callbackFunction();
                    dialogItself.close();
                }
            }],
            closable: true
        });

        return dialogInstance;
    },

	/**
	 * 데이터의 유효성 검증이 실패할 경우 '경고창'을 띄운다.
	 *
	 * @param msg - string
	 * @param callbackFunction - function
	 * @param params - Key-value object
	 *
	 * 코드 샘플
	 *
	 *   KSM_DIALOG.validate('아이디는 필수 입력 항목입니다.');
	 *
	 *   KSM_DIALOG.validate('댓글은 500자를 초과할 수 없습니다.', function() {
	 *       alert('콜백 처리');
	 *   });
	 *
	 *   KSM_DIALOG.validate('댓글은 500자를 초과할 수 없습니다.', function(data) {
	 *       // callback
	 *       alert('data.keyOne: ' + data.keyOne);
	 *   }, {
	 *       keyOne: 'value1',
	 *       keyTwo: 'value2'
	 *   });
	 *
	 */
	validate : function(msg, callbackFunction, params) {
		KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
		var dialogInstance = BootstrapDialog.alert({
            type: DIALOG_TYPES.WARNING,
            title: '데이터 유효성 체크',
            message: msg,
            data: (params !== null) ? params : {},
		    callback: function(result) {
		    	// result: 사용자가 확인 버튼 클릭하면 true, 다이렉트로 닫으면 false
		    	if (result) {
		    		if (typeof callbackFunction === 'function') {
		    			callbackFunction(params);
		    		}
		    	}
		    	else {
		    		// 확인 없이 confirm창을 닫은 경우임
		    	}
		    	KSM.flags.isOpenKsmDialog = false;
			},
			buttonLabel: '닫기',
			closable: true
		});

		return dialogInstance;
		/*
		var dialogInstance = this.show({
            type: DIALOG_TYPES.WARNING,
            title: '데이터 유효성 체크',
            message: msg,
            data: (params !== null) ? params : {},
		    callback: function(result) {
	    		if (typeof callbackFunction === 'function') {
	    			callbackFunction(params);
	    		}
			},
            buttons: [{
                label: '닫기',
                cssClass: 'btn-warning',
                action: function(dialogItself) {
                    dialogItself.close();
                }
            }]
		});
		*/
	},

	/**
	 * 현재 Page에 생성되어있는 모든 dialog 창을 닫는다.
	 */
	closeAll : function(msg, callbackFunction) {
	    $.each(BootstrapDialog.dialogs, function(id, dialog) {
	        dialog.close();
	    });
	},

	/**
	 * App(앱) 종료를 위한 '확인창'을 띄운다.
	 *
	 * @param options - Key-value object
	 */
	exitConfirm : function(options) { // App 종료
		// [방법1] dialog confirm 사용
		KSM.flags.isOpenKsmDialog = true; // Dialog가 오픈된 상태인지 여부를 트래킹
		BootstrapDialog.confirm({
            type: DIALOG_TYPES.PRIMARY,
            title: options.title,
            message: options.message,
		    callback: function(result) {
		    	if (result) {
		    		navigator.app.exitApp();
		    	}
		    	else {
		    		// 확인 없이 confirm창을 닫은 경우임
		    	}
		    	KSM.flags.isOpenKsmDialog = false;
			},
		    btnCancelLabel: '취소',
		    btnOKLabel: '종료',
			closable: true
		});

		/*
		// [방법2] Cordova API로 종료
		navigator.notification.confirm(options.message, function(button) {
			if (button === 2) { // 취소 = 1, 종료 = 2
				navigator.app.exitApp();
			}
		}, options.title, '취소, 종료');
		*/
	}

};
