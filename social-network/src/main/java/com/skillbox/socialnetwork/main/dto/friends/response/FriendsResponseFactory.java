package com.skillbox.socialnetwork.main.dto.friends.response;

import com.skillbox.socialnetwork.main.dto.universal.Dto;
import com.skillbox.socialnetwork.main.dto.universal.ErrorResponse;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendsResponseFactory {
    public static Dto getMessage(String message) {
        return new MessageDto(message);
    }

    public static Response getErrorMessage(){
        return new ErrorResponse("invalid_request" ,"string");
    }

    public static List<IsFriendDto> getIsFriendDtoList(Map<Person, String> personStatusMap){
        List<IsFriendDto> isFriendDtos = new ArrayList<>();

        for (Map.Entry<Person, String> entry : personStatusMap.entrySet()){
            isFriendDtos.add(new IsFriendDto(entry.getKey().getId(), entry.getValue()));
        }

        return isFriendDtos;
    }
}
