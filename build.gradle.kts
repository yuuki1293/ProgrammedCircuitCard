import com.hypherionmc.modpublisher.plugin.ModPublisherGradleExtension
import com.hypherionmc.modpublisher.properties.CurseEnvironment
import com.hypherionmc.modpublisher.properties.ModLoader
import com.hypherionmc.modpublisher.properties.ReleaseType
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.ChangelogPluginExtension
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java")
    id("java-library")
    id("idea")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moddev)
    alias(libs.plugins.modPublisher)
    alias(libs.plugins.changelog)
    alias(libs.plugins.spotless)
}

val modId = Constants.Mod.id
val mcVersion: String = libs.versions.minecraft.get()
val forgeVersion: String = libs.versions.forge.get()
val forgeVersionRange: String = libs.versions.forgeRange.get()
val jdkVersion = 17

val exportMixin = true

val changelogExtension = extensions.getByType<ChangelogPluginExtension>()
val sourceSets = the<SourceSetContainer>()
val mainSourceSet = sourceSets.named("main")

fun parserChangelog(): String {
    if (!file("CHANGELOG.md").exists()) {
        throw GradleException("publish_with_changelog is true, but CHANGELOG.md does not exist in the workspace!")
    }
    val parsedChangelog = changelogExtension.renderItem(
        changelogExtension.get(Constants.Mod.version).withHeader(false).withEmptySections(false),
        Changelog.OutputType.MARKDOWN
    )
    if (parsedChangelog.isEmpty()) {
        throw GradleException("publish_with_changelog is true, but the changelog for the latest version is empty!")
    }
    return parsedChangelog
}

val serverKey = "${modId}.server"
val isServer = objects.property<String>().convention(
    providers.environmentVariable(serverKey)
        .orElse("")
)

base {
    archivesName = "${project.name}-$mcVersion"
    version = Constants.Mod.version
    group = Constants.Mod.group
}

