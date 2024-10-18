package mixed;

public class NotFoundQueueBrokerException extends IllegalStateException {

	private static final long serialVersionUID = 1L;
	
	public NotFoundQueueBrokerException(String errorMsg) {
		
		super(errorMsg);
	}
	
}
