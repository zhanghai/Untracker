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

import android.annotation.SuppressLint
import android.net.Uri
import android.text.util.Linkify
import androidx.annotation.Keep
import androidx.core.util.PatternsCompat
import app.cash.quickjs.QuickJs
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.zhanghai.android.untracker.repository.RuleListRepository
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URI

object Untracker {
    fun untrack(text: String): String {
        val urlInfos = findUrls(text)
        if (urlInfos.isEmpty()) {
            return text
        }
        val ruleList = runBlocking { RuleListRepository.getAllRuleList() }
        val rules = ruleList.rules.filter { it.enabled }
        if (rules.isEmpty()) {
            return text
        }
        return QuickJs.create().use { quickJs ->
            quickJs.set("$", IBuiltins::class.java, Builtins())
            quickJs.evaluate(Builtins.INIT_SCRIPT)
            val untrackedTextBuilder = StringBuilder(text)
            for (urlInfo in urlInfos.asReversed()) {
                var untrackedUrl = urlInfo.url
                for (rule in rules) {
                    untrackedUrl =
                        quickJs.evaluate("(function(url) { ${rule.script} })(\"$untrackedUrl\")")
                            as? String ?: untrackedUrl
                }
                if (!urlInfo.hasScheme) {
                    untrackedUrl = untrackedUrl.removePrefix("http://")
                }
                untrackedTextBuilder.replace(urlInfo.start, urlInfo.end, untrackedUrl)
            }
            untrackedTextBuilder.toString()
        }
    }
}

private data class UrlInfo(val start: Int, val end: Int, val hasScheme: Boolean, val url: String)

private val urlWithIgnoredSchemeRegex = Regex("(rtsp|ftp)://.*", RegexOption.IGNORE_CASE)

private val urlWithSchemeRegex = Regex("https?://.*", RegexOption.IGNORE_CASE)

// See also androidx.core.text.util.LinkifyCompat.addLinks .
private fun findUrls(text: String): List<UrlInfo> {
    @SuppressLint("RestrictedApi") val matcher = PatternsCompat.AUTOLINK_WEB_URL.matcher(text)
    val urlInfos = mutableListOf<UrlInfo>()
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        val match = matcher.group(0)!!
        if (
            Linkify.sUrlMatchFilter.acceptMatch(text, start, end) &&
                !urlWithIgnoredSchemeRegex.matches(match)
        ) {
            val hasScheme = urlWithSchemeRegex.matches(match)
            var url = if (hasScheme) match else "http://$match"
            val uri = Uri.parse(url)
            if (!(uri.isHierarchical && uri.isAbsolute)) {
                continue
            }
            url = URI(url).normalize().toString()
            urlInfos.add(UrlInfo(start = start, end = end, hasScheme = hasScheme, url = url))
        }
    }
    return urlInfos
}

@Keep
private interface IBuiltins {
    fun matches(
        url: String,
        hostPattern: String?,
        encodedPathPattern: String?,
        encodedQueryPattern: String?,
        encodedFragmentPattern: String?
    ): Boolean

    fun getHost(url: String): String

    fun getEncodedPath(url: String): String

    fun getEncodedQuery(url: String): String?

    fun getEncodedFragment(url: String): String?

    fun setHost(url: String, host: String): String

    fun setEncodedPath(url: String, encodedPath: String): String

    fun setEncodedQuery(url: String, encodedQuery: String?): String

    fun setEncodedFragment(url: String, encodedFragment: String?): String

    fun removeQueryParameters(url: String, keyPattern: String?, valuePattern: String?): String

    fun retainQueryParameters(url: String, keyPattern: String?, valuePattern: String?): String

    fun fetch(argumentsJson: String): String
}

