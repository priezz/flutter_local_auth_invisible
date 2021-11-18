# Changelog

## 2.2.0

- Updated example;
- **Breaking change**. All `LocalAuthentication` methods are now static.

## 2.1.0

- Updated minimum Flutter SDK to 2.5 and iOS deployment target to 9.0;
- Switched to Android V2 embedding;
- Updated Android lint settings;
- Migrated maven repository from jcenter to mavenCentral;
- Merged the upstream iOS code (1.1.8);
- Updated dependencies to latest stable versions;
- `authenticateWithBiometrics` method is deprecated in favor of `authenticate`;
- **Breaking change**. `maxTimeoutMillis` parameter is removed.

## 2.0.0

- Migrated to null-safety;
- Fixed a bug which caused the following error: "Attempted to finish an input event but the input event receiver has already been disposed.".

## 1.1.0

- Support Flutter 1.22.4+.

## 1.0.0

- Merge the upstream iOS code (0.6.3).

## 0.4.1+1

- Updated README.

## 0.4.1

- Added `stopAuthentication` method for Android platform.

## 0.4.0+3

- Updated the homepage link.

## 0.4.0+2

- Merge the upstream iOS code (0.6.0+1), disable the Android dialog.

## 0.4.0

- **Breaking change**. Migrate from the deprecated original Android Support
  Library to AndroidX. This shouldn't result in any functional changes, but it
  requires any Android apps using this plugin to [also
  migrate](https://developer.android.com/jetpack/androidx/migrate) if they're
  using the original support library.

## 0.3.1

- Fix crash on Android versions earlier than 24.

## 0.3.0

- **Breaking change**. Add canCheckBiometrics and getAvailableBiometrics which leads to a new API.

## 0.2.1

- Updated Gradle tooling to match Android Studio 3.1.2.

## 0.2.0

- **Breaking change**. Set SDK constraints to match the Flutter beta release.

## 0.1.2

- Fixed Dart 2 type error.

## 0.1.1

- Simplified and upgraded Android project template to Android SDK 27.
- Updated package description.

## 0.1.0

- **Breaking change**. Upgraded to Gradle 4.1 and Android Studio Gradle plugin
  3.0.1. Older Flutter projects need to upgrade their Gradle setup as well in
  order to use this version of the plugin. Instructions can be found
  [here](https://github.com/flutter/flutter/wiki/Updating-Flutter-projects-to-Gradle-4.1-and-Android-Studio-Gradle-plugin-3.0.1).

## 0.0.3

- Add FLT prefix to iOS types

## 0.0.2+1

- Update messaging to support Face ID.

## 0.0.2

- Support stickyAuth mode.

## 0.0.1

- Initial release of local authentication plugin.
