plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    // Applied on the root so it can aggregate coverage across modules.
    alias(libs.plugins.kover)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.5.0")
        filter {
            exclude { it.file.path.contains("/build/") }
        }
    }

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        basePath = rootProject.projectDir.absolutePath
    }
}

// Aggregate coverage over the modules that hold testable logic.
dependencies {
    kover(project(":core"))
    kover(project(":app:shared"))
}

kover {
    reports {
        filters {
            excludes {
                // UI, template demo, platform entry points, and generated code
                // aren't unit-tested; exclude so the gate reflects real logic.
                classes(
                    "com.firdous.expensa.App*",
                    "com.firdous.expensa.ComposableSingletons*",
                    "com.firdous.expensa.Greeting*",
                    "com.firdous.expensa.Platform*",
                    "com.firdous.expensa.MainViewControllerKt",
                )
                // UI composables and the composition root are verified by running
                // the app / UI tests, not unit coverage.
                packages(
                    "expensa.app.shared.generated.resources",
                    "com.firdous.expensa.ui",
                    "com.firdous.expensa.di",
                )
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}