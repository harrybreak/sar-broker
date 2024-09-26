package sar;

public class NotFoundBrokerException extends Exception {

	private static final long serialVersionUID = 0L;
	
	NotFoundBrokerException(String errorMsg) {
		super(errorMsg);
	}

}
