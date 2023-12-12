#!/bin/bash

# Copyright 2023 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e

for dpi in {m,h,xh,xxh,xxxh}dpi; do
    cp "launcher_icon-${dpi}.png" "../app/src/main/res/mipmap-${dpi}/launcher_icon.png"
done

cp launcher_icon-play.png ../fastlane/metadata/android/en-US/images/icon.png

cp banner-xhdpi.png ../app/src/main/res/drawable-xhdpi/banner.png
cp banner-play.png ../fastlane/metadata/android/en-US/images/tvBanner.png
cp feature_graphic.png ../fastlane/metadata/android/en-US/images/featureGraphic.png