legacyForge {
    version = "$mcVersion-$forgeVersion"

    parchment {
        mappingsVersion = libs.versions.parchmentmc.get()
        minecraftVersion = mcVersion
    }

    runs {
        register("client") {
            client()
            gameDirectory.set(file("run"))
            systemProperty("forge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        register("client-disableSlot") {
            client()
            gameDirectory.set(file("run"))
            systemProperty("forge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
            jvmArgument("-Dpccard.disableSlot")
        }

        register("server") {
            server()
            gameDirectory.set(file("run-server"))
            programArgument("--nogui")
            systemProperty("forge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
            environment(serverKey, "1")
        }

        register("data") {
            data()
            gameDirectory.set(file("run-data"))
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
        }
    }
}

mixin {
    add(sourceSets["main"], "${modId}.refmap.json")

    config("${modId}.mixins.json")
}

repositories {
    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
    maven {
        name = "GTCEu Maven"
        url = uri("https://maven.gtceu.com")
        content {
            includeGroup ("com.gregtechceu.gtceu")
            includeGroup ("appeng")
        }
    }
    maven {
        name = "Mod Maven"
        url = uri("https://modmaven.dev/")
        content {
            includeGroup ("appeng")
            includeGroup ("mezz.jei")
        }
    }
    maven {
        name = "Gnomecraft"
        url = uri("https://maven.gnomecraft.net/releases/")
        content {
            includeGroup ("dev.emi")
        }
    }
    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
        content {
            includeGroup ("dev.emi")
        }
    }
    maven {
        name = "Registrate"
        url = uri("https://maven.tterrag.com/")
        content {
            includeGroup ("com.tterrag.registrate")
        }
    }
    maven {
        name = "firstdarkdev"
        url = uri("https://maven.firstdarkdev.xyz/snapshots")
        content {
            includeGroup("com.lowdragmc.ldlib")
        }
    }
    maven {
        name = "Curse Maven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup ("curse.maven")
        }
    }
    maven { url = uri("https://maven.shedaniel.me/") }
    maven {
        name = "Curios"
        url = uri("https://maven.theillusivec4.top/")
    }
    maven { url = uri("https://maven.bawnorton.com/releases") }
    mavenLocal()
}

dependencies {
    modImplementation (libs.gtm) { isTransitive = false } // GregTech CEu: Modern
    modImplementation (libs.ae2) // AE2

    /* Those compete with AE2 */
//    modCompileOnly (libs.ae2cosmo) // AE2 cosmolite
//    modImplementation (libs.monilabs) // AE2 monilabs
//    modRuntimeOnly (libs.kubejs) // depended by monilabs
//    modRuntimeOnly (libs.rhino) // depended by KubeJS
//    modRuntimeOnly (libs.melody) // depended by FancyMenu
//    modRuntimeOnly (libs.konkrete) // depended by FancyMenu

    modImplementation (libs.jei) // JEI
    modCompileOnly (variantOf(libs.emi, "api")) // EMI
    modRuntimeOnly (libs.emi) // EMI
    modCompileOnly (libs.registrate) // Registrate
    modCompileOnly (libs.ldlib) { isTransitive = false } // ldlib
    modCompileOnly (libs.adae) // Advanced AE
//    modRuntimeOnly (libs.adae) // Advanced AE
    modRuntimeOnly (libs.geckolib)
    modCompileOnly (libs.exae) // Extended AE
    modRuntimeOnly (libs.exae)
    modRuntimeOnly (libs.glodium)
//    modRuntimeOnly (libs.appflux) // Applied Flux
    modImplementation (libs.mae2) // MAE2
    modCompileOnly (variantOf(libs.guideme, "api")) // GuideME
    modRuntimeOnly (libs.guideme)
    modCompileOnly (libs.expandedae) // Expanded AE
//    modRuntimeOnly (libs.expandedae)
//    modRuntimeOnly (libs.megacells) // Mega Cells
    modRuntimeOnly (libs.cloth.config) // Mega Cells
    modRuntimeOnly (libs.wirelessterminals) // AE2 Wireless Terminals
    modRuntimeOnly (libs.curios)
    modRuntimeOnly (libs.architectury.api)

    if (isServer.get() != "1") {
//        modRuntimeOnly (libs.chloride) // depended by monilabs
//        modRuntimeOnly (libs.embeddium) // depended by monilabs
//        modRuntimeOnly (libs.fancymenu) // depended by monilabs
//        modRuntimeOnly (libs.oculus) // depended by monilabs
        modRuntimeOnly (libs.jade) // Jade
    }

    annotationProcessor(variantOf(libs.mixin, "processor"))
    libs.mixinExtrasCommon.let {
        annotationProcessor(it)
        modCompileOnly(it)
    }
    libs.mixinExtrasForge.let {
        jarJar(it) {
            version {
                val version = it.get().version.toString()
                strictly("[$version,)")
                prefer(version)
            }
        }
        modImplementation(it)
    }
    libs.mixinSquaredCommon.let {
        annotationProcessor(it)
        compileOnly(it)
    }
    libs.mixinSquaredForge.let {
        jarJar(it) {
            version {
                val version = it.get().version.toString()
                strictly("[$version,)")
                prefer(version)
            }
        }
        modImplementation(it)
    }
}

val modDependencies = listOf(
    ModDep("forge", extractVersionSegments(forgeVersion), forgeVersionRange),
    ModDep("minecraft", mcVersion),
    ModDep("gtceu", libs.versions.gtmRange.get()),
    ModDep("ae2", libs.versions.ae2Range.get()), // comment out when use monilabs
    ModDep("guideme", libs.versions.guidemeRange.get()),
    ModDep("expandedae", libs.versions.expandedaeRange.get(), mandatory = false)
)

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "version" to version,
        "group" to project.group,
        "minecraft_version" to mcVersion,
        "mod_loader" to "javafml",
        "mod_loader_version_range" to "[47,)",
        "mod_name" to Constants.Mod.name,
        "mod_author" to Constants.Mod.author,
        "mod_id" to modId,
        "license" to Constants.Mod.license,
        "description" to Constants.Mod.description,
        "display_url" to Constants.Mod.repositoryUrl,
        "issue_tracker_url" to Constants.Mod.issueTrackerUrl,
        "logo_file" to "logo.png",

        "dependencies" to buildDeps(*modDependencies.toTypedArray())
    )

    inputs.properties(replaceProperties)
    from("src/main/templates") {
        filter<ReplaceTokens>("beginToken" to "\${", "endToken" to "}", "tokens" to replaceProperties)
        rename("template(\\..+)?.mixins.json", "${modId}$1.mixins.json")
    }
    into("build/generated/sources/modMetadata")

    // GuideME guide
    from("guidebook") {
        into("assets/pccard/guides/pccard/guide")
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = jdkVersion
    }

    java {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(jdkVersion)
            vendor = JvmVendorSpec.JETBRAINS
        }
        JavaVersion.toVersion(jdkVersion).let {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    processResources {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.id}" }
        }
        dependsOn(generateModMetadata)
    }

    jar {
        manifest {
            attributes(
                "Specification-Title" to Constants.Mod.name,
                "Specification-Vendor" to Constants.Mod.author,
                "Specification-Version" to version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to version,
                "Implementation-Vendor" to Constants.Mod.author,
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to mcVersion,
                "MixinConfigs" to "${modId}.mixins.json"
            )
        }
    }

    named<Jar>("sourcesJar") {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.id}" }
        }
    }

    named<Wrapper>("wrapper").configure {
        distributionType = Wrapper.DistributionType.BIN
    }

    named { it.startsWith("publish") }.forEach {
        it.notCompatibleWithConfigurationCache("ModPublisher plugin is not compatible with configuration cache")
    }
}

