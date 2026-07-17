package com.heng.cms.bookingservice.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ResourceNotFoundException(String resourceName, UUID id) {
		super(resourceName+ " with id= "+id + " not found.");
	}
	public ResourceNotFoundException(String resourceName) {
		super(resourceName + " not found.");
	}

}