@Keep
private class Builtins : IBuiltins {
    override fun matches(
        url: String,
        hostPattern: String?,
        encodedPathPattern: String?,
        encodedQueryPattern: String?,
        encodedFragmentPattern: String?
    ): Boolean {
        val uri = Uri.parse(url)
        if (
            hostPattern != null && !Regex(hostPattern, RegexOption.IGNORE_CASE).matches(uri.host!!)
        ) {
            return false
        }
        if (
            encodedPathPattern != null &&
                !Regex(encodedPathPattern, RegexOption.IGNORE_CASE).matches(uri.encodedPath!!)
        ) {
            return false
        }
        if (encodedQueryPattern != null) {
            val encodedQuery = uri.encodedQuery
            if (
                encodedQuery != null &&
                    !Regex(encodedQueryPattern, RegexOption.IGNORE_CASE).matches(encodedQuery)
            ) {
                return false
            }
        }
        if (encodedFragmentPattern != null) {
            val encodedFragment = uri.encodedFragment
            if (
                encodedFragment != null &&
                    !Regex(encodedFragmentPattern, RegexOption.IGNORE_CASE).matches(encodedFragment)
            ) {
                return false
            }
        }
        return true
    }

    override fun getHost(url: String): String = Uri.parse(url).host!!

    override fun getEncodedPath(url: String): String = Uri.parse(url).encodedPath!!

    override fun getEncodedQuery(url: String): String? = Uri.parse(url).encodedQuery

    override fun getEncodedFragment(url: String): String? = Uri.parse(url).encodedFragment

    override fun setHost(url: String, host: String): String {
        val uri = Uri.parse(url)
        val encodedAuthority = buildString {
            uri.encodedUserInfo?.let {
                append(it)
                append('@')
            }
            append(Uri.encode(host))
            uri.port.let {
                if (it != -1) {
                    append(':')
                    append(it)
                }
            }
        }
        return uri.buildUpon().encodedAuthority(encodedAuthority).toString()
    }

    override fun setEncodedPath(url: String, encodedPath: String): String {
        val uri = Uri.parse(url)
        return uri.buildUpon().encodedPath(encodedPath).toString()
    }

    override fun setEncodedQuery(url: String, encodedQuery: String?): String {
        val uri = Uri.parse(url)
        return uri.buildUpon().encodedQuery(encodedQuery).toString()
    }

    override fun setEncodedFragment(url: String, encodedFragment: String?): String {
        val uri = Uri.parse(url)
        return uri.buildUpon().encodedFragment(encodedFragment).toString()
    }

    override fun removeQueryParameters(
        url: String,
        keyPattern: String?,
        valuePattern: String?
    ): String = removeQueryParameters(url, keyPattern, valuePattern, false)

    override fun retainQueryParameters(
        url: String,
        keyPattern: String?,
        valuePattern: String?
    ): String = removeQueryParameters(url, keyPattern, valuePattern, true)

    private fun removeQueryParameters(
        url: String,
        keyPattern: String?,
        valuePattern: String?,
        invertMatch: Boolean
    ): String {
        val uri = Uri.parse(url)
        return uri.buildUpon()
            .clearQuery()
            .apply {
                val keyRegex = keyPattern?.let { Regex(keyPattern, RegexOption.IGNORE_CASE) }
                val valueRegex = valuePattern?.let { Regex(valuePattern, RegexOption.IGNORE_CASE) }
                for ((key, value) in uri.queryParameters) {
                    val matches =
                        (keyRegex == null || keyRegex.matches(key)) &&
                            (valueRegex == null || valueRegex.matches(value))
                    if (matches == !invertMatch) {
                        continue
                    }
                    appendQueryParameter(key, value)
                }
            }
            .toString()
    }

