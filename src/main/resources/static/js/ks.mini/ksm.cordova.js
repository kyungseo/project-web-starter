/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > CORDOVA
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-11-07      initial version
 * ========================================================================= */

// ============================================================================
// Mobile Groupware App
// ============================================================================

var APP_STORAGE_KEY = "ksmAppState";

//console.log('APP_STORAGE_KEY: ' + APP_STORAGE_KEY);

// onResume()과 onPause() 간에 있어 app의 상태를 저장하고 복원하기 위한 개체
var appState = {
    someValue: true,
    otherValue: ""
};

var app = {
	initialize: function() {

		if (typeof initNetworkCheck !== 'undefined' &&
				typeof initNetworkCheck === 'function') {
			initNetworkCheck();
		}

		/*window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function (fs) {

		    console.log('file system open: ' + fs.name);
		    fs.root.getFile("newPersistentFile.txt", { create: true, exclusive: false }, function (fileEntry) {

		        console.log("fileEntry is file?" + fileEntry.isFile.toString());
		        // fileEntry.name == 'someFile.txt'
		        // fileEntry.fullPath == '/someFile.txt'
		        writeFile(fileEntry, null);

		    }, function(){});

		}, function(){});*/


		this.bindEvents();
	},
	bindEvents: function() {
		// event listener를 등록한다.
		//  - pause / resume / backbutton / menubutton /
		//    searchbutton / endcallbutton / volumedownbutton / activated
		document.addEventListener('pause', this.onPause, false);
		document.addEventListener('resume', this.onResume, false);
		document.addEventListener('menubutton', this.onMenuKeyDown, false);
		document.addEventListener('backbutton', this.onBackKeyDown, false);

		//document.addEventListener('online', this.onOnline, false);//2016.12.22 정병호 주석
		//document.addEventListener('offline', this.onOffline, false);//2016.12.22 정병호 주석


		/* windows에 대응하기 위해서는 다음 코드를 사용해야 함.
		document.addEventListener('backbutton', function (evt) {
		    if (cordova.platformId !== 'windows') {
		        return;
		    }

		    if (window.location.href !== firstPageUrl) {
		        window.history.back();
		    } else {
		        throw new Error('Exit'); // This will suspend the app
		    }
		}, false);
		*/
		this.deviceInfoRetrieve();
	},
	// app이 background로 갈 때
	onPause: function() {
		window.localStorage.setItem(APP_STORAGE_KEY, JSON.stringify(appState));
	},
	// pause된 app이 background에서 다시 활성화 될 때
	onResume: function(e) {
		setTimeout(function() {
			// iOS의 경우 alert()과 같은 interactive function이 호출될 때
			// timeout 값을 '0'로 하여 setTimeout()으로 wrapping해야 한다.
			// 그렇지 않으면, app에 hang이 발생함.
			var storedState = window.localStorage.getItem(APP_STORAGE_KEY);

			if (storedState) {
				appState = JSON.parse(storedState);
			}
		}, 0);
	},
	// backKey기 눌렸을 때
	onBackKeyDown: function() {
		// KSM Dialog 창이 오픈되어 있으면 backKey에서 닫아준다.
		if (KSM.flags.isOpenKsmDialog) {
			KSM_DIALOG.closeAll();
			KSM.flags.isOpenKsmDialog = false; // Dialog가 오픈된 상태인지 여부를 트래킹
			return false;
		}

		// 현재 페이지를 체크하여 backKey를 체러한다.
		var currentPage = document.location.href;
		//console.log('currentPage: ' + currentPage);

		if (currentPage.indexOf('login.html') > -1 || currentPage.indexOf('index.html') > -1) {
			//e.preventDefault();
			//confirmExit();
			KSM_DIALOG.exitConfirm({
				title: 'KSM Mobile Groupware 종료',
				message: 'KSM 모바일 그룹웨어를 종료하시겠습니까?'});
		}
		else {
			// 네트워크 연결 체크, 네트워크가 off이면 navigation 하지 않도록 한다.
			// (위 index, login 페이지들을 포함한 전체 페이지에서 network 체크를
			// 하지 않는 이유는 어차피 이 페이지들에서 백키는 종료 팝업을 띄우는 것이기 때문,
			// 종료하고자 백키를 눌렀는데 네트워크 체크 팝업이 뜨면 이상해지기 때문임)
			var isConnectedNetwork = true;
			try {
				isConnectedNetwork = KSM_DEVICE_API.fnNetworkOffCheck();
			} catch(e) {}
			if (! isConnectedNetwork) return false;

			// 네트워크가 online이면 navigation 하도록 한다.

			navigator.app.overrideBackbutton(false);
			if (typeof pageEventHandleForObBackKeyDown !== 'undefined' &&
				typeof pageEventHandleForObBackKeyDown === 'function') {
				// page.js에 'eventHandleForObBackKeyDown'란 function이 정의되어
				// 있으면, backkey에 대한 이벤트를 각자 처리할 수 있도록 호출해준다.
				//console.log('>> call pageEventHandleForObBackKeyDown()!!!');
				navigator.app.overrideBackbutton(true);
				pageEventHandleForObBackKeyDown();
			}
			else {
				// page.js에 'pageEventHandleForObBackKeyDown' function이 없으면
				// 기본 수행을 한다.
				//console.log('>> NOT DEFINED pageEventHandleForObBackKeyDown()');
				navigator.app.backHistory();
			}
		}
	},
	// menuKey가 놀렸을 때
	onMenuKeyDown: function() {

	},
	onOnline: function() {
		if (KSM.flags.isOpenKsmDialog) {
			// 잠깐 Offline으로 변경되어 경고 팝업이 떠 있는 상태에서
			// 바로 LTE 망으로 Online 전환된 경우 위 팝업창 위에 Wi-Fi망이 아니라는
			// 경고 창이 뜨게 된다. 따라서 Offline 경고 창이 있다면 닫아준다.
			//
			// To 정병호: 쉽게 이해하기 위해 열린 팝업(Dialog)를 닫아주는 다음
			// 코드를 넣지않고 핸드폰에서 LTE를 껏다 켯다를 반복하면 그만큼의
			// 팝업이 누적해서 쌓이게 됨. 아래 onOffline도 마찬가지.
			//
			KSM_DIALOG.closeAll();
			KSM.flags.isOpenKsmDialog = false; // Dialog가 오픈된 상태인지 여부를 트래킹
		}

		KSM_DEVICE_API.fnNetworkCheck(true, true);

	},
	onOffline: function() {
		if (KSM.flags.isOpenKsmDialog) {
			// LTE 망으로을 체크하여 Wi-Fi망이 아니라는 경고 팝업이 뜬 상태에서
			// 바로 네트워크가 끊겨 Offline으로 전환되는 경우 위 경고 팝업 위에
			// Offline 경고 팝업이 뜨게 된다. 따라서 기존 경고 창이 있다면 닫아준다.
			KSM_DIALOG.closeAll();
			KSM.flags.isOpenKsmDialog = false; // Dialog가 오픈된 상태인지 여부를 트래킹
		}

		setTimeout(function(){
			KSM_DEVICE_API.fnNetworkOffCheck();
		}, 3000);
	},
	deviceInfoRetrieve: function() {
		/*
		console.log(device.platform);//플랫폼Android
		console.log(device.version);//버전6.0.1 //안드로이드 버전 // os확인후
		console.log(device.uuid);//uuid//d2fc6ab5f7d0b57a
		console.log(device.available);//true--
		console.log(device.cordova);//5.2.2--
		console.log(device.model);//SM-N915S //모델번호
		console.log(device.manufacturer);//samsung
		console.log(device.isVirtual);//false--
		console.log(device.serial);//410018a5e407c1bf
		*/

		if (KSM_UTIL.nullToEmptyString(KSM_STORAGE.storage({action:'SELECT', key:'device.uuid', value:'', storage:'session'})) === '') {
			KSM_STORAGE.storage({action:'save', key:'device.platform', value:device.platform, storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.version', value:device.version, storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.uuid', value:device.uuid, storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.available', value:device.available+'', storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.cordova', value:device.cordova+'', storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.model', value:device.model, storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.manufacturer', value:device.manufacturer, storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.isVirtual', value:device.isVirtual, storage:'session'});
			KSM_STORAGE.storage({action:'save', key:'device.serial', value:device.serial, storage:'session'});
			KSM_DEVICE_CUSTOM.getDeviceNumber();
			KSM_DEVICE_CUSTOM.getDeviceImei();
			KSM_DEVICE_CUSTOM.getDeviceWifiMac();
			KSM_DEVICE_CUSTOM.getDeviceBluetoothMac();
		}
	},
	createShortcut: function() {
		window.plugins.Shortcut.CreateShortcut(
			"KSM 그룹웨어",
			function() {
				//console.log('yes');
			},
			function() {
				//console.log('no');
		});
	}

};

var KSM_DEVICE_API = {
	cameraPhotograph: function(params) {
		var Camera = navigator.camera;
		var cameraOption  = {};

		if (params.type === 'api') {
			cameraOption  = {
	            quality: (params.quality != undefined) ? params.quality : 100,
	            destinationType : Camera.DestinationType.FILE_URI, //Camera.DestinationType.DATA_URL, 이진데이터
	            sourceType : (params.sourceType != undefined) ? params.sourceType : 1,
	            allowEdit: (params.allowEdit != undefined) ? params.allowEdit : true,
	            correctOrientation: (params.correctOrientation != undefined) ? params.correctOrientation : true
			};
		}
		else {
			cameraOption  = {
	            quality: (params.quality != undefined) ? params.quality : 100,
	            destinationType : Camera.DestinationType.FILE_URI, //Camera.DestinationType.DATA_URL, 이진데이터
	            sourceType : (params.sourceType != undefined) ? params.sourceType : 1,
	            targetWidth: (params.targetWidth != undefined) ? params.targetWidth : 1000,
	            targetHeight: (params.targetHeight != undefined) ? params.targetHeight : 1000,
	            allowEdit: (params.allowEdit != undefined) ? params.allowEdit : true,
	            correctOrientation: (params.correctOrientation != undefined) ? params.correctOrientation : true
			};
		}

	    navigator.camera.getPicture(this.successGetPictureCallback, this.errorCallback, cameraOption);
	}, successGetPictureCallback: function(imageURI) {
		if (typeof onSuccessGetPictureCallback !== 'undefined' &&
			typeof onSuccessGetPictureCallback === 'function') {
			onSuccessGetPictureCallback(imageURI);
		}
	}, errorCallback: function(error) {
		//에러 정보 display
		//console.log("Error Function.");
	},
	fileUpload: function(params) {
		var options = new FileUploadOptions();
	    /*options.fileKey = "file";
	    options.fileName = USER_INFO.code+".jpg";
	    options.mimeType = "image/jpeg";*/

	    options.fileKey = (params.fileKey != undefined) ? params.fileKey : "file";
	    options.fileName = (params.fileName != undefined) ? params.fileName : USER_INFO.code+".jpg";//디폴트 값은 프로필 이미지 경우
	    options.mimeType = (params.mimeType != undefined) ? params.mimeType : "image/jpeg";

	    if (params.info != undefined) {
	    	options.params = params.info;
	    }
	    else {
	    	var paramsInfo = {
                userCode :USER_INFO.code,
                token :USER_INFO.token
            };
	    	options.params = paramsInfo;
	    };

	    //파일과 관련된 정보를 입력

	    var ft = new FileTransfer();

	    //해당 경로 전역으로 설정하기
	    var uri = "";
	    if (params.type != undefined) {
	    	uri = encodeURI(KSM_UTIL.getApiFullyQualifiedDomainName('gw/api/v1/deviceSample/singleFileUpload'));
	    	 var hearder = {
		   		'Authorization':'Bearer '+USER_INFO.token
		    };
		    options.headers = hearder;
	    }
	    else {
	    	uri = encodeURI(KSM_UTIL.getAppFullyQualifiedDomainName(params.method));
	    }
	    //var fileUploadUrl ='http://test.kico.co.kr:10080/mobile/file.do?method='+params.method;
	    //var fileUploadUrl ='http://km.kico.co.kr/mobile/file.do?method='+params.method;

	    ft.upload(params.filepath, uri,
	    		function(r) {
	    			//각페이지에서 처리할 함수 선언
	    			//onSuccessFileUploadCallback
		    		if (typeof onSuccessFileUploadCallback !== 'undefined' &&
		    			typeof onSuccessFileUploadCallback === 'function') {
	    				onSuccessFileUploadCallback(r);
		    		}
	    		},
	    		function(error) {
	    			//파일 업로드 에러처리 공통 실패
	    			KSM_DIALOG.error('An error has occurred: Code = ' + error.code);
	    		    console.log("upload error source " + error.source);
	    		    console.log("upload error target " + error.target);

	    		}, options);
	},
	saveContact: function(params) {
		var contact = navigator.contacts.create();

		contact.displayName = params.displayName;//"연락처 저장테스트";
		var phoneNumbers = [];
		//phoneNumbers[0] = new ContactField('mobile', params.phoneNumber, true);
		phoneNumbers[0] = {type : 'mobile', value: params.phoneNumber, id: 0};
		contact.phoneNumbers = phoneNumbers;

		var emails = [];
		emails[0] = new ContactField('work', params.email, false);
		contact.emails = emails;

		if (params.photo !== '') {
			var photos = [];
			photos[0] = {pref : false,type : 'url',value : params.photo};
			contact.photos  = photos;
		}
		contact.save(function() {
			KSM_DIALOG.info('연락처 저장에 성공했습니다.');
			setTimeout(function(){
				KSM_DIALOG.closeAll();
			}, 1500);
		},
		function(error) {
			KSM_DIALOG.error('연락처 저장에 실패했습니다.');
		});
	},
	findContact: function(params) {

		var options = new ContactFindOptions();
		options.filter   = params.filter; //phoneNumber
		options.multiple = true;
		options.hasPhoneNumber = true;
		var fields = [params.fields];

		navigator.contacts.find(fields,
				function (contacts) {
					if (typeof onSuccessFindContactCallback !== 'undefined' &&
							typeof onSuccessFindContactCallback === 'function') {
						onSuccessFindContactCallback(contacts);
					}
				},
				function (contactError) {
					KSM_DIALOG.error('연락처 조회에 실패했습니다.');
				},
				options
				);
	},
	fileDownlaoder: function(fileInfo) {
		//상수로 변경하기
		var uri = '';

		if (fileInfo.to) {
			uri = encodeURI(KSM_UTIL.getApiFullyQualifiedDomainName('gw/api/v1/download'));
		} else {
			uri = encodeURI(KSM_UTIL.getAppFullyQualifiedDomainName('download'));
		}

		KSM_UTIL.progressEffect(true);

		//Cordova 전용 파일다운로드
		window.requestFileSystem(LocalFileSystem.PERSISTENT, 0,
		    function (fileSystem) {
		        var fileTransfer = new FileTransfer();
		        var filename = fileSystem.root.toURL() +'/Download/'+fileInfo.logicalFileName;
		        var perc = 0;
		        fileTransfer.onprogress = function(progressEvent) {
		        	var statusTxt = '0%';
		        	perc = 0;
		        	if (progressEvent.lengthComputable) {
		        		perc = Math.floor(progressEvent.loaded / progressEvent.total * 100);
		        		statusTxt = perc + '%';
		        	}
		        	$('#myBar').css('width',perc + '%');
		        	$('#myBar').text(perc + '%');
		        }

		        //다운로드 시작하는 중... //Toast 만들기

		        fileTransfer.download(uri, filename,
		            function(entry) { // download success
		                var path = entry.toURL(); //**THIS IS WHAT I NEED**
		                KSM_UTIL.progressEffect(false);
		                if(perc < 100) {
		                	/*navigator.notification.alert(
		                		    '실패 - 다운로드 오류',  // message
		                		    function(){},         // callback
		                		    'Error',            // title
		                		    '확인'                  // buttonName
		                		);*/
		                	KSM_DIALOG.error('실패 - 다운로드 오류');//에러

		                } else {
		                	navigator.notification.confirm(fileInfo.logicalFileName+' 다운로드가 완료되었습니다.', function(button) {
			            		if (button === 2) {
			            			window.open(path, "_system");
			            		}
			            	}, 'KSM Mobile Groupware', '취소, 열기'); // 취소 = 1, 열기 = 2

		                }
		            },
		            function(error) {} // irrelevant download error
		            , false
		            , {
		            	headers: {
		            			'filePath':fileInfo.filePath,
		            			'logicalFileName':fileInfo.logicalFileName,
		            			'physicalFileName':fileInfo.physicalFileName,
		            			'ext':fileInfo.ext,
		            			'token':USER_INFO.token,
		            			'Authorization':'Bearer ' + USER_INFO.token
		            		}
		            	}
		        );
		    },
		    function(error) {} // irrelevant request fileSystem error
		);
	},
	/** 데이터망 체크 여부
	var isLteCheck = true; //WIFI가 아닌 경우 무조건 확인
	var isLte = false;*/
	fnNetworkCheck: function(doCheck,isLteCheck){//doCheck false인 경우 컨펌 체크 하지 않고 그냥 데이터통신 사용하겠다. true인 경우 물어보고 사용을 하고 아니면 false 로 네트워크 사용하지 않겠다.
		var networkState = navigator.connection.type;
	    if (networkState == Connection.UNKNOWN || networkState == Connection.NONE) {
	        /*KSM_DIALOG.exitConfirm({
				title: 'KSM Mobile Groupware 종료',
				message: '현재 네트워크가 연결되어 있지 않습니다.\nKSM 모바일 그룹웨어를 종료하시겠습니까?'});
	        return false;// 2016.12.20 정병호 수정 주석처리*/
	    }

		if (networkState != Connection.WIFI){//WIFI 가 맞다면 TRUE
			//doCheck(false인경우 아래 체크를 안함, true인 경우 한방에 아래 체크수행)
			if(!doCheck) {
				if(isLteCheck) {//상단 선언을 true로 한 경우 그냥 데이터망 쓰겟다. 체크 안함가 //와이파이가 아니라도 그냥 아래 메시지 안타고 쓰겟다
				    //isLte = true;
					return true;
				}
			}
			else {
				KSM_DIALOG.confirm('Wi-Fi 망이 아닐 경우 추가적인 비용이 발생할 수 있습니다.\n계속 하시겠습니까?'
						, function() {}
						, function() {
							navigator.app.exitApp();
						}
						, null
						, '확인'
						, '그룹웨어 종료');
				return false;
			}

			/*if(confirm('Wi Fi 망이 아닐경우 추가적인 비용이 발생 할 수 있습니다. \n계속 하시겠습니까?')) {
				//isLte = true;
				return true;
			} else {
				navigator.app.exitApp();
				return false;
			}*/
		} else {
			return true;
		}
	},
	/** 데이터망 체크 여부
	var isLteCheck = true; //WIFI가 아닌 경우 무조건 확인
	var isLte = false;*/
	fnNetworkOffCheck: function(){//doCheck false인 경우 컨펌 체크 하지 않고 그냥 데이터통신 사용하겠다. true인 경우 물어보고 사용을 하고 아니면 false 로 네트워크 사용하지 않겠다.
		var networkState = navigator.connection.type;
	    if (networkState == Connection.UNKNOWN || networkState == Connection.NONE) {
	        KSM_DIALOG.exitConfirm({
				title: 'KSM Mobile Groupware 종료',
				message: '현재 네트워크가 연결되어 있지 않습니다.\nKSM 모바일 그룹웨어를 종료하시겠습니까?'});
	        return false;
	    }
	    else {
			return true;
		}
	}

}

//============================================================================
//Cordova Custom Plug-In
//============================================================================
var KSM_DEVICE_CUSTOM = {
	getDeviceNumber: function() {
        return cordova.exec(this.fnDeviceNumberSuccess, this.fnFail, "KicoDeviceInfoPlugin", "phoneNumber", []);
    },
    getDeviceImei	: function() {
    	return cordova.exec(this.fnDeviceImeiSuccess, this.fnFail, "KicoDeviceInfoPlugin", "imei", []);
    },
    getDeviceWifiMac	: function() {
    	return cordova.exec(this.fnDeviceWifiMacSuccess, this.fnFail, "KicoDeviceInfoPlugin", "wifiMac", []);
    },
    getDeviceBluetoothMac	: function() {
    	return cordova.exec(this.fnDeviceBluetoothMacSuccess, this.fnFail, "KicoDeviceInfoPlugin", "bluetoothMac", []);
    },
    fnDeviceNumberSuccess: function(result) {
    	KSM_STORAGE.storage({action:'save', key:'kicoPlugin.phoneNumber', value:result, storage:'session'});
    },
    fnDeviceImeiSuccess: function(result) {
    	KSM_STORAGE.storage({action:'save', key:'kicoPlugin.imei', value:result, storage:'session'});
    },
    fnDeviceWifiMacSuccess: function(result) {
    	KSM_STORAGE.storage({action:'save', key:'kicoPlugin.wifiMac', value:result, storage:'session'});
    },
    fnDeviceBluetoothMacSuccess: function(result) {
    	KSM_STORAGE.storage({action:'save', key:'kicoPlugin.bluetoothMac', value:result, storage:'session'});
    },
    fnFail: function(error) {
    	//console.log("KSM_DEVICE_CUSTOM fnFail : " + error);
    }
}

/*
navigator.app.backHistory();
navigator.app.cancelLoadUrl();
navigator.app.clearCache();
navigator.app.clearHistory();
navigator.app.exitApp();
navigator.app.loadUrl(url, props);
navigator.app.overrideBackbutton(override);
 */

// ============================================================================
// Cordova 공통 처리
// ============================================================================

$(document).ready(function() {
	document.addEventListener('deviceready', onDeviceReady, false);
});

function onDeviceReady() {
	//console.log('>> Cordova Device API 준비 완료!');
	app.initialize();
}

// Mobile Groupware App 종료 확인
function confirmExit() {
	navigator.notification.confirm('KSM 모바일 그룹웨어를 종료하시겠습니까?', function(button) {
		if (button === 2) {
			navigator.app.exitApp();
		}
	}, 'KSM Mobile Groupware 종료', '취소, 종료'); // 취소 = 1, 종료 = 2
}





