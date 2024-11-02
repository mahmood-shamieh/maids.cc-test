package cc.maids.test.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseDTO {


    private String message;
    private int code;
    private boolean status;
    private Object data;

    public static ResponseDTO forbiddenResponse(String message) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.code = 403;
        responseDTO.status = false;
        responseDTO.data = null;
        responseDTO.message = message;
        return responseDTO;
    }

    public static ResponseDTO badResponse(String message, Object data) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.code = 500;
        responseDTO.status = false;
        responseDTO.data = data;
        responseDTO.message = message;
        return responseDTO;
    }

    public static ResponseDTO successResponse(String message, Object data) {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.code = 200;
        responseDTO.status = true;
        responseDTO.data = data;
        responseDTO.message = message;
        return responseDTO;
    }

    public static ResponseDTO successResponse(String message) {


        return successResponse(message, null);
    }

    public static ResponseDTO successResponse(Object data) {


        return successResponse("Success", data);
    }

    public static ResponseDTO badResponse(String message) {
        return badResponse(message, null);
    }
}
