package com.radicle.mesh.privilege.service.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "Authorisation")
@Document
public class Authorisation {

	@Id private String id;
	private Boolean whitelisted;
	private String stxAddress;
	private List<Domain> domains;
	private List<String> roles;
	
	public boolean addPrivilege(String host, String priv) {
		if (domains == null) {
			domains = new ArrayList<Domain>();
		}
		boolean foundDom = false;
		boolean found = false;
		for (Domain domain : domains) {
			if (domain.getHost().equals(host)) {
				foundDom = true;
				for (String privilege : domain.getPrivileges()) {
					if (privilege.equals(priv)) {
						found = true;
					}
				}
				if (!found) {
					domain.getPrivileges().add(priv);
					return true;
				}
			}
		}
		if (!foundDom) {
			Domain d = new Domain();
			d.setHost(host);
			List<String> privs = new ArrayList<String>();
			privs.add(priv);
			d.setPrivileges(privs);
			domains.add(d);
			return true;
		}
		return false;
	}
	
	public boolean removePrivilege(String host, String priv) {
		if (domains == null) {
			domains = new ArrayList<Domain>();
		}
		for (Domain domain : domains) {
			if (domain.getHost().equals(host)) {
				return domain.getPrivileges().remove(priv);
			}
		}
		return false;
	}
	
	public boolean addDomain(Domain domain) {
		if (domains == null) {
			domains = new ArrayList<Domain>();
		}
		return domains.add(domain);
	}
	
	public boolean hasPrivilege(String origin, String searchPrivilege) {
		boolean found = false;
		for (Domain domain : domains) {
			for (String privilege : domain.getPrivileges()) {
				if (privilege.equalsIgnoreCase(searchPrivilege)) {
					found = true;
				}
			}
		}
		return found;
	}
	
	public boolean isWhitelisted(String protectedResource) {
		if (roles != null && roles.contains("admin")) {
			return true;
		}
		return true;
	}
}
