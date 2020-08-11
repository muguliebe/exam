package com.exam.bank.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableScheduling
class BaseConfig {

    /**
     * 쓰레드 개수 & 이름
     */
    @Primary
    @Bean(name = ["threadPoolTaskExecutor"])
    fun taskExecutor(): TaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = 100
        taskExecutor.setQueueCapacity(1000)
        taskExecutor.maxPoolSize = 200
        taskExecutor.setThreadNamePrefix("exe-")
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)
        taskExecutor.setAwaitTerminationSeconds(20)
        return taskExecutor
    }

}
