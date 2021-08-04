# Coding Style

Our code style guidelines are based on the [Android Code Style Guidelines for Contributors](https://developer.android.com/kotlin/style-guide). We only changed a few rules:

* Line length is 120 characters
* FIXME must not be committed in the repository use TODO instead. FIXME can be used in your own local repository only.

On top of the Android linter rules (best run for this project using `./gradlew lintVanillaRelease`), we use two linters: [Checkstyle](http://checkstyle.sourceforge.net/) (for Java and some language-independent custom project rules), and [detekt](https://detekt.github.io/detekt/) (for Kotlin).

## Checkstyle

You can run checkstyle via a gradle command:

```
$ ./gradlew checkstyle
```

It generates an HTML report in `WooCommerce/build/reports/checkstyle/checkstyle.html`.

You can also view errors and warnings in realtime with the Checkstyle plugin.  When importing the project into Android Studio, Checkstyle should be set up automatically.  If it is not, follow the steps below.

You can install the CheckStyle-IDEA plugin in Android Studio here:

`Android Studio > Preferences... > Plugins > CheckStyle-IDEA`

Once installed, you can configure the plugin here:

`Android Studio > Preferences... > Tools > Checkstyle`

From there, add and enable the custom configuration file, located at [config/checkstyle.xml](https://github.com/woocommerce/woocommerce-android/blob/develop/config/checkstyle.xml).

## Detekt

You can run detekt via a gradle command:

```
$ ./gradlew detektAll
```

It generates an HTML report in `WooCommerce/build/reports/detekt/detekt.html`.

You can also view errors and warnings in realtime with the Detekt plugin.

You can install the detekt plugin in Android Studio here:

`Android Studio > Preferences... > Plugins > detekt`

Once installed, you can configure the plugin here:

`Android Studio > Preferences... > Tools > Detekt`

From there, add and enable the custom configuration file, located at [config/detekt/detekt.yml](https://github.com/woocommerce/woocommerce-android/blob/develop/config/detekt/detekt.yml).