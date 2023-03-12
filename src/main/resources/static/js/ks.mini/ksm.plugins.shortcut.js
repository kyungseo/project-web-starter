/* ============================================================================
 * KyungSeo's Mini JavaScript Development Templates - KSM.js
 *
 * Copyright (c) 2016 by Kyungseo.Park@gmail.com
 * ============================================================================
 * AUTHOR      : Kyungseo Park
 * DESCRIPTION : KSM JavaScript Templates > Short Cut
 * ============================================================================
 * Revision History
 * Author   Date            Description
 * ------   ----------      ---------------------------------------------------
 * 박경서   2016-11-22      initial version
 * ========================================================================= */

var KsmShortcut = function() {};

KsmShortcut.prototype.CreateShortcut = function (shortcut_text, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'KsmShortcut',
            'addShortcut',
            [{
                "shortcuttext": shortcut_text
            }]
        );
};

KsmShortcut.prototype.RemoveShortcut = function(shortcut_text, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'KsmShortcut',
            'delShortcut',
            [{
                "shortcuttext": shortcut_text
            }]
        );
};

if (! window.plugins) {
    window.plugins = {};
}

if (! window.plugins.Shortcut) {
    window.plugins.Shortcut = new KsmShortcut();
}
