package com.frcal.friendcalender.Authentication;

import static android.content.Context.KEYGUARD_SERVICE;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.KeyguardManager;

import androidx.biometric.BiometricManager;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class AuthenticationManager {

    public enum authenticationMethod {
        BIOMETRIC_AUTHENTICATION,
        DEVICE_CREDENTIAL_AUTHENTICATION,
        NO_AUTHENTICATION
    }

    // Überprüft, welche Authentifizierungsmethode der Nutzer eingerichtet hat
    public static authenticationMethod checkForAuthenticationMethod(Context activityContext) {
        KeyguardManager manager = (KeyguardManager) activityContext.getSystemService(
                KEYGUARD_SERVICE);
        BiometricManager biometricManager = BiometricManager.from(activityContext);
        if (biometricManager.canAuthenticate(
                BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            return authenticationMethod.BIOMETRIC_AUTHENTICATION;
        } else if (!manager.isDeviceSecure() || biometricManager.canAuthenticate(
                DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
            return authenticationMethod.DEVICE_CREDENTIAL_AUTHENTICATION;
        } else {
            return authenticationMethod.NO_AUTHENTICATION;
        }
    }

    // Falls der Nutzer keine Authentifizierungsmethode eingerichtet, so wird dies nachgeholt
    // Danach wird geprüft, ob er tatsächlich eine Authentifizierungsmethode eingerichtet hat
    public static authenticationMethod setAuthenticationMethod(Context activityContext,
                                                               Bundle bundle) {
        KeyguardManager manager = (KeyguardManager) activityContext.getSystemService(
                KEYGUARD_SERVICE);
        BiometricManager biometricManager = BiometricManager.from(activityContext);
        if (biometricManager.canAuthenticate(
                BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            enrollAuthenticationMethod(authenticationMethod.BIOMETRIC_AUTHENTICATION,
                    activityContext, bundle);
        } else if (!manager.isDeviceSecure() || biometricManager.canAuthenticate(
                DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            enrollAuthenticationMethod(authenticationMethod.DEVICE_CREDENTIAL_AUTHENTICATION,
                    activityContext, bundle);
        }
        return checkForAuthenticationMethod(activityContext);
    }

    // Falls die notwendige Authentifizierungsmethode noch nicht eingerichtet ist, wird der
    // Nutzer in die Systemeinstellungen geleitet um dies nachzuholen
    public static void enrollAuthenticationMethod(authenticationMethod authMethod,
                                                  Context activityContext, Bundle bundle) {
        Intent enrollIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
            if (authMethod == authenticationMethod.BIOMETRIC_AUTHENTICATION) {
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
            } else {
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        DEVICE_CREDENTIAL);
            }
        } else if (authMethod == authenticationMethod.BIOMETRIC_AUTHENTICATION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
        } else {
            enrollIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        }
        startActivity(activityContext, enrollIntent, bundle);
    }

}
