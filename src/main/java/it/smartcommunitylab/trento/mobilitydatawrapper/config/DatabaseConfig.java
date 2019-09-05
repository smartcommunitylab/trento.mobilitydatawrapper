/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package it.smartcommunitylab.trento.mobilitydatawrapper.config;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration 
@EnableTransactionManagement
@EnableAutoConfiguration
public class DatabaseConfig {

	@Autowired
	private Environment env;
	
	@Bean(name="parkingsDataSource")
	public DriverManagerDataSource getParkingDataSource()  {
		DriverManagerDataSource bean = new DriverManagerDataSource();
		
		bean.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
		bean.setUrl(env.getProperty("db.parkings.url"));
		bean.setUsername(env.getProperty("db.parkings.username"));
		bean.setPassword(env.getProperty("db.parkings.password"));

		return bean;
	}	
	
	@Bean(name="parkingsEntityManagerFactory")
	public EntityManagerFactory getParkingsEntityManagerFactory()  {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceUnitName(env.getProperty("jdbc.name"));
		bean.setDataSource(getParkingDataSource());
		
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform(env.getProperty("spring.jpa.hibernate.dialect"));
		bean.setJpaVendorAdapter(adapter);
		
		bean.setJpaDialect(new DefaultJpaDialect());
		
		Properties props = new Properties();
		bean.setJpaProperties(props);
		bean.afterPropertiesSet();
		
		return bean.getObject();
	}	
	
	@Bean(name = "parkingsEntityManager")
    public EntityManager parkingsEntityManager() {
        return getParkingsEntityManagerFactory().createEntityManager();
    }	
	
	
	@Bean(name="trafficDataSource")
	public DriverManagerDataSource getTrafficDataSource()  {
		DriverManagerDataSource bean = new DriverManagerDataSource();
		
		bean.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
		bean.setUrl(env.getProperty("db.traffic.url"));
		bean.setUsername(env.getProperty("db.traffic.username"));
		bean.setPassword(env.getProperty("db.traffic.password"));

		return bean;
	}	
	
	@Bean(name="trafficEntityManagerFactory")
	public EntityManagerFactory getTrafficEntityManagerFactory()  {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceUnitName(env.getProperty("jdbc.name"));
		bean.setDataSource(getTrafficDataSource());
		
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform(env.getProperty("spring.jpa.hibernate.dialect"));
		bean.setJpaVendorAdapter(adapter);
		
		bean.setJpaDialect(new DefaultJpaDialect());
		
		Properties props = new Properties();
		bean.setJpaProperties(props);
		bean.afterPropertiesSet();
		
		return bean.getObject();
	}	
	
	@Bean(name = "trafficEntityManager")
    public EntityManager trafficEntityManager() {
        return getTrafficEntityManagerFactory().createEntityManager();
    }
	
	
	
}
