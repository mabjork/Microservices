package no.mabjork.auth_service.factories

import java.io.IOException
import java.util.Properties

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.EncodedResource
import org.springframework.core.io.support.PropertySourceFactory
import org.springframework.util.Assert


class YamlPropertySourceFactory : PropertySourceFactory {

    private val activeRuntimeProfiles: Array<String>
        get() {
            val profileConfig = System.getProperty("spring.profiles.active")
            return if (profileConfig === "") arrayOf() else profileConfig.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }

    @Throws(IOException::class)
    override fun createPropertySource(name: String?, resource: EncodedResource): PropertySource<*> {
        val effectiveProperties = Properties()

        // First, load all properties from the default resource
        val defaultResource = resource.resource
        if (defaultResource.exists()) {
            effectiveProperties.putAll(this.loadYamlIntoProperties(defaultResource))

        }

        // Next, if profile-specific properties exist, merge them in and override the default values
        val profileResource = this.findResourceBasedOnActiveProfiles(resource)
        if (profileResource == null)
            Assert.isTrue(defaultResource.exists(), String.format("The YAML file %s does not exist.", defaultResource.filename))
        else {
            effectiveProperties.putAll(this.loadYamlIntoProperties(profileResource))

        }

        return if (name.isNullOrBlank()){
            PropertiesPropertySource(resource.resource.filename ?: "", effectiveProperties)
        } else {
            PropertiesPropertySource(name, effectiveProperties)
        }
    }

    private fun loadYamlIntoProperties(resource: Resource): Properties {
        val factory = YamlPropertiesFactoryBean()
        factory.setResources(resource)
        factory.afterPropertiesSet()
        return factory.getObject() ?: Properties()
    }

    private fun findResourceBasedOnActiveProfiles(resource: EncodedResource): Resource? {
        val resourceName = resource.resource.filename
        val activeProfiles = this.activeRuntimeProfiles

        for (activeProfile in activeProfiles) {
            // Assuming the format of the YAML file's name is "<RESOURCE_NAME>_<PROFILE_NAME>.yml"
            val resourceNameRelativeToActiveProfile = resourceName!!.replaceFirst("\\.yml".toRegex(), String.format("_%s.yml", activeProfile))

            val potentialResource = ClassPathResource(resourceNameRelativeToActiveProfile)
            if (potentialResource.exists())
                return potentialResource
        }

        return null
    }
}