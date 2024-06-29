package com.mashup.dojo.external

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.mashup.dojo.external.aws.ImageUploadUrlProvider
import com.mashup.dojo.external.aws.S3ImageUploadUrlProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class ExternalConfiguration {
    @Bean
    fun s3Client(
        environment: Environment
    ): AmazonS3Client {
        val accessKey = environment.getProperty("aws.access-key")
        val secretKey = environment.getProperty("aws.secret-key")
        val credential = BasicAWSCredentials(accessKey, secretKey)
        return AmazonS3ClientBuilder.standard()
            .withCredentials(
                AWSStaticCredentialsProvider(credential)
            )
            .withRegion(Regions.AP_NORTHEAST_2)
            .build() as AmazonS3Client
    }

    @Bean
    fun imageUploader(s3Client: AmazonS3Client): ImageUploadUrlProvider {
        return S3ImageUploadUrlProvider(s3Client)
    }
}
