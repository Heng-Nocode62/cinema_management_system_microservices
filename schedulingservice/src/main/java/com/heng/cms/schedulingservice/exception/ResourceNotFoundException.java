package com.heng.cms.schedulingservice.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ResourceNotFoundException(String resourceName, UUID id) {
		super(resourceName+ " with id= "+id + " not found.");
	}
	public ResourceNotFoundException(String message) {
		super(message);
	}

}
