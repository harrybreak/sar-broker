package mixed;

public class DisconnectChannelException extends IllegalStateException {

	private static final long serialVersionUID = 1L;
	
	public DisconnectChannelException(String errorMsg) {
		
		super(errorMsg);
	}

}
