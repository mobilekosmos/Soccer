package com.mobilekosmos.android.clubs.data.network

import com.mobilekosmos.android.clubs.BuildConfig
import com.mobilekosmos.android.clubs.MyApplication
import com.mobilekosmos.android.clubs.data.model.ClubEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.concurrent.TimeUnit

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.

// With flavors we set this in build.gradle
//private const val BASE_URL = "https://public.allaboutapps.at/hiring/clubs.json"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 * "As per the Kotlin part of the readme, you must add the KotlinJsonAdapterFactory if you're not using Moshi's codegen. This was a specific behavior change in Moshi 1.9 as per the blog post about Moshi 1.9"
 * https://github.com/square/moshi/blob/master/README.md#kotlin
 */
private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Create a cache object
const val DISK_CACHE_SIZE = 1 * 1024 * 1024 // 1 MB
val httpDiskCacheDirectory = File(MyApplication.instance.cacheDir, "http-cache")
val diskCache = Cache(httpDiskCacheDirectory, DISK_CACHE_SIZE.toLong())

// create a network cache interceptor, setting the max age to 1 minute
private val networkOfflineCacheInterceptor = Interceptor { chain ->
    var request = chain.request()

    val cacheControl: CacheControl = if (Utils.isNetworkAvailable()) {
        CacheControl.Builder()
            .maxAge(1, TimeUnit.DAYS)
            .build()
    } else {
        CacheControl.Builder()
            .onlyIfCached()
            .maxStale(1, TimeUnit.DAYS)
            .build()
    }

    request = request.newBuilder().cacheControl(cacheControl).build()
    chain.proceed(request)
}

private val networkCacheInterceptor = Interceptor { chain ->
    val request = chain.request()
    val cacheControl: CacheControl = if (Utils.isNetworkAvailable()) {
        CacheControl.Builder()
            .maxAge(1, TimeUnit.DAYS)
            .build()
    } else {
        CacheControl.Builder()
            .onlyIfCached()
            .maxStale(1, TimeUnit.DAYS)
            .build()
    }
    val response = chain.proceed(request)
    // Servers may set "no-cache" in the response, so we remove this.
    response.newBuilder()
        .removeHeader("Pragma")
        .removeHeader("Cache-Control")
        .header("Cache-Control", cacheControl.toString())
        .build()
}

// Create the logging interceptor. Search for tag "okhttp" in Logcat.
private val networkLoggingInterceptor = HttpLoggingInterceptor()
    .setLevel(HttpLoggingInterceptor.Level.BODY)

// Create the httpClient, configure it
// with cache, network cache interceptor and logging interceptor
// TODO: don't use loggingInterceptor in release build.
private val httpClient = OkHttpClient.Builder()
    .cache(diskCache)
    .addNetworkInterceptor(networkCacheInterceptor)
    .addInterceptor(networkOfflineCacheInterceptor)
    .addInterceptor(networkLoggingInterceptor)
    .build()

// Create the Retrofit with the httpClient
private val retrofit = Retrofit.Builder()
    .baseUrl("http://localhost/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(httpClient)
    .build()

/**
 * A public interface that exposes the [getClubs] method.
 */
interface ClubsApiService {
    // With flavors we set this in [build.gradle].
    @GET(BuildConfig.HOST_URL)
    suspend fun getClubs(): List<ClubEntity>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service.
 */
object ClubsApi {
    val RETROFIT_SERVICE: ClubsApiService by lazy {
        retrofit.create(ClubsApiService::class.java)
    }
}