    @Throws(IOException::class)
    override fun fetch(argumentsJson: String): String {
        val arguments = Json.parseToJsonElement(argumentsJson).jsonArray
        val resource = arguments[0]
        val url: String
        val options: JsonObject?
        if (resource is JsonPrimitive) {
            url = resource.content
            options = arguments.getOrNull(1)?.jsonObject
        } else {
            val request = resource.jsonObject
            url = request["url"]!!.jsonPrimitive.content
            options = request
        }
        val method = options?.get("method")?.jsonPrimitive?.content?.uppercase() ?: "GET"
        val headers =
            options?.get("headers")?.jsonObject?.mapValues { it.value.jsonPrimitive.content }
        val body = options?.get("body")?.jsonPrimitive?.content
        val redirect =
            options?.get("redirect")?.jsonPrimitive?.content?.let {
                require(it in listOf("follow", "error", "manual"))
            } ?: "follow"

        val client =
            if (redirect == "follow") {
                okHttpClient
            } else {
                okHttpClient.newBuilder().followRedirects(false).build()
            }
        val request =
            Request.Builder()
                .url(url)
                .apply {
                    var contentType: MediaType? = null
                    headers?.forEach { (name, value) ->
                        if (name.equals("Content-Type", true)) {
                            if (contentType != null) {
                                throw IOException("fetch: Duplicate Content-Type in headers")
                            }
                            contentType = value.toMediaType()
                        } else {
                            header(name, value)
                        }
                    }
                    val requestBody = body?.toRequestBody(contentType)
                    method(method, requestBody)
                }
                .build()
        val response = client.newCall(request).execute()
        if (redirect == "error" && response.isRedirect) {
            throw IOException("fetch: Request was redirected")
        }

        val responseJson = buildJsonObject {
            put("body", JsonPrimitive(response.body?.string()))
            put(
                "headers",
                buildJsonObject {
                    response.headers.forEach { (name, value) -> put(name, JsonPrimitive(value)) }
                }
            )
            put("ok", JsonPrimitive(response.isSuccessful))
            put("redirected", JsonPrimitive(response.priorResponse != null))
            put("status", JsonPrimitive(response.code))
            put("statusText", JsonPrimitive(response.message))
            put("url", JsonPrimitive(response.request.url.toString()))
        }
        return responseJson.toString()
    }

    companion object {
        val INIT_SCRIPT =
            """
            (function () {
                function defined(value) {
                    return value !== undefined ? value : null;
                }
                const matches = $.matches.bind($);
                $.matches = function (url, hostPattern, encodedPathPattern, encodedQueryPattern, encodedFragmentPattern) {
                    return matches(url, defined(hostPattern), defined(encodedPathPattern), defined(encodedQueryPattern), defined(encodedFragmentPattern));
                };
                const removeQueryParameters = $.removeQueryParameters.bind($);
                $.removeQueryParameters = function (url, keyPattern, valuePattern) {
                    return removeQueryParameters(url, defined(keyPattern), defined(valuePattern));
                };
                const retainQueryParameters = $.retainQueryParameters.bind($);
                $.retainQueryParameters = function (url, keyPattern, valuePattern) {
                    return retainQueryParameters(url, defined(keyPattern), defined(valuePattern));
                };
                const fetch = $.fetch.bind($);
                $.fetch = function (resource, options) {
                    return JSON.parse(fetch(JSON.stringify(Array.from(arguments))));
                };
            })();
        """
                .trimIndent()

        private val okHttpClient = OkHttpClient()
    }
}

private val Uri.queryParameters: List<Pair<String, String>>
    get() {
        if (isOpaque) {
            return emptyList()
        }
        val query = encodedQuery ?: return emptyList()
        val queryParameters = mutableListOf<Pair<String, String>>()
        var start = 0
        while (true) {
            val nextAmpersand = query.indexOf('&', start)
            val end = if (nextAmpersand != -1) nextAmpersand else query.length
            val separator = query.indexOf('=', start).let { if (it != -1 && it < end) it else end }
            val key = Uri.decode(query.substring(start, separator))
            val value = if (separator < end) Uri.decode(query.substring(separator + 1, end)) else ""
            queryParameters.add(key to value)
            start = if (nextAmpersand != -1) nextAmpersand + 1 else break
        }
        return queryParameters
    }
