@file:OptIn(ExperimentalEncodingApi::class)

package com.eazywrite.app.data.network

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.eazywrite.app.BuildConfig
import com.eazywrite.app.MyApplication
import com.eazywrite.app.data.moshi
import com.eazywrite.app.data.network.service.AccountService
import com.eazywrite.app.data.network.service.AccountServiceKt
import com.eazywrite.app.data.network.service.AppVersionService
import com.eazywrite.app.data.network.service.BillService
import com.eazywrite.app.data.network.service.ImageService
import com.eazywrite.app.data.network.service.OpenaiService
import com.eazywrite.app.data.network.service.TemplateKotlinService
import com.eazywrite.app.data.network.service.TencentCloudAudioService
import com.eazywrite.app.data.repository.NotLoggedInException
import com.eazywrite.app.data.repository.UserRepository
import com.thomasbouvier.persistentcookiejar.PersistentCookieJar
import com.thomasbouvier.persistentcookiejar.cache.SetCookieCache
import com.thomasbouvier.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Network {

    // 服务端 baseUrl
    private const val baseUrl = "https://easywrite.wilinz.com/"
    //    private const val baseUrl = "http://192.168.1.5:10010/"

    //    val openaiUrl = "${baseUrl}openai/v1/"
    // See https://www.ohmygpt.com/
    private const val openaiUrl = "https://c-z0-api-01.hash070.com"

    // 必选
    private val openaiKey = Base64.decode("api key base64").toString(Charsets.UTF_8) // TODO

    // 可选
    private const val textinAppId = ""

    // 可选
    private const val textinSecretCode = ""

    var cookieJar =
        PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(MyApplication.instance))

    val baseOkhttpClientBuilder
        get() = OkHttpClient.Builder().apply {
            cookieJar(cookieJar)
            this.callTimeout(1, TimeUnit.MINUTES)
            this.connectTimeout(1, TimeUnit.MINUTES)
            this.readTimeout(1, TimeUnit.MINUTES)
            this.writeTimeout(1, TimeUnit.MINUTES)
            addInterceptor {
                val req = it.request().newBuilder()
                req.addHeader("app-id", BuildConfig.APPLICATION_ID)
                req.addHeader("app-version-code", BuildConfig.VERSION_CODE.toString())
                req.addHeader("app-version-name", BuildConfig.VERSION_NAME)
                it.proceed(req.build())
            }
        }

    fun OkHttpClient.Builder.addLogger() = this.addInterceptor(HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    })

    private val baseRetrofitBuilder
        get() = Retrofit.Builder()
            .client(baseOkhttpClientBuilder.addLogger().build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())

    private val textinRetrofit = baseRetrofitBuilder
        .client(baseOkhttpClientBuilder.apply {
            this.addInterceptor(TextinApiRequestInterceptor(textinAppId, textinSecretCode))//请求拦截器
        }.addLogger().build())
        .baseUrl("https://api.textin.com")
        .build()

    private fun loginInterceptor(chain: Interceptor.Chain): Response {
        val resp = chain.proceed(chain.request())
        if (resp.code == 401) {
            runBlocking {
                kotlin.runCatching { UserRepository.logoutLocal() }
            }
            throw NotLoggedInException()
        }
        return resp
    }

    private val appServerOkhttpClient = baseOkhttpClientBuilder
        .addInterceptor {
            loginInterceptor(it)
        }
        .addLogger()
        .build()

    private val appServerRetrofit =
        baseRetrofitBuilder.client(appServerOkhttpClient).baseUrl(baseUrl)
            .build()

    val openaiOkHttpClient = baseOkhttpClientBuilder.apply {
        this.callTimeout(5, TimeUnit.MINUTES)
        this.connectTimeout(5, TimeUnit.MINUTES)
        this.readTimeout(5, TimeUnit.MINUTES)
        this.writeTimeout(5, TimeUnit.MINUTES)
//        addInterceptor(ProxyInterceptor())
        addInterceptor {
            it.proceed(
                it.request().newBuilder().addHeader("connection", "Keep-Alive")
                    .addHeader("authorization", "Bearer $key").build()
            )
        }
        addInterceptor {
            loginInterceptor(it)
        }
        addLogger()
    }.build()

    val openAI = OpenAI(
        token = key,
        logging = LoggingConfig(LogLevel.None),
        host = OpenAIHost(baseUrl = openaiUrl),
        onClientCreated = {
//            it.plugin(HttpSend).intercept { request ->
//                fun cookieHeader(cookies: List<Cookie>): String = buildString {
//                    cookies.forEachIndexed { index, cookie ->
//                        if (index > 0) append("; ")
//                        append(cookie.name).append('=').append(cookie.value)
//                    }
//                }
//
//                val cookie = cookieJar.loadForRequest(request.url.buildString().toHttpUrl())
//                request.headers.append("Cookie", cookieHeader(cookie))
//                val call = execute(request)
//                if (call.response.status.value == 401) {
//                    kotlin.runCatching { UserRepository.logoutLocal() }
//                    toast("请登录")
//                }
//                call
//            }
        },
    )

    private val openaiRetrofit = baseRetrofitBuilder
        .client(openaiOkHttpClient)
        .baseUrl(openaiUrl).build()

    val templateKotlinService = textinRetrofit.create<TemplateKotlinService>()

    val imagesService = textinRetrofit.create<ImageService>()

    val accountService = appServerRetrofit.create<AccountService>()

    val billService = appServerRetrofit.create<BillService>()

    val openaiService = openaiRetrofit.create<OpenaiService>()

    val accountServiceKt = appServerRetrofit.create<AccountServiceKt>()

    val appVersionService = appServerRetrofit.create<AppVersionService>()

    val tencentCloudAudioService = appServerRetrofit.create<TencentCloudAudioService>()

    private inline fun <reified T> Retrofit.create() = create(T::class.java)


}