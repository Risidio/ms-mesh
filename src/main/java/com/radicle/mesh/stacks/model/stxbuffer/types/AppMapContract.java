package com.radicle.mesh.stacks.model.stxbuffer.types;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.TypeAlias;

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
@TypeAlias(value = "AppMapContract")
public class AppMapContract {
	private List<Application> applications;
	private String administrator;
	private String adminContractAddress;
	private String adminContractName;
	private long appCounter;
	
	public void addApplication(Application application) {
		if (this.applications == null) {
			this.applications = new ArrayList<>();
		}
		applications.add(application);
	}
	
	public List<Application> getApplications() {
		return this.applications;
	}
}
