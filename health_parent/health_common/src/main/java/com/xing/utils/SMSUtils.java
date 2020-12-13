package com.xing.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * send validation code message
 */
public class SMSUtils {
	public static final String VALIDATE_CODE = "SMS_159620392"; // send validation code template code
	public static final String ORDER_NOTICE = "SMS_159771588"; // reservation completed template code
	private String accessKeyId;
	private String accessKeySecret;

	public SMSUtils(String accessKeyId, String accessKeySecret){
		this.accessKeyId = accessKeyId;
		this.accessKeySecret = accessKeySecret;
	}
	/**
	 * @param phoneNumbers
	 * @param param
	 * @throws ClientException
	 */
	public void sendShortMessage(String templateCode,String phoneNumbers,String param) throws ClientException{
		// timeout time
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		// init params for ascClient
		final String product = "Dysmsapi";// SMS product name
		final String domain = "dysmsapi.aliyuncs.com";// API domain

		// init ascClient , need specify region
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		// construct request object
		SendSmsRequest request = new SendSmsRequest();
		// post method
		request.setMethod(MethodType.POST);
		// phone number to receive this message , multiple number should be seperated by comma
		request.setPhoneNumbers(phoneNumbers);
		// message signature
		request.setSignName("");
		// message template
		request.setTemplateCode(templateCode);
		// optional : replace params with JSON string in template , eg. hi ${name} , your code is ${code}
		// break line signs should follow the JSON standard, \r\n will be replaced by \\r\\n
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		// optional : extension code of last message , length should be less than 7
		// request.setSmsUpExtendCode("90997");
		// optional : outId will be used in acknowledgment message ,
		// request.setOutId("yourOutId");
		// throw ClientException when request fail
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			// success
			System.out.println("SMS request successfully.");
		}
	}

	public static void main(String[] args) {

	}
}
