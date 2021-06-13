package com.radicle.mesh.common.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Josh Cummings
 */
@EnableWebSecurity
public class WebServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${radicle.security.enable-csrf}")
    private boolean csrfEnabled;

    @Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
        // super.configure(http);

        if(!csrfEnabled)
        {
          http.csrf().disable();
        }
		http
			.authorizeRequests(authorizeRequests ->
				authorizeRequests
					//.antMatchers(HttpMethod.OPTIONS, "/buy-now/**").permitAll()
					.antMatchers("/**").permitAll()
					//.antMatchers(HttpMethod.POST, "/buy-now/**").hasAuthority("SCOPE_message:write")
					.anyRequest().authenticated()
			);
//			.oauth2ResourceServer(oauth2ResourceServer ->
//				oauth2ResourceServer
//					.opaqueToken(opaqueToken ->
//						opaqueToken
//							.introspectionUri(this.introspectionUri)
//							.introspectionClientCredentials(this.clientId, this.clientSecret)
//					)
//			);
		// @formatter:on
	}
}