# Gaug.es Android App [![Build Status](https://travis-ci.org/github/gauges-android.png)](https://travis-ci.org/github/gauges-android)

This repository contains the source code for the [Gaug.es](http://get.gaug.es/)
Android app available from [Google Play](https://play.google.com/store/apps/details?id=com.github.mobile.gauges).

Please see the [issues](https://github.com/github/gauges-android/issues) section
to report any bugs or feature requests and to see the list of known issues.

<a href="https://play.google.com/store/apps/details?id=com.github.mobile.gauges" alt="Download from Google Play">
  <img src="http://img.skitch.com/20120709-kmtkmc3k3ib8tjym8eesxrs39r.png">
</a>

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Building

The build requires [Maven](http://maven.apache.org/download.html)
v3.0.3+ and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/home/roberto/tools/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `mvn clean package` from the `app` directory to build the APK only
* Run `mvn clean install` from the root directory to build the app and also run
  the integration tests, this requires a connected Android device or running
  emulator

You might find that your device doesn't let you install your build if you
already have the version from the Android Market installed.  This is standard
Android security as it it won't let you directly replace an app that's been
signed with a different key.  Manually uninstall Gauges from your device and
you will then be able to install your own built version.

## Acknowledgements

Gaug.es for Android is built on the awesome [Gauges API](http://get.gaug.es/documentation/api/)
and uses many great open-source libraries from the Android dev community:

* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock) for a
  consistent, great looking header across all Android platforms,
  [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)
  for swiping between content, traffic, & referrer pages, and
  [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids) for the
  AirTraffic view animations - all from the prolific
  [Jake Wharton](http://jakewharton.com/).
* [RoboGuice](http://code.google.com/p/roboguice/) for dependency-injection.
* [Robotium](http://code.google.com/p/robotium/)
  for driving our app during integration tests.
* [android-maven-plugin](https://github.com/jayway/maven-android-plugin)
  for automating our build and producing release-ready APKs.

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/github/gauges-android/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.
