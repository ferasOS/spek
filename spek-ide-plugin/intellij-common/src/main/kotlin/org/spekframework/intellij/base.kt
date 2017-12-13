package org.spekframework.intellij

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.configurations.RunConfigurationModule
import com.intellij.openapi.util.JDOMExternalizerUtil
import org.jdom.Element
import org.jetbrains.kotlin.idea.KotlinIcons

abstract class SpekBaseConfigurationType(id: String, displayName: String): ConfigurationTypeBase(
    id,
    displayName,
    "Run specifications",
    KotlinIcons.SMALL_LOGO_13
)


abstract class SpekBaseRunConfiguration<T: RunConfigurationModule>(name: String,
                                                                   configurationModule: T,
                                                                   factory: ConfigurationFactory)
    : ModuleBasedConfiguration<T>(name, configurationModule, factory), CommonProgramRunConfigurationParameters {

    private var workingDirectory: String? = null
    private var envs = mutableMapOf<String, String>()
    private var passParentEnvs: Boolean = false
    private var programParameters: String? = null

    override fun getWorkingDirectory() = workingDirectory

    override fun getEnvs() = envs

    override fun setWorkingDirectory(value: String?) {
        workingDirectory = value
    }

    override fun setEnvs(envs: MutableMap<String, String>) {
        this.envs = envs
    }

    override fun isPassParentEnvs() = passParentEnvs

    override fun setPassParentEnvs(passParentEnvs: Boolean) {
        this.passParentEnvs = passParentEnvs
    }

    override fun setProgramParameters(value: String?) {
        programParameters = value
    }

    override fun getProgramParameters() = programParameters

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JDOMExternalizerUtil.writeField(element, PASS_PARENT_ENVS, passParentEnvs.toString())
        JDOMExternalizerUtil.writeField(element, WORKING_DIRECTORY, workingDirectory)
        JDOMExternalizerUtil.writeField(element, PROGRAM_PARAMETERS, programParameters)
        EnvironmentVariablesComponent.writeExternal(element, envs)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        passParentEnvs = JDOMExternalizerUtil.readField(element, PASS_PARENT_ENVS, "false").toBoolean()
        workingDirectory = JDOMExternalizerUtil.readField(element, WORKING_DIRECTORY)
        programParameters = JDOMExternalizerUtil.readField(element, PROGRAM_PARAMETERS)
        EnvironmentVariablesComponent.readExternal(element, envs)
    }

    companion object {
        const val PASS_PARENT_ENVS = "passParentEnvs"
        const val WORKING_DIRECTORY = "workingDirectory"
        const val PROGRAM_PARAMETERS = "programParameters"
    }
}
