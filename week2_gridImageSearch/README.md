# Project 2 - *Google Image Search*

**Name of your app** is an android app that allows a user to search for images on web using simple filters. The app utilizes [Google Image Search API](https://developers.google.com/image-search/). Please note that API has been officially deprecated as of May 26, 2011.

Time spent: **X** hours spent in total

## User Stories

The following **required** functionality is completed:

* [v] User can **search for images** by specifying a query and launching a search. Search displays a grid of image results from the Google Image API.
* [v] User can click on "settings" which allows selection of **advanced search options** to filter results
* [v] User can configure advanced search filters such as:
  * [v] Size (small, medium, large, extra-large)
  * [v] Color filter (black, blue, brown, gray, green, etc...)
  * [v] Type (faces, photo, clip art, line art)
  * [v] Site (espn.com)
* [v] Subsequent searches have any filters applied to the search results
* [v] User can tap on any image in results to see the image **full-screen**
* [v] User can **scroll down to see more images**. The maximum number of images is 64 (limited by API).

The following **optional** features are implemented:

* [v] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [v] Used the **ActionBar SearchView** or custom layout as the query box instead of an EditText
* [v] User can **share an image** to their friends or email it to themselves
* [v] Replaced Filter Settings Activity with a lightweight modal overlay
* [v] Improved the user interface and experiment with image assets and/or styling and coloring

The following **bonus** features are implemented:

* [v] Used the [StaggeredGridView](https://github.com/f-barth/AndroidStaggeredGrid) to display improve the grid of image results
* [ ] User can [zoom or pan images](https://github.com/MikeOrtiz/TouchImageView) displayed in full-screen detail view

The following **additional** features are implemented:

* [v] List anything else that you can get done to improve the app functionality!
  * [v] Remember previous filter settings after the model overlay dismisses

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

![Video Walkthrough](android_week2_demo_ydlin.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.
[CodePath Link for This Assignment](http://courses.codepath.com/courses/intro_to_android/week/2#!assignment)

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android

## License

    Copyright [yyyy] [name of copyright owner]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
