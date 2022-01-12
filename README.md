# koin-module-processor
Annotation Processor to generate a list of Koin Modules.

## Usage

### Setup
In your module buildscript, add the KSP plugin:
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.6.0-1.0.1"
}
```

Then add the following dependencies:
```kotlin
val kmpLatestVersion = "2.0.0"
implementation("com.rocca23:koin-module-processor:$kmpLatestVersion")
ksp("com.rocca23:koin-module-processor:$kmpLatestVersion")
```

### Declaring Koin modules
Declare your Modules as top-level properties, annotated with `@KoinModule`:
```kotlin
// MyModule.kt

@KoinModule
val myModule = module {
    single {
        // ...
    }
    factory {
        // ...
    }
}
```

### Installing the modules
After building the project, a top-level property named `org.koin.generated.koinModules` is created.
This is a list of all the annotated modules discovered by the annotation processor. 
You can finally add the generated list to your Koin initialization:
```kotlin
startKoin {
    androidContext(context)
    modules(koinModules)
}
```
Whenever you add a new Koin Module, it will be automatically added to the same list.

### Avoid conflicts in multi-module projects
To avoid ambiguity and class-loading conflicts in case you want to use KMP in multiple project modules, you can specify a prefix
to be used in the generated names by passing an argument named `"kmp.prefix"` to the
annotation processor in your build.gradle.kts:
```kotlin
kapt {
    arguments {
        arg("kmp.prefix", "library")
    }
}
```
This way, the generated module list will be named (as per the above example) `libraryKoinModules`.