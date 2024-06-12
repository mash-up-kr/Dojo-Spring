package com.mashup.dojo.config

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableEncryptableProperties
class DojoJasyptConfig {

    @Value("\${JASYPT_ENCRYPTOR_PASSWORD}")
    private lateinit var password: String

    @Bean("jasyptStringEncryptor")
    fun stringEncryptor(): StringEncryptor {
        val encryptor = PooledPBEStringEncryptor()
        val configPassword = password
        val config = SimpleStringPBEConfig().apply {
            this.password = configPassword
            this.algorithm = "PBEWithMD5AndDES"
            this.keyObtentionIterations = 1000
            this.poolSize = 1
            this.providerName = "SunJCE"
            this.saltGenerator = org.jasypt.salt.RandomSaltGenerator()
            this.ivGenerator = org.jasypt.iv.NoIvGenerator()
            this.stringOutputType = "base64"
        }
        encryptor.setConfig(config)
        return encryptor
    }
}
