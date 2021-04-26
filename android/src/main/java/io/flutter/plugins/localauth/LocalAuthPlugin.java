// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.localauth;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugins.localauth.AuthenticationHelper.AuthCompletionHandler;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/** LocalAuthPlugin */
public class LocalAuthPlugin implements MethodCallHandler {
  private final Registrar registrar;
  private final AtomicBoolean authInProgress = new AtomicBoolean(false);
  private AuthenticationHelper authenticationHelper;
  private final Handler handler = new Handler(Looper.getMainLooper());
  private Runnable delayRunnable;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel =
        new MethodChannel(registrar.messenger(), "plugins.flutter.io/local_auth");
    channel.setMethodCallHandler(new LocalAuthPlugin(registrar));
  }

  private LocalAuthPlugin(Registrar registrar) {
    this.registrar = registrar;
  }

  private void logDebug(Object value) {
    Log.d("LocalAuthPlugin", value.toString());
  }

  void stopIfNotStopped(Result result) {
    if (authInProgress.compareAndSet(true, false)) {
      logDebug("LOCAL AUTH STOPPED");
      result.success(false);
    }
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    if (call.method.equals("authenticateWithBiometrics")) {
      if (!authInProgress.compareAndSet(false, true)) {
        stopCurrentAuthentication();
      }
      int maxTimeoutMillis = call.argument("maxTimeoutMillis");
      
      if (delayRunnable != null) {
        handler.removeCallbacks(delayRunnable);
        delayRunnable = null;
      }

      delayRunnable = new Runnable() {
        @Override
        public void run() {
          stopIfNotStopped(result);
        }
      };

      handler.postDelayed(delayRunnable, maxTimeoutMillis);

      Activity activity = registrar.activity();
      if (activity == null || activity.isFinishing()) {
        result.error("no_activity", "local_auth plugin requires a foreground activity", null);
        return;
      }
      authenticationHelper =
          new AuthenticationHelper(
              activity,
              call,
              new AuthCompletionHandler() {
                @Override
                public void onSuccess() {
                  if (authInProgress.compareAndSet(true, false)) {
                    result.success(true);
                  }
                }

                @Override
                public void onFailure() {
                  if (authInProgress.compareAndSet(true, false)) {
                    result.success(false);
                  }
                }

                @Override
                public void onError(String code, String error) {
                  if (authInProgress.compareAndSet(true, false)) {
                    result.error(code, error, null);
                  }
                }
              });
      authenticationHelper.authenticate();
    } else if (call.method.equals("getAvailableBiometrics")) {
      try {
        ArrayList<String> biometrics = new ArrayList<String>();
        FingerprintManagerCompat fingerprintMgr =
            FingerprintManagerCompat.from(registrar.activity());
        if (fingerprintMgr.isHardwareDetected()) {
          if (fingerprintMgr.hasEnrolledFingerprints()) {
            biometrics.add("fingerprint");
          } else {
            biometrics.add("undefined");
          }
        }
        result.success(biometrics);
      } catch (Exception e) {
        result.error("no_biometrics_available", e.getMessage(), null);
      }

    } else if (call.method.equals(("stopAuthentication"))) {

      stopAuthentication(result);
    } else {
      result.notImplemented();
    }
  }

  private void stopCurrentAuthentication() {
    if (authenticationHelper != null && authInProgress.get()) {
      authenticationHelper.stopAuthentication();
      authenticationHelper = null;
    }
  }

  /*
   Stops the authentication if in progress.
  */
  private void stopAuthentication(Result result) {
    logDebug("Stop authentication called");
    try {
      if (authenticationHelper != null && authInProgress.get()) {
        stopCurrentAuthentication();
        result.success(true);
        return;
      }
      result.success(false);
    } catch (Exception e) {
      result.success(false);
    }
  }
}
