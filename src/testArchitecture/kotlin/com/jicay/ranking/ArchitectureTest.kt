package com.jicay.ranking

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Test


class ArchitectureTest {

    private val basePackage = "com.jicay.ranking"

    @Test
    fun `it should respect the clean architecture concept`() {
        val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(basePackage)

        val rule = layeredArchitecture().consideringAllDependencies()
            .layer("Infrastructure").definedBy("$basePackage.infrastructure..")
            .layer("Primary").definedBy("$basePackage.infrastructure.primary..")
            .layer("Secondary").definedBy("$basePackage.infrastructure.secondary..")
            .layer("Application").definedBy("$basePackage.infrastructure.application..")
            .layer("Domain").definedBy("$basePackage.domain..")
            .layer("Standard API")
            .definedBy("java..", "kotlin..", "kotlinx..", "org.jetbrains.annotations..")
            .withOptionalLayers(true)
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Primary").mayOnlyBeAccessedByLayers("Application")
            .whereLayer("Secondary").mayOnlyBeAccessedByLayers("Application")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Primary", "Secondary")
            .whereLayer("Domain").mayOnlyAccessLayers("Standard API")

        rule.check(importedClasses)
    }
}
