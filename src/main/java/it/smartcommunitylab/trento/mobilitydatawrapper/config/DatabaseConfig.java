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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration 
@EnableTransactionManagement
@EnableAutoConfiguration
public class DatabaseConfig {

	@Value("${spring.datasource.driverClassName}")
	private String dbDriverClassname;
	@Value("${spring.jpa.hibernate.dialect}")
	private String jpaDialect;

	@Value("${db.parkings.url}")
	private String dbParkingsUrl;
	@Value("${db.parkings.username}")
	private String dbParkingsUser;
	@Value("${db.parkings.password}")
	private String dbParkingsPassword;

	@Value("${db.traffic.url}")
	private String dbTrafficUrl;
	@Value("${db.traffic.username}")
	private String dbTrafficUser;
	@Value("${db.traffic.password}")
	private String dbTrafficPassword;

	@Bean(name="parkingsDataSource")
	public DriverManagerDataSource getParkingDataSource()  {
		DriverManagerDataSource bean = new DriverManagerDataSource();
		
		bean.setDriverClassName(dbDriverClassname);
		bean.setUrl(dbParkingsUrl);
		bean.setUsername(dbParkingsUser);
		bean.setPassword(dbParkingsPassword);

		return bean;
	}	
	
	@Bean(name="parkingsEntityManagerFactory")
	public EntityManagerFactory getParkingsEntityManagerFactory()  {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceUnitName("jdbc");
		bean.setDataSource(getParkingDataSource());
		
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform(jpaDialect);
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
		
		bean.setDriverClassName(dbDriverClassname);
		bean.setUrl(dbTrafficUrl);
		bean.setUsername(dbTrafficUser);
		bean.setPassword(dbTrafficPassword);

		return bean;
	}	
	
	@Bean(name="trafficEntityManagerFactory")
	public EntityManagerFactory getTrafficEntityManagerFactory()  {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceUnitName("jdbc");
		bean.setDataSource(getTrafficDataSource());
		
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform(jpaDialect);
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
