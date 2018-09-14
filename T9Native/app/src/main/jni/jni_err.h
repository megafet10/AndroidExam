//
// Created by minhbq on 9/13/2018.
//

#ifndef T9NATIVE_JNI_ERR_H
#define T9NATIVE_JNI_ERR_H


#define ERR_NONE                (0)
#define ERR_INVALID_PARAM       (-1)
#define ERR_NOT_READY           (-2)
#define ERR_NOT_SUPPORT         (-3)
#define ERR_FAILED              (-4)
#define ERR_UNKNOWN             (-5)
#define ERR_ADD_HEADER_FAILED   (-6)
#define ERR_SEND_FAILED         (-7)
#define ERR_NOT_FOUND	        (-8)
#define ERR_INVALID_DATA 		(-9)
#define ERR_NO_MEMORY	 		(-10)
#define ERR_OPERATION_FAILED	(-11)
#define ERR_GEN_KEY_FAILED		(-12)
#define ERR_NO_KEY				(-13)
#define ERR_PENDING				(-14)
#define ERR_FAILED_ENCRYPT		(-15)
#define ERR_NO_SESSION			(-16)
#define ERR_EXPIRED_KEY			(-17)
#define ERR_EXPIRED_SESSION		(-18)
#define ERR_NO_DATA 			(-19)
#define ERR_INIT_FAILED			(-20)
#define ERR_UPDATE_FAILED		(-21)
#define ERR_TIMEOUT				(-22)
#define ERR_BAD_REQ				(-23)
#define ERR_ALREADY_EXIST		(-24)
#define ERR_NO_TARGET			(-25)
#define ERR_NOT_EXIST			(-26)
#define ERR_NO_LISTENER			(-27)
#define ERR_NO_CONNECTION		(-28)
#define ERR_ATTACH_FAILED		(-29)
#define ERR_DETACH_FAILED		(-30)
#define ERR_NO_OP       		(-31)
#define ERR_NO_HWSEC       		(-32)
#define ERR_VERIFY_FAILED     	(-33)
#define ERR_SIGNED_FAILED     	(-34)
#define ERR_NOT_MATCH        	(-35)
#define ERR_INVALID_CERT       	(-36)
#define ERR_BUSY             	(-37)
#define ERR_KEY_FAILED        	(-38)

/*
 * 4xx responses are definite failure responses from a particular server. The client SHOULD NOT retry the same request without modification (for example, adding appropriate authorization). However, the same request to a different server might be successful.

400 Bad Request
The request could not be understood due to malformed syntax. The Reason-Phrase SHOULD identify the syntax problem in more detail, for example, ""Missing Call-ID header field"".

401 Unauthorized
The request requires user authentication. This response is issued by UASs and registrars, while 407 (Proxy Authentication Required) is used by proxy servers.

402 Payment Required
Reserved for future use.

403 Forbidden
The server understood the request, but is refusing to fulfill it. Authorization will not help, and the request SHOULD NOT be repeated.

404 Not Found
The server has definitive information that the user does not exist at the domain specified in the Request-URI. This status is also returned if the domain in the Request-URI does not match any of the domains handled by the recipient of the request.

405 Method Not Allowed
The method specified in the Request-Line is understood, but not allowed for the address identified by the Request-URI.
The response MUST include an Allow header field containing a list of valid methods for the indicated address.

406 Not Acceptable
The resource identified by the request is only capable of generating response entities that have content characteristics not acceptable according to the Accept header field sent in the request.

407 Proxy Authentication Required
This code is similar to 401 (Unauthorized), but indicates that the client MUST first authenticate itself with the proxy.
This status code can be used for applications where access to the communication channel (for example, a telephony gateway) rather than the callee requires authentication.

408 Request Timeout
The server could not produce a response within a suitable amount of time, for example, if it could not determine the location of the user in time. The client MAY repeat the request without modifications at any later time.

410 Gone
The requested resource is no longer available at the server and no forwarding address is known. This condition is expected to be considered permanent. If the server does not know, or has no facility to determine, whether or not the condition is permanent, the status code 404 (Not Found) SHOULD be used instead.

413 Request Entity Too Large
The server is refusing to process a request because the request entity-body is larger than the server is willing or able to process. The server MAY close the connection to prevent the client from continuing the request.
If the condition is temporary, the server SHOULD include a Retry-After header field to indicate that it is temporary and after what time the client MAY try again.

414 Request-URI Too Long
The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.

415 Unsupported Media Type
The server is refusing to service the request because the message body of the request is in a format not supported by the server for the requested method. The server MUST return a list of acceptable formats using the Accept, Accept-Encoding, or Accept-Language header field, depending on the specific problem with the content.

416 Unsupported URI Scheme
The server cannot process the request because the scheme of the URI in the Request-URI is unknown to the server.

420 Bad Extension
The server did not understand the protocol extension specified in a Proxy-Require or Require header field. The server MUST include a list of the unsupported extensions in an Unsupported header field in the response.

421 Extension Required
The UAS needs a particular extension to process the request, but this extension is not listed in a Supported header field in the request. Responses with this status code MUST contain a Require header field listing the required extensions.
A UAS SHOULD NOT use this response unless it truly cannot provide any useful service to the client. Instead, if a desirable extension is not listed in the Supported header field, servers SHOULD process the request using baseline SIP capabilities and any extensions supported by the client.

423 Interval Too Brief
The server is rejecting the request because the expiration time of the resource refreshed by the request is too short. This response can be used by a registrar to reject a registration whose Contact header field expiration time was too small.

480 Temporarily Unavailable
The callee's end system was contacted successfully but the callee is currently unavailable (for example, is not logged in, logged in but in a state that precludes communication with the callee, or has activated the ""do not disturb"" feature). The response MAY indicate a better time to call in the Retry-After header field. The user could also be available elsewhere (unbeknownst to this server). The reason phrase SHOULD indicate a more precise cause as to why the callee is unavailable. This value SHOULD be settable by the UA. Status 486 (Busy Here) MAY be used to more precisely indicate a particular reason for the call failure.
This status is also returned by a redirect or proxy server that recognizes the user identified by the Request-URI, but does not currently have a valid forwarding location for that user.

481 Call/Transaction Does Not Exist
This status indicates that the UAS received a request that does not match any existing dialog or transaction.

482 Loop Detected
The server has detected a loop.

483 Too Many Hops
The server received a request that contains a Max-Forwards header field with the value zero.

484 Address Incomplete
The server received a request with a Request-URI that was incomplete. Additional information SHOULD be provided in the reason phrase.
This status code allows overlapped dialing. With overlapped dialing, the client does not know the length of the dialing string. It sends strings of increasing lengths, prompting the user for more input, until it no longer receives a 484 (Address Incomplete) status response.

485 Ambiguous
The Request-URI was ambiguous. The response MAY contain a listing of possible unambiguous addresses in Contact header fields. Revealing alternatives can infringe on privacy of the user or the organization. It MUST be possible to configure a server to respond with status 404 (Not Found) or to suppress the listing of possible choices for ambiguous Request-URIs.
Some email and voice mail systems provide this functionality. A status code separate from 3xx is used since the semantics are different: for 300, it is assumed that the same person or service will be reached by the choices provided. While an automated choice or sequential search makes sense for a 3xx response, user intervention is required for a 485 (Ambiguous) response.

486 Busy Here
The callee's end system was contacted successfully, but the callee is currently not willing or able to take additional calls at this end system. The response MAY indicate a better time to call in the Retry-After header field. The user could also be available elsewhere, such as through a voice mail service. Status 600 (Busy Everywhere) SHOULD be used if the client knows that no other end system will be able to accept this call.

487 Request Terminated
The request was terminated by a BYE or CANCEL request. This response is never returned for a CANCEL request itself.

488 Not Acceptable Here
The response has the same meaning as 606 (Not Acceptable), but only applies to the specific resource addressed by the Request-URI and the request may succeed elsewhere.
A message body containing a description of media capabilities MAY be present in the response, which is formatted according to the Accept header field in the INVITE (or application/sdp if not present), the same as a message body in a 200 (OK) response to an OPTIONS request.

491 Request Pending
The request was received by a UAS that had a pending request within the same dialog.

493 Undecipherable
The request was received by a UAS that contained an encrypted MIME body for which the recipient does not possess or will not provide an appropriate decryption key. This response MAY have a single body containing an appropriate public key that should be used to encrypt MIME bodies sent to this UA.
 * */

