package sar;

public class DisconnectQueueException extends DisconnectChannelException {

	private static final long serialVersionUID = 1L;
	
	public DisconnectQueueException(String errorMsg) {
		
		super(errorMsg);
	}

}
