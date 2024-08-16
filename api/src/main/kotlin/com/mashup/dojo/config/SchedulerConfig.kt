package com.mashup.dojo.config

import com.mashup.dojo.scheduler.SchedulerBasePackage
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import javax.sql.DataSource

@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = [SchedulerBasePackage::class])
@EnableSchedulerLock(defaultLockAtMostFor = "PT5M")
class SchedulerConfig {
    @Bean
    fun lockProvider(dataSource: DataSource): JdbcTemplateLockProvider {
        return JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(JdbcTemplate(dataSource))
                // 서버 시간 대신 데이터베이스 시간을 사용
                .usingDbTime()
                .build()
        )
    }
}
