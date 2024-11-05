plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.tank"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tank"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
    testOptions {
        unitTests.all {

        }
    }
}

dependencies {
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")

    //firebase
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.android.gms:play-services-auth:20.2.0")
    //picasso
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.google.android.material:material:1.4.0")
    //
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("androidx.work:work-runtime-ktx:2.7.1")


    /*Unix*/

    testImplementation ("org.mockito:mockito-core:5.0.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")

    testImplementation ("org.robolectric:robolectric:4.8.2") // O la versión más reciente

    androidTestImplementation ("androidx.fragment:fragment-testing:1.6.0")
    testImplementation ("io.mockk:mockk:1.12.3")
    testImplementation ("junit:junit:4.13.2")

    // Dependencias de JUnit
    testImplementation ("junit:junit:4.13.2")

    // Dependencias de AndroidX para pruebas
    androidTestImplementation ("androidx.test.ext:junit:1.1.5") // Para JUnit 4
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1") // Para Espresso
    androidTestImplementation ("androidx.test:core:1.6.0") // Dependencia base para pruebas

    androidTestImplementation ("androidx.test:rules:1.5.0")

    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test:core:1.10.0")
    androidTestImplementation ("androidx.test:rules:1.5.0")

    implementation ("org.apache.poi:poi:5.2.3")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")

    //hola
    implementation ("com.itextpdf:html2pdf:1.0.3")
    implementation ("com.itextpdf:kernel:2.1.7")
    implementation ("com.dmitryborodin:pdfview-android:1.1.0")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)






}