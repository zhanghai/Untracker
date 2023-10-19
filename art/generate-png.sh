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

inkscape -o launcher_icon-mdpi.png --export-area=15:15:93:93 -w 48 -h 48 launcher_icon.svg
inkscape -o launcher_icon-hdpi.png --export-area=15:15:93:93 -w 72 -h 72 launcher_icon.svg
inkscape -o launcher_icon-xhdpi.png --export-area=15:15:93:93 -w 96 -h 96 launcher_icon.svg
inkscape -o launcher_icon-xxhdpi.png --export-area=15:15:93:93 -w 144 -h 144 launcher_icon.svg
inkscape -o launcher_icon-xxxhdpi.png --export-area=15:15:93:93 -w 192 -h 192 launcher_icon.svg

cp launcher_icon.svg launcher_icon-web.svg
inkscape --batch-process --actions='select:circle;edit-remove-filter;file-close;quit' launcher_icon-web.svg
inkscape -o launcher_icon-web.png --export-area=18:18:90:90 -w 512 -h 512 launcher_icon-web.svg
rm launcher_icon-web.svg

inkscape -o banner-xhdpi.png --export-area=0:0:320:180 -w 320 -h 180 banner.svg
inkscape -o banner-play.png --export-area=0:0:320:180 -w 1280 -h 720 banner.svg
