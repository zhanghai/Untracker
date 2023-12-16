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

import me.zhanghai.android.untracker.model.Rule
import me.zhanghai.android.untracker.model.RuleList

val BuiltinRuleList =
    RuleList(
        rules =
            listOf(
                Rule(
                    id = "a925a9f0-84bb-46eb-bea2-1bded576d8c9",
                    name = "Common short links",
                    description = "Expand common tracking short links",
                    script =
                        """
                            if ($.matches(url, 'a\\.co|b23\\.tv|u\\.jd\\.com|([cm]\\.)?tb\\.cn|xhslink\\.com')
                                    || $.matches(url, 'www\\.reddit\\.com', '/r/[^/]+/s/.+')) {
                                return $.followRedirect(url);
                            }
                        """
                            .trimIndent()
                ),
                Rule(
                    id = "19d74b86-5ac0-4218-8ec8-ee89e4d237f1",
                    name = "Common analytics",
                    description = "Remove tracking for common analytics",
                    script = """return $.removeQueryParameters(url, '[isu]tm_.*|ref');"""
                ),
                Rule(
                    id = "87af5849-1e81-42bd-984e-e30a1ec08db4",
                    name = "Common ads",
                    description = "Remove tracking for common ads",
                    script =
                        """return $.removeQueryParameters(url, '(fb|g|tt|wicked|y)cl(id|source|src)|[gw]braid');"""
                ),
                Rule(
                    id = "3805dd66-b341-4da3-b5ba-f3acc69ed189",
                    name = "Amazon",
                    description = "Remove tracking for Amazon",
                    script =
                        """
                            if ($.matches(url, '.+\\.amazon\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent()
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
                            .trimIndent()
                ),
                Rule(
                    id = "db094a08-e3c4-4bd9-aa98-16df86237f6d",
                    name = "Bilibili BV",
                    description = "Convert BV code for Bilibili",
                    script =
                        """
                            if ($.matches(url, '.+\\.bilibili\\.com', '/video/BV[0-9A-Za-z]+/?')) {
                                const bvPath = $.getEncodedPath(url);
                                const bvCode = /^\/video\/BV([0-9A-Za-z]+)\/?/.exec(bvPath)[1];
                                function avToBv(bv) {
                                    const table = 'fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF'.split('');
                                    const tr = {};
                                    table.forEach((c, i) => tr[c] = i);
                                    const s = [11, 10, 3, 8, 4, 6, 2, 9, 5, 7];
                                    const xor = 177451812n;
                                    const add = 100618342136696320n;
                                    let r = 0n;
                                    s.forEach((n, i) => r += BigInt(tr[bv[n - 2]]) * BigInt(58 ** i));
                                    return (r - add) ^ xor;
                                }
                                const avCode = avToBv(bvCode);
                                const avPath = '/video/av' + avCode + '/';
                                return $.setEncodedPath(url, avPath);
                            }
                        """
                            .trimIndent(),
                    enabled = false
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
                            .trimIndent()
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
                            .trimIndent()
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
                            .trimIndent()
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
                            .trimIndent()
                ),
                Rule(
                    id = "67035e8c-9418-47e7-9f62-56cd30666772",
                    name = "Reddit",
                    description = "Remove tracking for Reddit",
                    script =
                        """
                            if ($.matches(url, 'www\\.reddit\\.com')) {
                                return $.retainQueryParameters(url, 'context');
                            }
                        """
                            .trimIndent()
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
                            .trimIndent()
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
                            .trimIndent()
                ),
                Rule(
                    id = "84c837db-f1c0-4738-b0ea-0f3d091885d7",
                    name = "Xiaohongshu",
                    description = "Remove tracking for Xiaohongshu",
                    script =
                        """
                            if ($.matches(url, '.+\\.xiaohongshu\\.com')) {
                                return $.setEncodedQuery(url, null);
                            }
                        """
                            .trimIndent()
                ),
                Rule(
                    id = "88a68140-7653-4923-991d-19d1a98cd5e3",
                    name = "Youtube",
                    description = "Remove tracking for Youtube",
                    script =
                        """
                            if ($.matches(url, 'youtu\\.be|(www\\.)?youtube\\.com')) {
                                return ${'$'}.retainQueryParameters(url, 't|v');
                            }
                        """
                            .trimIndent()
                )
            )
    )
