/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > UTIL
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-11-22      initial version
 * ========================================================================= */

// ============================================================================
// Utility, Helper
// ============================================================================

var KSM_UTIL = {

	// ------------------------------------------
	// UI 관련 유틸: 로딩
	// ------------------------------------------

	/**
	 * spin.js 동작 함수 - 로딩바 이미지를 javascript로 구현한 라이브러리
	 */
	spin : function( divSelector ){
		var opts = {
			  lines: 15 // The number of lines to draw
			, length: 47 // The length of each line
			, width: 37 // The line thickness
			, radius: 37 // The radius of the inner circle
			, scale: 1 // Scales overall size of the spinner
			, corners: 1 // Corner roundness (0..1)
			, color: '#000' // #rgb or #rrggbb or array of colors
			, opacity: 0.25 // Opacity of the lines
			, rotate: 0 // The rotation offset
			, direction: 1 // 1: clockwise, -1: counterclockwise
			, speed: 1 // Rounds per second
			, trail: 60 // Afterglow percentage
			, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
			, zIndex: 2e9 // The z-index (defaults to 2000000000)
			, className: 'spinner' // The CSS class to assign to the spinner
			, top: '50%' // Top position relative to parent
			, left: '50%' // Left position relative to parent
			, shadow: false // Whether to render a shadow
			, hwaccel: false // Whether to use hardware acceleration
			, position: 'absolute' // Element positioning
		}

		//var target = document.getElementById('aa');
		var target = $( divSelector )[0];
		var spinner = new Spinner(opts).spin(target);
	},

	/**
	 * 로딩바 동작 함수
	 */
	loadingEffect : function(show) {
		if (show) {
			$.blockUI({ message :  '<div class="loader"></div>'
				, css : { border: 'none',
					 width : '80px',
					 backgroundColor : 'rgba(256,256,256,.0)',
					 '-webkit-border-radius': '10px',
            				 '-moz-border-radius': '10px',
					 color: '#fff',
					 top:  ($(window).height() - 80) /2 + 'px',
                				 left: ($(window).width() - 80) /2 + 'px',
					 opacity : 0.8 }
			});
		}
		else {
			$.unblockUI();
		}
	},

	/**
	 * PROGRESS BAR 동작 함수
	 */
	progressEffect : function(show) {
		if (show) {
			$.blockUI({ message :  '<div id="myProgress"><div id="myBar" class="ksm-progress-bar ksm-progress-bar-stripes active">0%</div></div>'
				, css : { border: 'none',
					 width : '300px',
					 height : '30px',
					 backgroundColor : 'rgba(256,256,256,.0)',
					 '-webkit-border-radius': '10px',
            		 '-moz-border-radius': '10px',
					 color: '#fff',
					 top:  ($(window).height() - 100) /2 + 'px',
                				 left: ($(window).width() - 300) /2 + 'px',
					 opacity : 0.8 }
			});
		}
		else {
			$.unblockUI();
		}
	},

    // ------------------------------------------
    // UI 관련 유틸: Message Layer Popup
    // ------------------------------------------

     /**
      * 메세지, confirm 등을 처리 할 수 있는 layer popup을 생성한다.
      * @param value - 레이어에 뿌려질 메세지 값
      * @param option - json opject를 설정시 버튼명과 버튼에 따른 실행 함수를 설정한다.
      *               - params.popupSect : 팝업의 형태를 구분한다. none(일반 메세지), confirm(확인,취소), ok(확인), cancel(취소)
      *               - params.fnOk : 확인시 실행될 함수
      *               - params.fnOkParam : 확인시 실행될 함수 파라메터 json object
      *               - params.okTitle : 확인 버튼 명
      *               - params.fnCancel : 취소시 실행될 함수
      *               - params..fnCancelParam : 취소시 실행될 함수 파라메터 json object
      *               - params.cancelTitle : 취소 버튼 명
      *               - params.fnClosed : 닫기 버튼 동작시 실행될 함수
      *               - params.msgSect : 메세지 구분에 따라 아이콘으로 함축된 메세지를 표시한다. { question, warnning, info, error}
      * default message popup
      */
    msgLayerPopup : function(value, params) {
        //confirm 버튼 처리
        var options = { popupSect : 'none',
                        fnClosed : (function(){ $('.alert-backdrop').remove(); $('.alert').remove(); }) };
        //params 존재시
        if (params) {
            options = {
                msgSect : params.msgSect ? params.msgSect : '',
                popupSect : params.popupSect ? params.popupSect : 'none',
                fnOk : params.fnOk ? params.fnOk : this.msgLayerPopupClose,
                fnOkParam : params.fnOkParam ? params.fnOkParam : {},
                okTitle : params.okTitle ? params.okTitle : '확인',
                fnCancel : params.fnCancel ? params.fnCancel : this.msgLayerPopupClose,
                fnCancelParam : params.fnCancelParam ? params.fnCancelParam : {},
                cancelTitle : params.cancelTitle ? params.cancelTitle : '취소',
                fnClosed : params.fnClosed ? params.fnClosed : this.msgLayerPopupClose
            }
        }

        //confirm 버튼 생성
        var btnAreaHTML = '';
        if (options.popupSect == 'none') btnAreaHTML += '<a href="javascript:;" class="close-alert">Close</a>';
            btnAreaHTML += '<div class="confirm-btn-area">';
        if (options.popupSect == 'confirm' || options.popupSect == 'ok') btnAreaHTML += '<a class="btn btn-primary right-btn" id="confirmOkBtn">' + options.okTitle + '</a>';
        if (options.popupSect == 'confirm' || options.popupSect == 'cancel') btnAreaHTML += '<a class="btn btn-danger right-btn" id="confirmCancelBtn">' + options.cancelTitle + '</a>';
            btnAreaHTML += '</div>';

        //팝업 메세지 구분
        vPopupMsgSectHTML = '';
        if (options.msgSect != '') vPopupMsgSectHTML = '<span class="' + options.msgSect + '"></span>';

        //배경 dimmed 영역
        var vBackDropHTML = '';
            vBackDropHTML += '<div class="alert-backdrop fade in">';
            vBackDropHTML += '</div>';

        //popup 영역 조립
        var vPopupHTML = '';
            vPopupHTML += '<div class="alert">';
            vPopupHTML += '<div class="alert-content">';
            vPopupHTML += vPopupMsgSectHTML;
            vPopupHTML += '<p>' + this.textareaEnterReplaceHtmlTag(this.nullToEmptyString(value)) + '</p>';
            vPopupHTML += btnAreaHTML;
            vPopupHTML += '</div>';
            vPopupHTML += '</div>';

        $('body').append(vBackDropHTML);
        $('body').append(vPopupHTML);

        //버튼 이벤트 bind
        if (options.popupSect == 'none') {
            $('.close-alert').bind('click', function(e) {
                e.preventDefault();
                options.fnClosed();
            });
        }

        if (options.popupSect == 'confirm' || options.popupSect == 'ok') {
            $('#confirmOkBtn').bind('click', function(e) {
                e.preventDefault();
                if (typeof options.fnOk === 'function') {
                    options.fnOk(options.fnOkParam);
                }
                else if (typeof options.fnOk === 'string') {
                    var fnConfirmOk = eval(options.fnOk);
                    fnConfirmOk(options.fnOkParam);
                }
                else {
                    //console.log('>> fnOk: type error');
                }
            });
        }

        if (options.popupSect == 'confirm' || options.popupSect == 'cancel') {
            $('#confirmCancelBtn').bind('click', function(e) {
                e.preventDefault();
                if (typeof options.fnCancel === 'function') {
                    options.fnCancel(options.fnCancelParam);
                }
                else if (typeof options.fnCancel === 'string') {
                    var fnConfirmCancel = eval(options.fnCancel);
                    fnConfirmCancel(options.fnCancelParam);
                }
                else {
                    //console.log('>> fnCancel: type error');
                }
            });
        }

     },

     /**
      * 메세지 레이어 팝업을 닫는 함수
      */
     msgLayerPopupClose : function() {
         $('.alert-backdrop').remove();
         $('.alert').remove();
     },

	// ------------------------------------------
	// 기타 유틸리티 함수들
	// ------------------------------------------

	/**
	 * API에 대한 FQDN(Fully Qualified Domain Name)을 가져오는 함수
	 */
	getApiFullyQualifiedDomainName : function(url) {
        var vUrl = url;
        if (url.startsWith('/')) {
            vUrl = url.substring(1);
        }
		var fullyQualifiedDomainName = BACKEND_CONST.API_PROTOCOL
			+ BACKEND_CONST.API_HOST
			+ ((BACKEND_CONST.API_PORT === '80') ? '' : ':')
			+ ((BACKEND_CONST.API_PORT === '80') ? '' : BACKEND_CONST.API_PORT)
			+ BACKEND_CONST.API_CONTEXT_ROOT
			+ vUrl;

		return fullyQualifiedDomainName;
	},

	/**
	 * 애플리케이션에 대한 FQDN(Fully Qualified Domain Name)을 가져오는 함수
	 */
	getAppFullyQualifiedDomainName : function(method) {
		var appFullyQualifiedDomainName = FRONTEND_CONST.FE_PROTOCOL
		+ FRONTEND_CONST.FE_HOST
		+ ((FRONTEND_CONST.FE_PORT === '80') ? '' : ':')
		+ ((FRONTEND_CONST.FE_PORT === '80') ? '' : FRONTEND_CONST.FE_PORT)
		+ FRONTEND_CONST.FE_CONTEXT_ROOT
		+ FRONTEND_CONST.FE_FILE_URL
		+ method;

		return appFullyQualifiedDomainName;
	},

    /**
     * 넘어온 값에서 html 코드를 제거해서 리턴한다.
     * @param html
     * @returns
     */
	 removeHtmlTag : function(html) {
		 html = html.replace(/(<([^>]+)>)/gi, "");
		 html = html.replace("&nbsp;");
		 html = html.replace("&nb");
		 html = html.replace("&n");
		 return html;
	 },

	 /**
	  * textarea 에서 입력한 값을 화면 출력시 개행 표시하기 위한 이벤트
	  * @param html
	  * @returns
	  */
	 textareaEnterReplaceHtmlTag : function(html) {
		 return html.replace(/\n/g, "<br/>");
	 },

	 /**
	  * 화면 범위를 넘어간 이미지 태그에 대해서 리사이징 하는 태그
	  * @param targetId - 대상 범위의 ID를 넘겨주면 됨
	  * @returns
	  */
	 imgResizeHtmlTag : function(targetId) {
		 $.each($('#'+targetId).find('img'), function(i) {
			 $('#'+targetId + ' img:eq('+ i +')').load(function(){
				 var vWindowWidth     =  $( window ).width();
				 var vTargetImgWidth  = $('#'+targetId + ' img:eq('+ i +')').width();
				 var vTargetImgHeight = $('#'+targetId + ' img:eq('+ i +')').height();
				 var vResizePer		  = 0;

				 if (vTargetImgWidth > vWindowWidth) {
					 vResizePer = Math.floor( (vWindowWidth / vTargetImgWidth) * 100 ) / 100 - 0.1;

					 $('#'+targetId + ' img:eq('+ i +')').width(vTargetImgWidth * vResizePer);
					 $('#'+targetId + ' img:eq('+ i +')').height(vTargetImgHeight * vResizePer);
				 }
			 })

		 });
		 return '';
	 },

	/**
	 * 키 이벤트를 잡아서 동작시키는 함수
	 * @param event - key 이벤트 값 전달
	 * @param keyCode - 이벤트 동작을 위한 특정 키 코드
	 * @param callBack - 완료시 동작을 원하는 함수
	 * @param callBackParam - 동작 함수에 넘기는 파라메터
	 */
	 keyEventCatch : function(event, keyCode, callBack, callBackParam) {
		 if (event.keyCode == keyCode) {
			if (typeof callBack === 'function') {
				callBack(callBackParam);
			}
			else if (typeof callBack === 'string') {
				var fnCallBack = eval(callBack);
				fnCallBack(callBackParam);
			}
			else {
				//console.log('>> fnCallBack: type error');
			}
		}
	},

    /**
     * 오늘 날짜를 8자리로 가져오는 함수
     */
    getTodayDate : function(sectCode) {
        var dateObject = new Date();

        var vFullYear   =  dateObject.getFullYear();
        var vMonth  =  (dateObject.getMonth() + 1);
        var vDate   =  dateObject.getDate();

        // 구분값
        if ( sectCode === undefined ) { sectCode = '' };
        if ( vMonth < 10 ) { vMonth = '0' + vMonth };
        if ( vDate < 10 ) { vDate = '0' + vDate };

        return vFullYear + sectCode + vMonth + sectCode + vDate;
    },

    /**
     * 현재 시간을 6자리로 가져오는 함수
     */
    getNowTIme : function(sectCode) {
        var dateObject = new Date();

        var vHours  =  dateObject.getHours();
        var vMin    =  dateObject.getMinutes();
        var vSec    =  dateObject.getSeconds();

        //구분값
        if ( sectCode === undefined ) { sectCode = '' };
        if ( vHours < 10 ) { vHours = '0' + vHours };
        if ( vMin < 10 ) { vMin = '0' + vMin };
        if ( vSec < 10 ) { vSec = '0' + vSec };

        return vHours + sectCode + vMin + sectCode + vSec;
    },

    getTimeStamp : function () {
        var d = new Date();
          var s =
            this.leadingZeros(d.getFullYear(), 4) + '-' +
            this.leadingZeros(d.getMonth() + 1, 2) + '-' +
            this.leadingZeros(d.getDate(), 2) + ' ' +

            this.leadingZeros(d.getHours(), 2) + ':' +
            this.leadingZeros(d.getMinutes(), 2) + ':' +
            this.leadingZeros(d.getSeconds(), 2);

          return s;
    },

    /**
     * 화면 출력시 값이 없는 객체표현을 공백으로 변환
     */
    nullToEmptyString : function( value ) {
        if (value === null || value === undefined) {
            return '';
        }
        else if (typeof value === 'string' && (value.toLowerCase() === 'null' || value.toLowerCase() === 'undefined')) {
            return '';
        }
        else {
            return value;
        }
    },

    /**
     * 자바스크립트 prototype 으로 trim 만들기 (공백제거 함수)
     */
    trim : function (value) {
        return value.replace(/\s/g, "");
    },

    /**
     * LPAD
     */
    lpad: function(s, c, n) {
        if (! s || ! c || s.length >= n) {
            return s;
        }

        var max = (n - s.length)/c.length;
        for (var i = 0; i < max; i++) {
            s = c + s;
        }

        return s;
    },

    leadingZeros : function (n, digits) {
        var zero = '';
          n = n.toString();
          if (n.length < digits) {
            for (i = 0; i < digits - n.length; i++)
              zero += '0';
          }
          return zero + n;
    },

	byteCalculation : function (bytes) {
		var bytes = parseInt(bytes);
		var s = ['bytes', 'KB', 'MB', 'GB', 'TB', 'PB'];
		var e = Math.floor(Math.log(bytes)/Math.log(1024));

		if (e == "-Infinity") return "0 "+s[0];
		else
		return (bytes/Math.pow(1024, Math.floor(e))).toFixed(2)+" "+s[e];
	},

	fileDownlaoder : function (fileInfo) {
		//상수로 변경하기
		var uri = encodeURI(KSM_UTIL.getAppFullyQualifiedDomainName('webDownload'));
		document.fileDownloadForm.filePath.value = fileInfo.filePath;
		document.fileDownloadForm.logicalFileName.value = fileInfo.logicalFileName;
		document.fileDownloadForm.physicalFileName.value = fileInfo.physicalFileName;
		document.fileDownloadForm.ext.value = fileInfo.ext;
		document.fileDownloadForm.token.value = USER_INFO.token;
		document.fileDownloadForm.action=uri;
		document.fileDownloadForm.submit();
	},

    phoneNumberChk : function (val) {
        if (val.match( /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})[0-9]{3,4}[0-9]{4}$/)) {
            return true;
        }
        else {
            return false;
        }
    }

};
