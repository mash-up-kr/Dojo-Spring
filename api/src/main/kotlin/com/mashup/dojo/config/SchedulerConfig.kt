package com.mashup.dojo.config

import com.mashup.dojo.scheduler.SchedulerBasePackage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = [SchedulerBasePackage::class])
class SchedulerConfig {
    @Bean(name = ["questionSetSchedulerExecutor"])
    fun questionSetSchedulerExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            maxPoolSize = 5
            queueCapacity = 20
            setThreadNamePrefix("questionSetScheduler-")
            setWaitForTasksToCompleteOnShutdown(true)
        }
    }

    @Bean(name = ["questionSheetSchedulerExecutor"])
    fun questionSheetSchedulerExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            maxPoolSize = 5
            queueCapacity = 20
            setThreadNamePrefix("questionSheetScheduler-")
            setWaitForTasksToCompleteOnShutdown(true)
        }
    }
}
