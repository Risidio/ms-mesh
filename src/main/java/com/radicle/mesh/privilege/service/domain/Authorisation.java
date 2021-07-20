package com.radicle.mesh.privilege.service.domain;

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
