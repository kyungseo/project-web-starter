/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : CryptoJS에 기반해서 작성한 암호화 유틸리티
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-11-03      initial version
 * ========================================================================= */

//============================================================================
// Crypto
//============================================================================

var KSM_CRYPTO = {

	encryptByDES: function(message, key) {
	    var keyHex = CryptoJS.enc.Utf8.parse(key);
	    var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
	        mode: CryptoJS.mode.ECB,
	        padding: CryptoJS.pad.Pkcs7
	    });
	    return encrypted.toString();
	},

	decryptByDES: function(ciphertext, key) {
	    var keyHex = CryptoJS.enc.Utf8.parse(key);
	    // direct decrypt ciphertext
	    var decrypted = CryptoJS.DES.decrypt({
	        ciphertext: CryptoJS.enc.Base64.parse(ciphertext)
	    }, keyHex, {
	        mode: CryptoJS.mode.ECB,
	        padding: CryptoJS.pad.Pkcs7
	    });
	    return decrypted.toString(CryptoJS.enc.Utf8);
	}

};