sourceSets {
    main {
        resources {
            srcDirs(
                "src/generated/resources",
                generateModMetadata.get().outputs.files
            )
            exclude("**/.cache")
        }
    }
}

legacyForge.ideSyncTask(generateModMetadata)

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        resourceDirs.add(file("src/main/templates"))
    }
}

fun ModPublisherGradleExtension.Dependencies.fromModDependencies(modDependencies: List<ModDep>) {
    modDependencies.filter {
        it.id != "minecraft" && it.id != "forge"
    }.forEach {
        if (it.mandatory) {
            required(it.id)
        } else {
            optional(it.id)
        }
    }
}

publisher {
    apiKeys {
        curseforge(System.getenv("CURSEFORGE_TOKEN"))
        modrinth(System.getenv("MODRINTH_TOKEN"))
        github(System.getenv("GITHUB_TOKEN"))
    }

    setReleaseType(ReleaseType.RELEASE)
    setLoaders(ModLoader.FORGE, ModLoader.NEOFORGE)
    setCurseEnvironment(CurseEnvironment.BOTH)

    curseID.set(Constants.Publisher.curseforgeProjectId)
    modrinthID.set(Constants.Publisher.modrinthProjectId)
    changelog.set(parserChangelog())
    projectVersion.set("${project.version}")
    displayName.set("[$mcVersion] v${project.version}")
    setGameVersions(mcVersion)
    setJavaVersions(jdkVersion)
    artifact.set(tasks.named("reobfJar"))

    curseDepends {
        required("applied-energistics-2")
        required("gregtechceu-modern")
        required("guideme")
        optional("ex-pattern-provider")
        optional("advancedae")
        optional("expanded-ae")
        optional("modern-ae2-additions")
        optional("mega-cells")
        optional("jei")
        optional("emi")
    }

    modrinthDepends {
        required("ae2")
        required("gregtechceu-modern")
        required("guideme")
        optional("extended-ae")
        optional("advancedae")
        optional("expanded-ae")
        optional("modern-ae2-additions")
        optional("mega")
        optional("jei")
        optional("emi")
    }

    github {
        repo("yuuki1293/ProgrammedCircuitCard")
        tag("v${mcVersion}-${project.version}")
        displayName("[$mcVersion] v${project.version}")
        createTag(true)
        createRelease(true)
        updateRelease(true)
        target("1.20.1")
    }
}

spotless {
    encoding("UTF-8")

    format("misc") {
        target(".gitignore")

        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
    java {
        target("src/*/java/**/*.java", "src/*/scala/**/*.java")

        toggleOffOn()
        importOrderFile(file("spotless.importorder"))
        removeUnusedImports()
        eclipse("4.19").configFile(file("spotless.eclipseformat.xml"))
    }
    kotlin {
        target("src/*/kotlin/**/*.kt", "src/*/java/**/*.kt")

        toggleOffOn()
        trimTrailingWhitespace()
        endWithNewline()
        ktlint("1.7.1").editorConfigOverride(mapOf(
            "ktlint_code_style" to "intellij_idea"
        ))
    }
    scala {
        target("src/*/scala/**/*.scala")

        scalafmt("3.7.15")
    }
}
