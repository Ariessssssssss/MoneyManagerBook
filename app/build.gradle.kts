
plugins {
    id("com.android.application")
}

android {

    signingConfigs {
        create("key") {
            storeFile = file("D:\\Local\\Android\\Android_keyStore\\MoneyManger_key.jks")
            keyAlias = "key0"
            keyPassword = "123456"
            storePassword = "123456"
        }
    }
    namespace = "com.example.moneymanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moneymanager"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    // Additional configurations if needed
    packagingOptions {
        pickFirst ("com/itextpdf/text/pdf/fonts/cmap_info.txt")
        // Additional configurations if needed
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("key")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "34.0.0"


}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    implementation("androidx.annotation:annotation:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation ("androidx.sqlite:sqlite:2.3.1")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    //微信
    implementation ("com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0")
    //网络请求
    implementation ("com.squareup.okhttp3:okhttp:3.10.0")
    //数据解析
    implementation ("com.google.code.gson:gson:2.10")
    //数据库加密
    implementation ("net.zetetic:android-database-sqlcipher:4.5.3")
    //数据导出
    implementation("com.itextpdf:itextpdf:5.5.13.3")
}