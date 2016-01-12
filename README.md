# Android RecyclerView Joiner Library

This library provides you a functionality of joining together several adapters and layouts into a single RecyclerView. The result of joining will be an [adapter](http://developer.android.com/intl/ru/reference/android/support/v7/widget/RecyclerView.Adapter.html), which you can set to your RecyclerView. Actually, this library allows you to construct multitype adapter from separate parts (such as an adapters and layouts). This approach gives you next advantages:
* More flixibility of using adapters (they also can be used separately or in different constructions)
* You can dynamically change your RecyclerView structure by adding and removing parts programmatically
* Add header, footer, or devider layout without adding a new item type to your adapter

For example, you can combine two adapters and use title layout to separate them, like in demo app:

![Example screenshot](img/readme-1.jpg)

## Quick guide

### Dependencies

Configure dependencies in you <b>module</b> build.gradle file:

<pre><code>
repositories {
    maven {
        url 'https://dl.bintray.com/j2esu/maven/'
    }
}

dependencies {
    //your other dependencies
    compile 'su.j2e:rv-joiner:1.0.0'
}
</code></pre>
