package config;

import org.apache.commons.configuration.XMLConfiguration;

import com.google.inject.AbstractModule;

public class ConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(XMLConfiguration.class)
		.to(XMLConfiguration.class);

	}

}
