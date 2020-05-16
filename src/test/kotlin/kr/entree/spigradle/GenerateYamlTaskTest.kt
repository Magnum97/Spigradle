package kr.entree.spigradle

import kr.entree.spigradle.internal.create
import kr.entree.spigradle.module.common.task.GenerateYamlTask
import kr.entree.spigradle.module.spigot.data.Load
import kr.entree.spigradle.module.spigot.extension.SpigotPluginAttributes
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by JunHyung Lim on 2020-05-13
 */
class GenerateYamlTaskTest {
    val project = ProjectBuilder.builder().build()
    val yamlTask = project.tasks.create<GenerateYamlTask>("yaml")

    init {
        yamlTask.file.deleteOnExit()
    }

    @Test
    fun `simple generation`() {
        val contents = "test contents"
        yamlTask.apply {
            value = contents
            generate()
        }
        assertEquals(contents, yamlTask.file.readText().trimIndent())
    }

    @Test
    fun serialization() {
        val extension = project.extensions.create<SpigotPluginAttributes>("spigot").apply {
            init(project)
            main = "kr.entree.spigradle.Main"
            name = "Spigradle"
            version = "1.1"
            description = "This plugin does so much stuff it can't be contained!"
            website = "https://github.com/EntryPointKR/Spigradle"
            authors = listOf("EntryPoint")
            apiVersion = "1.15"
            load = Load.POST_WORLD
            prefix = "Its prefix"
            softDepends = listOf("ProtocolLib")
            loadBefore = listOf("ABC")
            commands.apply {
                create("give").apply {
                    description = "Give command."
                    usage = "/<command> [test|stop]"
                    permission = "test.foo"
                    permissionMessage = "You do not have permission!"
                    aliases = listOf("alias")
                }
            }
            permissions.apply {
                create("test.*").apply {
                    description = "Wildcard permission"
                    defaults = "op"
                    children = mapOf("test.foo" to true)
                }
                create("test.foo").apply {
                    description = "Allows foo command"
                    defaults = "true"
                }
            }
        }
        yamlTask.apply {
            value = extension as SpigotPluginAttributes
            generate()
        }
        val expected = javaClass.getResourceAsStream("/spigot/plugin.yml").bufferedReader().readText()
        assertEquals(expected, yamlTask.file.readText())
    }
}