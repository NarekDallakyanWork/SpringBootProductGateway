package flower.app.flowers.product.config

import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductConfiguration {

    @Bean
    fun orikaMapperFactoryBean(): MapperFactory = DefaultMapperFactory.Builder().build()
}
