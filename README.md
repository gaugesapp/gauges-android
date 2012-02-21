# Gaug.es for Android

This repository contains the source code for the Gaug.es Android
application available from the [Android Market](https://market.android.com/).

Please see the [issues](https://github.com/github/gauges-android/issues) section
to report any bugs or feature requests and to see the list of known issues.

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Building

The build requires [Maven](http://maven.apache.org/download.html)
(v3.0.3 minimum) and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your sdk, eg:

    export ANDROID_HOME=/home/roberto/tools/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `mvn clean package` from the `app` directory - to simply build the APK
* Run `mvn clean install` from the root directory to build the app and also run
  the integration tests - you'll need to have an Android device or emulator
  connected.

You might find that your device doesn't let you install your build if you
already have the version from the Android Market installed - this is standard
Android security, it won't let you directly replace an app that's been signed
with a differing key. Just manully uninstall Gauges from your device and you'll
then be able to install your own version.

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/github/gauges-android/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.
