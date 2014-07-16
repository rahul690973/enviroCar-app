package org.envirocar.app.dao.exception;

public class FriendsRetrievalException extends DAOException{
	
	private static final long serialVersionUID = 1L;

	public FriendsRetrievalException(String string) {
		super(string);
	}

	public FriendsRetrievalException(Exception e) {
		super(e);
	}

}
