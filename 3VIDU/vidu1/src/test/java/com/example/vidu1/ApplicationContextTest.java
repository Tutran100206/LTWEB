package com.example.vidu1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.*;

/** Boots actual bean scanning and validates JPA queries using SQL Server dialect, without opening a database connection. */
@SpringBootTest(properties={
    "app.seed-demo=false", "spring.config.import=",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.properties.hibernate.boot.allow_jdbc_metadata_access=false",
    "spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect",
    "spring.datasource.url=jdbc:sqlserver://127.0.0.1:1;databaseName=test;loginTimeout=1",
    "spring.datasource.username=unused", "spring.datasource.password=unused"
})
class ApplicationContextTest {
    @Autowired ApplicationContext context;
    @Test void scanningRepositoriesAndGeneratedMappersAreValid() {
        assertNotNull(context.getBean(com.example.vidu1.repository.UserRepository.class));
        assertFalse(org.mockito.Mockito.mockingDetails(context.getBean(com.example.vidu1.repository.UserRepository.class)).isMock());
        assertNotNull(context.getBean(com.example.vidu1.mapper.UserMapper.class));
        assertEquals(1,context.getBeansOfType(org.springframework.security.web.SecurityFilterChain.class).size());
    }
}
