package com.lednet.LEDBluetooth.COMM;

public class LEDResponse<T>
{

	public static final int LED_ErrorCode_Successful = 200;
    public static final int LED_ErrorCode_Error = 0;
	
    private int ErrorCode = 1;
    private String ErrorMessage = "";
	private T Result;
	
	
	public void setResponseResult(T result)
	{
		this.Result = result;
		this.ErrorCode = LED_ErrorCode_Successful;
	}
	
	public void setErrorMessage(String errorMessage)
	{
		this.ErrorMessage = errorMessage;
		this.ErrorCode = LED_ErrorCode_Error;
	}
	public void setErrorMessage(int errorCode,String errorMessage)
	{
		this.ErrorMessage = errorMessage;
		this.ErrorCode = errorCode;
	}
	

	/**返回错误信息
	 * @return the errorMessage_
	 */
	public String getErrorMessage() {
		return ErrorMessage;
	}

	/**
	 * 返回错误代码，1 代表成功，其他代表失败
	 * @return the errorCode_
	 */
	public int getErrorCode() {
		return ErrorCode;
	}
	
    public T getResult() {
		return Result;
	}

	
	
	public LEDResponse()
	{
		this.ErrorCode = LED_ErrorCode_Error;
		this.ErrorMessage = "No error message";
	}
	
	public LEDResponse(int errorcode,String errorMessage)
	{
			this.ErrorCode = errorcode;
			this.ErrorMessage = errorMessage;
	}
	

}



