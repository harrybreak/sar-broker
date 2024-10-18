package threaded;

public class NotFoundBrokerException extends IllegalStateException {

	private static final long serialVersionUID = 1L;
	
	public NotFoundBrokerException(String errorMsg) {
		
		super(errorMsg);
	}

}
