import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_auth_invisible/flutter_local_auth_invisible.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _authorized = 'Not Authorized';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            Text(
              'Can ${AuthProviderState.biometricsEnabled ? '' : 'not '}'
              'auth with biometrics',
            ),
            Text('Current State: $_authorized\n'),
            AuthProvider(
              'hash',
              onSuccess: () => setState(
                () => _authorized = 'Authorized',
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class AuthProvider extends StatefulWidget {
  const AuthProvider(
    this.pinHash, {
    this.onFinishChecking,
    this.onStartChecking,
    this.onSuccess,
  });
  final Function()? onFinishChecking;
  final Function()? onStartChecking;
  final Function()? onSuccess;
  final String pinHash;

  @override
  AuthProviderState createState() => AuthProviderState();
}

class AuthProviderState extends State<AuthProvider> {
  static bool biometricsEnabled = false;
  bool _biometricsSelected = true;
  List<BiometricType> _biometricSensors = [];
  // ignore: unused_field
  String? _biometricsIcon;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance!.addPostFrameCallback((_) => _init());
  }

  Future<void> _init() async {
    _biometricSensors = await LocalAuthentication.getAvailableBiometrics();
    biometricsEnabled = (await LocalAuthentication.canCheckBiometrics) &&
        _biometricSensors.isNotEmpty;
    _biometricsIcon = _biometricSensors.contains(BiometricType.face)
        ? 'ui.faceId'
        : 'ui.fingerprint';
    setState(() {});
    await _launchScannerSchedule();
  }

  @override
  void didUpdateWidget(oldWidget) {
    _launchScannerSchedule();
    super.didUpdateWidget(oldWidget);
  }

  @override
  Widget build(BuildContext context) => Column(
        children: [
          biometricsEnabled && _biometricsSelected
              ? Text('BiometricsView()')
              : Text('PinPadView()'),
          TextButton(
            onPressed: _toggleView,
            child: Text('Toggle auth method'),
          ),
        ],
      );

  Future<void> _launchScannerSchedule() async {
    if (!biometricsEnabled) return;

    await LocalAuthentication.stopAuthentication();
    await Future.delayed(const Duration(milliseconds: 300), _launchScanner);
  }

  Future<void> _launchScanner() async {
    try {
      while (_biometricsSelected) {
        final didAuthenticate = await LocalAuthentication.authenticate(
          localizedReason:
              'Please pass the biometrical authentication to continue.',
          stickyAuth: false,
          useErrorDialogs: true,
        );
        if (didAuthenticate) {
          widget.onSuccess?.call();
          break;
        } else {
          await Future.delayed(const Duration(milliseconds: 500));
        }
      }
    } on PlatformException catch (_) {
      // Report the issue
    }
  }

  void _toggleView() {
    setState(() => _biometricsSelected = !_biometricsSelected);
    if (_biometricsSelected) {
      _launchScanner();
    } else {
      LocalAuthentication.stopAuthentication();
    }
  }
}