#define SERR_200_OK			(200)
#define SERR_200_OK_STR		"OK"


#define SERR_100_TRYING			(100)
#define SERR_100_TRYING_STR		"Trying"

#define SERR_180_RINGING		(180)
#define SERR_180_RINGING_STR	"Ringing"

#define SERR_400_BAD_REQ		(400)
#define SERR_400_BAD_REQ_STR	"Bad Request"

#define SERR_401_UNAUTHEN		(401)
#define SERR_401_UNAUTHEN_STR	"Unauthorized"

#define SERR_403_FORHIBIT		(403)
#define SERR_403_FORHIBIT_STR	"Forbiden"

#define SERR_404_NOT_FOUND		(404)
#define SERR_404_NOT_FOUND_STR	"Not found"

#define SERR_406_NOT_ACCEPT		(406)
#define SERR_406_NOT_ACCEPT_STR	"Not Acceptable"


#define SERR_408_TIMEOUT		(408)
#define SERR_408_TIMEOUT_STR	"Request timeout"


#define SERR_410_GONE		    (410)
#define SERR_410_GONE_STR	    "Gone"

#define SERR_437_CERT_ERR_KEY		(437)
#define SERR_437_CERT_ERR_STR	"Unsupported Certificate"

#define SERR_438_HMAC_FAILED_ERR_KEY		(438)
#define SERR_438_HMAC_FAILED_ERR_STR	"HMAC Failed"


#define SERR_480_UNAVALIABLE		(480)
#define SERR_480_UNAVALIABLE_STR	"Unavaliable"


#define SERR_486_BUSY			(486)
#define SERR_486_BUSY_STR		"Busy"

#define SERR_487_TERMINATED		(487)
#define SERR_487_TERMINATED_STR	"Request Terminated"


#define SERR_488_NOT_ACCEPT		(488)
#define SERR_488_NOT_ACCEPT_STR	"Not Acceptable Here"

#define SERR_488_BAD_REQ_NO_DATA_STR	"Bad Request (no data)"
#define SERR_488_BAD_REQ_INVALID_DATA_STR	"Bad Request (invalid data)"

#define SERR_1000_INVALID_KEY		(418)
#define SERR_1000_INVALID_KEY_STR	"Invalid key"

//tcp connect error
#define TCP_CONNECT_TIMEOUT_ERROR (120110)
#define TCP_SIP_SEND_FAILD (-1)

#endif //T9NATIVE_JNI_ERR_H
