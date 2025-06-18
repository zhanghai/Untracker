/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zhanghai.android.untracker

import me.zhanghai.android.untracker.model.I18nStrings
import me.zhanghai.android.untracker.model.Rule
import me.zhanghai.android.untracker.model.RuleList

val BuiltinRuleList =
    RuleList(
        rules =
            listOf(
                Rule(
                    id = "0db0b0dc-f46f-40a4-8c0d-8a575a09f2a3",
                    name = "Common redirections",
                    description = "Remove common redirections",
                    script =
                        """
                            if ($.matches(url, 'www\\.douban\\.com', '/link2/', '.*\\burl=.+')) {
                                return $.getQueryParameter(url, 'url');
                            } else if ($.matches(url, 'search\\.app', '/', '.*\\blink=.+')) {
                                return $.getQueryParameter(url, 'link');
                            } else if ($.matches(url, 'link\\.zhihu\\.com', '/', '.*\\btarget=.+')) {
                                return $.getQueryParameter(url, 'target');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "常见重定向",
                        localeDescription = "删除常见重定向",
                    )
                ),
                Rule(
                    id = "a925a9f0-84bb-46eb-bea2-1bded576d8c9",
                    name = "Common short links",
                    description = "Expand common tracking short links",
                    script =
                        """
                            while (true) {
                                if ($.matches(url, '163cn\\.tv|a\\.co|amzn\\.(asia|eu|to)|b23\\.tv|bili2233\\.cn|v\\.douyin\\.com|dwz\\.cn|u\\.jd\\.com|v\\.kuaishou\\.com|pin\\.it|t\\.cn|vm\\.tiktok\\.com|url\\.cn|xhslink\\.com')
                                        || $.matches(url, 'm\\.gifshow\\.com', '/s/.+')
                                        || $.matches(url, 'www\\.instagram\\.com', '/share/reel/.+')
                                        || $.matches(url, 'api\\.pinterest\\.com', '/url_shortener/.+')
                                        || $.matches(url, 'www\\.reddit\\.com', '/r/[^/]+/s/.+')
                                        || $.matches(url, 'search.app', '/.+')) {
                                    const response = $.fetch(url, { redirect: 'manual' });
                                    if ([301, 302, 303, 307, 308].includes(response.status)) {
                                        const location = response.headers.find(it => it[0].toLowerCase() === 'location')?.[1];
                                        if (location) {
                                            url = location;
                                            continue;
                                        }
                                    }
                                } else if ($.matches(url, '([cm]\\.)?tb\\.cn')) {
                                    const response = $.fetch(url);
                                    const groups = /var url = '([^']+)';/.exec(response.body);
                                    if (groups) {
                                        url = groups[1];
                                        continue;
                                    }
                                }
                                break;
                            }
                            return url;
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "常见短链",
                        localeDescription = "展开常见短链",
                    )
                ),
                Rule(
                    id = "19d74b86-5ac0-4218-8ec8-ee89e4d237f1",
                    name = "Common analytics",
                    description = "Remove tracking for common analytics",
                    script = """return $.removeQueryParameters(url, '[isu]tm_.*|ref');""",
                    zhCNValue = I18nStrings(
                        localeName = "常见统计",
                        localeDescription = "删除常见统计",
                    )
                ),
                Rule(
                    id = "87af5849-1e81-42bd-984e-e30a1ec08db4",
                    name = "Common ads",
                    description = "Remove tracking for common ads",
                    script =
                        """return $.removeQueryParameters(url, '(fb|g|tt|wicked|y)cl(id|source|src)|[gw]braid');""",
                    zhCNValue = I18nStrings(
                        localeName = "常见广告",
                        localeDescription = "删除常见广告",
                    )
                ),
                Rule(
                    id = "3805dd66-b341-4da3-b5ba-f3acc69ed189",
                    name = "Amazon",
                    description = "Remove tracking for Amazon",
                    script =
                        """
                            if ($.matches(url, '.+\\.amazon\\.(ae|ca|cn|co\\.jp|co\\.uk|com|com\\.au|com\\.be|com\\.br|com\\.mx|com\\.tr|de|eg|es|fr|in|it|nl|pl|sa|se|sg)')) {
                                url = $.setEncodedQuery(url, null);
                                const path = $.getEncodedPath(url);
                                const newPath = path.replace(/(?<=\/)ref=.+$/i, '');
                                if (path !== newPath) {
                                    url = $.setEncodedPath(url, newPath);
                                }
                                return url;
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Amazon 亚马逊",
                        localeDescription = "删除亚马逊跟踪",
                    )
                ),
                Rule(
                    id = "7edf803f-c165-46ef-b4d1-b8cbc6b5cb65",
                    name = "Bilibili",
                    description = "Remove tracking for Bilibili",
                    script =
                        """
                            if ($.matches(url, '.+\\.bilibili\\.com')) {
                                url = $.retainQueryParameters(url, 'business_id|business_type|itemsId|lottery_id|p|start_progress|t');
                                return $.removeQueryParameters(url, 'p', '1');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "哔哩哔哩",
                        localeDescription = "删除哔哩哔哩跟踪",
                    )
                ),
                Rule(
                    id = "db094a08-e3c4-4bd9-aa98-16df86237f6d",
                    name = "Bilibili BV",
                    description = "Convert BV code for Bilibili",
                    // https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/bvid_desc.md
                    script =
                        """
                            if ($.matches(url, '.+\\.bilibili\\.com', '/video/BV[0-9A-Za-z]+/?')) {
                                const bvPath = $.getEncodedPath(url);
                                const bvCode = /^\/video\/BV([0-9A-Za-z]+)\/?/.exec(bvPath)[1];
                                function bvToAv(bv) {
                                    const XOR_CODE = 23442827791579n;
                                    const MASK_CODE = 2251799813685247n;
                                    const BASE = 58n;
                                    const data = 'FcwAPNKTMug3GV5Lj7EJnHpWsx4tb8haYeviqBz6rkCy12mUSDQX9RdoZf';
                                    const bvArray = Array.from(bv);
                                    [bvArray[1], bvArray[7]] = [bvArray[7], bvArray[1]];
                                    [bvArray[2], bvArray[5]] = [bvArray[5], bvArray[2]];
                                    bvArray.splice(0, 1);
                                    const temp = bvArray.reduce((accumulator, bvChar) => accumulator * BASE + BigInt(data.indexOf(bvChar)), 0n);
                                    return Number((temp & MASK_CODE) ^ XOR_CODE);
                                }
                                const avCode = bvToAv(bvCode);
                                const avPath = '/video/av' + avCode + '/';
                                return $.setEncodedPath(url, avPath);
                            }
                        """
                            .trimIndent(),
                    enabled = false,
                    zhCNValue = I18nStrings(
                        localeName = "哔哩哔哩 BV",
                        localeDescription = "将哔哩哔哩 BV 号转换为 AV 号",
                    )
                ),
                Rule(
                    id = "ad21dada-9f09-4adf-9db2-d1575ca8b4a4",
                    name = "Douban",
                    description = "Remove tracking for Douban",
                    script =
                        """
                            if ($.matches(url, '.+\\.douban\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "豆瓣",
                        localeDescription = "删除豆瓣跟踪",
                    )
                ),
                Rule(
                    id = "c112da1e-384e-42e2-b110-ce6d8edbfe7a",
                    name = "Douyin",
                    description = "Remove tracking for Douyin",
                    script =
                        """
                            if ($.matches(url, '.+\\.(douyin|iesdouyin)\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "抖音",
                        localeDescription = "删除抖音跟踪",
                    )
                ),
                Rule(
                    id = "926090a8-a98a-4168-b6a1-b6b801c76955",
                    name = "Google Search",
                    description = "Remove tracking for Google Search",
                    script =
                        """
                            if ($.matches(url, 'www\\.google\\.com', '/search')) {
                                return $.retainQueryParameters(url, 'q|tbm');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Google 搜索",
                        localeDescription = "删除 Google 搜索跟踪",
                    )
                ),
                Rule(
                    id = "bcd9fcb8-bf1c-41f8-b18d-b248507e43c7",
                    name = "Instagram",
                    description = "Remove tracking for Instagram",
                    script =
                        """
                            if ($.matches(url, 'www\\.instagram\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Instagram",
                        localeDescription = "删除 Instagram 跟踪",
                    )
                ),
                Rule(
                    id = "c68c4cbf-9ae5-41f6-89ba-6e3f31ffb6a2",
                    name = "JD.com",
                    description = "Remove tracking for JD.com",
                    script =
                        """
                            if ($.matches(url, '.+\\.jd\\.com')) {
                                return $.retainQueryParameters(url, 'id|shopId|skuIds|suitId|wareId');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "京东",
                        localeDescription = "删除京东跟踪",
                    )
                ),
                Rule(
                    id = "1d0c3aae-c456-4352-972a-8b0b0f6e36c1",
                    name = "Kuaishou",
                    description = "Remove tracking for Kuaishou",
                    script =
                        """
                            if ($.matches(url, '(.+\\.)?m\\.chenzhongtech\\.com|m\\.gifshow\\.com|.+\\.kuaishou\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "快手",
                        localeDescription = "删除快手跟踪",
                    )
                ),
                Rule(
                    id = "c722500a-17c8-462a-8a74-0c15fefe1b3e",
                    name = "NetEase Cloud Music",
                    description = "Remove tracking for NetEase Cloud Music",
                    script =
                        """
                            if ($.matches(url, 'y\\.music\\.163\\.com')) {
                                return $.retainQueryParameters(url, 'id');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "网易云音乐",
                        localeDescription = "删除网易云音乐跟踪",
                    )
                ),
                Rule(
                    id = "465d579e-bc3b-4c5b-bac3-9b84c67c7554",
                    name = "Netflix",
                    description = "Remove tracking for Netflix",
                    script =
                        """
                            if ($.matches(url, 'www\\.netflix\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Netflix",
                        localeDescription = "删除 Netflix 跟踪",
                    )
                ),
                Rule(
                    id = "21065dad-2c4c-4431-acd7-32825e831c32",
                    name = "Pinterest",
                    description = "Remove tracking for Pinterest",
                    script =
                        """
                            if ($.matches(url, 'www\\.pinterest\\.com')) {
                                url = $.setEncodedQuery(url, null);
                                const path = $.getEncodedPath(url);
                                const newPath = path.replace(/(?<=\/)sent\/?$/i, '');
                                if (path !== newPath) {
                                    url = $.setEncodedPath(url, newPath);
                                }
                                return url;
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Pinterest",
                        localeDescription = "删除 Pinterest 跟踪",
                    )
                ),
                Rule(
                    id = "67035e8c-9418-47e7-9f62-56cd30666772",
                    name = "Reddit",
                    description = "Remove tracking for Reddit",
                    script =
                        """
                            if ($.matches(url, '(.+\\.)?reddit\\.com')) {
                                return $.retainQueryParameters(url, 'context');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Reddit",
                        localeDescription = "删除 Reddit 跟踪",
                    )
                ),
                Rule(
                    id = "55662cf5-b43e-491a-b72b-adc1111b8583",
                    name = "Old Reddit",
                    description = "Use old Reddit instead of new Reddit",
                    enabled = false,
                    script =
                        """
                            if ($.matches(url, '(www\\.)?reddit\\.com')) {
                                return $.setHost(url, 'old.reddit.com');
                            } else if ($.matches(url, 'v\\.redd\\.it', '/[^/]+/?')) {
                                return $.setEncodedPath($.setHost(url, 'old.reddit.com'), '/video' + $.getEncodedPath(url));
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "旧 Reddit",
                        localeDescription = "使用旧 Reddit 代替新 Reddit",
                    )
                ),
                Rule(
                    id = "4244faaa-b50e-47f1-87a5-ac994c32b94f",
                    name = "SMZDM",
                    description = "Remove tracking for SMZDM",
                    script =
                        """
                            if ($.matches(url, '.+\\.smzdm\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "什么值得买",
                        localeDescription = "删除什么值得买跟踪",
                    )
                ),
                Rule(
                    id = "8cbda6f2-d71d-4ee3-8ee0-05b245861c0d",
                    name = "Spotify",
                    description = "Remove tracking for Spotify",
                    script =
                        """
                            if ($.matches(url, '(open\\.)?spotify\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Spotify",
                        localeDescription = "删除 Spotify 跟踪",
                    )
                ),
                Rule(
                    id = "5ed9b3ef-f4de-44c8-bd34-5c8da6e330af",
                    name = "Stack Exchange",
                    description = "Remove tracking for Stack Exchange sites",
                    script =
                        """
                            if ($.matches(url, '(.+\\.stackexchange|askubuntu|serverfault|stackoverflow|superuser)\\.com', '/[aq]/[0-9]+/[0-9]+/?')) {
                                return $.setEncodedPath(url, $.getEncodedPath(url).replace(/\/[0-9]+\/?$/i, ''));
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "Stack Exchange",
                        localeDescription = "删除 Stack Exchange 跟踪",
                    )
                ),
                Rule(
                    id = "9cb803b3-ed57-46ad-b604-8adb8c515c07",
                    name = "Taobao",
                    description = "Remove tracking for Taobao (and Tmall)",
                    script =
                        """
                            if ($.matches(url, '.+\\.(taobao|tmall)\\.com')) {
                                return $.retainQueryParameters(url, 'id');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "淘宝",
                        localeDescription = "删除淘宝和天猫跟踪",
                    )
                ),
                Rule(
                    id = "8420b788-c6ee-46a6-ab3b-da04d6299beb",
                    name = "TikTok",
                    description = "Remove tracking for TikTok",
                    script =
                        """
                            if ($.matches(url, '.+\\.tiktok\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "TikTok",
                        localeDescription = "删除 TikTok 跟踪",
                    )
                ),
                Rule(
                    id = "7a6a2ddb-a0a4-43fe-a97f-7cb74cd29ad5",
                    name = "X",
                    description = "Remove tracking for X (formerly Twitter)",
                    script =
                        """
                            if ($.matches(url, '(twitter|x)\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "X",
                        localeDescription = "删除 X（原 Twitter）跟踪",
                    )
                ),
                Rule(
                    id = "84c837db-f1c0-4738-b0ea-0f3d091885d7",
                    name = "rednote",
                    description = "Remove tracking for rednote",
                    script =
                        """
                            if ($.matches(url, '.+\\.xiaohongshu\\.com')) {
                                return $.retainQueryParameters(url, 'xsec_token');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "小红书",
                        localeDescription = "删除小红书跟踪",
                    )
                ),
                Rule(
                    id = "88a68140-7653-4923-991d-19d1a98cd5e3",
                    name = "Youtube",
                    description = "Remove tracking for Youtube",
                    script =
                        """
                            if ($.matches(url, 'youtu\\.be|((music|www)\\.)?youtube\\.com')) {
                                return $.retainQueryParameters(url, 'index|list|t|v');
                            }
                        """
                            .trimIndent(),
                    zhCNValue = I18nStrings(
                        localeName = "YouTube",
                        localeDescription = "删除 YouTube 跟踪",
                    )
                )
            )
    )
