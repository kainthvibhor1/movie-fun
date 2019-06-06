package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DatabaseServiceCredentials parseDBCreds() {
        return new DatabaseServiceCredentials(System.getenv("VCAP_SERVICES"));
    }

    @Bean(name = "albums-source")
    public DataSource getAlbumsDataSource(DatabaseServiceCredentials credentials) {
        MysqlDataSource source = new MysqlDataSource();
        source.setURL(credentials.jdbcUrl("albums-mysql"));
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(source);
        return hikariDataSource;
    }

    @Bean(name = "movies-source")
    public DataSource getMoviesDataSource(DatabaseServiceCredentials credentials) {
        MysqlDataSource source = new MysqlDataSource();
        source.setURL(credentials.jdbcUrl("movies-mysql"));
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(source);
        return hikariDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter getAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");
        adapter.setGenerateDdl(true);
        return adapter;
    }

    @Bean(name = "albums-em")
    public LocalContainerEntityManagerFactoryBean getAlbumEM(@Autowired @Qualifier("albums-source") DataSource albumSource, HibernateJpaVendorAdapter albumAdapter) {
        LocalContainerEntityManagerFactoryBean managerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        managerFactoryBean.setDataSource(albumSource);
        managerFactoryBean.setJpaVendorAdapter(albumAdapter);
        managerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        managerFactoryBean.setPersistenceUnitName("albums-persistence");
        return managerFactoryBean;
    }

    @Bean(name = "movies-em")
    public LocalContainerEntityManagerFactoryBean getMoviesEM(@Autowired @Qualifier("movies-source") DataSource albumSource, HibernateJpaVendorAdapter albumAdapter) {
        LocalContainerEntityManagerFactoryBean managerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        managerFactoryBean.setDataSource(albumSource);
        managerFactoryBean.setJpaVendorAdapter(albumAdapter);
        managerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        managerFactoryBean.setPersistenceUnitName("movies-persistence");
        return managerFactoryBean;
    }

    @Bean(name = "albums-tm")
    public PlatformTransactionManager getAlbumTM(@Autowired @Qualifier("albums-em") EntityManagerFactory albumEm) {
        JpaTransactionManager manager = new JpaTransactionManager(albumEm); // Instantiate manager
        return manager;
    }

    @Bean(name = "movies-tm")
    public PlatformTransactionManager getMoviesTM(@Autowired @Qualifier("movies-em") EntityManagerFactory movieEm) {
        JpaTransactionManager manager = new JpaTransactionManager(movieEm); // Instantiate manager
        return manager;
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }
}
